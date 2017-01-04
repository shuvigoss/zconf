package com.github.shuvigoss.zconf.zookeeper;

import com.github.shuvigoss.zconf.base.KVPair;
import com.github.shuvigoss.zconf.base.ZConfParser;
import com.github.shuvigoss.zconf.base.ZConfSynchronizer;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_ADDED;
import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_REMOVED;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfTreeNodeListener implements TreeCacheListener {
  private final static Logger log = LoggerFactory.getLogger(ZConfTreeNodeListener.class);
  private final ZConfSynchronizer sync;

  private Set<String> keys = Sets.newConcurrentHashSet();

  public ZConfTreeNodeListener(ZConfSynchronizer sync) {
    this.sync = sync;
  }

  @Override
  public void childEvent(CuratorFramework curatorFramework,
                         TreeCacheEvent treeCacheEvent) throws Exception {
    log.info("receive event {}", treeCacheEvent);
    switch (treeCacheEvent.getType()) {
      case NODE_ADDED:
        noticeModify(treeCacheEvent); return;
      case NODE_REMOVED:
        noticeRemove(treeCacheEvent); return;
      case NODE_UPDATED:
        noticeModify(treeCacheEvent); return;
      case INITIALIZED:
        sync.noticeSync();
        sync.mergeKeys(keys);
        return;
      default:
        log.info("unknow event {}", treeCacheEvent);
    }
  }

  private void noticeModify(TreeCacheEvent treeCacheEvent) {
    byte[] data = treeCacheEvent.getData().getData();
    Stat stat = treeCacheEvent.getData().getStat();
    String path = cacheAndBuildKey(treeCacheEvent);
    //may be the root node "/"
    if (path.length() == 0 || data.length == 0) return;

    if (sync != null) {
      byte[] header = ZConfParser.toByte(stat);
      byte[] content = ArrayUtils.addAll(header, data);
      sync.modify(new KVPair<>(path, content));
    } else log.error("no ZConfSynchronizer for update !");
  }

  private void noticeRemove(TreeCacheEvent treeCacheEvent) {
    String path = cacheAndBuildKey(treeCacheEvent);
    if (sync != null) {
      sync.modify(path);
    } else log.error("no ZConfSynchronizer for update !");
  }

  private String cacheAndBuildKey(TreeCacheEvent treeCacheEvent) {
    String key = treeCacheEvent.getData().getPath().replaceAll("/", "");
    if (treeCacheEvent.getType() == NODE_ADDED) {
      if (!keys.contains(key)) keys.add(key);
    } else if (treeCacheEvent.getType() == NODE_REMOVED) {
      if (keys.contains(key)) keys.remove(key);
    }
    return key;
  }
}
