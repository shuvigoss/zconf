<#include "common/header.ftl">
<#include "common/body-js.ftl">

<@header title="登录">
<link rel="stylesheet" href="${simple.get("js.res.path")}/plugins/iCheck/square/blue.css">
</@header>
<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <b>ZConf</b>
    </div>
  <!-- /.login-logo -->
    <div class="login-box-body">
        <p class="login-box-msg">请登录</p>

        <form id="loginForm" method="post">
            <div class="form-group has-feedback">
                <input type="text" class="form-control" placeholder="Username" name="username">
                <span class="glyphicon glyphicon-user form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
                <input type="password" class="form-control" placeholder="Password" name="password">
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                <span class="help-block" id="msg" style="display: none;"></span>
            </div>
            <div class="row">
                <div class="col-xs-8">
                    <div class="checkbox icheck">
                        <label>
                            <input type="checkbox" value="true" id="remeber" name="remeberme"> 记住我
                        </label>
                    </div>
                </div>
              <!-- /.col -->
                <div class="col-xs-4">
                    <button type="button" id="login" class="btn btn-primary btn-block btn-flat">登录</button>
                </div>
              <!-- /.col -->
            </div>
        </form>
    </div>
  <!-- /.login-box-body -->
</div>
<!-- /.login-box -->
<@js>
<script src="${simple.get("js.res.path")}/plugins/iCheck/icheck.min.js"></script>
<script src="${simple.get("js.res.path")}/plugins/jQuery/jquery.cookie-1.4.1.min.js"></script>
<script>
    $(function () {
      $("#remeber").iCheck({
        checkboxClass: "icheckbox_square-blue",
        radioClass: "iradio_square-blue",
        increaseArea: "20%" // optional
      });

      var name = $.cookie("n");
      if (name) {
        $("input[name=username]").val(name);
        $("#remeber").iCheck("check");
      }

      function showErrorMessage(msg) {
        var msgDiv = $("#msg");
        $(".form-group").removeClass("has-feedback").addClass("has-error");
        msgDiv.show().text(msg);
      }

      $("#login").click(function () {
        $.ajax(G.basePath + "/login", {
          dataType: "json",
          method: "POST",
          data: JSON.stringify($("#loginForm").serializeObject()),
          contentType: "application/json ;charset=utf-8",
          success: function (data) {
            if (data.status) {
              window.location.href = G.basePath + "/dashboard";
            } else {
              showErrorMessage(data.message);
            }
          },
          error: function () {
            showErrorMessage("系统异常!");
          }
        })
      })

    });
</script>
</@js>