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

                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown">Projects<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="https://github.com/cheroliv/ceelo" target="_blank"><i class="fab fa-github"></i> game.ceelo</a></li>
                        <li><a href="https://github.com/cheroliv/kotlin-springboot-monolith" target="_blank"><i class="fab fa-github"></i> kotlin-springboot-monolith</a></li>
                        <li><a href="https://github.com/cheroliv/cheroliv.com" target="_blank"><i class="fab fa-github"></i> cheroliv.com</a></li>
                    </ul>
                </li>


                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown">Trainings<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                         <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0036_training_kotlin_playground_post.html"><i class="fa-brands fa-java"></i> kotlin playground</a></li>
                         <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0049_training_android-jetpack_post.html"><i class="fa-brands fa-android"></i> android-jetpack</a></li>
                         <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0043_training_jetpack-compose_post.html"><i class="fa-brands fa-android"></i> jetpack-compose</a></li>
<#--SpringBoot                          <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0044_training_khipster_post.html"><i class="fa-duotone fa-coffee-bean"></i> khipster-compose</a></li> -->
<#--mathematique                          <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0044_training_khipster_post.html">khipster-compose</a></li> -->
<#--algorithmique                          <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0044_training_khipster_post.html">khipster-compose</a></li> -->
<#--architecture                          <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0044_training_khipster_post.html">khipster-compose</a></li> -->
<#--devops                          <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0044_training_khipster_post.html">khipster-compose</a></li> -->
                    </ul>
                </li>


                <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown">Memos<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0052_memo_jvm_post.html">jvm</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0053_memo_kotlin_post.html">kotlin</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0047_memo_gradle_post.html">gradle</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2020/0021_memo_git_post.html">git</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2021/0030_memo_nix_post.html">linux</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2022/0031_memo_design_system_post.html">design system</a></li>
                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2020/0016_asciidoc_markdown_memo_post.html">asciidoc/markdown</a></li>
<#--                    <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>blog/2020/00XX_memo_agility_post.html">agility</a></li>-->
                </ul>
                </li>


                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>about.html">&Agrave; propos</a></li>
            </ul>


            <ul class="nav navbar-nav">
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>${config.feed_file}"><i class="fa fa-rss fa-lg"></i></a></li>
                <li><a href="https://github.com/cheroliv" target="_blank"><i class="fab fa-github fa-lg"></i></a></li>
                <li><a href="https://www.linkedin.com/in/cheroliv-com/" target="_blank"><i class="fab fa-linkedin fa-lg"></i></a></li>
            </ul>

        </div><!--/.nav-collapse -->
    </div>
</div>
<div class="container">