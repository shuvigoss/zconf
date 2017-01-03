package com.github.shuvigoss.zconf.web.controller;

import com.github.shuvigoss.zconf.web.components.eql.UserEqler;
import com.github.shuvigoss.zconf.web.components.eql.ZConfEqler;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.components.eql.entity.Zconf;
import com.github.shuvigoss.zconf.web.controller.base.BaseController;
import com.github.shuvigoss.zconf.web.controller.base.Result;
import com.github.shuvigoss.zconf.web.controller.base.ZConfRequest;
import com.google.common.hash.Hashing;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.hibernate.validator.constraints.NotEmpty;
import org.n3r.eql.Eql;
import org.n3r.eql.EqlTran;
import org.n3r.eql.util.Closes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.github.shuvigoss.zconf.web.utils.Constants.USER;
import static com.github.shuvigoss.zconf.web.utils.PasswordRandom.generateShortUuid;
import static com.github.shuvigoss.zconf.web.utils.ZconfUtil.createACL;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Controller
@RequestMapping("/admin")
public class RootController extends BaseController {

  @Resource
  private ZConfEqler zConfEqler;

  @Resource
  private UserEqler userEqler;

  @Resource
  private CuratorFramework client;

  @Value("${zconf.zookeeper.root.digest}")
  private String digestRoot;
  @Value("${zconf.zookeeper.rootPath}")
  private String rootPath;

  @RequestMapping("/zconf")
  public String createZConfIndex() {
    return "zconf/admin-index";
  }

  @RequestMapping(value = "/zconf/find", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<ZConfRequest> findPage(@RequestBody final ZConfRequest request) {
    List<Zconf> result = zConfEqler.findPage(request, request.getPath());
    request.setResult(result);
    return success(request);
  }

  @RequestMapping(value = "/zconf/pwd", method = RequestMethod.GET)
  public
  @ResponseBody
  Result<String> generatePwd() {
    return success(generateShortUuid());
  }

  @RequestMapping(value = "/zconf/add", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<Boolean> createZConf(@RequestBody @Valid Zconf zconf) {
    Zconf z = zConfEqler.findOne(zconf.getRootPath());
    if (z != null) return fail(null, "rootPath 已经存在了！");
    zconf.setRootPath(rootPath + "/" + zconf.getRootPath().replaceAll("/", ""));
    zconf.setAdminAuth("admin:" + zconf.getAdminAuth());
    zconf.setReadAuth("zconf:" + zconf.getReadAuth());

    EqlTran tran = new Eql().newTran();
    try {
      tran.start();
      zConfEqler.insert(zconf, tran);

      client.create()
            .creatingParentsIfNeeded()
            .withACL(createACL(zconf.getAdminAuth(), zconf.getReadAuth()))
            .forPath(zconf.getRootPath());
      Stat stat = client.checkExists().forPath(zconf.getRootPath());
      if (stat != null) {
        tran.commit();
        return success(true);
      } else {
        tran.rollback();
        return fail(false, "create zconf error! ");
      }
    } catch (Exception e) {
      tran.rollback();
      return fail(false, "create zconf error!");
    } finally {
      Closes.closeQuietly(tran);
    }
  }

  @RequestMapping(value = "/zconf/findOne", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<Zconf> find(@RequestBody Map<String, String> body) {
    String rootPath = body.get("rootPath");
    List<User> users = zConfEqler.findUsers(rootPath);
    Zconf one = zConfEqler.findOne(rootPath);
    one.setUsers(users);
    return success(one);
  }

  @RequestMapping(value = "/zconf/addUser", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<Boolean> addUser(@RequestBody @Valid ZConfUser user) {
    Zconf zconf = zConfEqler.findOne(user.getRootPath());
    if (zconf == null) return fail(false, "no zconf find for " + user.getRootPath());
    User newUser = new User();
    newUser.setActive(true);
    newUser.setUsername(user.getUsername());
    String md5Pwd = Hashing.md5().hashString(user.getPassword(), UTF_8).toString();
    newUser.setPassword(md5Pwd);

    EqlTran tran = new Eql().newTran();
    try {
      tran.start();
      zConfEqler.insertUser(zconf.getRootPath(), newUser.getUsername(), tran);
      userEqler.insert(newUser, tran);
      userEqler.insertRole(newUser.getUsername(), USER, tran);
      tran.commit();
    } catch (Exception e) {
      return fail(false, "插入数据异常");
    } finally {
      Closes.closeQuietly(tran);
    }
    return success(true);
  }

  static class ZConfUser {
    @NotEmpty
    private String rootPath;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    public String getRootPath() {
      return rootPath;
    }

    public void setRootPath(String rootPath) {
      this.rootPath = rootPath;
    }

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
  }
}
