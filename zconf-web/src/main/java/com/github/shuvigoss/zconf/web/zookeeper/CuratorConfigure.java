package com.github.shuvigoss.zconf.web.zookeeper;

import com.google.common.base.Throwables;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Configuration
public class CuratorConfigure implements ApplicationListener {

  private Logger log = LoggerFactory.getLogger(CuratorConfigure.class);
  @Value("${zconf.zookeeper.rootPath}")
  private String rootPath;
  @Value("${zconf.zookeeper.root.digest}")
  private String digestRoot;

  @Bean(initMethod = "start", destroyMethod = "close")
  public CuratorFramework curatorFramework(
      @Value("${zconf.zookeeper.connectStr}") String connectionStr) {
    return CuratorFrameworkFactory
        .builder()
        .retryPolicy(new ExponentialBackoffRetry(3000, 10))
        .connectString(connectionStr)
        .authorization("digest", digestRoot.getBytes())
        .build();
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof ApplicationReadyEvent) {
      log.info("start to create rootPath for zconf!");
      CuratorFramework client = ((ApplicationReadyEvent) event)
          .getApplicationContext()
          .getBean(CuratorFramework.class);
      try {
        Stat stat = client.checkExists().forPath(rootPath);
        if (stat == null) {
          log.info("no root path found : {} , try to create", rootPath);
          client.create().creatingParentsIfNeeded().forPath(rootPath);
          log.info("create root path success : {}", rootPath);
        } else log.info("root path :{} has exist ", rootPath);
      } catch (Exception e) {
        log.error("check rootPath error", e);
        Throwables.propagate(e);
      }
    }
  }

}
