package com.github.shuvigoss.zconf.web.components.eql.entity;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;
import java.util.List;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class Zconf {

  @NotEmpty
  private String rootPath;

  @NotEmpty
  private String adminAuth;//acl for admin

  @NotEmpty
  private String readAuth;//acl for read

  private Date createTime;

  private List<User> users;

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  public String getAdminAuth() {
    return adminAuth;
  }

  public void setAdminAuth(String adminAuth) {
    this.adminAuth = adminAuth;
  }

  public String getReadAuth() {
    return readAuth;
  }

  public void setReadAuth(String readAuth) {
    this.readAuth = readAuth;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}
