## Exercise 4: Microservices

![](../images/micronaut_mini_copy_tm-50.png)


In this exercise, we'll learn how to create a Hello World Micronaut GraalVM application. To get started, clone the git repository:
![user input](../images/userinput.png)
```bash$ git clone https://github.com/swseighman/micronaut-graalvm-helloworld.git```Change directory to `micronaut-graalvm-helloworld`:![user input](../images/userinput.png)
```bash$ cd micronaut-graalvm-helloworld```![user input](../images/userinput.png)
```bash$ ./mvnw package```You can run either the JAR or native-image version:**JAR:**![user input](../images/userinput.png)
```bash$ java -jar target/micronaut-graalvm-helloworld-0.1.jar16:11:07.159 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 630ms. Server Running: http://:8080```In a separate terminal, send a request to the service:![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"Java Rules!"}```**native-image:**![user input](../images/userinput.png)
```bash$ target/micronaut-graalvm-helloworld16:13:34.873 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 46ms. Server Running: http://:8080```In a separate terminal, send a request to the service:![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"GraalVM Rocks!"}```### Deploying a JAR inside a containerWith this approach you only need the fat jar.Build a container image, make certain the docker daemon service is running (or use `podman`).![user input](../images/userinput.png)
```bash$ ./docker-build-jvm.sh```Start the container:![user input](../images/userinput.png)
```bash$ docker run -p 8080:8080 helloworld-graal-jvm19:58:42.934 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 642ms. Server Running: http://9c1ab24b58df:8080```Notice the container started in **642ms**.  Bet we can do better with the native image!
![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"GraalVM Rocks!"}```Quit the docker container by issuing a `CTRL-C` in the original terminal window.
### Deploying a native image inside a containerWith this approach you only need to build the fat jar and then use Docker/Podman to build the native image.Then build a container image, make certain the docker daemon service is running (or use `podman`).![user input](../images/userinput.png)
```bash$ ./docker-build.sh```Start the container:![user input](../images/userinput.png)
```bash$ docker run -p 8080:8080 helloworld-graal/app/micronaut-graalvm-helloworld: /usr/lib/libstdc++.so.6: no version information available (required by /app/micronaut-graalvm-helloworld)15:34:41.145 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 56ms. Server Running: http://aa22eb808a30:8080```Notice the container started in **56ms** compared to **642ms** with the JAR version.
![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"Java Rocks!"}```![user input](../images/userinput.png)
```bash$ docker imagesREPOSITORY                               TAG                       IMAGE ID       CREATED             SIZEhelloworld-graal                         latest                    3779528da123   12 minutes ago      83.1MBhelloworld-graal-jvm                     latest                    ae6f8aea4300   45 minutes ago      300MB```

Finally, quit the docker container by issuing a `CTRL-C` in the original terminal window.

NOTE:
>>
>>If you are using Fedora 31 and above, you may have encountered an error when excuting the `docker-build.sh` script. Fedora 31+ is using CGroup v2 by default which is not compatible with Docker at this time.
>>On my Fedora 33 system, the script failed with message _**"OCI runtime create failed: this version of runc doesn't work on cgroups v2: unknown"**_
Here's the error output:
>>
>>```
>>$ sudo ./docker-build.sh
>>Sending build context to Docker daemon  65.13MB
>>Step 1/10 : FROM oracle/graalvm-ce:20.1.0-java8 as graalvm
>>---> fa8819f7526a
>>Step 2/10 : RUN gu install native-image
>>---> Running in 97c5d3a66402
>>OCI runtime create failed: this version of runc doesn't work on cgroups v2: unknown
>>```
>>
>>The workaround can be find [here](https://www.linuxuprising.com/2019/11/how-to-install-and-use-docker-on-fedora.html)
>>
>> On Fedora, run the following commands:
>>
>>![user input](../images/userinput.png)
>>
>>```
>> $ sudo dnf install grubby
>> $ sudo grubby --update-kernel=ALL --args="systemd.unified_cgroup_hierarchy=0"
>> $ sudo reboot now  #reboot your machine
>>```
>>
>> Once your Fedora machine rebooted, try to execute `docker-build.sh` script again :
>>
>>![user input](../images/userinput.png)
>>
>>```bash
>> $ sudo ./docker-build.sh
>>```
>>
>> You should be able to build a container image now.


### A Note on Building on OSXIf you use a Mac, you will need to build your native images inside a **Linux container** if you want to deploy it inside a Docker container. If you stop to think about it, makes perfect sense, right?  You build on a Mac, you get a Mac executable.You will, from time to time, forget this and then you will see the following error when you deploy your app into a docker container:```textstandard_init_linux.go:211: exec user process caused "exec format error"```

Of course, there are a number of frameworks you can use to develop your microservices applications including Micronaut, Quarkus, Helidon and Spring.  In fact. here's an interesting benchmark project comparing the various frameworks:

* [Comparing Java frameworks: Quarkus, Micronaut and Spring Boot](https://github.com/ivangfr/graalvm-quarkus-micronaut-springboot) 
 

---<a href="../ex05/">    <img src="../images/noun_Next_511450_100.png"/></a>