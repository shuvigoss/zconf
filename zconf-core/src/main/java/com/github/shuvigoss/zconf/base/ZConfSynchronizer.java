package com.github.shuvigoss.zconf.base;

import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.base.sync.LocalCacheSynchronizer;
import com.github.shuvigoss.zconf.base.sync.Synchronizer;
import com.github.shuvigoss.zconf.base.sync.ZKCacheSynchronizer;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.zookeeper.ZConfClient;
import com.github.shuvigoss.zconf.zookeeper.ZConfTreeNodeListener;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * synchronize all conf one time
 *
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfSynchronizer {

  private final static Logger log = LoggerFactory.getLogger(ZConfSynchronizer.class);

  private final ZConfInternal            zconf;
  private final ZConfClient              client;
  private       Synchronizer             local;
  private final Thread                   syncWorker;
  private       ScheduledExecutorService localStore;

  private       CountDownLatch        syncCDL = new CountDownLatch(1);
  private final BlockingQueue<Object> queue   = new LinkedBlockingDeque<>();

  public ZConfSynchronizer(ZConfInternal zconf, DefaultProfiles profiles) {
    this.zconf = zconf;
    client = ZConfClient.instance();
    client.setProfiles(profiles);
    client.setListener(new ZConfTreeNodeListener(this));

    if (useLocalCache(client.getProfiles())) {
      local = new LocalCacheSynchronizer(client.getProfiles());
      localStore = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          Thread thread = new Thread(r);
          thread.setName("ZConf-local-store-Thread");
          return thread;
        }
      });
    }
    syncWorker = new Thread(new ZConfWorker());
    syncWorker.setDaemon(true);
    syncWorker.setName("ZConf-zk-syncWorker-Thread");
  }

  private boolean useLocalCache(DefaultProfiles profiles) {
    return profiles.isUseLocalCache();
  }

  public void sync() {
    DefaultProfiles profiles = client.getProfiles();
    syncWorker.start();

    boolean cached = profiles.isUseLocalCache()
        && ((LocalCacheSynchronizer) local).hasCached(profiles);
    if (cached) {
      syncLocalCache();
    } else {
      syncRemote();
    }
    startLocalStore();

  }

  //modify key add or update
  public void modify(KVPair<String, byte[]> cache) {
    try {
      queue.put(cache);
    } catch (InterruptedException e) {
      log.error("notic error !", e);
      Throwables.propagate(e);
    }
  }

  //remove key
  public void modify(String key) {
    try {
      queue.put(key);
    } catch (InterruptedException e) {
      log.error("notic error !", e);
      Throwables.propagate(e);
    }
  }

  private void syncRemote() {
    try {
      syncCDL.await(15, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error("wait for syncRemote error !", e);
      trySync();
    }
  }

  private void trySync() {
    log.info("try sync by ZKCacheSynchronizer!");
    Map<String, byte[]> cache = new ZKCacheSynchronizer(client).sync();
    zconf.reload(cache);
  }

  private void syncLocalCache() {
    log.info("start to load local cache!");
    Map<String, byte[]> sync = local.sync();
    zconf.reload(sync);
  }

  private void reload(KVPair<String, byte[]> cache) {
    log.info("start to reload cache for key:{}", cache.getKey());
    zconf.reload(cache);
  }

  private void reload(String key) {
    zconf.remove(key);
  }

  public void mergeKeys(Set<String> keys) {
    zconf.doMerge(keys);
  }

  public void noticeSync() {
    try {
      queue.put(this);
    } catch (InterruptedException e) {
      log.error("notic error !", e);
      Throwables.propagate(e);
    }
  }

  private void startLocalStore() {
    DefaultProfiles profiles = client.getProfiles();
    if (useLocalCache(profiles))
      localStore.scheduleAtFixedRate(
          new ZConfLocalStoreWorker(),
          profiles.getLocalCacheInitialDelay(),
          profiles.getLocalCachePeriod(),
          TimeUnit.SECONDS
      );
  }

  public void destory() {
    client.shutdown();
  }

  private class ZConfWorker implements Runnable {
    @Override
    public void run() {
      for (; ; ) {
        try {
          log.info("start to load zk cache!");
          if (!client.isRunning()) client.start();

          Object cache = queue.take();
          if (cache instanceof CharSequence)
            ZConfSynchronizer.this.reload((String) cache);
          else if (cache instanceof KVPair)
            //noinspection unchecked
            ZConfSynchronizer.this.reload((KVPair<String, byte[]>) cache);
          else if (cache instanceof ZConfSynchronizer) {
            if (ZConfSynchronizer.this.syncCDL.getCount() != 0)
              ZConfSynchronizer.this.syncCDL.countDown();
            log.info("finish sync!");
          }

        } catch (InterruptedException e) {
          log.error("ZConfWorker Interrupted !");
          break;
        }
      }
    }
  }

  private class ZConfLocalStoreWorker implements Runnable {

    @Override
    public void run() {
      Map<String, ZConfData> dump = ZConfSynchronizer.this.zconf.dump();
      ((LocalCacheSynchronizer) local).store(dump);
    }
  }
}
