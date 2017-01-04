<#macro header title="">
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>${title}</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link href="${simple.get("js.res.path")}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${simple.get("js.res.path")}/dist/css/AdminLTE.min.css">
    <link rel="stylesheet" href="${simple.get("js.res.path")}/dist/css/skins/_all-skins.min.css">
    <#nested>
  <script>
    var G = {
      basePath: "${simple.get('js.base.path')}"
    }
  </script>
</head>
</#macro>