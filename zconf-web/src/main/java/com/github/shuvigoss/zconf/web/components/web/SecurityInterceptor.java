package com.github.shuvigoss.zconf.web.components.web;

import com.alibaba.fastjson.JSON;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.utils.CookieUtil;
import com.github.shuvigoss.zconf.web.utils.UserLogin;
import com.github.shuvigoss.zconf.web.utils.UserLoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.shuvigoss.zconf.web.utils.Constants.COOKIE_USER;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

  private static final Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);

  private static final ThreadLocal<User> userCache = new ThreadLocal<>();

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler) throws Exception {

    UserLogin userLogin = UserLoginUtil.get();
    log.info("user info is {}", JSON.toJSONString(userLogin));

    switch (userLogin.getStatus()) {
      case NO_LOGIN:
        response.sendRedirect("/");
        break;
      case VALID: {
        userCache.set(userLogin.getUserInfo());
        return true;
      }
      case INVALID: {
        CookieUtil.del(COOKIE_USER);
        response.sendRedirect("/");
        break;
      }
      case EXCEPTION:
        break;
    }
    return false;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) throws Exception {
    userCache.remove();
  }

  public static User getUser() {
    return userCache.get();
  }
}
