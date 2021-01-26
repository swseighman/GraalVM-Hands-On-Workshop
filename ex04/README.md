## Exercise 4: Microservices

![](../images/micronaut_mini_copy_tm-50.png)


In this exercise, we'll learn how to create a Hello World Micronaut GraalVM application.  To get started, clone the git repository:
![user input](../images/userinput.png)
```bash$ git clone https://github.com/swseighman/micronaut-graalvm-helloworld.git```Change directory to `micronaut-graalvm-helloworld`:![user input](../images/userinput.png)
```bash$ cd micronaut-graalvm-helloworld```
This exercise was tested with Micronaut 2.2.x and Micronaut 2.3.0 (latest).  Simply edit the `pom.xml` file to choose a particular version.
![user input](../images/userinput.png)
```bash$ ./mvnw package```You can now run the `jar` version from the `target` directory:![user input](../images/userinput.png)
```bash$ java -jar target/micronaut-graalvm-helloworld-0.1.jar
 __  __ _                                  _
|  \/  (_) ___ _ __ ___  _ __   __ _ _   _| |_
| |\/| | |/ __| '__/ _ \| '_ \ / _` | | | | __|
| |  | | | (__| | | (_) | | | | (_| | |_| | |_
|_|  |_|_|\___|_|  \___/|_| |_|\__,_|\__,_|\__|
  Micronaut (v2.3.0)

22:28:04.190 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 832ms. Server Running: http://localhost:8080```In a separate terminal, send a request to the service:![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"Java Rules!"}```**Create a native image**Next, let's build a native image of our application.Edit the `pom.xml` file and uncomment the Graal dependency (lines 93-98) and the Graal plugin (lines 125-147).  The native image build will take 1-3 minutes depending on your system resources.Package the application:![user input](../images/userinput.png)
```bash$ ./mvnw package...[micronaut-graalvm-helloworld:12406]     (inline):   2,971.36 ms,  5.62 GB[micronaut-graalvm-helloworld:12406]    (compile):  49,922.64 ms,  6.91 GB[micronaut-graalvm-helloworld:12406]      compile:  61,715.08 ms,  6.91 GB[micronaut-graalvm-helloworld:12406]        image:   4,883.90 ms,  6.91 GB[micronaut-graalvm-helloworld:12406]        write:     760.46 ms,  6.91 GB[micronaut-graalvm-helloworld:12406]      [total]: 113,450.38 ms,  6.91 GB[INFO] ------------------------------------------------------------------------[INFO] BUILD SUCCESS[INFO] ------------------------------------------------------------------------[INFO] Total time:  01:58 min[INFO] Finished at: 2021-01-26T11:09:38-05:00[INFO] ------------------------------------------------------------------------```With the addition of the Maven-GraalVM plugin (and dependencies) our native image is created in the `target` directory.

Let's run the native image version:
![user input](../images/userinput.png)
```bash$ target/micronaut-graalvm-helloworld
 __  __ _                                  _
|  \/  (_) ___ _ __ ___  _ __   __ _ _   _| |_
| |\/| | |/ __| '__/ _ \| '_ \ / _` | | | | __|
| |  | | | (__| | | (_) | | | | (_| | |_| | |_
|_|  |_|_|\___|_|  \___/|_| |_|\__,_|\__,_|\__|
  Micronaut (v2.3.0)

22:27:08.314 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 13ms. Server Running: http://localhost:8080```In a separate terminal, send a request to the service:![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"GraalVM Rocks!"}```Note the startup times for the JAR (**832ms**) versus the native image (**13ms**) applications.
### Deploying a JAR inside a containerWith this approach you only need the fat jar.Build a container image, make certain the docker daemon service is running (or use `podman`).![user input](../images/userinput.png)
```bash$ ./docker-build-jvm.sh```Start the container:![user input](../images/userinput.png)
```bash$ docker run -p 8080:8080 micronaut-graalvm-helloworld:jvm __  __ _                                  _|  \/  (_) ___ _ __ ___  _ __   __ _ _   _| |_| |\/| | |/ __| '__/ _ \| '_ \ / _` | | | | __|| |  | | | (__| | | (_) | | | | (_| | |_| | |_|_|  |_|_|\___|_|  \___/|_| |_|\__,_|\__,_|\__|  Micronaut (v2.3.0)13:03:15.921 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 656ms. Server Running: http://999bd40b5e54:8080```Notice the container started in **642ms**.  Bet we can do better with the native image!
![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"GraalVM Rocks!"}```
Check the container CPU/memory usage:

![user input](../images/userinput.png)
```bash$ docker ps (note the container ID)
$ docker stats <container-ID>```
Quit the docker container by issuing a `CTRL-C` in the original terminal window.
### Deploying a native image inside a containerWith this approach you only need to build the fat jar and then use Docker/Podman to build the native image.Then build a container image, make certain the docker daemon service is running (or use `podman`).![user input](../images/userinput.png)
```bash$ ./docker-build.sh```Start the container:![user input](../images/userinput.png)
```bash$ docker run -p 8080:8080 micronaut-graalvm-helloworld:graalvm __  __ _                                  _|  \/  (_) ___ _ __ ___  _ __   __ _ _   _| |_| |\/| | |/ __| '__/ _ \| '_ \ / _` | | | | __|| |  | | | (__| | | (_) | | | | (_| | |_| | |_|_|  |_|_|\___|_|  \___/|_| |_|\__,_|\__,_|\__|  Micronaut (v2.3.0)08:01:53.208 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 64ms. Server Running: http://0d1168f199ff:8080```Notice the container started in **56ms** compared to **642ms** with the JAR version.![user input](../images/userinput.png)
```bash$ curl http://localhost:8080/randomplay{"name":"Java Rocks!"}```Once again, check the container CPU/memory usage:

![user input](../images/userinput.png)
```bash$ docker ps (note the container ID)
$ docker stats <container-ID>```Compare the container image size:
![user input](../images/userinput.png)
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
>> 
>> (Optional): If you're using `podman`, these commands worked too:
>> 
>> ![user input](../images/userinput.png)
>>
>> ```bash
>> $ podman --runtime /usr/bin/crun run --rm --pids-limit 1 fedora echo it works
>> $ sudo mount -t cgroup2 none /sys/fs/cgroup
>> $ sudo touch /etc/{subgid,subuid}
>> $ sudo usermod --add-subuids 100000-165535 --add-subgids 100000-165535 <your-username>
>> $ grep {USER} /etc/subuid /etc/subgid
>> 
```



### A Note on Building on OSXIf you use a Mac, you will need to build your native images inside a **Linux container** if you want to deploy it inside a Docker container. If you stop to think about it, makes perfect sense, right?  You build on a Mac, you get a Mac executable.You will, from time to time, forget this and then you will see the following error when you deploy your app into a docker container:```textstandard_init_linux.go:211: exec user process caused "exec format error"```

Of course, there are a number of frameworks you can use to develop your microservices applications including Micronaut, Quarkus, Helidon and Spring.  In fact. here's an interesting benchmark project comparing the various frameworks:

* [Comparing Java frameworks: Quarkus, Micronaut and Spring Boot](https://github.com/ivangfr/graalvm-quarkus-micronaut-springboot) 
 

---<a href="../ex05/">    <img src="../images/noun_Next_511450_100.png"/></a>