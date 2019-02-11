<!DOCTYPE html>
<html lang="zh-CN">
<#assign $header='<#include "../header.html">'/>
<#assign $footer='<#include "../footer.html">'/>
<#assign $str='$'/>
<head>
    <base href="${$str}{base}"/>
    <title>${comments!}</title>
    ${$header}
</head>
</head>
<body>
<div style="margin:auto">
    <div class="panel-body">
        <form id="${webPagename!}Form">
            <input id="${webPagename!}Id" name="${(pk.columnName)!}" type="hidden" value="${$str}{(${(webPagename)!}.${(pk.columnName)!})!}">
        <#if columns??>
            <#list columns as column>
                <#if (column.attrname)?? && (pk.columnName)?? && (column.attrname) != (pk.columnName)
                              && (column.attrname)?index_of("Time") == -1>
                <div class="form-group">
                    <label for="${(column.attrname)!}" class="control-label">${(column.columnComment)!}：</label>
                    <input id="${(column.attrname)!}" name="${(column.attrname)!}" class="form-control" type="text"
                           placeholder="${(column.columnComment)!}"
                           value="${$str}{(${(webPagename)!}.${(column.attrname)!})!}">
                </div>
                </#if>
            </#list>
        </#if>
            <button type="submit" class="btn btn-primary">提交</button>
        </form>
    </div>
</div>
${$footer}
<script src="statics/js/${packageDir!}/${webPagename!}Edit.js?t=${$str}{.now?long}"></script>
</body>
</html>