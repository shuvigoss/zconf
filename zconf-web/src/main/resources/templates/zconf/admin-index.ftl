<#include "../common/header.ftl">
<#include "../common/body.ftl">

<@header title="ZConf">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
</@header>
<@body>
<div class="row" ng-app="zconfManager" ng-controller="zconfManagerController">
<div class="col-xs-12">
  <div class="box">
    <div class="box-header with-border">
      <h3 class="box-title">ZConf管理</h3>
      <div class="box-tools">

        <div class="input-group input-group-sm" style="width: 150px;">
          <input type="text" name="table_search" class="form-control pull-right" placeholder="Search" ng-model="query.path">

          <div class="input-group-btn">
            <button type="button" class="btn btn-default"><i class="fa fa-search" ng-click="search()"></i></button>
          </div>
        </div>
      </div>
    </div>
    <!-- /.box-header -->
    <div class="box-body table-responsive no-padding">
      <table class="table table-hover">
        <tbody><tr>
          <th>路径(rootPath)</th>
          <th>ACL(ADMIN)</th>
          <th>ACL(READ)</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
        <tr ng-repeat="zconf in result">
          <td>{{zconf.rootPath}}</td>
          <td>{{zconf.adminAuth}}</td>
          <td>{{zconf.readAuth}}</td>
          <td>{{zconf.createTime}}</td>
          <td><a class="btn btn-primary btn-xs" ng-click="editUser(zconf.rootPath)">编辑用户</a></td>
        </tr>
      </tbody></table>
    </div>
    <div class="box-footer clearfix">
      <button type="button" class="btn btn-block btn-info pull-left" style="width: 80px;" ng-click="open()">新增</button>
      <ul class="pagination pagination-sm no-margin pull-right">
        <li><a href="#" ng-click="goto(query.currentPage - 1)">«</a></li>
        <li ng-class="{'active':$index + 1 == query.currentPage}" ng-repeat="i in getNumber() track by $index" ng-click="goto($index + 1)">
          <a href="#">{{$index + 1}}</a>
        </li>
        <li><a href="#" ng-click="goto(query.currentPage + 1)">»</a></li>
      </ul>
    </div>
    <!-- /.box-body -->
  </div>
  <!-- /.box -->
</div>

<script id="zconfAdd.html" type="text/ng-template">
  <div class="modal-header">
      <h3 class="modal-title" id="modal-title">新增</h3>
  </div>
  <div class="modal-body" id="modal-body">
    <div class="box box-primary">
        <form role="form" name="zconfForm">
          <div class="box-body">
            <div class="form-group">
              <label for="rootPath">rootPath</label>
              <input type="text" class="form-control" id="rootPath" ng-model="zconf.rootPath" placeholder="rootPath" required>
              <span class="help-block" ng-show="errorMsg">{{errorMsg}}</span>
            </div>
            <div class="form-group">
              <label for="rootPath">ACL(ADMIN)</label>
              <div class="input-group">
                <span class="input-group-addon">admin:</span>
                <input type="text" class="form-control" placeholder="adminPassword" ng-model="zconf.adminAuth" required>
                <span class="input-group-btn">
                      <button type="button" ng-click="adminGenerate()" class="btn btn-info btn-flat">自动生成</button>
                </span>
              </div>
            </div>
            <div class="form-group">
              <label for="rootPath">ACL(READ)</label>
              <div class="input-group">
                <span class="input-group-addon">zconf:</span>
                <input type="text" class="form-control" placeholder="zconfPassword" ng-model="zconf.readAuth" required>
                <span class="input-group-btn">
                    <button type="button" ng-click="zconfGenerate()" class="btn btn-info btn-flat">自动生成</button>
                </span>
              </div>
            </div>
          </div>
        </form>
    </div>
  </div>
  <div class="modal-footer">
      <button class="btn btn-primary" type="button" ng-click="ok()" ng-disabled="!zconfForm.$valid">确定</button>
      <button class=" btn btn-warning" type="button" ng-click="cancel()">取消</button>
  </div>
</script>

<script id="zconfUserEdit.html" type="text/ng-template">
  <div class="modal-header">
      <h3 class="modal-title" id="modal-title">编辑用户</h3>
  </div>
  <div class="modal-body" id="modal-body">
    <div class="box box-primary">
      <div class="box-header">
        <h3 class="box-title">用户列表</h3>
      </div>
      <!-- /.box-header -->
      <div class="box-body no-padding">
        <table class="table table-striped">
          <tbody>
          <tr>
            <th style="width: 10px">#</th>
            <th>用户名</th>
            <th>创建时间</th>
            <th>状态</th>
            <th style="width: 80px">操作</th>
          </tr>
          <tr ng-repeat="user in zconf.users">
            <td>{{$index + 1}}.</td>
            <td>
              {{user.username}}
            </td>
            <td>
              {{user.createTime}}
            </td>
            <td>
              {{user.active ? "生效" : "失效"}}
            </td>
            <td></td>
          </tr>
          </tbody>
        </table>
      </div>
      <!-- /.box-body -->
    </div>
    <form class="form-horizontal" name="addUserForm">
    <div class="box-body">
      <div class="form-group">
        <label for="username" class="col-sm-2 control-label">用户名</label>

        <div class="col-sm-10">
          <input type="text" class="form-control" id="username" ng-model="currentUser.username" placeholder="username" autocomplete="off">
        </div>
      </div>
      <div class="form-group">
        <label for="password" class="col-sm-2 control-label">密码</label>

        <div class="col-sm-10">
          <input type="text" class="form-control" id="password" ng-model="currentUser.password" placeholder="password" autocomplete="off">
        </div>
      </div>
      <div class="box-footer">
        <button type="button" class="btn btn-info pull-left" ng-click="add()" ng-disabled="!addUserForm.$valid">添加</button>
      </div>
    </div>
  </form>
  </div>
  <div class="modal-footer">
      <button class=" btn btn-warning" type="button" ng-click="ok()">完成</button>
  </div>
