package com.github.shuvigoss.zconf;

import com.github.shuvigoss.zconf.base.ConvertibleZConfigure;
import com.github.shuvigoss.zconf.profiles.DefaultProfiles;
import com.github.shuvigoss.zconf.profiles.DefaultProfilesCreator;
import com.github.shuvigoss.zconf.profiles.ProfilesCreator;
import com.github.shuvigoss.zconf.resource.ResourceLoader;
import com.github.shuvigoss.zconf.util.ReflectionUtil;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConf extends ConvertibleZConfigure {

  public ZConf() {
    super(createProfiles());
  }

  private static DefaultProfiles createProfiles() {
    String profilesCreator =
        ResourceLoader.get().getProperty(
            "profiles",
            DefaultProfilesCreator.class.getCanonicalName()
        );
    ProfilesCreator creator = ReflectionUtil.create(profilesCreator);
    if (creator == null) creator = new DefaultProfilesCreator();
    return creator.create();
  }

}
