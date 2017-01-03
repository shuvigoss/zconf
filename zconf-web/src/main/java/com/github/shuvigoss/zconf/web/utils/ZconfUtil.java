package com.github.shuvigoss.zconf.web.utils;

import com.github.shuvigoss.zconf.web.components.ApplicationContextHolder;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.Perms.*;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ZconfUtil {

  public static String parse(String source) {
    return source.substring(source.lastIndexOf('/') + 1, source.length());
  }

  public static String rootPath(String source) {
    return source.substring(0, source.lastIndexOf('/'));
  }

  public static List<ACL> createACL(String adminAuth, String readAuth) {
    String rootAuth = ApplicationContextHolder.env()
                                              .getProperty("zconf.zookeeper.root.digest");
    List<ACL> result = Lists.newArrayListWithCapacity(3);
    try {
      ACL root = new ACL(
          ALL,
          new Id(
              "digest",
              DigestAuthenticationProvider.generateDigest(rootAuth)
          )
      );
      ACL admin = new ACL(
          READ | WRITE | CREATE | DELETE,
          new Id(
              "digest",
              DigestAuthenticationProvider.generateDigest(adminAuth)
          )
      );

      ACL read = new ACL(
          READ,
          new Id(
              "digest",
              DigestAuthenticationProvider.generateDigest(readAuth)
          )
      );
      result.add(root);
      result.add(admin);
      result.add(read);
    } catch (NoSuchAlgorithmException e) {
      Throwables.propagate(e);
    }
    return result;
  }

}
