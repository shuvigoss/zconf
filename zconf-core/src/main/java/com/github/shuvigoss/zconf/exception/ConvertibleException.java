package com.github.shuvigoss.zconf.exception;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ConvertibleException extends RuntimeException {
  public ConvertibleException() {
  }

  public ConvertibleException(String message) {
    super(message);
  }

  public ConvertibleException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConvertibleException(Throwable cause) {
    super(cause);
  }

  public ConvertibleException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
