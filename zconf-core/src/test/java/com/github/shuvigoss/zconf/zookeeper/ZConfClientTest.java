package com.github.shuvigoss.zconf.zookeeper;

import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.profiles.ProfilesFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfClientTest {

  final static String PATH = "/test";
  static ZConfClient   zConfClient;
  static TestingServer server;

  @BeforeClass
  public static void startClient() throws Exception {
    server = new TestingServer(3333);
    zConfClient = ZConfClient.instance();
    DefaultProfiles profiles = ProfilesFactory.builder().build();
    zConfClient.setProfiles(profiles);
    zConfClient.start();
  }

  @Test
  public void test() throws Exception {
    CuratorFramework client = zConfClient.get();
    client.create().creatingParentsIfNeeded().forPath(PATH, "test".getBytes());
    String test = new String(client.getData().forPath(PATH));
    assertThat(test, equalTo("test"));

  }

  @AfterClass
  public static void shutdownClient() {
    zConfClient.shutdown();
  }

}
