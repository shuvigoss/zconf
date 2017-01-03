package com.github.shuvigoss.zconf.web.components.freemarker;

import com.google.common.collect.Maps;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;
import java.util.Objects;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Configuration
public class FreemarkerConfigureExt extends
    FreeMarkerAutoConfiguration.FreeMarkerWebConfiguration {
  private Logger log = LoggerFactory.getLogger(FreemarkerConfigureExt.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public FreeMarkerConfigurer freeMarkerConfigurer() {
    FreeMarkerConfigurer configurer = super.freeMarkerConfigurer();

    Map<String, Object> variables = Maps.newHashMap();
    registMethodModel(variables);
    registFunctions(variables);
    configurer.setFreemarkerVariables(variables);
    return configurer;
  }

  private void registFunctions(Map<String, Object> variables) {
    BeansWrapper wrapper = new BeansWrapper(freemarker.template.Configuration
                                                .VERSION_2_3_25);
    wrapper.setExposureLevel(BeansWrapper.EXPOSE_ALL);
    variables.put("enums", wrapper.getEnumModels());
    TemplateHashModel staticModels = wrapper.getStaticModels();
    variables.put("statics", staticModels);

    Map<String, Object> ftlFunctions = applicationContext
        .getBeansWithAnnotation(FtlFunction.class);
    for (Object instance : ftlFunctions.values()) {
      String prefix =
          Objects.requireNonNull(
              instance.getClass().getAnnotation(FtlFunction.class).prefix());
      Class<?> clazz = instance.getClass();
      try {
        variables.put(prefix, staticModels.get(clazz.getName()));
      } catch (TemplateModelException e) {
        // do nothing
        log.error("can not add function :" + clazz.getName(), e);
      }
    }
  }

  private void registMethodModel(Map<String, Object> variables) {
    Map<String, TemplateMethodModelEx> beansOfType =
        applicationContext.getBeansOfType(TemplateMethodModelEx.class);
    for (String key : beansOfType.keySet()) {
      if (key.startsWith("ftl.")) {
        variables.put(key.substring(4), beansOfType.get(key));
      }
    }
  }
}
