package com.github.shuvigoss.zconf.zookeeper;

import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.profiles.ProfilesFactory;
import com.google.common.base.Throwables;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.shuvigoss.zconf.zookeeper.ZConfClient.Status.*;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfClient {
  private final static Logger log = LoggerFactory.getLogger(ZConfClient.class);

  enum Status {
    READY,
    STARTING,
    STARTED,
    STOPING,
    STOPED
  }

  private ZConfClient() {}

  private AtomicReference<Status> state = new AtomicReference<>(READY);

  private CuratorFramework client;
  private DefaultProfiles profiles;
  private TreeCache cache;
  private TreeCacheListener listener;

  public static ZConfClient instance() {
    return ZConfClientWrapper.client;
  }

  private static class ZConfClientWrapper {
    private static final ZConfClient client = new ZConfClient();
  }

  public CuratorFramework get() {
    return client;
  }

  private void addListener() {
    if (listener == null) {
      log.info("no listener for TreeCache !");
      return;
    }
    cache = new TreeCache(client, "/");
    cache.getListenable().addListener(listener);
    try {
      cache.start();
    } catch (Exception e) {
      log.error("start TreeCache error!", e);
      Throwables.propagate(e);
    }
  }

  public void start() {
    if (profiles == null) profiles = ProfilesFactory.builder().build();
    if (!state.compareAndSet(READY, STARTING)) {
      log.warn("current status is {}, can not change to {}", state, STARTING);
      return;
    }

    client = CuratorFrameworkFactory.builder()
                                    .retryPolicy(profiles.getRetryPolicy())
                                    .connectString(profiles.getConnectionString())
                                    .namespace(profiles.getRootPath())
                                    .authorization(
                                        "digest", profiles.getAuth().getBytes())
                                    .build();
    addListener();

    client.start();
    state.set(STARTED);
    log.info("zookeeper client start success !");
  }

  public boolean isRunning() {
    return STARTED == state.get();
  }

  public DefaultProfiles getProfiles() {return this.profiles;}

  public void setProfiles(DefaultProfiles profiles) {
    this.profiles = Objects.requireNonNull(profiles, "profiles can not be null!");
  }

  public TreeCacheListener getListener() {
    return listener;
  }

  public void setListener(TreeCacheListener listener) {
    this.listener = listener;
  }

  public void shutdown() {
    if (!state.compareAndSet(STARTED, STOPING)) {
      log.warn("current status is {}, can not change to {}", state, STARTING);
      return;
    }
    client.close();
    state.set(STOPED);
    log.info("zookeeper client has closed !");
  }

}
