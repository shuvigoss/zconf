package com.github.shuvigoss.zconf.web.utils.collection;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class CollectionUtil {

  public interface Filter<T> {
    T filter(Object source);
  }

  public static <T> Collection<T> copy(Collection source, Filter<T> filter) {
    notNull(source, "source can not be null");
    if (source.isEmpty())
      return new ArrayList<>(0);
    ArrayList<T> result = Lists.newArrayListWithCapacity(source.size());
    for (Object obj : source) {
      T t = filter.filter(obj);
      result.add(t);
    }
    return result;
  }
}
