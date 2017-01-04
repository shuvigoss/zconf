<#macro body>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <#include "body-header.ftl"/>
    <!-- Left side column. contains the logo and sidebar -->
    <#include "body-menu.ftl"/>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
        </section>

        <section class="content">
          <#nested >
        </section>
    </div>
    <!-- /.content-wrapper -->
    <footer class="main-footer">
        <div class="pull-right hidden-xs">
            <b>Version</b> 2.3.6
        </div>
        <strong>Copyright &copy; 2014-2016 <a href="http://almsaeedstudio.com">Almsaeed Studio</a>.</strong>
        All rights
        reserved.
    </footer>
</div>

</body>
</#macro>
<#include "body-js.ftl">