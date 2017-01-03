package com.github.shuvigoss.zconf.web;

import org.n3r.eql.eqler.spring.EqlerScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EqlerScan("com.github.shuvigoss.zconf.web.components.eql")
public class Main {

  public static void main(String[] args) {
    SpringApplication.run(Main.class);
  }

}
