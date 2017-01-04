package com.github.shuvigoss.zconf.base;

import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.exception.ConvertibleException;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ConvertibleZConfigure extends ZConfInternal implements Convertible {
  private final static Logger log = LoggerFactory.getLogger(ConvertibleZConfigure.class);

  private final ConcurrentHashMap<String, ConvertorWrapper> convertorCache = new
      ConcurrentHashMap<>();

  private final ReentrantLock lock = new ReentrantLock();

  public ConvertibleZConfigure(DefaultProfiles profiles) {
    super(profiles);
  }

  @Override
  public <V> V getC(String key) {
    ZConfData data = getData(key);
    if (data == null) return null;

    if (!canConvert(data)) throw new ConvertibleException("no ValueConvertor key:" + key);
    //noinspection unchecked
    ConvertorWrapper<V> convertorWrapper = convertorCache.get(key);
    if (convertorWrapper == null) {
      initConvertor(key, data);
      //noinspection unchecked
      convertorWrapper = convertorCache.get(key);
    }

    if (convertorWrapper.isNull())
      doConvert(key, convertorWrapper);

    return convertorWrapper.getValue();
  }

  private void initConvertor(String key, ZConfData data) {
    lock.lock();
    try {
      String convertor = data.getHeader().getCvt();
      ValueConvertor c = ReflectionUtil.create(convertor);
      if (c != null) {
        //noinspection unchecked
        ConvertorWrapper wrapper = new ConvertorWrapper(key, c);
        wrapper.doConvert(data.getData());
        convertorCache.put(key, wrapper);
      }
    } finally {
      lock.unlock();
    }

  }

  private boolean canConvert(ZConfData data) {
    return !Objects.equals("default", data.getHeader().getCvt());
  }

  private void doConvert(String key, ConvertorWrapper convertorWrapper) {
    lock.lock();
    try {
      if (!convertorWrapper.isNull()) return;
      String val = get(key);
      if (val == null) {
        convertorCache.remove(key);
        log.warn("no cache key for {}, remove convertor {}", key, convertorWrapper
            .convertor.getClass().getName());
        return;
      }
      convertorWrapper.doConvert(val);
    } finally {
      lock.unlock();
    }
  }

  @Override
  protected void afterPut(String key, ZConfData data) {
    ConvertorWrapper convertorWrapper = convertorCache.get(key);
    if (convertorWrapper == null) return;
    convertorWrapper.clear();
  }

  @Override
  protected void afterRemove(String key) {
    ConvertorWrapper remove = convertorCache.remove(key);
    if (remove != null)
      log.info("remove convertor {}", remove.convertor.getClass().getName());
  }

  private static class ConvertorWrapper<V> {
    private String            key;
    private ValueConvertor<V> convertor;
    private V                 value;

    private ConvertorWrapper(String key, ValueConvertor<V> convertor) {
      this.key = key;
      this.convertor = convertor;
    }

    private void setValue(V value) {
      this.value = value;
    }

    private V getValue() {
      return value;
    }

    private String getKey() {
      return key;
    }

    private void clear() {
      setValue(null);
    }

    private boolean isNull() {
      return getValue() == null;
    }

    public void doConvert(String val) {
      setValue(convertor.convert(val));
    }
  }
}
