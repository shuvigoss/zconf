package com.github.shuvigoss.zconf.base.sync;

import com.github.shuvigoss.zconf.base.ZConfParser;
import com.github.shuvigoss.zconf.base.ZConfSynchronizer;
import com.github.shuvigoss.zconf.zookeeper.ZConfClient;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZKCacheSynchronizer implements Synchronizer {
  private static final Logger log = LoggerFactory.getLogger(ZKCacheSynchronizer.class);

  private ZConfClient client;

  public ZKCacheSynchronizer(ZConfClient client) {
    this.client = client;
  }

  @Override
  public Map<String, byte[]> sync() {
    if (!client.isRunning()) client.start();
    CuratorFramework curator = client.get();
    Map<String, byte[]> result = Maps.newHashMap();
    try {
      List<String> caches = curator.getChildren().forPath("/");
      for (String cache : caches) {
        Stat stat = new Stat();
        byte[] body = curator.getData().storingStatIn(stat).forPath("/" + cache);
        byte[] header = ZConfParser.toByte(stat);
        byte[] content = ArrayUtils.addAll(header, body);
        result.put(cache, content);
      }

    } catch (Exception e) {
      log.error("error to get children from path:{}", curator.getNamespace());
      Throwables.propagate(e);
    }
    return result;
  }
}
