package com.github.shuvigoss.zconf;

import com.github.shuvigoss.zconf.resource.ResourceLoader;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static com.github.shuvigoss.zconf.base.Const.CRLF;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfTest {

  static ZConf         zConf;
  static TestingServer server;
  static Properties properties = ResourceLoader.get();

  @BeforeClass
  public static void before() throws Exception {
    prepareProperties();
    server = new TestingServer(3333);
    insert();

    zConf = new ZConf();
    zConf.init();
  }

  private static void insert() throws Exception {
    CuratorFramework client = CuratorFrameworkFactory
        .builder().namespace(properties.getProperty("rootPath"))
        .connectString(server.getConnectString())
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
        .build();
    client.start();
    client.create().creatingParentsIfNeeded().forPath(
        "/test", ("default" + CRLF + CRLF + "test").getBytes());
    client.create().creatingParentsIfNeeded().forPath(
        "/test1", ("default" + CRLF + CRLF + "test1").getBytes());

    String convertorConf = "com.github.shuvigoss.zconf.PropertiesConvertor"
        + CRLF + CRLF + "key=1" + CRLF + "value=2";
    client.create().creatingParentsIfNeeded().forPath(
        "/test2", convertorConf.getBytes(UTF_8)
    );

    CloseableUtils.closeQuietly(client);
  }

  private static void prepareProperties() {
    properties.setProperty("zookeeper", "127.0.0.1:3333");
    properties.setProperty("rootPath", "test");
    properties.setProperty("useLocalCache", "false");
    properties.setProperty("localCachePath", "");
    properties.setProperty("localCacheInitialDelay", "15");
    properties.setProperty("localCachePeriod", "60");
    properties.remove("profiles");
  }

  @Test
  public void test() {
    assertThat(zConf.get("test"), equalTo("test"));
    assertThat(zConf.get("test1"), equalTo("test1"));
    Properties p = zConf.getC("test2");
    assertThat(p.getProperty("key"), equalTo("1"));
    assertThat(p.getProperty("value"), equalTo("2"));
  }

  @AfterClass
  public static void after() throws IOException {
    zConf.destory();
    server.close();
  }

}
