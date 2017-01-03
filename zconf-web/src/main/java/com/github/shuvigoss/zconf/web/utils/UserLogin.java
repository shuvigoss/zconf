package com.github.shuvigoss.zconf.web.utils;

import com.github.shuvigoss.zconf.web.components.eql.entity.User;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class UserLogin {

  public UserLogin(Status status, User userInfo) {
    this.status = status;
    this.userInfo = userInfo;
  }

  private Status status;
  private User   userInfo;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public User getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(User userInfo) {
    this.userInfo = userInfo;
  }

  public enum Status {
    //未登录
    NO_LOGIN,
    //登录失效
    INVALID,
    //有效
    VALID,
    //信息异常
    EXCEPTION
  }
}
