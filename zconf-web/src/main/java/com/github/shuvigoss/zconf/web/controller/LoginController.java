package com.github.shuvigoss.zconf.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.github.shuvigoss.zconf.web.components.eql.UserEqler;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.controller.base.BaseController;
import com.github.shuvigoss.zconf.web.controller.base.Result;
import com.github.shuvigoss.zconf.web.utils.CookieUtil;
import com.github.shuvigoss.zconf.web.utils.UserLogin;
import com.github.shuvigoss.zconf.web.utils.UserLoginUtil;
import com.github.shuvigoss.zconf.web.utils.security.TripleDesCryptor;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

import static com.github.shuvigoss.zconf.web.utils.Constants.COOKIE_REMEBER;
import static com.github.shuvigoss.zconf.web.utils.Constants.COOKIE_USER;
import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Controller
@RequestMapping("/")
public class LoginController extends BaseController {

  @Resource
  private UserEqler userEqler;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ModelAndView index() {
    ModelAndView mv = new ModelAndView("login");

    UserLogin userLogin = UserLoginUtil.get();
    if (userLogin.getStatus() == UserLogin.Status.VALID)
      return new ModelAndView("redirect:/dashboard");

    return mv;
  }

  @RequestMapping(value = "signout", method = RequestMethod.GET)
  public ModelAndView signout() {
    UserLoginUtil.del();
    return new ModelAndView("redirect:/");
  }

  @RequestMapping(value = "login", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<String> login(
      @Valid @RequestBody LoginUser user,
      @CookieValue(value = COOKIE_REMEBER, required = false) String name
  ) {
    String pwdmd5 = Hashing.md5().hashString(user.getPassword(), UTF_8).toString();
    User member = userEqler.login(user.getUsername(), pwdmd5);
    if (member == null)
      return fail(EMPTY, "错误的用户名密码");
    if (!member.getActive())
      return fail(EMPTY, "账户异常,请联系管理员");

    doRemeber(user, name);
    writeCookie(member);
    return success(EMPTY, "success");
  }

  // cookie is "{userinfo json}|time"
  private void writeCookie(User member) {
    String userInfo = JSON.toJSONString(
        member,
        new SimplePropertyPreFilter(
            User.class, "username")
    ) + "|" + System.currentTimeMillis();

    TripleDesCryptor des3 = new TripleDesCryptor();
    String val = Base64Utils.encodeToString(
        des3.encrypt(userInfo).getBytes());
    CookieUtil.add(COOKIE_USER, val, CookieUtil.MAXAGE_DEFAULT, null);
  }

  private void doRemeber(LoginUser user, String n) {
    if (!user.isRemeberme() && !Strings.isNullOrEmpty(n)) {
      CookieUtil.del(COOKIE_REMEBER);
      return;
    }
    if (Strings.isNullOrEmpty(n) || !Objects.equals(n, user.getUsername()))
      CookieUtil.add(COOKIE_REMEBER, user.getUsername(), 365 * 24 * 60 * 60, null);

  }

  public static class LoginUser {
    @NotEmpty
    private String  username;
    @NotEmpty
    private String  password;
    private boolean remeberme;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public boolean isRemeberme() {
      return remeberme;
    }

    public void setRemeberme(boolean remeberme) {
      this.remeberme = remeberme;
    }
  }

}
