import Build_gradle.Site.CNAME
import Build_gradle.Site.TASK_BAKE
import Build_gradle.Site.TASK_PUBLISH_SITE
import Build_gradle.Site.TASK_PUSH_PAGES
import Build_gradle.Site.origin
import Build_gradle.Site.remote
import Build_gradle.Site.separator
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

val localConf: ManagedBlogConf by lazy {
    ObjectMapper(YAMLFactory()).apply {
        disable(WRITE_DATES_AS_TIMESTAMPS)
        registerKotlinModule()
    }.readValue(
        File(
            "${project.rootDir}$separator${
                properties["managed_config_path"]
            }"
        ), ManagedBlogConf::class.java
    )
}
object Site {
    const val TASK_PUSH_PAGES = "pushPages"
    const val TASK_BAKE = "bake"
    const val TASK_PUBLISH_SITE = "publishSite"
    val separator: String by lazy { getProperty("file.separator") }
    val origin: String by lazy { "origin" }
    val remote: String by lazy { "remote" }
    const val USER_HOME_KEY = "user.home"
    const val CNAME = "CNAME"
}

data class ManagedBlogConf(
    val bake: BakeConf,
    val pushPage: GitPushConf,
    val pushSource: GitPushConf? = null,
    val pushTemplate: GitPushConf? = null,
) {
    companion object {

    }
}

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

fun createCnameFile(conf: ManagedBlogConf) {
    if (conf.bake.cname != "") file(
        "${project.buildDir.absolutePath}$separator${
            conf.bake.destDirPath
        }$separator$CNAME"
    ).run {
        if (exists() && isDirectory) assert(deleteRecursively())
        else if (exists()) assert(delete())
        assert(!exists())
        assert(createNewFile())
        @Suppress("USELESS_ELVIS")
        appendText(conf.bake.cname ?: "", UTF_8)
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
    assert(copyRecursively(repoDir, true))
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
        .setGitDir(File("${repoDir.absolutePath}$separator.git"))
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

tasks.register(TASK_PUBLISH_SITE) {
    group = "managed"
    description = "Publish site online."
    dependsOn(TASK_BAKE)
    finalizedBy(TASK_PUSH_PAGES)
    jbake {
        srcDirName = localConf.bake.srcPath
        destDirName = localConf.bake.destDirPath
    }
    doFirst { createCnameFile(localConf) }
}

tasks.register(TASK_PUSH_PAGES) {
    group = "managed"
    description = "Push pages to repository."
    doFirst {
        //1) créer un dossier cvs
        createRepoDir(
            "${project.buildDir.absolutePath}$separator${localConf.pushPage.to}"
        ).apply {
            //2) déplacer le contenu du dossier jbake dans le dossier cvs
            copyBakedFilesToRepo(
                "${project.buildDir.absolutePath}$separator${
                    localConf.bake.destDirPath
                }", this
            )
            //3) initialiser un repo dans le dossier cvs
            // 4 & 5) ajouter les fichiers du dossier cvs à l'index et commit
            initAddCommit(this, localConf)
            //6) push
            push(this, localConf)
            deleteRecursively()
        }
    }
    doLast {
        File(
            "${project.buildDir.absolutePath}$separator${
                localConf.bake.destDirPath
            }"
        ).deleteRecursively()
    }
}

tasks.register("displayParam") {
    group = "managed"
    println("site_push_repo_name : ${properties["site_push_repo_name"]}")
    println("site_push_repo_url : ${properties["site_push_repo_url"]}")
    println("site_push_repo_username : ${properties["site_push_repo_username"]}")
    println("site_push_repo_password : ${properties["site_push_repo_password"]}")
}
