<#--TODO: va devenir index.ftl -->
<#include "header.ftl">
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/"><i class="fas fa-xs">cheroliv.com</i></a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>${config.feed_file}"><i class="fa fa-rss fa-lg"></i></a></li>
                <li><a href="https://github.com/cheroliv"><i class="fab fa-github fa-lg"></i></a></li>
                <li><a href="https://www.linkedin.com/in/cheroliv-com/"><i class="fab fa-linkedin fa-lg"></i></a></li>
            </ul>

        </div><!--/.nav-collapse -->
    </div>
</div>
<div class="container">
    <div class="page-header"/>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-6 col-md-2">Telecom</div>
            <div class="col-xs-6 col-md-2">Software</div>
            <div class="col-xs-6 col-md-2">Career</div>
        </div>
    </div>
    <#include "footer.ftl">
