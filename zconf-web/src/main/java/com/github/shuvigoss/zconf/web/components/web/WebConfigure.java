package com.github.shuvigoss.zconf.web.components.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Configuration
public class WebConfigure extends WebMvcConfigurerAdapter {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(securityInterceptor())
            .excludePathPatterns("/", "/login");
    super.addInterceptors(registry);
  }

  @Bean
  SecurityInterceptor securityInterceptor() {
    return new SecurityInterceptor();
  }
}
