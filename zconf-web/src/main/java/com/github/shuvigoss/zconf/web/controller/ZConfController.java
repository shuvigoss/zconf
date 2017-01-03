package com.github.shuvigoss.zconf.web.controller;

import com.github.shuvigoss.zconf.base.KVPair;
import com.github.shuvigoss.zconf.base.ZConfParser;
import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.web.components.eql.UserEqler;
import com.github.shuvigoss.zconf.web.components.eql.ZConfEqler;
import com.github.shuvigoss.zconf.web.components.eql.entity.Role;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.components.eql.entity.Zconf;
import com.github.shuvigoss.zconf.web.components.eql.entity.ZconfConvert;
import com.github.shuvigoss.zconf.web.components.web.SecurityInterceptor;
import com.github.shuvigoss.zconf.web.controller.base.BaseController;
import com.github.shuvigoss.zconf.web.controller.base.Result;
import com.github.shuvigoss.zconf.web.controller.base.ZConfDataRequest;
import com.github.shuvigoss.zconf.web.controller.base.ZConfRequest;
import com.github.shuvigoss.zconf.web.utils.ZconfUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.github.shuvigoss.zconf.base.Const.*;
import static com.github.shuvigoss.zconf.web.utils.UserLoginUtil.isRoot;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Controller
@RequestMapping("/zconf")
public class ZConfController extends BaseController {

  @Resource
  private CuratorFramework client;

  @Resource
  private UserEqler userEqler;

  @Resource
  private ZConfEqler zConfEqler;

  @RequestMapping("/index")
  public String index() {
    return "zconf/zconf-index";
  }

  @RequestMapping(value = "/index/findPage", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<ZConfRequest> findPage(@RequestBody final ZConfRequest request) {
    User user = SecurityInterceptor.getUser();
    List<Role> userRoles = userEqler.findUserRoles(user.getUsername());
    boolean isAdmin = isRoot(userRoles);
    List<Zconf> result;
    if (isAdmin)
      result = zConfEqler.findPage(request, request.getPath());
    else
      result = zConfEqler.findPageByUser(request, request.getPath(), user.getUsername());
    request.setResult(result);
    return success(request);
  }

  @RequestMapping(value = "/detail", method = RequestMethod.GET)
  public String zconfDetail(String rootPath, Model model) {
    validateAccess(rootPath);
    model.addAttribute("rootPath", rootPath);
    return "zconf/zconf-detail";
  }

  @RequestMapping(value = "/detail/find", method = RequestMethod.GET)
  public
  @ResponseBody
  Result<List<KVPair<String, ZConfData>>> findAllKeys(String rootPath) {
    List<KVPair<String, ZConfData>> result = Lists.newArrayList();
    try {
      List<String> paths = client.getChildren().forPath(rootPath);
      for (String path : paths) {
        Stat stat = new Stat();
        byte[] bytes = client.getData()
                             .storingStatIn(stat)
                             .forPath(rootPath + "/" + path);
        byte[] header = ZConfParser.toByte(stat);
        KVPair<String, ZConfData> data = ZConfParser
            .parse(new KVPair<>(ZconfUtil.parse(path), ArrayUtils.addAll(header, bytes)));
        result.add(data);
      }
    } catch (Exception ignored) {
      return fail(result, "查询异常");
    }
    return success(result);
  }

  @RequestMapping(value = "/detail/findOne", method = RequestMethod.GET)
  public
  @ResponseBody
  Result<ZConfDataRequest> findOne(String keyPath) {
    try {
      byte[] bytes = client.getData().forPath(keyPath);
      int index = 0;
      for (int i = 0; i < bytes.length; i++) {
        byte b = bytes[i];
        if (b == CR && (i + 3) < bytes.length &&
            bytes[i + 1] == LF &&
            bytes[i + 2] == CR &&
            bytes[i + 3] == LF) {
          index = i; break;
        }
      }
      String convert = new String(ArrayUtils.subarray(bytes, 0, index));
      String data = new String(ArrayUtils.subarray(bytes, index + 4, bytes.length));
      String key = ZconfUtil.parse(keyPath);
      ZConfDataRequest request = new ZConfDataRequest();
      request.setRootPath(keyPath);
      if (Objects.equals(convert, "default")) {
        request.setConvertId("");
      } else {
        ZconfConvert zconvert = zConfEqler
            .getByValue(ZconfUtil.rootPath(keyPath), convert);
        request.setConvertId(String.valueOf(zconvert.getId()));
      }
      request.setData(data);
      request.setKey(key);
      return success(request);
    } catch (Exception e) {
      return fail(null, "未找到:" + keyPath);
    }
  }

  @RequestMapping(value = "/detail/create", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<Boolean> create(@Valid @RequestBody ZConfDataRequest reqeust) {
    validateAccess(reqeust.getRootPath());
    Zconf zconf = zConfEqler.findOne(reqeust.getRootPath());
    try {
      if (Strings.isNullOrEmpty(reqeust.getConvertId())) {
        add(zconf.getRootPath() + "/" + reqeust.getKey(), reqeust.getData(), "default");
      } else {
        ZconfConvert convert = zConfEqler.getById(Long.parseLong(reqeust.getConvertId()));
        if (convert == null) return fail(false, "没找到Convert :" + reqeust.getConvertId());
        add(
            zconf.getRootPath() + "/" + reqeust.getKey(), reqeust.getData(),
            convert.getValue()
        );
      }
    } catch (Exception e) {
      return fail(false, "创建节点异常");
    }
    return success(true);
  }

  @RequestMapping(value = "/detail/update", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<Boolean> update(@Valid @RequestBody ZConfDataRequest reqeust) {
    validateAccess(reqeust.getRootPath());
    try {
      if (Strings.isNullOrEmpty(reqeust.getConvertId())) {
        mod(reqeust.getRootPath(), reqeust.getData(),
            "default"
        );
      } else {
        ZconfConvert convert = zConfEqler.getById(Long.parseLong(reqeust.getConvertId()));
        if (convert == null) return fail(false, "没找到Convert :" + reqeust.getConvertId());
        mod(
            reqeust.getRootPath(), reqeust.getData(),
            convert.getValue()
        );
      }
    } catch (Exception e) {
      return fail(false, "更新节点失败");
    }
    return success(true);
  }

  private void mod(String key, String data, String convert) throws Exception {
    String finalData = convert.trim() + CRLF + CRLF + data.trim();
    client.setData().forPath(key, finalData.getBytes());
  }

  @RequestMapping(value = "/detail/convertCreate", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<Boolean> createConvert(@Valid @RequestBody ZconfConvert convert) {
    validateAccess(convert.getRootPath());
    zConfEqler.insertConvert(convert);
    return success(true);
  }

  @RequestMapping(value = "/detail/convertFind", method = RequestMethod.POST)
  public
  @ResponseBody
  Result<List<ZconfConvert>> getAllConvert(@RequestBody Map<String, String> request) {
    return success(zConfEqler.getConvertByPath(request.get("rootPath")));
  }

  private void add(String key, String data, String convert) throws Exception {
    String finalData = convert.trim() + CRLF + CRLF + data.trim();
    client.create().creatingParentsIfNeeded().forPath(key, finalData.getBytes());
  }

  private void validateAccess(String rootPath) {
    //TODO 权限校验
  }

}
