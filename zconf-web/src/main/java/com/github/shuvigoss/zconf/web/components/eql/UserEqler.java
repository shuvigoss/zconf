package com.github.shuvigoss.zconf.web.components.eql;

import com.github.shuvigoss.zconf.web.components.eql.entity.Role;
import com.github.shuvigoss.zconf.web.components.eql.entity.User;
import org.n3r.eql.EqlTran;
import org.n3r.eql.eqler.annotations.EqlerConfig;
import org.n3r.eql.eqler.annotations.Param;
import org.n3r.eql.eqler.annotations.Sql;

import java.util.List;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@EqlerConfig
public interface UserEqler {
  String base   = "select username, active, create_time, password, update_time ";
  String insert = "insert into user (username, active, create_time, password)" +
      " values " +
      "(#username#, #active#, now(), #password#)";

  @Sql(base + "from user where username=#username# and password=#pwd#")
  User login(@Param("username") String username, @Param("pwd") String pwdmd5);

  @Sql(insert)
  void insert(User user, EqlTran tran);

  @Sql(insert)
  void insert(User user);

  @Sql("insert into user_role(user_name, role_name) values (#username#, #rolename#)")
  void insertRole(@Param("username") String username, @Param("rolename") String
      rolename, EqlTran tran);

  @Sql("select role_name as \"name\" from user_role where user_name = ##")
  List<Role> findUserRoles(String username);
}
