import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Git.init
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.lang.System.getProperty
import java.nio.charset.StandardCharsets.UTF_8

plugins { id("org.jbake.site") }

data class ManagedBlogConf(
    val bake: BakeConf,
    val pushPage: GitPushConf,
    val pushSource: GitPushConf?,
    val pushTemplate: GitPushConf?,
)

data class RepoCredentials(
    val username: String,
    val password: String
)

data class RepoConf(
    val name: String,
    val repository: String,
    val credentials: RepoCredentials,
)

data class GitPushConf(
    val from: String,
    val to: String,
    val repo: RepoConf,
    val branch: String,
    val message: String,
)

data class BakeConf(
    val srcPath: String,
    val destDirPath: String,
    val cname: String,
)

data class site(
    val cnameFileName: String = "CNAME",
    val homePath: String = getProperty("user.home"),
    val separator: String = getProperty("file.separator"),
    val origin: String = "origin",
    val remote: String = "remote",
    val confPath: String = "$homePath$separator${properties["managed_config_path"]}",
    val configFile: File = File(confPath),
)



val cnameFileName: String by lazy { "CNAME" }
val homePath: String by lazy { getProperty("user.home") }
val separator: String by lazy { getProperty("file.separator") }
val origin: String by lazy { "origin" }
val remote: String by lazy { "remote" }
val confPath: String by lazy { "$homePath$separator${properties["managed_config_path"]}" }
val configFile by lazy { File(confPath) }
val conf: ManagedBlogConf by lazy {
    mapper().readValue(
        configFile,
        ManagedBlogConf::class.java
    )
}
val dummyConfig by lazy {
    ManagedBlogConf(
        bake = BakeConf(
            srcPath = ".",
            destDirPath = "jbake",
            cname = "www.acme.com"
        ),
        pushPage = GitPushConf(
            from = "build/jbake",
            to = ".",
            repo = RepoConf(
                name = "static-content.github.io",
                repository = "https://github.com/static-content/static-content.github.io.git",
                credentials = RepoCredentials(
                    username = "",
                    password = ""
                )
            ),
            branch = "master",
            message = "www.acme.com"
        ),
        pushSource = GitPushConf(
            from = "",
            to = "",
            repo = RepoConf(
                name = "",
                repository = "",
                credentials = RepoCredentials(
                    username = "jdoe@acme.com",
                    password = "pw"
                )
            ),
            branch = "master",
            message = ""
        ),
        pushTemplate = GitPushConf(
            from = "",
            to = "",
            repo = RepoConf(
                name = "",
                repository = "",
                credentials = RepoCredentials(
                    username = "",
                    password = ""
                )
            ),
            branch = "master",
            message = ""
        ),
    )
}

fun mapper() = ObjectMapper(YAMLFactory()).apply {
    disable(WRITE_DATES_AS_TIMESTAMPS)
    registerKotlinModule()
}

fun createCnameFile() {
    if (conf.bake.cname != "") file(
        "${project.buildDir.absolutePath}$separator${
            conf.bake.destDirPath
        }$separator$cnameFileName"
    ).run {
        if (exists() && isDirectory) assert(deleteRecursively())
        else if (exists()) assert(delete())
        assert(!exists())
        assert(createNewFile())
        @Suppress("USELESS_ELVIS")
        appendText(text = conf.bake.cname ?: "", UTF_8)
        assert(exists() && !isDirectory)
    }
}

fun createRepoDir(path: String)
        : File = File(path).apply {
    if (exists() && !isDirectory) assert(delete())
    if (exists()) assert(deleteRecursively())
    assert(!exists())
    if (!exists()) assert(mkdir())
}

fun copyBakedFilesToRepo(
    bakeDirPath: String,
    repoDir: File
): Unit = File(bakeDirPath).run {
    assert(
        copyRecursively(
            target = repoDir,
            overwrite = true
        )
    )
    assert(deleteRecursively())
}


fun initAddCommit(repoDir: File): RevCommit {
    //3) initialiser un repo dans le dossier cvs
    init()
        .setDirectory(repoDir)
        .call().apply {
            assert(!repository.isBare)
            assert(repository.directory.isDirectory)
            // add remote repo:
            remoteAdd().apply {
                setName(origin)
                setUri(URIish(conf.pushPage.repo.repository))
                // you can add more settings here if needed
            }.call()
            //4) ajouter les fichiers du dossier cvs à l'index
            add().addFilepattern(".").call()
            //5) commit
            return commit().setMessage(conf.pushPage.message).call()
        }
}

fun push(repoDir: File): MutableIterable<PushResult>? {
    Git(FileRepositoryBuilder()
        .setGitDir(File("${repoDir.absolutePath}${separator}.git"))
        .readEnvironment()
        .findGitDir()
        .setMustExist(true)
        .build()
        .apply {
            config.apply {
                getString(
                    remote,
                    origin,
                    conf.pushPage.repo.repository
                )
                save()
            }
            assert(isBare)
        }).run {
        // push to remote:
        return push().apply {
            setCredentialsProvider(
                UsernamePasswordCredentialsProvider(
                    conf.pushPage.repo.credentials.username,
                    conf.pushPage.repo.credentials.password
                )
            )
            //you can add more settings here if needed
            remote = origin
            isForce = true
        }.call()
    }
}

tasks.register("pushPages") {
    group = "managed"
    description = "Push pages to repository."
    val bakedPath = "${project.buildDir.absolutePath}$separator${conf.bake.destDirPath}"
    doFirst {
        //1) créer un dossier cvs
        createRepoDir(
            path = "${project.buildDir.absolutePath}$separator${conf.pushPage.to}"
        ).apply {
            //2) déplacer le contenu du dossier jbake dans le dossier cvs
            copyBakedFilesToRepo(
                bakeDirPath = bakedPath,
                repoDir = this
            )
            //3) initialiser un repo dans le dossier cvs
            // 4 & 5) ajouter les fichiers du dossier cvs à l'index et commit
            initAddCommit(repoDir = this)
            //6) push
            push(repoDir = this)
            deleteRecursively()
        }
    }
    doLast { File(bakedPath).deleteRecursively() }
}

tasks.register("publishSite") {
    group = "managed"
    description = "Publish site online."
    dependsOn("bake")
    finalizedBy("pushPages")
    jbake {
        srcDirName = conf.bake.srcPath
        destDirName = conf.bake.destDirPath
    }
    doFirst { createCnameFile() }
}

tasks.register("showBlogContextFile") {
    group = "managed"
    description = "Show blog context file."
    doLast { println(configFile.readText(UTF_8)) }
}
tasks.register("showBlogContextYaml") {
    group = "managed"
    description = "Show blog context in yaml format."
    doLast { println(mapper().writeValueAsString(conf)) }
}
tasks.register("showBlogContext") {
    group = "managed"
    description = "Show blog context toString()."
    doLast { println(conf) }
}

tasks.register("showHardCodedBlogContextObjectToYaml") {
    group = "managed"
    description = "Show blog context object in yaml format."
    doLast { println(mapper().writeValueAsString(dummyConfig)) }
}

tasks.register("initConf") {
    group = "managed"
    description =
        "create a new blog configuration if not exists and adapt it for your needs by editing the file $confPath"
    doFirst {
        println(
            "create a new blog configuration if not exists and adapt it for your needs by editing the file $confPath"
        )
        File(confPath).apply { if (!exists()) createConf() }
    }
}

fun createConf() = println("Not yet implemented")