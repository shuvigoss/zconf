package com.github.shuvigoss.zconf.web.components.eql.entity;

import java.util.Objects;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class Role {

  private String name;

  public Role() {
  }

  public Role(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return Objects.equals(name, role.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
