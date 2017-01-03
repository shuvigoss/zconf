package com.github.shuvigoss;

import com.alibaba.fastjson.JSONObject;
import com.github.shuvigoss.zconf.ZConf;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class DemoTest {

  public static void main(String[] args) throws InterruptedException {
    final ZConf zConf = new ZConf();
    zConf.init();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        zConf.destory();
      }
    });
    for (; ; ) {
      System.out.println(zConf.get("test"));
      JSONObject object = zConf.getC("test1");
      System.out.println(object.getString("hello"));
      Thread.sleep(1000);
    }

  }
}
