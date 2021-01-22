## Exercise 1: High-Performance JIT Compiler for Java


The GraalVM compiler is a dynamic just-in-time (JIT) compiler, written in Java, that transforms bytecode into machine code. The GraalVM compiler integrates with the Java HotSpot VM, which supports a compatible version of the JVM Compiler Interface (JVMCI). JVMCI is a privileged, low-level interface to the JVM, enabling a compiler written in Java to be used by the JVM as a dynamic compiler (see JEP 243)

It can read metadata from the VM, such as method bytecode, and install machine code into the VM . GraalVM includes a version of the HotSpot JVM that supports JVMCI.

GraalVM supports two operating modes, the _**JIT compiler**_, and _**AOT (Ahead-of-Time) compiler**_.  We'll explore both in the following exercises, let's begin with the JIT compiler and examine how it boosts application performance.

### GraalVM JIT

Source code for this exercise can be found [here](https://github.com/marthenlt/native-image-workshop).

You can simply clone the source code by using this command:

![user input](../images/userinput.png)

```
$ git clone https://github.com/marthenlt/native-image-workshop.git
```

Once you've cloned the repo, change directory to `native-image-workshop` and unzip `large.zip`file. See the following commands:

![user input](../images/userinput.png)

```
$ cd native-image-workshop
$ unzip large.zip
```

Executing a `ls -al` of your working directory should output something similar to this:

```
total 394112
drwxr-xr-x  15 sseighma  staff        480 Nov 22 11:56 .
drwxr-xr-x  15 sseighma  staff        480 Jan 20 11:33 ..
drwxr-xr-x  12 sseighma  staff        384 Nov 22 11:53 .git
-rw-r--r--   1 sseighma  staff         33 Nov 22 11:53 .gitignore
-rw-r--r--   1 sseighma  staff        545 Nov 22 11:53 README.md
-rw-r--r--   1 sseighma  staff       2341 Nov 22 11:53 Streams.java
-rw-r--r--   1 sseighma  staff       1127 Nov 22 11:53 TopTen.java
-rw-r--r--   1 sseighma  staff         81 Nov 22 11:53 c2.sh
-rw-r--r--   1 sseighma  staff         59 Nov 22 11:53 graal.sh
-rwxr-xr-x   1 sseighma  staff  151397500 Sep 19  2019 large.txt
-rw-r--r--   1 sseighma  staff   40230188 Nov 22 11:53 large.zip
-rw-r--r--   1 sseighma  staff       1024 Nov 22 11:53 small.txt
-rw-r--r--   1 sseighma  staff         55 Nov 22 11:53 timer.bat
```

We'll use the `TopTen.java` example program, which displays the top ten words in `large.txt` (file size is approximately 151 MB).

It uses the Stream Java API to traverse, sort and count all the words (there are 22,377,500 words).

Below is the contents of the `TopTen.java` program:

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopTen {

    public static void main(String[] args) {
        Arrays.stream(args)
                .flatMap(TopTen::fileLines)
                .flatMap(line -> Arrays.stream(line.split("\\b")))
                .map(word -> word.replaceAll("[^a-zA-Z]", ""))
                .filter(word -> word.length() > 0)
                .map(word -> word.toLowerCase())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> -a.getValue().compareTo(b.getValue()))
                .limit(10)
                .forEach(e -> System.out.format("%s = %d%n", e.getKey(), e.getValue()));
    }

    private static Stream<String> fileLines(String path) {
        try {
            return Files.lines(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
```

### GraalVM JIT - Compile The TopTen.java Program

GraalVM includes a `javac` compiler, but it isn't any different from the
standard compiler for the purposes of this demo, so optionally you could use your system `javac` instead.

To compile, use the following command:

![user input](../images/userinput.png)

`$ javac TopTen.java`

If we run the `java` command included in GraalVM, we'll automatically be using
the GraalVM JIT compiler (no extra configuration is required). Rather than set up a complicated micro-benchmark, we'll use the `time` command to display the real, wall-clock elapsed time to run the entire program from start to finish.

Use the command below to measure the time GraalVM Enterprise takes to run `TopTen.java`:

![user input](../images/userinput.png)

`$ time java TopTen large.txt`

The output will appear similar to the example below (results are dependent on your system/laptop).

On Linux

```
sed = 502500
ut = 392500
in = 377500
et = 352500
id = 317500
eu = 317500
eget = 302500
vel = 300000
a = 287500
sit = 282500

real	0m21.411s
user	0m30.780s
sys	0m1.224s
```

On MacOS

```
sed = 502500
ut = 392500
in = 377500
et = 352500
id = 317500
eu = 317500
eget = 302500
vel = 300000
a = 287500
sit = 282500
java TopTen large.txt  11.62s user 0.49s system 114% cpu 10.535 total
```

GraalVM is written in Java, rather than C++ like other JIT compilers. This allows us to quickly add improvements with powerful new optimizations (such as partial escape analysis) that aren't available in the standard HotSpot JIT compilers. This can help your Java programs run significantly faster.

To run without the GraalVM JIT compiler, we can use the `-XX:-UseJVMCICompiler` flag. JVMCI is the interface between GraalVM and the JVM. You could also compare against your standard JVM as well.

![](../images/userinput.png)

`$ time java -XX:-UseJVMCICompiler TopTen large.txt`

On Linux

```
sed = 502500
ut = 392500
in = 377500
et = 352500
id = 317500
eu = 317500
eget = 302500
vel = 300000
a = 287500
sit = 282500

real	0m32.080s
user	0m32.719s
sys	0m0.490s
```

On MacOS

```
sed = 502500
ut = 392500
in = 377500
et = 352500
id = 317500
eu = 317500
eget = 302500
vel = 300000
a = 287500
sit = 282500
java -XX:-UseJVMCICompiler TopTen large.txt  15.91s user 0.30s system 106% cpu 15.282 total
```

These results show GraalVM uses approximately two-thirds of the wall-clock (`real`) time to run the application using a standard HotSpot compiler. In an atmosphere where single-digit percentage increases in performance is considered significant, this is impressive!

You'll see similar results if you use GraalVM Community Edition, but they won't be quite as impressive as the Enterprise Edition results.

As you can see, by simply replacing the underlying JIT compiler, you'll realize improved performance from your existing Java applications.

---<a href="../ex02/">    <img src="../images/noun_Next_511450_100.png"/></a>