# empty-object-creator
creates entrypoint and shows new object by requesting param 

1. if you use maven
  just add 
  <dependency>
      <groupId>ru.ls.utils</groupId>
      <artifactId>empty-object-creator</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>${basedir}/lib/empty-object-creator-1.0.jar</systemPath>
  </dependency>
  and put jar to lib folder.

2. add two props to project \n
ls.util.empty-object-controller.enable=true
ls.util.empty-object-controller.domain-package=ur.pack.with.classes 

3. start project and go to GET entry point  http://yourservcontext/getNewInstance?className=requestedclassname
