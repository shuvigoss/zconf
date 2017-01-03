package com.github.shuvigoss.zconf.web.controller.base;

import org.n3r.eql.EqlPage;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfRequest extends EqlPage {
  private String path;
  private Object result;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }
}