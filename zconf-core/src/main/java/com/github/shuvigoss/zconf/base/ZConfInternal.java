package com.github.shuvigoss.zconf.base;

import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ZConfInternal implements ZConfigure {
  private final static Logger log = LoggerFactory.getLogger(ZConfInternal.class);

  private Map<String, ZConfData> confCache = new ConcurrentHashMap<>();

  private final ZConfSynchronizer sync;
  private final DefaultProfiles   defaultProfiles;

  public ZConfInternal(DefaultProfiles profiles) {
    defaultProfiles = Objects.requireNonNull(profiles);
    sync = new ZConfSynchronizer(this, profiles);
  }

  public final void init() {
    sync.sync();
  }

  @Override
  public String get(String key) {
    ZConfData zConfData = confCache.get(key);
    if (zConfData == null) return null;
    return zConfData.getData();
  }

  protected ZConfData getData(String key) {
    return confCache.get(key);
  }

  void reload(Map<String, byte[]> conf) {
    Map<String, ZConfData> parse = ZConfParser.parse(conf);
    for (Map.Entry<String, ZConfData> entry : parse.entrySet()) {
      String key = entry.getKey();
      ZConfData value = entry.getValue();
      ZConfData zConfData = confCache.get(key);
      if (zConfData == null) {
        log.debug("new cache for key:{}, value:{}", key, value);
        put(key, value);
      } else {
        long currentZxid = zConfData.getHeader().getMzxid();
        long newZxid = value.getHeader().getMzxid();
        if (currentZxid < newZxid)
          put(key, value);
        log.debug("current zxid:{}, new zxid:{}", currentZxid, newZxid);
      }
    }
    doMerge(conf.keySet());
  }

  private void put(String key, ZConfData data) {
    beforePut(key, data);
    confCache.put(key, data);
    afterPut(key, data);
  }

  private void removeKey(String key) {
    beforeRemove(key);
    confCache.remove(key);
    afterRemove(key);
  }

  protected void beforePut(String key, ZConfData data) {}

  protected void afterPut(String key, ZConfData data) {}

  protected void beforeRemove(String key) {}

  protected void afterRemove(String key) {}

  public void doMerge(Set<String> keys) {
    Set<String> currentKeys = confCache.keySet();
    Sets.SetView<String> willRemove = Sets.difference(currentKeys, keys);
    for (String oldKey : willRemove)
      remove(oldKey);

  }

  void reload(KVPair<String, byte[]> cache) {
    KVPair<String, ZConfData> c = ZConfParser.parse(cache);
    String key = c.getKey();
    ZConfData value = c.getValue();

    ZConfData zConfData = confCache.get(c.getKey());
    ZConfData newData = c.getValue();

    if (zConfData == null) {
      log.debug("new cache for key:{}, value:{}", key, value);
      put(key, value);
    } else {
      long currentZxid = zConfData.getHeader().getMzxid();
      long newZxid = newData.getHeader().getMzxid();
      if (currentZxid < newZxid)
        put(c.getKey(), newData);

      log.debug("current zxid:{}, new zxid:{}", currentZxid, newZxid);
    }

  }

  void remove(String key) {
    checkNotNull(key);
    log.debug("will remove key:{}", key);
    removeKey(key);
  }

  Map<String, ZConfData> dump() {
    Map<String, ZConfData> dump = Maps.newHashMapWithExpectedSize(confCache.size());
    for (Map.Entry<String, ZConfData> entry : confCache.entrySet()) {
      try {
        dump.put(entry.getKey(), (ZConfData) entry.getValue().clone());
      } catch (CloneNotSupportedException e) {
        log.error("dump {} error!", entry);
      }
    }
    return dump;
  }

  public DefaultProfiles getDefaultProfiles() {
    return defaultProfiles;
  }

  public void destory() {
    sync.destory();
  }
}
