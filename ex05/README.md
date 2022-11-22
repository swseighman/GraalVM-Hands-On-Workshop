## Exercise 5: Spring Boot and GraalVM Native Image

![Spring Boot](../images/spring.png)

This exercise will focus on GraalVM and Spring Boot and was tested with Spring
Boot 2.4.2.

### Exercise 5.1: Clone the sample SpringBoot Application

Clone the Spring Boot sample application that uses GraalVM Native Image. Note
that this project is developed by the Spring framework team and is still in an
experimental phase. See
[this link](https://github.com/spring-projects-experimental/spring-native) for
additional information.

![User Input](../images/userinput.png)

```shell
$ git clone https://github.com/spring-projects-experimental/spring-native.git
```

### Exercise 5.2: Compile and run the application using GraalVM Native Image

In order to proceed with compiling and building this application, you need to
have Apache Maven version 3.x installed on your system.

If you type:

![User Input](../images/userinput.png)

```shell
$ mvn --version
```

Your output should be similar:

```
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /Users/sseighma/.sdkman/candidates/maven/current
Java version: 1.8.0_271, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/graalvm-ee-java8-20.3.0/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.7", arch: "x86_64", family: "mac"
```

Once Apache Maven is verified, you can proceed with the following commands:

![User Input](../images/userinput.png)

```shell
$ cd spring-native
$ ./build.sh
```

This can take some time.  Once ready, proceed with the following commands:

```shell
$ cd samples/commandlinerunner
$ ./build.sh
```

First, execute the `commandlinerunner` jar version:

![User Input](../images/userinput.png)

```shell
$ java -jar target/commandlinerunner-0.0.1-SNAPSHOT.jar
```

> TODO:  I am getting the following error.
>
> ```
> no main manifest attribute, in target/commandlinerunner-0.0.1-SNAPSHOT.jar
> ```

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.2)

Jan 22, 2021 12:01:30 AM org.springframework.boot.StartupInfoLogger logStarting
INFO: Starting CommandlinerunnerApplication v0.0.1-SNAPSHOT using Java 11.0.10 on sseighma-mac with PID 65352 (/Users/sseighma/code/graalvm/spring-native/spring-native-samples/commandlinerunner/target/commandlinerunner-0.0.1-SNAPSHOT.jar started by sseighma in /Users/sseighma/code/graalvm/spring-native/spring-native-samples/commandlinerunner)
Jan 22, 2021 12:01:30 AM org.springframework.boot.SpringApplication logStartupProfileInfo
INFO: No active profile set, falling back to default profiles: default
Jan 22, 2021 12:01:31 AM org.springframework.boot.StartupInfoLogger logStarted
INFO: Started CommandlinerunnerApplication in 0.674 seconds (JVM running for 1.02)
commandlinerunner running!
Jan 22, 2021 12:01:31 AM com.example.commandlinerunner.CLR run
INFO: INFO log message
Jan 22, 2021 12:01:31 AM com.example.commandlinerunner.CLR run
WARNING: WARNING log message
Jan 22, 2021 12:01:31 AM com.example.commandlinerunner.CLR run
SEVERE: ERROR log message
```

Next, run the native image version:

![User Input](../images/userinput.png)

```bash
$ ./target/commandlinerunner

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.2)

Jan 22, 2021 12:05:05 AM org.springframework.boot.StartupInfoLogger logStarting
INFO: Starting CommandlinerunnerApplication using Java 11.0.10 on sseighma-mac with PID 65409 (/Users/sseighma/code/graalvm/spring-native/spring-native-samples/commandlinerunner/target/commandlinerunner started by sseighma in /Users/sseighma/code/graalvm/spring-native/spring-native-samples/commandlinerunner)
Jan 22, 2021 12:05:05 AM org.springframework.boot.SpringApplication logStartupProfileInfo
INFO: No active profile set, falling back to default profiles: default
Jan 22, 2021 12:05:05 AM org.springframework.boot.StartupInfoLogger logStarted
INFO: Started CommandlinerunnerApplication in 0.103 seconds (JVM running for 0.125)
commandlinerunner running!
Jan 22, 2021 12:05:05 AM com.example.commandlinerunner.CLR run
INFO: INFO log message
Jan 22, 2021 12:05:05 AM com.example.commandlinerunner.CLR run
WARNING: WARNING log message
Jan 22, 2021 12:05:05 AM com.example.commandlinerunner.CLR run
SEVERE: ERROR log message
```

Notice the startup times comparing the traditional Spring fat-jar **(0.674 seconds**) vs GraalVM Native Image (**0.103 seconds**).

---

<a href="../review/"><img src="../images/noun_Next_511450_100.png"/></a>
