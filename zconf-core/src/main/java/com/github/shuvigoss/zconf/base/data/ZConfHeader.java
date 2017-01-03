package com.github.shuvigoss.zconf.base.data;

import java.io.Serializable;

import static com.github.shuvigoss.zconf.base.Const.CRLF;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 * @see org.apache.zookeeper.data.Stat
 */
public class ZConfHeader implements Serializable, Cloneable {
  private long   czxid;
  private long   mzxid;
  private long   ctime;
  private long   mtime;
  private int    version;
  private String cvt; //ValueConvertor

  public ZConfHeader() {
  }

  public ZConfHeader(long czxid, long mzxid, long ctime, long mtime, int version) {
    this.czxid = czxid;
    this.mzxid = mzxid;
    this.ctime = ctime;
    this.mtime = mtime;
    this.version = version;
  }

  public long getCzxid() {
    return czxid;
  }

  public void setCzxid(long czxid) {
    this.czxid = czxid;
  }

  public long getMzxid() {
    return mzxid;
  }

  public void setMzxid(long mzxid) {
    this.mzxid = mzxid;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public long getMtime() {
    return mtime;
  }

  public void setMtime(long mtime) {
    this.mtime = mtime;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getCvt() {
    return cvt;
  }

  public void setCvt(String cvt) {
    this.cvt = cvt;
  }

  @Override
  public String toString() {
    return
        czxid + CRLF +
            mzxid + CRLF +
            ctime + CRLF +
            mtime + CRLF +
            version + CRLF +
            cvt + CRLF;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
