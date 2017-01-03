<#include "../common/header.ftl">
<#include "../common/body.ftl">

<@header title="ZConf">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
</@header>
<@body>
<div class="row" ng-app="zconfConfigure" ng-controller="zconfConfigureController">
<div class="col-xs-12">
  <div class="box">
    <div class="box-header with-border">
      <h3 class="box-title">ZConf配置</h3>
      <div class="box-tools">

        <div class="input-group input-group-sm" style="width: 150px;">
          <input type="text" name="table_search" class="form-control pull-right" placeholder="Search">

          <div class="input-group-btn">
            <button type="button" class="btn btn-default"><i class="fa fa-search"></i></button>
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
          <td><a class="btn btn-primary btn-xs" ng-click="edit(zconf.rootPath)">编辑</a></td>
        </tr>
      </tbody></table>
    </div>
    <div class="box-footer clearfix">
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
  var zconfManager = angular.module("zconfConfigure", ["ngAnimate", "ui.bootstrap"]);
  zconfManager.controller("zconfConfigureController", function ($scope, $http, $uibModal) {
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
      $http.post("${simple.get("js.base.path")}/zconf/index/findPage",
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

    $scope.edit = function (rootPath) {
      window.location.href = "${simple.get("js.base.path")}/zconf/detail?rootPath=" +
          rootPath;
    }
  });
</script>
</@js>