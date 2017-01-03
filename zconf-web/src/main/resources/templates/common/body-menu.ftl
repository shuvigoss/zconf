<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- Sidebar user panel -->
        <div class="user-panel">
            <div class="pull-left image">
                <img src="${simple.get("js.res.path")}/dist/img/user2-160x160.jpg"
                     class="img-circle" alt="User Image">
            </div>
            <div class="pull-left info">
                <p>Alexander Pierce</p>
                <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
            </div>
        </div>
        <ul class="sidebar-menu">
        <#--${menus()}-->
          <ul class="sidebar-menu">
            <li class="header">MAIN NAVIGATION</li>
            <li>
              <a href="${simple.get("js.res.path")}/admin/zconf">
                <i class="fa fa-circle-o text-red"></i> <span>ZConf管理</span>
              </a>
            </li>
            <li>
              <a href="${simple.get("js.res.path")}/zconf/index">
                <i class="fa fa-circle-o text-red"></i> <span>ZConf配置</span>
              </a>
            </li>
          </ul>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>