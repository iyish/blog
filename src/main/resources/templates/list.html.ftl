<!DOCTYPE html>
<html lang="zh-CN">
<#assign $header='<#include "../header.html">'/>
<#assign $footer='<#include "../footer.html">'/>
<#assign $list='<#if shiro.hasPermission("'+permPrefix+':list")>'/>
<#assign $add='<#if shiro.hasPermission("'+permPrefix+':add")>'/>
<#assign $edit='<#if shiro.hasPermission("'+permPrefix+':edit")>'/>
<#assign $delete='<#if shiro.hasPermission("'+permPrefix+':delete")>'/>
<#assign $end='</#if>'/>
<#assign $str='$'/>
<head>
    <base href="${$str}{base}"/>
    <title>${comments!}</title>
    ${$header}
</head>
<body>
<div class="row" style="margin:auto">
    <div class="panel-body">
        <!-- 过滤条件 -->
        <div class="panel panel-default">
            <div class="panel-heading">查询条件</div>
            <div class="panel-body">
                <form id="${webPagename!}Frm" class="form-horizontal">
                    <div class="form-group">
                        <div class="col-md-3">
                            <input style="display:none"/>
                            <input type="text" class="form-control" id="${(pk.columnName)!}Id" name="${(pk.attrname)!}"
                                   placeholder="${(pk.columnComment)!}"
                                   onkeydown="if(event.keyCode===13)query();">
                        </div>
                        <div class="btn-group">
                        ${$list}
                            <button type="button" class="btn btn-primary" id="btnQuery" onclick="query()">
                                <span class="glyphicon glyphicon-search" aria-hidden="true"></span>&nbsp;查询
                            </button>
                        ${$end}
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <!-- bootstrap-table工具栏 -->
        <div id="toolbar" class="btn-group">
        ${$add}
            <button id="btnAdd" type="button" class="btn btn-success">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>&nbsp;新增
            </button>
        ${$end}
        ${$edit}
            <button id="btnEdit" type="button" class="btn btn-info">
                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;修改
            </button>
        ${$end}
        ${$delete}
            <button id="btnDel" type="button" class="btn btn-danger">
                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;删除
            </button>
        ${$end}
        </div>
        <table id="bsTable"></table>
    </div>
</div>
${$footer}
<script src="statics/js/${packageDir!}/${webPagename!}.js?t=${$str}{.now?long}"></script>
</body>
</html>