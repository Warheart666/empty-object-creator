package ru.ls.utils.emptyobjectcreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Класс создает Rest Controller для получаения пустых объектов по типу объекта.
 * Автокогнфиг срабатывает только если котрабатывает кондишн( существует пакет указанный в пропертях и мы включили проперти  ls.util.empty-object-controller.enable
 */
@Configuration
@ConditionalOnExpression("${ls.util.empty-object-controller.enable:true} &&  T(ru.ls.utils.emptyobjectcreator.EmptyObjectCreatorAutoConfiguration).isPackageExists('${ls.util.empty-object-controller.domain-package:}') ")
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class EmptyObjectCreatorAutoConfiguration {

    public static boolean isPackageExists(String s) {
        String packName = s.replace(".", "/");
        return Thread.currentThread().getContextClassLoader().getResource(packName) != null;
    }

    @RestController
    @RequestMapping("/getNewInstance")
    public static class EmptyObjectCreatorController {

        @Value("${ls.util.empty-object-controller.domain-package}")
        String domainPackage;

        @GetMapping
        public Object getNewInstance(@RequestParam(value = "className") String s) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
            return EmptyObjectCreatorService.getEmptyObjectInstance(domainPackage, s);
        }

    }

}