package com.github.shuvigoss.zconf.profiles;

import org.apache.curator.RetryPolicy;

/**
 * default ProfilesCreator base on Curator!
 *
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class DefaultProfiles extends AbstractProfiles {

  private RetryPolicy retryPolicy;

  public DefaultProfiles(String connectionString, String rootPath) {
    super(connectionString, rootPath);
  }

  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

  public void setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
  }

}
