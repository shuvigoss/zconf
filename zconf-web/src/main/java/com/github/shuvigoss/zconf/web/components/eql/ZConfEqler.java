package com.github.shuvigoss.zconf.web.components.eql;

import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import com.github.shuvigoss.zconf.web.components.eql.entity.Zconf;
import com.github.shuvigoss.zconf.web.components.eql.entity.ZconfConvert;
import org.n3r.eql.EqlPage;
import org.n3r.eql.EqlTran;
import org.n3r.eql.eqler.annotations.EqlerConfig;
import org.n3r.eql.eqler.annotations.Param;
import org.n3r.eql.eqler.annotations.Sql;

import java.util.List;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@EqlerConfig
public interface ZConfEqler {

  String insert = "insert into zconf(root_path, admin_auth, read_auth, create_time) " +
      "values " +
      "(#rootPath#, #adminAuth#, #readAuth#, now())";

  String base = "select root_path, admin_auth, create_time, read_auth ";

  List<Zconf> findPage(EqlPage page, @Param("rootPath") String rootPath);

  List<Zconf> findPageByUser(EqlPage page,
                             @Param("rootPath") String rootPath,
                             @Param("username") String username);

  @Sql(base + "from zconf where root_path=##")
  Zconf findOne(String rootPath);

  @Sql(insert)
  int insert(Zconf zconf);

  @Sql(insert)
  int insert(Zconf zconf, EqlTran tran);

  @Sql("select username, active, create_time, update_time " +
           "from zconf_users z, user u " +
           "where z.users_username = u.username and z.zconf_root_path = ##")
  List<User> findUsers(String rootPath);

  @Sql("insert into zconf_users(zconf_root_path, users_username) " +
           "values " +
           "(#rootPath#, #username#)")
  void insertUser(@Param("rootPath") String rotPath, @Param("username") String
      username, EqlTran tran);

  @Sql("select root_path, admin_auth, create_time, read_auth " +
           "from zconf z, zconf_users zu " +
           "where z.root_path = zu.zconf_root_path and zu.users_username = ##")
  List<Zconf> findUserZconfs(String username);

  @Sql("select id, value, description, root_path from zconf_convert where root_path = ##")
  List<ZconfConvert> getConvertByPath(String rootPath);

  @Sql("select id, value, description, root_path from zconf_convert where id = ##")
  ZconfConvert getById(Long id);

  @Sql("select id, value, description, root_path " +
           "from zconf_convert where root_path = #rootPath# and value = #value#")
  ZconfConvert getByValue(@Param("rootPath") String rootPath,
                          @Param("value") String value);

  @Sql("insert into zconf_convert (value, description, root_path) values " +
           "(#value#, #description#, #rootPath#)")
  int insertConvert(ZconfConvert convert);

}
