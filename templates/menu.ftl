<!-- Fixed navbar -->
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

                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>consulting.html">Consulting</a></li>
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>trainings.html" >Trainings</a></li>

                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown">Projects<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0033_ceelo_post.html">ceelo</a></li>
                        <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0034_jobdone_post.html">jobdone</a></li>
                        <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0032_managed-jbake_post.html">managed-jbake</a></li>
                    </ul>
                </li>

                <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown">Memos<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0052_memo_jvm_post.html">jvm</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0053_memo_kotlin_post.html">kotlin</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0031_memo_design_system_post.html">design system</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0047_memo_gradle_post.html">gradle</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2020/0021_memo_git_post.html">git</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2021/0030_memo_nix_post.html">linux</a></li>
                    </ul>
                </li>


<#--                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog.html">Blog</a></li>-->

                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>about.html">&Agrave; propos</a></li>
            </ul>


            <ul class="nav navbar-nav">
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>${config.feed_file}"><i class="fa fa-rss fa-lg"></i></a></li>
                <li><a href="https://github.com/cheroliv"><i class="fab fa-github fa-lg"></i></a></li>
                <li><a href="https://www.linkedin.com/in/cheroliv-com/"><i class="fab fa-linkedin fa-lg"></i></a></li>
            </ul>

        </div><!--/.nav-collapse -->
    </div>
</div>
<div class="container">