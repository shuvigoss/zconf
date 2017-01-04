<#include "../common/header.ftl">
<#include "../common/body.ftl">

<@header title="ZConf">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
</@header>
<@body>
<div class="row" ng-app="zconfDetail" ng-controller="zconfDetailController">
<script id="zconfAddConvert.html" type="text/ng-template">
<div class="modal-header">
    <h3 class="modal-title" id="modal-title">新增Convert</h3>
</div>
<div class="modal-body" id="modal-body">
  <form class="form-horizontal" name="addConvertForm">
    <div class="box-body">
      <div class="form-group">
        <label for="convert" class="col-sm-2 control-label">Value</label>

        <div class="col-sm-10">
          <input type="text" class="form-control" id="convert" ng-model="convert.value" placeholder="value" autocomplete="off" required>
        </div>
      </div>
      <div class="form-group">
        <label for="description" class="col-sm-2 control-label">Description</label>

        <div class="col-sm-10">
          <input type="text" class="form-control" id="description" ng-model="convert.description" placeholder="description" autocomplete="off" required>
        </div>
      </div>
    </div>
  </form>
</div>
<div class="modal-footer">
    <button class="btn btn-primary" type="button" ng-click="add()" ng-disabled="!addConvertForm.$valid">添加</button>
    <button class="btn btn-warning" type="button" ng-click="close()">取消</button>
</div>
</script>
<script id="zconfAddNew.html" type="text/ng-template">
  <div class="modal-header">
      <h3 class="modal-title" id="modal-title">{{title}}</h3>
  </div>
  <div class="modal-body" id="modal-body">

    <form class="form-horizontal" name="addZconfForm">
    <div class="box-body">
      <div class="form-group">
        <label for="key" class="col-sm-2 control-label">Key</label>

        <div class="col-sm-10">
          <input type="text" class="form-control" id="key" required ng-model="dataRequest.key" ng-disabled="key" placeholder="key" autocomplete="off">
        </div>
      </div>
      <div class="form-group">
        <label for="data" class="col-sm-2 control-label">Data</label>

        <div class="col-sm-10">
          <textarea class="form-control" id="data" rows="8" ng-model="dataRequest.data" required placeholder="Input Data"></textarea>
        </div>
      </div>
      <div class="form-group">
        <label for="key" class="col-sm-2 control-label">Convert</label>
        <div class="col-sm-8">
          <select class="form-control" ng-model="dataRequest.convertId" ng-change="convertChange()">
            <option value="">default</option>
            <option ng-repeat="item in convert" value="{{item.id}}" ng-selected="{{dataRequest.convertId == item.id}}">{{item.value}}</option>
          </select>
          <span class="help-block">{{description}}</span>
        </div>
        <div class="col-sm-2">
          <button type="button" class="btn btn-info btn-sm" ng-click="addNewConvert()">
            <i class="fa fa-plus"></i></button>
        </div>
      </div>

    </div>
  </form>
  </div>
  <div class="modal-footer">
      <button class=" btn btn-primary" type="button" ng-click="addZconf()" ng-disabled="!addZconfForm.$valid">确认</button>
      <button class=" btn btn-warning" type="button" ng-click="close()">取消</button>
  </div>
