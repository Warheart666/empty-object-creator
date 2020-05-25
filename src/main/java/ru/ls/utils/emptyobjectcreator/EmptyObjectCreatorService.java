package ru.ls.utils.emptyobjectcreator;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//@SpringBootApplication
@Slf4j
class EmptyObjectCreatorService {


//    @Value("${user.dir}")
//    public static String basedir;


    //    @Override
    static void run(String s, String packageName) throws Exception {

        getClassesFromJarFile(new File(s), packageName);
    }

//    public static void main(String[] args) {
//        SpringApplication.run(EmptyObjectCreatorService.class, args);
//    }
//
//    static Object getEmptyObjectInstance(String packageName, String className) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
//        Class[] classes = getClasses(packageName);
//        Class searchingClass = Arrays.stream(classes).filter(aClass -> aClass.getSimpleName().equalsIgnoreCase(className)).findAny().orElse(null);
//
//        if (searchingClass != null)
//            return searchingClass.newInstance();
//
//        return null;
//    }


//    private static Class[] getClasses(String packageName)
//            throws ClassNotFoundException, IOException {
//
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        assert classLoader != null;
//        String path = packageName.replace('.', '/');
//        Enumeration<URL> resources = classLoader.getResources(path);
//        List<File> dirs = new ArrayList<>();
//        while (resources.hasMoreElements()) {
//            URL resource = resources.nextElement();
//            dirs.add(new File(resource.getFile()));
//        }
//        ArrayList<Class> classes = new ArrayList<>();
//        for (File directory : dirs) {
//            classes.addAll(findClasses(directory, packageName));
//        }
//        return classes.toArray(new Class[0]);
//    }

//    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
//        List<Class> classes = new ArrayList<>();
//
//        Files.isDirectory(Paths.get(directory.toURI()).resolve(packageName));
//
//        if (!directory.exists()) {
//            return classes;
//        }
//        File[] files = directory.listFiles();
//        assert files != null;
//
//        for (File file : files) {
//            if (file.isDirectory()) {
//                assert !file.getName().contains(".");
//                classes.addAll(findClasses(file, packageName + "." + file.getName()));
//            } else if (file.getName().endsWith(".class")) {
//                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
//            }
//        }
//        return classes;
//    }


//    static List<Class> getAllKnownClasses() throws IOException {
//        List<Class> classFiles = new ArrayList<>();
//        List<File> classLocations = getClassLocationsForCurrentClasspath();
//        for (File file : classLocations) {
//            classFiles.addAll(getClassesFromPath(file));
//        }
//        return classFiles;
//    }

    public static List<File> getClassLocationsForCurrentClasspath() {
        List<File> urls = new ArrayList<>();
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {
            for (String path : javaClassPath.split(File.pathSeparator)) {
                urls.add(new File(path));
            }
        }
        return urls;
    }


//    private static Collection<? extends Class> getClassesFromPath(File path) throws IOException {
////        if (path.isDirectory()) {
////            return getClassesFromDirectory(path);
////        } else {
//        return getClassesFromJarFile(path);
////        }
//    }

    static Map<String, Object> emptyObjects = new HashMap<>();

    private static void getClassesFromJarFile(File path, String packageName) throws IOException {
        List<Class> classes = null;  //= new ArrayList<Class>();

        if (path.canRead()) {
            JarFile jar;

            try {
                jar = new JarFile(path);
            } catch (FileNotFoundException e) {
                log.error("Service Jar file not found for EmptyObjectCreator!");
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