</script>

</div>
</@body>
<@js>
<script src="${simple.get("js.res.path")}/plugins/fastclick/fastclick.js"></script>
<script src="${simple.get("js.res.path")}/dist/js/app.min.js"></script>
<script src="${simple.get("js.res.path")}/dist/js/demo.js"></script>
<script src="http://cdn.bootcss.com/angular.js/1.5.8/angular.min.js"></script>
<script src="http://cdn.bootcss.com/angular.js/1.5.8/angular-animate.min.js"></script>
<script src="${simple.get("js.res.path")}/plugins/angular/ui-bootstrap-tpls-2.3.1.min.js"></script>
<script>
  var zconfManager = angular.module("zconfManager", ["ngAnimate", "ui.bootstrap"]);
  zconfManager.controller("zconfManagerController", function ($scope, $http, $uibModal) {
    $scope.query = {
      path: "",
      startIndex: 0,
      pageRows: 20,
      currentPage: 1,
      totalPages: 1,
      totalRows: 0
    };
    $scope.result = [];


    $scope.load = function () {
      $http.post("${simple.get("js.base.path")}/admin/zconf/find",
          $scope.query).success(function (data) {
        $scope.result = data.result.result;
        $scope.query.currentPage = data.result.currentPage;
        $scope.query.totalPages = data.result.totalPages;
        $scope.query.totalRows = data.result.totalRows;
      });
    };

    $scope.load();

    $scope.getNumber = function () {
      return new Array($scope.query.totalPages);
    };

    $scope.goto = function (index) {
      if (index == $scope.query.currentPage) return;
      if (index < 1 || index > $scope.query.totalPages) return;
      $scope.query.currentPage = index;
      $scope.load();
    };

    $scope.search = function () {
      $scope.load();
    };

    $scope.open = function (size) {
      var modalInstance = $uibModal.open({
        animation: true,
        templateUrl: 'zconfAdd.html',
        controller: 'zconfAddController',
        size: size,
        backdrop: 'static',
        resolve: {
          items: function () {
            return [];
          }
        }
      });

      modalInstance.result.then(function () {
        $scope.load();
      });
    };

    $scope.editUser = function (rootPath) {
      var modalInstance = $uibModal.open({
        animation: true,
        templateUrl: 'zconfUserEdit.html',
        controller: 'zconfUserEditController',
        backdrop: 'static',
        resolve: {
          rootPath: function () {
            return rootPath;
          }
        }
      });
    }

  });
  zconfManager.controller("zconfAddController", function ($scope, $http,
                                                          $uibModalInstance) {
    $scope.zconf = {};
    $scope.ok = function () {
      $http.post("${simple.get("js.base.path")}/admin/zconf/add",
          $scope.zconf).success(function (data) {
        if (data.status == false) {
          $scope.errorMsg = data.message;
          return;
        }
        $uibModalInstance.close();
      });
    };

    $scope.cancel = function () {
      $uibModalInstance.close();
    };

    $scope.adminGenerate = function () {
      $http.get("${simple.get("js.base.path")}/admin/zconf/pwd")
          .success(function (data) {
            $scope.zconf.adminAuth = data.result;
          })
    };
    $scope.zconfGenerate = function () {
      $http.get("${simple.get("js.base.path")}/admin/zconf/pwd")
          .success(function (data) {
            $scope.zconf.readAuth = data.result;
          })
    }

  });

  zconfManager.controller("zconfUserEditController", function ($scope, $http,
                                                               $uibModalInstance, rootPath) {
    $scope.zconf = [];
    $scope.loadUsers = function () {
      $http.post("${simple.get("js.base.path")}/admin/zconf/findOne", {
        rootPath: rootPath
      })
          .success(function (data) {
            console.log(data);
            $scope.zconf = data.result;
          })
    };
    $scope.loadUsers();

    $scope.currentUser = {rootPath: rootPath};

    $scope.add = function () {
      $http.post("${simple.get("js.base.path")}/admin/zconf/addUser", $scope.currentUser)
          .success(function (data) {
            console.log(data);
            if (data.status) {
              $scope.loadUsers();
              $scope.currentUser.username = null;
              $scope.currentUser.password = null;
            }
          })
    };

    $scope.ok = function () {
      $uibModalInstance.close();
    };

  });
</script>
</@js>