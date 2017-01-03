package com.github.shuvigoss.zconf.profiles;

import com.github.shuvigoss.zconf.resource.ResourceLoader;
import com.google.common.base.Strings;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ProfilesFactory {

  private static final Properties config = ResourceLoader.get();

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private RetryPolicy     retryPolicy;
    private DefaultProfiles profiles;
    private Boolean         useLocalCache;
    private String          localCachePath;
    private int             localCacheInitialDelay;
    private int             localCachePeriod;

    private Builder() {

      profiles = new DefaultProfiles(
          config.getProperty("zookeeper"),
          config.getProperty("rootPath")
      );
      profiles.setAuth(config.getProperty("auth", ""));
      useLocalCache = Boolean.parseBoolean(config.getProperty("useLocalCache", "true"));
      String localCache = config.getProperty("localCachePath");
      if (Strings.isNullOrEmpty(localCache))
        localCache = System.getProperty("user.home");

      String realPath = ".zconf" + File.separator + profiles.getRootPath();
      if (localCache.endsWith(File.separator))
        localCache = localCache + realPath;
      else
        localCache = localCache + File.separator + realPath;

      localCachePath = localCache;

      localCacheInitialDelay = Integer.parseInt(config.getProperty
          ("localCacheInitialDelay", "15"));
      localCachePeriod = Integer.parseInt(config.getProperty
          ("localCachePeriod", "60"));
    }

    public Builder retryPolicy(RetryPolicy retryPolicy) {
      this.retryPolicy = Objects.requireNonNull(retryPolicy);
      return this;
    }

    public DefaultProfiles build() {
      if (retryPolicy == null) retryPolicy = new ExponentialBackoffRetry(3000, 10);

      profiles.setRetryPolicy(retryPolicy);
      profiles.setLocalCachePath(localCachePath);
      profiles.setUseLocalCache(useLocalCache);
      profiles.setLocalCacheInitialDelay(localCacheInitialDelay);
      profiles.setLocalCachePeriod(localCachePeriod);
      return profiles;
    }
  }

}
