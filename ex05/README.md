## Exercise 5: SpringBoot and GraalVM Native Image

This exercise will focus on GraalVM and Spring Boot.

### Exercise 5.1: Clone the sample SpringBoot Application

Clone the Spring Boot sample application that uses GraalVM Native Image. Do note that this is developed by Spring framework team and is still in an experimental phase.

![user input](../images/userinput.png)

`$ git clone https://github.com/spring-projects-experimental/spring-graal-native.git`

### Exercise 5.2: Compile and run the application using GraalVM Native Image

In order to proceed with compiling and building this application, you need to have Apache Maven version 3.x installed in your machine.
If you type:

![user input](../images/userinput.png)

`$ mvn --version`

In my environment, it shows:

```
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /Users/sseighma/.sdkman/candidates/maven/current
Java version: 1.8.0_271, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/graalvm-ee-java8-20.3.0/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.7", arch: "x86_64", family: "mac"
```

Once Apache Maven is already installed, you can proceed with the following commands:

![user input](../images/userinput.png)

```$ cd spring-graal-native
$ ./build.sh
$ cd spring-graalvm-native-samples/commandlinerunner
$ ./build.sh
$ cd target
$ ./commandlinerunner
```

Notice the startup times comparing the traditional Spring fat-JAR vs GraalVM Native Image.

# Conclusions

Today, we have seen GraalVM in action, Microservices with GraalVM and also how a SpringBoot application works with GraalVM.