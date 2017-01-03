package com.github.shuvigoss.zconf;

import com.github.shuvigoss.zconf.base.KVPair;
import com.github.shuvigoss.zconf.base.ZConfParser;
import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.resource.ResourceLoader;
import com.google.common.io.Files;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static com.github.shuvigoss.zconf.FileUtil.deleteFile;
import static com.github.shuvigoss.zconf.base.Const.CRLF;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfLocalSyncTest {

  static ZConf         zConf;
  static TestingServer server;
  static Properties properties = ResourceLoader.get();

  static Stat stat;

  @BeforeClass
  public static void before() throws Exception {
    prepareProperties();
    server = new TestingServer(4444);
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
    stat = client.checkExists().forPath("/test");

    CloseableUtils.closeQuietly(client);
  }

  private static void prepareProperties() {
    properties.setProperty("zookeeper", "127.0.0.1:4444");
    properties.setProperty("rootPath", "localTest");
    properties.setProperty("useLocalCache", "true");
    properties.setProperty("localCachePath", System.getProperty("user.home"));
    properties.setProperty("localCacheInitialDelay", "0");
    properties.setProperty("localCachePeriod", "5");
    properties.remove("profiles");
  }

  @Test
  public void test() throws IOException {
    assertThat(zConf.get("test"), equalTo("test"));
    try {
      Thread.sleep(8000);
    } catch (InterruptedException ignored) {
    }
    DefaultProfiles defaultProfiles = zConf.getDefaultProfiles();
    String localCachePath = defaultProfiles.getLocalCachePath();
    File cacheFile = new File(localCachePath + File.separator + "test-" + stat.getMzxid
        () + ".cache");
    String content = Files.toString(cacheFile, UTF_8);
    KVPair<String, ZConfData> test =
        ZConfParser.parse(new KVPair<>("test", content.getBytes()));
    assertThat(test.getValue().getData(), equalTo("test"));
  }

  @AfterClass
  public static void after() throws IOException {
    File tmp = new File(System.getProperty("user.home") + File.separator + ".zconf");
    deleteFile(tmp);
    zConf.destory();
    server.close();
  }

}
