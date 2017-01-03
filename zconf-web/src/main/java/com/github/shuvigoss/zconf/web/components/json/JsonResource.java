package com.github.shuvigoss.zconf.web.components.json;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Component
public class JsonResource implements ResourceLoaderAware {
  private Logger log = LoggerFactory.getLogger(JsonResource.class);

  private static Map<String, Object> resources;
  private        ApplicationContext  resourceLoader;

  @PostConstruct
  public void init() throws IOException {
    String path = "classpath:json/*.json";
    log.info("start to load json resouces from {}", path);
    startLoad(resourceLoader.getResources(path));
  }

  private void startLoad(Resource[] jsons) throws IOException {
    resources = Maps.newHashMapWithExpectedSize(jsons.length);
    for (Resource resource : jsons) {
      String filename = resource.getFilename();
      log.info("load file {}", filename);
      String key = filename.substring(0, filename.lastIndexOf('.'));
      String content = Files.toString(resource.getFile(), UTF_8);
      resources.put(key, JSON.parse(content));
    }
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = (ApplicationContext) resourceLoader;
  }

  public <T> T get(String key) {
    //noinspection unchecked
    return (T) resources.get(key);
  }
}
