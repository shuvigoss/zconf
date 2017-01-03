package com.github.shuvigoss.zconf.web.utils;

import com.alibaba.fastjson.JSON;
import com.github.shuvigoss.zconf.web.components.ApplicationContextHolder;
import com.github.shuvigoss.zconf.web.components.eql.entity.Role;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.utils.security.TripleDesCryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import javax.servlet.http.Cookie;
import java.util.List;

import static com.github.shuvigoss.zconf.web.utils.Constants.COOKIE_USER;
import static com.github.shuvigoss.zconf.web.utils.Constants.ROOT;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class UserLoginUtil {
  private static final Logger log = LoggerFactory.getLogger(UserLoginUtil.class);

  public static UserLogin get() {
    Cookie cookie = CookieUtil.get(COOKIE_USER);
    if (cookie == null)
      return new UserLogin(UserLogin.Status.NO_LOGIN, null);

    String value = cookie.getValue();
    TripleDesCryptor desCryptor = new TripleDesCryptor();
    try {
      String userInfo = desCryptor.decrypt(
          new String(Base64Utils.decode(value.getBytes())));
      long loginTime = Long.parseLong(
          userInfo.substring(userInfo.lastIndexOf('|') + 1, userInfo.length()));
      Integer maxAlive = ApplicationContextHolder.env().getProperty(
          "js.cookie.alive", Integer.class);
      if (maxAlive == null)
        maxAlive = 259200000;

      User user = JSON
          .parseObject(userInfo.substring(0, userInfo.lastIndexOf('|')), User.class);

      if (System.currentTimeMillis() - loginTime > maxAlive) {
        log.warn("cookie有效期超过" + maxAlive);
        return new UserLogin(UserLogin.Status.INVALID, user);
      }
      return new UserLogin(UserLogin.Status.VALID, user);
    } catch (Throwable e) {
      log.error("解析cookie 异常", e);
      return new UserLogin(UserLogin.Status.EXCEPTION, null);
    }
  }

  public static void del() {
    CookieUtil.del(COOKIE_USER);
  }

  public static boolean isRoot(List<Role> roles) {
    for (Role role : roles)
      if (role.getName().equals(ROOT)) return true;
    return false;
  }

}