</script>
<div class="col-xs-12">
  <div class="box">
    <div class="box-header with-border">
      <h3 class="box-title"></h3>
      <div class="box-tools">

        <button type="button" class="btn btn-block btn-info pull-left" ng-click="addNew()">新增</button>
      </div>
    </div>
    <!-- /.box-header -->
    <div class="box-body table-responsive no-padding">
      <table class="table table-hover">
        <tbody><tr>
          <th>Key</th>
          <th>czxid</th>
          <th>mzxid</th>
          <th>mtime</th>
          <th>version</th>
          <th>cvt</th>
          <th>data</th>
          <th>edit</th>
        </tr>
        <tr ng-repeat="zconf in zConflist">
          <td>{{zconf.key}}</td>
          <td>{{zconf.value.header.czxid}}</td>
          <td>{{zconf.value.header.mzxid}}</td>
          <td>{{zconf.value.header.mtime}}</td>
          <td>{{zconf.value.header.version}}</td>
          <td>{{zconf.value.header.cvt}}</td>
          <td>{{zconf.value.data}}</td>
          <td>
            <a class="btn btn-primary btn-xs" ng-click="edit(zconf.key)">编辑</a>
            <a class="btn btn-warning btn-xs" ng-click="delete(zconf.key)">删除</a>
          </td>
        </tr>
      </tbody></table>
    </div>
  </div>
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
  var rootPath = "${rootPath}";
  var zconfManager = angular.module("zconfDetail", ["ngAnimate", "ui.bootstrap"]);
  zconfManager.controller("zconfDetailController", function ($scope, $http, $uibModal) {
    $scope.zConflist = [];
    $scope.load = function () {
      $http.get("${simple.get("js.base.path")}/zconf/detail/find?rootPath=" + rootPath,
          $scope.query).success(function (data) {
        $scope.zConflist = data.result;
      });
    };

    $scope.addNew = function () {
      modal({
        rootPath: rootPath
      });
    };

    $scope.edit = function (key) {
      modal({
        rootPath: rootPath,
        key: key
      });
    };

    function modal(obj) {
      var modalInstance = $uibModal.open({
        animation: true,
        templateUrl: 'zconfAddNew.html',
        controller: 'zconfAddNewController',
        backdrop: 'static',
        resolve: {
          param: function () {
            return obj;
          }
        }
      });
      modalInstance.result.then(function () {
        $scope.load();
      });
    }

    $scope.delete = function (key) {
      var r = confirm("确定删除？");
      if (r) {
        $http.get("${simple.get("js.base.path")}/zconf/detail/delete?rootPath=" +
            rootPath + "/" + key,
            $scope.query).success(function (data) {
          $scope.load();
        });
      }
    };

    $scope.load();
  });

  zconfManager.controller("zconfAddNewController", function ($scope, $http, $uibModal,
                                                             $uibModalInstance,
                                                             param) {
    var rootPath = param.rootPath;
    var key = param.key;
    $scope.key = key;
    $scope.title = "新增";
    $scope.addNewConvert = function () {
      var modalInstance = $uibModal.open({
        animation: true,
        templateUrl: 'zconfAddConvert.html',
        controller: 'zconfAddConvertController',
        backdrop: 'static',
        resolve: {
          rootPath: function () {
            return rootPath;
          }
        }
      });

      modalInstance.result.then(function () {
        $scope.loadConvert();
      });
    };

    $scope.addZconf = function () {
      var url;
      if (key) {
        url = "${simple.get("js.base.path")}/zconf/detail/update"
      } else {
        url = "${simple.get("js.base.path")}/zconf/detail/create"
      }
      $http.post(url,
          $scope.dataRequest).success(function (data) {
        $uibModalInstance.close();
      });

    };

    $scope.close = function () {
      $uibModalInstance.close();
    };

    $scope.description = "Basic String Convertor";
    $scope.convertChange = function () {
      for (var i = 0; i < $scope.convert.length; i++) {
        var temp = $scope.convert[i];
        if ($scope.dataRequest.convertId == temp.id) {
          $scope.description = temp.description;
          return;
        }
        $scope.description = "Basic String Convertor";
      }
    };

    $scope.dataRequest = {
      convertId: "",
      rootPath: rootPath
    };

    if (key) {
      $http.get("${simple.get("js.base.path")}/zconf/detail/findOne?keyPath=" +
          rootPath + "/" + key,
          $scope.query).success(function (data) {
        $scope.dataRequest = data.result;
      });
      $scope.title = "变更"
    }

    $scope.convert = [];

    $scope.loadConvert = function () {
      $http.post("${simple.get("js.base.path")}/zconf/detail/convertFind",
          {rootPath: rootPath}).success(function (data) {
        $scope.convert = data.result;
        $scope.convertChange();
      });
    };
    $scope.loadConvert();
  });

  zconfManager.controller("zconfAddConvertController", function ($scope, $http,
                                                                 $uibModalInstance, rootPath) {
    $scope.convert = {
      rootPath: rootPath,
      data: "",
      description: ""
    };
    $scope.add = function () {
      $http.post("${simple.get("js.base.path")}/zconf/detail/convertCreate",
          $scope.convert).success(function (data) {
        if (data.status == false) {
          $scope.errorMsg = data.message;
          return;
        }
        $uibModalInstance.close();
      });
    };

    $scope.close = function () {
      $uibModalInstance.close();
    };

  });
</script>
</@js>