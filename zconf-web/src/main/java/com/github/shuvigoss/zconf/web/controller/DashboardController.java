package com.github.shuvigoss.zconf.web.controller;

import com.github.shuvigoss.zconf.web.controller.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Controller
public class DashboardController extends BaseController {
  @RequestMapping("/dashboard")
  public String dashboard() {
    return "index";
  }
}
