package com.github.shuvigoss.zconf.base.data;

import static com.github.shuvigoss.zconf.base.Const.CRLF;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfData implements Cloneable {
  private ZConfHeader header;
  private String      data;

  public ZConfData(ZConfHeader header, String data) {
    this.header = header;
    this.data = data;
  }

  public ZConfHeader getHeader() {
    return header;
  }

  public String getData() {
    return data;
  }

  @Override
  public String toString() {
    return header.toString() + CRLF + data;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    ZConfData z = (ZConfData) super.clone();
    z.header = (ZConfHeader) header.clone();
    z.data = data;
    return z;
  }
}
