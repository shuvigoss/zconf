package com.github.shuvigoss.zconf.exception;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class IllegalCacheContentException extends RuntimeException {
  public IllegalCacheContentException() {
  }

  public IllegalCacheContentException(String message) {
    super(message);
  }

  public IllegalCacheContentException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalCacheContentException(Throwable cause) {
    super(cause);
  }

  public IllegalCacheContentException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
