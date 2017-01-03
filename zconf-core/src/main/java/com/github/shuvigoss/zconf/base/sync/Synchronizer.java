package com.github.shuvigoss.zconf.base.sync;

import com.github.shuvigoss.zconf.profiles.DefaultProfiles;

import java.util.Map;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public interface Synchronizer {

  Map<String, byte[]> sync();


}
