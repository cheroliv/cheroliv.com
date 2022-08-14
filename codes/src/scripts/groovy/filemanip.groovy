#!/usr/bin/env groovy
//Mes criteres de selection des fichiers à detruire
String strCriteria = "Dummies Dummie ajax Objective-C Windows blackberry " +
        "wordpress joomla ios c# f# rails ruby flex asp flash Dreamweaver adobe " +
        "CoffeeScript c++ .chm .epub .rar .zip .tar drupal drupals .net php javascript " +
        ".js iphone ipad js _js facebook gwt vaadin linq python py zope window " +
        "3D scala gaming active_directory tomcat jboss glassfish microsoft 2d " +
        "lego blogger blog cocoa dojo dom dotnetnuke eclipse .gz unity sharepoint " +
        ".tgz iwork filemaker .doc .txt"


//repertoire personnel
String userDir = System.getProperty("user.home")
//un separateur de chemin crossplatform
String sep = System.getProperty("file.separator")
//la variable symbolique
String sym_books_vrac_path = "books_vrac_path"

//le chemin de mon fichier gradle.properties hors projet
String propertiesFilePath = userDir
        .concat("${sep}.gradle${sep}gradle.properties")


File propertiesFile = new File(propertiesFilePath)


//je m'assure que le fichier existe et que ce n'est pas un dossier
assert propertiesFile.exists() && !propertiesFile.directory

Properties gradleProperties = new Properties()

//on boucle avec un inpustream sur le fichier
//gradle.properties pour peupler ma variable gradleProperties
propertiesFile.withInputStream { InputStream it ->
    gradleProperties.load(it)
}

//le chemin du dossier books_vrac est la valeur de notre clé books_vrac_path
String pathVracDir = gradleProperties.get(sym_books_vrac_path)

//j'ai recuperé tous ces livres en tapant cette commande dans le terminal
//wget -m -np -c -R "index.html*" "http://the-eye.eu/public/Books/IT%20Various/"

//on ouvre le dossier dont le chemin est dans la variable pathVracDir
// on s'assure qu'il existe et que c'est un dossier
File vracDir = new File(pathVracDir)
assert vracDir.exists() && vracDir.directory


//separer les elements de ma liste de critere
StringTokenizer tokenizer = new StringTokenizer(strCriteria)

//la liste de critere
List<String> criteriaList = new ArrayList<>()

//j'itère sur les tokens et je construit ma liste de token,
// mot clef critere de recherche
tokenizer.each { String it ->
    criteriaList.add(it.toLowerCase())
}

//je collect tous les noms de fichier dans le dossier
List<String> fileNameList = vracDir
        .listFiles()
        .collect { File it ->
            it.directory ?: it.name
        }

// je collect les noms de fichier qui contiendraient un des criteres
List<String> resultList = fileNameList.findAll { String name ->
    criteriaList.findAll { String crit ->
        name.toLowerCase().contains(crit)
    }
}

// je parcours ma liste de résultat pour les effacers
resultList.each { String fileName ->
    assert new File(vracDir.path + sep + fileName).delete()
}

println fileNameList.size()
println resultList.size()
resultList.empty ?: println(vracDir.path + sep + resultList.first())
