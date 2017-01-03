package com.github.shuvigoss.zconf.web.controller.base;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class BaseController {

  protected <R> Result<R> success(R result, String message) {
    return Result.success(result, message);
  }

  protected <R> Result<R> success(R result) {
    return Result.success(result);
  }

  protected <R> Result<R> fail(R result, String message) {
    return Result.fail(result, message);
  }
}
