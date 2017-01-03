package com.github.shuvigoss.zconf.web.controller.base;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class Result<T> {
  private boolean status;
  private T       result;
  private String  message;

  private Result() {
  }

  private Result(boolean status, T result, String message) {
    this.status = status;
    this.result = result;
    this.message = message;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public static <T> Result<T> success(T result, String message) {
    return new Result<T>(true, result, message);
  }

  public static <T> Result<T> success(T result) {
    return success(result, null);
  }

  public static <T> Result<T> fail(T result, String message) {
    return new Result<T>(false, result, message);
  }
}
