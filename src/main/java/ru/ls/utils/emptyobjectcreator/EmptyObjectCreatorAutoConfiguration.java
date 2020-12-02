package ru.ls.utils.emptyobjectcreator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс создает Rest Controller для получаения пустых объектов по типу объекта.
 * Автоконфиг срабатывает только если отрабатывает кондишн( существует пакет указанный в пропертях и мы включили проперти  ls.util.empty-object-controller.enable
 */
@Configuration
@ConditionalOnExpression("${ls.util.empty-object-controller.enable:true} &&  T(ru.ls.utils.emptyobjectcreator.EmptyObjectCreatorAutoConfiguration).isPackageExists('${ls.util.empty-object-controller.domain-package:}') ")
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@Slf4j
public class EmptyObjectCreatorAutoConfiguration {

    public static boolean isPackageExists(String s) {
        String packName = s.replace(".", "/");
        return Thread.currentThread().getContextClassLoader().getResource(packName) != null;
    }

    @RestController
    @RequestMapping("/getNewInstance")
    public static class EmptyObjectCreatorController {

        private final ApplicationContext context;

        @Value("${ls.util.empty-object-controller.domain-package}")
        public String packageName;

        @Autowired
        public EmptyObjectCreatorController(ApplicationContext context) {
            this.context = context;
        }


        @GetMapping
        public Object getNewInstance(@RequestParam(value = "className") String s) throws Exception {

            if (EmptyObjectCreatorService.emptyObjects.containsKey(s.toLowerCase())) {
                return EmptyObjectCreatorService.emptyObjects.get(s.toLowerCase());
            }


            ApplicationHome home = new ApplicationHome();
            String path;
            if (home.getSource() == null) {  // if run local
                Class<?> mainClass = findBootClass();

                if (mainClass == null) {
                    log.error("Main class not found! EmptyObjectCreatorController will not work.");
                    return new Object();
                }

                home = new ApplicationHome(mainClass);

                String name = Arrays.stream(Objects.requireNonNull(new File(home.getDir().getParent()).listFiles()))
                        .filter(file -> file.getName().endsWith(".jar"))
                        .collect(Collectors.toList()).get(0).getName();

                path = home.getDir().getParent() + "/" + name;

            } else {
                path = home.getSource().getAbsolutePath();
            }

            EmptyObjectCreatorService.run(path, packageName);

            return EmptyObjectCreatorService.emptyObjects.getOrDefault(s.toLowerCase(), "Required class not found!");
        }

        private Class<?> findBootClass() {
            Map<String, Object> annotatedBeans = context.getBeansWithAnnotation(SpringBootApplication.class);
            return annotatedBeans.isEmpty() ? null : annotatedBeans.values().toArray()[0].getClass();
        }

    }

}