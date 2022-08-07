<#include "header.ftl">
<#include "menu.ftl">
<a href="${content.rootpath}${config.archive_file}">Archives</a>
<div class="page-header"/>
<div class="row">
    <#list posts as post>
        <div class="panel panel-default col-md-7">
            <div class="panel-body">
                <#if (post.status == "published")>
                    <a href="${post.uri}">
                        <h3><#escape x as x?xml>${post.title}</#escape></h3></a>
                    <p>${post.date?string("dd MMMM yyyy")}</p>
                    <#if post.summary??>
                        <div class="well">${post.summary}</div><#else></#if>
                </#if>
            </div>
        </div>
    </#list>
</div>
<hr/>
<p>Les anciens post sont disponible içi : <a href="${content.rootpath}${config.archive_file}">archive</a>.</p>
<#include "footer.ftl">