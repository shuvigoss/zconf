package com.github.shuvigoss.zconf.exception;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfStartupException extends RuntimeException {
  public ZConfStartupException() {
  }

  public ZConfStartupException(String message) {
    super(message);
  }

  public ZConfStartupException(String message, Throwable cause) {
    super(message, cause);
  }

  public ZConfStartupException(Throwable cause) {
    super(cause);
  }

  public ZConfStartupException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
