package com.github.shuvigoss.zconf.profiles;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class DefaultProfilesCreator implements ProfilesCreator {

  @Override
  public DefaultProfiles create() {
    return ProfilesFactory.builder().build();
  }
}
