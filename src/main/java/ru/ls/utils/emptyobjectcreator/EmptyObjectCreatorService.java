package ru.ls.utils.emptyobjectcreator;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
class EmptyObjectCreatorService {

    static Map<String, Object> emptyObjects = new HashMap<>();

    static void run(String s, String packageName) throws Exception {
        getClassesFromJarFile(new File(s), packageName);
    }

    private static void getClassesFromJarFile(File path, String packageName) throws IOException {

        if (path.canRead()) {
            JarFile jar;

            try {
                jar = new JarFile(path);
            } catch (FileNotFoundException e) {
                log.error("EmptyObjectCreatorService: Jar file not found for EmptyObjectCreator!");
                return;
            }

            Enumeration<JarEntry> en = jar.entries();

            for (JarEntry jarEntry : Collections.list(en)) {
                if (jarEntry.getName().contains(packageName.replace(".", "/")) && jarEntry.getName().endsWith("class")) {
                    String className = fromFileToClassName(jarEntry.getName(), packageName);
                    try {
                        emptyObjects.put(className.substring(className.lastIndexOf(".") + 1).toLowerCase(), Class.forName(className).newInstance());
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static String fromFileToClassName(final String fileName, final String packageName) {
        String strReplaced = fileName.substring(0, fileName.length() - 6).replaceAll("[/\\\\]", "\\.");
        return strReplaced.substring(strReplaced.indexOf(packageName));   //("ru.ls.dev.projectservice.model"))
    }

}
