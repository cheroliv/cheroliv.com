#!/usr/bin/env groovy
/**
 * get a crossplateform separator
 * @return
 */
static String getSeparator() {
    System.getProperty("file.separator")
}
/**
 * rename file
 * @param file
 * @param newName
 * @return the renamed file
 */
static File rename(File file, String newName) {
    assert file.renameTo(file
            .parentFile.path +
            getSeparator() +
            newName)
    new File(file
            .parentFile.path +
            getSeparator() +
            newName)
}

String rootFolderPath = System.getProperty("user.home") +
        "/Bureau/ng-courses/Udemy - Angular 7 " +
        "(formerly Angular 2) - The Complete Guide"

File rootFolder = new File(rootFolderPath)
assert rootFolder.exists()
assert rootFolder.directory
assert rootFolder.canRead()

List<File> dirList = new ArrayList<>()

//Adding rootFolder in dirList
// just in case there is some files in it
dirList.add(rootFolder)

//looping to get list of directories
//recursively inside rootFolder
rootFolder.traverse { File file ->
    !file.directory ?: dirList.add(file)
}

//looping through dirList
//to rename directories filling criterias
for (int i = dirList.size() - 1; i >= 0; i--) {
    Character[] chars = dirList.get(i).name.chars
    !(chars[0].digit && !chars[1].digit) ?:
            dirList.set(i, rename(dirList.get(i),
                    "0${dirList.get(i).name}"))
    if (dirList.get(i).directory) {
        //looping through directory
        //to rename file filling criterias
        dirList.get(i).listFiles().each { File f ->
            Character[] cs = f.name.chars
            !(cs[0].digit && !cs[1].digit) ?:
                    rename(f, "0${f.name}")
        }
    }
}
