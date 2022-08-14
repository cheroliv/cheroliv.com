import Build_gradle.Tasks.TASK_BAKE
import Build_gradle.Tasks.TASK_PUSH_PAGES
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

data class Site(
    val cnameFileName: String = "CNAME",
    val homePath: String = getProperty("user.home"),
    val separator: String = getProperty("file.separator"),
    val origin: String = "origin",
    val remote: String = "remote",
    //create property fun confPath
//    val confPath: String = "${java.lang.System.getProperty("user.home")}${java.lang.System.getProperty("file.separator")}${properties["managed_config_path"]}",
    //create property fun configFile
//    val configFile: File = File(confPath),
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

fun mapper() = ObjectMapper(YAMLFactory()).apply {
    disable(WRITE_DATES_AS_TIMESTAMPS)
    registerKotlinModule()
}

fun createCnameFile(conf: ManagedBlogConf) {
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


fun initAddCommit(
    repoDir: File,
    conf: ManagedBlogConf,
): RevCommit {
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

fun push(
    repoDir: File,
    conf: ManagedBlogConf,
): MutableIterable<PushResult>? {
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

object Tasks {
    const val TASK_PUSH_PAGES = "pushPages"
    const val TASK_BAKE = "bake"
}
tasks.register(TASK_PUSH_PAGES) {
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
            initAddCommit(repoDir = this, conf)
            //6) push
            push(repoDir = this, conf)
            deleteRecursively()
        }
    }
    doLast { File(bakedPath).deleteRecursively() }
}
abstract class CustomTask @Inject constructor(
    private val message: String,
    private val number: Int
) : DefaultTask()

tasks.register<CustomTask>("myTask", "hello", 42)


abstract class PushPagesTask @Inject constructor(
    private val conf: ManagedBlogConf,
) : DefaultTask()

//TODO: article sur https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:passing_arguments_to_a_task_constructor
//tasks.register("pushPages", conf)
//tasks.register<PushPagesTask>("pushPagesGitHubActions")
//{
//    group = "managed"
//    description = "Push pages to repository."
//    val bakedPath = "${project.buildDir.absolutePath}$separator${conf.bake.destDirPath}"
//    doFirst {
//        //1) créer un dossier cvs
//        createRepoDir(
//            path = "${project.buildDir.absolutePath}$separator${conf.pushPage.to}"
//        ).apply {
//            //2) déplacer le contenu du dossier jbake dans le dossier cvs
//            copyBakedFilesToRepo(
//                bakeDirPath = bakedPath,
//                repoDir = this
//            )
//            //3) initialiser un repo dans le dossier cvs
//            // 4 & 5) ajouter les fichiers du dossier cvs à l'index et commit
//            initAddCommit(repoDir = this, conf)
//            //6) push
//            push(repoDir = this, conf)
//            deleteRecursively()
//        }
//    }
//    doLast { File(bakedPath).deleteRecursively() }
//}


tasks.register("publishSite") {
    group = "managed"
    description = "Publish site online."
    dependsOn(TASK_BAKE )
    finalizedBy(TASK_PUSH_PAGES)
    jbake {
        srcDirName = conf.bake.srcPath
        destDirName = conf.bake.destDirPath
    }
    doFirst { createCnameFile(conf) }
}

//tasks.register("publishSiteGitHubActions") {
//    group = "managed"
//    description = "Publish site online with github actions."
//    dependsOn(TASK_BAKE )
//    finalizedBy(TASK_PUSH_PAGES)
//    //TODO:create conf here
//    jbake {
//        srcDirName = conf.bake.srcPath
//        destDirName = conf.bake.destDirPath
//    }
//    doFirst { createCnameFile(conf) }
//}