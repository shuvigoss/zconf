package com.github.shuvigoss.zconf.base.sync;

import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.exception.ZConfStartupException;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * cache file name is {cacheKey}-mzxid.cache
 *
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class LocalCacheSynchronizer implements Synchronizer {
  private final static Logger log = LoggerFactory.getLogger(LocalCacheSynchronizer.class);

  public static final String  syncFileFlag     = ".cached";
  public static final Pattern cacheFilePattern = Pattern.compile(".+-\\d+.cache");
  private File path;

  public LocalCacheSynchronizer(DefaultProfiles profiles) {
    path = new File(profiles.getLocalCachePath());
    if (!path.exists()) {
      boolean localCacheDir = path.mkdirs();
      if (!localCacheDir)
        throw new ZConfStartupException("create localCacheDir error ! " + path.toPath());
    }
  }

  @Override
  public Map<String, byte[]> sync() {
    Map<String, byte[]> caches = Maps.newHashMap();
    File[] files = path.listFiles();
    if (files == null) return caches;

    for (File file : files) {
      String name = file.getName();
      if (Objects.equals(name, syncFileFlag)) continue;
      if (!cacheFilePattern.matcher(name).matches()) continue;

      String cacheKey = name.substring(0, name.lastIndexOf('-'));
      byte[] value = readFile(file);
      caches.put(cacheKey, value);
    }
    return caches;
  }

  private byte[] readFile(File file) {
    try {
      return Files.toByteArray(file);
    } catch (IOException e) {
      log.error("read {} cache file error!", file.getName());
      Throwables.propagate(e);
    }
    return null;
  }

  public boolean hasCached(DefaultProfiles profiles) {
    File cacheDir = new File(profiles.getLocalCachePath());
    if (!cacheDir.exists()) return false;

    String[] list = cacheDir.list();
    if (list == null) return false;

    for (String fileName : list) {
      if (Objects.equals(syncFileFlag, fileName))
        return true;
    }

    return false;
  }

  public void store(Map<String, ZConfData> dump) {
    removeUnused(dump);

    update(dump);

    finish();
  }

  private void finish() {
    log.info("local store finish ! ");
    File finishFile = new File(path, syncFileFlag);
    try {
      Files.touch(finishFile);
    } catch (IOException e) {
      log.error("error to touch {}", finishFile, e);
    }
  }

  private void update(Map<String, ZConfData> dump) {
    File[] files = path.listFiles();
    if (files == null) return;

    Map<String, File> keyFiles = buildKeyFiles(files);

    for (Map.Entry<String, ZConfData> cache : dump.entrySet()) {
      File file = keyFiles.get(cache.getKey());
      if (file != null)
        doModify(file, cache);
      else
        writeCache(cache);
    }
  }

  private Map<String, File> buildKeyFiles(File[] files) {
    Map<String, File> result = Maps.newHashMapWithExpectedSize(files.length);
    for (File file : files) {
      String name = file.getName();
      if (Objects.equals(name, syncFileFlag)) continue;

      if (!cacheFilePattern.matcher(name).matches()) continue;

      String cacheKey = name.substring(0, name.lastIndexOf('-'));
      result.put(cacheKey, file);
    }
    return result;
  }

  private void doModify(File file, Map.Entry<String, ZConfData> zConfData) {
    String name = file.getName();
    int endIndex = name.lastIndexOf('-');
    String mzxid = name.substring(endIndex + 1, name.lastIndexOf('.'));
    long oldZxid = Long.parseLong(mzxid);
    long newZxid = zConfData.getValue().getHeader().getMzxid();
    boolean merge = newZxid > oldZxid;

    log.info(
        "old Zxid is {}, new Zxid is {}, doMerge ? {}",
        oldZxid, newZxid, merge
    );

    if (merge) {
      boolean delete = file.delete();
      log.info("remove old cache file {}:{}", name, delete);
      if (!delete) {
        log.warn("can not remove old cache file {} !", name);
        return;
      }

      writeCache(zConfData);
    }
  }

  private void writeCache(Map.Entry<String, ZConfData> zConfData) {
    ZConfData value = zConfData.getValue();
    String fileName = zConfData.getKey() + "-" +
        value.getHeader().getMzxid() + ".cache";

    File file = new File(path, fileName);
    if (file.exists()) {
      log.warn("file {} already exists !!", file);
      return;
    }
    try {
      log.info(
          "start to write local cahce file {} with content \r\n{}", file,
          zConfData.getValue()
      );
      Files.touch(file);
      Files.write(value.toString(), file, UTF_8);
    } catch (IOException e) {
      log.error("write file {} error !", file);
    }
  }

  private void removeUnused(Map<String, ZConfData> dump) {
    File[] files = path.listFiles();
    if (files == null) return;

    Set<String> keys = dump.keySet();
    for (File file : files) {
      String name = file.getName();
      if (Objects.equals(name, syncFileFlag)) continue;

      if (!cacheFilePattern.matcher(name).matches()) continue;

      String cacheKey = name.substring(0, name.lastIndexOf('-'));
      if (keys.contains(cacheKey)) continue;

      boolean delete = file.delete();
      log.info("remove old cache file {}:{}", name, delete);
    }
  }

}
