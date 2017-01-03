package com.github.shuvigoss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.shuvigoss.zconf.base.Convertible;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class JSONConvert implements Convertible.ValueConvertor<JSONObject> {
  @Override
  public JSONObject convert(String value) {
    return JSON.parseObject(value);
  }
}
