package com.github.shuvigoss.zconf.web.components.freemarker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.shuvigoss.zconf.web.components.eql.entity.Role;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.components.json.JsonResource;
import com.github.shuvigoss.zconf.web.components.web.SecurityInterceptor;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.github.shuvigoss.zconf.web.components.ApplicationContextHolder.getProperties;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Component("ftl.menus")
public class MenuTemplateMethodModelEx implements TemplateMethodModelEx {
  @Autowired
  private JsonResource jsonResource;

  @Autowired
  private HttpServletRequest request;

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    JSONArray menus = jsonResource.get("menus");
    StringBuilder sb = new StringBuilder(
        "<ul class=\"sidebar-menu\">\n<li class=\"header\">菜单</li>\n");
    User user = SecurityInterceptor.getUser();
    for (int i = 0; i < menus.size(); i++) {
      JSONObject menu = menus.getJSONObject(i);
      boolean auth = hasAccess(user, menu);
      if (!auth) continue;

      String urlPath = menu.getString("path");
      String icon = menu.getString("image");
      String menuName = menu.getString("name");
      if (isActive(menu)) {
        sb.append("<li class=\"active\">\n");
      } else {
        sb.append("<li>\n");
      }
      sb.append("<a href=\"")
        .append(getProperties("js.res.path"))
        .append(urlPath)
        .append("\">\n")
        .append("<i class=\"")
        .append(icon)
        .append("\"></i> <span>")
        .append(menuName)
        .append("</span>\n")
        .append("</a>\n</li>\n");
    }
    sb.append("</ul>");
    return sb.toString();
  }

  private boolean hasAccess(User user, JSONObject menu) {
    List<Role> roles = user.getRoles();
    JSONArray cRoles = menu.getJSONArray("roles");
    for (Role role : roles) {
      if (cRoles.contains(role.getName()))
        return true;
    }
    return false;
  }

  private boolean isActive(JSONObject menu) {
    String servletPath = request.getServletPath();
    String path = menu.getString("path");
    return servletPath.startsWith(path);
  }
}
