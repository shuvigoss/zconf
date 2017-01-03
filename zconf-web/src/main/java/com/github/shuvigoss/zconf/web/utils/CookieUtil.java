package com.github.shuvigoss.zconf.web.utils;

import com.google.common.base.Strings;

import javax.servlet.http.Cookie;

import static com.github.shuvigoss.zconf.web.utils.Args.isTrue;
import static com.github.shuvigoss.zconf.web.utils.Args.notNullNotEmpty;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class CookieUtil {

  public static final int    MAXAGE_DEFAULT = -1;
  public static final int    MAXAGE_DELETE  = 0;
  public static final String DEFAULT_DOMAIN = "/";

  public static void add(String name, String value,
                         int maxAge, String domain) {
    notNullNotEmpty(name, "cookie name can not be null");
    notNullNotEmpty(value, "cookie value can not be null");
    isTrue(maxAge >= -1, "maxAge must >= -1");
    addCookie(name, value, maxAge, domain);
  }

  private static void addCookie(String name, String value,
                                int maxAge, String domain) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    if (!Strings.isNullOrEmpty(domain))
      cookie.setDomain(domain);
    WebUtil.response().addCookie(cookie);
  }

  public static Cookie get(String name) {
    Cookie[] cookies = WebUtil.request().getCookies();
    if (null == cookies || Strings.isNullOrEmpty(name)) {
      return null;
    }
    for (Cookie c : cookies) {
      if (name.equals(c.getName())) {
        return c;
      }
    }
    return null;
  }

  public static void del(String name) {
    notNullNotEmpty(name, "cookie name can not be null");
    addCookie(name, "", MAXAGE_DELETE, null);
  }
}
