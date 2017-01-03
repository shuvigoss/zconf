package com.github.shuvigoss.zconf.profiles;

import java.util.Objects;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class AbstractProfiles {

  private String connectionString;

  private String rootPath;

  /**
   * if useLocalCache is true
   * 1.load config from local cache(user.dir or use your config)
   * 2.sync zookeeper config
   * 3.compare and merge
   * 4.store merged result to local cache.
   * <p>
   * if useLocalCache is false
   * load config from zookeeper only
   */
  private boolean useLocalCache;

  /**
   * if useLocalCache is true, the config store path
   */
  private String localCachePath;

  private int localCacheInitialDelay;

  private int localCachePeriod;

  private String auth;

  public AbstractProfiles(String connectionString, String rootPath) {
    this.connectionString = Objects.requireNonNull(connectionString);
    this.rootPath = Objects.requireNonNull(rootPath);
  }

  public boolean isUseLocalCache() {
    return useLocalCache;
  }

  public void setUseLocalCache(boolean useLocalCache) {
    this.useLocalCache = useLocalCache;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public String getRootPath() {
    return rootPath;
  }

  public String getLocalCachePath() {
    return localCachePath;
  }

  public void setLocalCachePath(String localCachePath) {
    this.localCachePath = localCachePath;
  }

  public int getLocalCacheInitialDelay() {
    return localCacheInitialDelay;
  }

  public void setLocalCacheInitialDelay(int localCacheInitialDelay) {
    this.localCacheInitialDelay = localCacheInitialDelay;
  }

  public int getLocalCachePeriod() {
    return localCachePeriod;
  }

  public void setLocalCachePeriod(int localCachePeriod) {
    this.localCachePeriod = localCachePeriod;
  }

  public String getAuth() {
    return auth;
  }

  public void setAuth(String auth) {
    this.auth = auth;
  }
}
