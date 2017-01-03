package com.github.shuvigoss.zconf.base.sync;

import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.base.data.ZConfHeader;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.profiles.ProfilesFactory;
import com.github.shuvigoss.zconf.resource.ResourceLoader;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.github.shuvigoss.zconf.FileUtil.deleteFile;
import static com.github.shuvigoss.zconf.base.Const.CRLF;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class LocalCacheSynchronizerTest {

  static Properties p = ResourceLoader.get();

  static {
    p.setProperty("useLocalCache", "true");
    p.setProperty("localCachePath", System.getProperty("user.home"));
  }

  static DefaultProfiles        profiles     = ProfilesFactory.builder().build();
  static LocalCacheSynchronizer synchronizer = new LocalCacheSynchronizer(profiles);

  @Test
  public void test() throws IOException {
    testCacheAll();
  }

  private void testCacheAll() throws IOException {
    Map<String, ZConfData> dump = Maps.newHashMap();

    File oldCache = new File(profiles.getLocalCachePath() + File.separator +
                                 "keyA-1.cache");
    String content =
        "1111" + CRLF +
            "2222" + CRLF +
            "3333" + CRLF +
            "4444" + CRLF +
            "5555" + CRLF +
            "6666" + CRLF +
            "com.github.shuvigoss.Test" + CRLF + CRLF +
            "some cache ";
    Files.write(content, oldCache, UTF_8);

    ZConfHeader header1 = new ZConfHeader(1, 1, 1, 1, 1);
    header1.setCvt("default");
    String data1 = "value1";
    ZConfData zConfData1 = new ZConfData(header1, data1);
    dump.put("key1", zConfData1);

    ZConfHeader oldHeader2 = new ZConfHeader(1, 1, 1, 1, 1);
    oldHeader2.setCvt("default");
    String oldData2 = "oldValue2";
    ZConfData oldZConfData2 = new ZConfData(oldHeader2, oldData2);
    File oldData2File = new File(profiles.getLocalCachePath() + File.separator +
                                     "key2-1.cache");
    Files.write(oldZConfData2.toString(), oldData2File, UTF_8);

    ZConfHeader header2 = new ZConfHeader(1, 2, 1, 1, 1);
    header2.setCvt("default");
    String data2 = "value2";
    ZConfData zConfData2 = new ZConfData(header2, data2);
    dump.put("key2", zConfData2);

    synchronizer.store(dump);
    File key1File = new File(profiles.getLocalCachePath() + File.separator +
                                 "key1-1.cache");
    File key2File = new File(profiles.getLocalCachePath() + File.separator +
                                 "key2-2.cache");
    assertTrue(key1File.exists());
    assertThat(Files.toString(key1File, UTF_8), equalTo(zConfData1.toString()));

    assertTrue(key2File.exists());
    assertThat(Files.toString(key2File, UTF_8), equalTo(zConfData2.toString()));

    assertTrue(!oldCache.exists());
    clear();
  }

  private void clear() {
    File dir = new File(profiles.getLocalCachePath());
    File parentFile = dir.getParentFile();
    deleteFile(parentFile);
  }

}
