## Exercise 2: Ahead-of-Time (AOT) Compilation

In the previous exercise, we discovered that GraalVM Enterprise can boost Java program performance without changing any code.

In the next exercise, we will be using GraalVM Native Image to compile Java Bytecode into a native binary executable file.

### Graal AOT

The Java platform can optimize long-running processes and produce peak performance, but short-running processes often suffer from longer startup times and
relatively high memory usage.

For example, if we run the same application with a much smaller input text file called `small.txt` (around 1 KB instead of 150 MB), it seems to take an unreasonably long time and quite a bit of memory (at 70 MB) to run for such a small file. Below we use `-l` to print the memory used as well as time used.


![user input](../images/userinput.png)

`$ /usr/bin/time -v java TopTen small.txt`    #`-l` on MacOS instead of `-v`

```
sed = 6
sit = 6
amet = 6
mauris = 3
volutpat = 3
vitae = 3
dolor = 3
libero = 3
tempor = 2
suscipit = 2
	Command being timed: "java TopTen small.txt"
	User time (seconds): 0.39
	System time (seconds): 0.04
	Percent of CPU this job got: 168%
	Elapsed (wall clock) time (h:mm:ss or m:ss): 0:00.26
	Average shared text size (kbytes): 0
	Average unshared data size (kbytes): 0
	Average stack size (kbytes): 0
	Average total size (kbytes): 0
	Maximum resident set size (kbytes): 76116
...
```

GraalVM provides a tool to solve this problem. We said that GraalVM is like a
compiler library and it can be used in many different manners. One of those options is to compile *ahead-of-time*, to a native executable image, instead of compiling
*just-in-time* at runtime. This is similar to how a conventional compiler like
`gcc` works.

### Graal AOT - _Creating Binary Executable Using Native Image_

Now let's create our first binary executable file using GraalVM Native Image from our existing TopTen bytecode.  Execute the command below to create a `TopTen` native binary executable:

![user input](../images/userinput.png)

`$ native-image --no-server --no-fallback TopTen`

The output will look similar to the following:

```
[topten:37970]    classlist:   1,801.57 ms
[topten:37970]        (cap):   1,289.45 ms
[topten:37970]        setup:   3,087.67 ms
[topten:37970]   (typeflow):   6,704.85 ms
[topten:37970]    (objects):   6,448.88 ms
[topten:37970]   (features):     820.90 ms
[topten:37970]     analysis:  14,271.88 ms
[topten:37970]     (clinit):     257.25 ms
[topten:37970]     universe:     766.11 ms
[topten:37970]      (parse):   1,365.29 ms
[topten:37970]     (inline):   3,829.55 ms
[topten:37970]    (compile):  34,674.51 ms
[topten:37970]      compile:  41,412.71 ms
[topten:37970]        image:   2,741.41 ms
[topten:37970]        write:     619.13 ms
[topten:37970]      [total]:  64,891.52 ms
```

This command produces a native executable called `topten`. This executable isn't
a launcher for the JVM, it doesn't link to the JVM, and it doesn't bundle the
JVM in any way. `native-image` really does compile your Java code and any Java
libraries you use, down to simple machine code. For runtime components like the garbage collector, we are running our own new VM called the SubstrateVM, which like GraalVM, is also written in Java.

If we look at the libraries which `topten` uses, you can see they are only
standard system libraries. We could also move just this one file to a system
without a JVM installed and run it to verify it doesn't use a JVM or any other files. It's also pretty small, this executable is less than 7.5MB.

![user input](../images/userinput.png)

`$ ldd topten`    #`otool -L topten` on MacOS

```
	linux-vdso.so.1 =>  (0x00007ffe1555b000)
	libm.so.6 => /lib64/libm.so.6 (0x00007f6bda7c6000)
	libpthread.so.0 => /lib64/libpthread.so.0 (0x00007f6bda5aa000)
	libdl.so.2 => /lib64/libdl.so.2 (0x00007f6bda3a6000)
	libz.so.1 => /lib64/libz.so.1 (0x00007f6bda190000)
	librt.so.1 => /lib64/librt.so.1 (0x00007f6bd9f88000)
	libcrypt.so.1 => /lib64/libcrypt.so.1 (0x00007f6bd9d51000)
	libc.so.6 => /lib64/libc.so.6 (0x00007f6bd9983000)
	/lib64/ld-linux-x86-64.so.2 (0x00007f6bdaac8000)
	libfreebl3.so => /lib64/libfreebl3.so (0x00007f6bd9780000)
$ du -h topten
7.2M  topten
```

If we run the executable we can see that it starts an order of magnitude
faster, and uses an order of magnitude less memory than running the same
program on the JVM. It's so fast that you don't notice the time taken when
using it at the command line - you don't feel that pause you always get when
executing a short-running command with the JVM.

![user input](../images/userinput.png)

`$ /usr/bin/time -v ./topten small.txt`  #`-l` on Mac instead of `-v`

```
sed = 6
sit = 6
amet = 6
mauris = 3
volutpat = 3
vitae = 3
dolor = 3
libero = 3
tempor = 2
suscipit = 2
	Command being timed: "./topten small.txt"
	User time (seconds): 0.00
	System time (seconds): 0.00
	Percent of CPU this job got: 50%
	Elapsed (wall clock) time (h:mm:ss or m:ss): 0:00.00
	Average shared text size (kbytes): 0
	Average unshared data size (kbytes): 0
	Average stack size (kbytes): 0
	Average total size (kbytes): 0
	Maximum resident set size (kbytes): 6192
...
```

So as you can see from the output above, GraalVM AOT via Native Image requires only 6.1 MB memory, whereas GraalVM JIT version requires 76.1 MB memory. Thats approximately a **11x smaller memory footprint**.

Application start-up is also worth mentioning, see the "Elapsed (wall clock)" from the 2 examples.  GraalVM JIT elapsed (wall clock) time is 26 ms (0:00.26), GraalVM AOT elapsed (wall clock) time is 0 ms (0:00.00) ... too fast for the `time` utility to measure.

Now, let's see how we can improve throughput performance of our AOT application (TPS - transaction per second).

In the next AOT example, we will create a **PGO (Profile Guided Optimization)** file to improve throughput of our native executable application.

### Graal AOT - _PGO (Profile Guided Optimization)_

`PGO` is a way to _teach_ GraalVM AOT compiler to further optimize the throughput of the resulted native binary executable application.

For this exercise we will be using the `Streams.java` progam below.  Open your favorite IDE and paste the code into a new file called `Streams.java`.

```java
import java.util.Arrays;
import java.util.Random;

public class Streams {

	static final double EMPLOYMENT_RATIO = 0.5;
	static final int MAX_AGE = 100;
	static final int MAX_SALARY = 200_000;

	public static void main(String[] args) {

		int iterations;
		int dataLength;
		try {
			iterations = Integer.valueOf(args[0]);
			dataLength = Integer.valueOf(args[1]);
		} catch (Throwable ex) {
			System.out.println("expected 2 integer arguments: number of iterations, length of data array");
			return;
		}

		/* Create data set with a deterministic random seed. */
		Random random = new Random(42);
		Person[] persons = new Person[dataLength];
		for (int i = 0; i < dataLength; i++) {
			persons[i] = new Person(
					random.nextDouble() >= EMPLOYMENT_RATIO ? Employment.EMPLOYED : Employment.UNEMPLOYED,
					random.nextInt(MAX_SALARY),
					random.nextInt(MAX_AGE));
		}

		long totalTime = 0;
		for (int i = 1; i <= 20; i++) {
			long startTime = System.currentTimeMillis();

			long checksum = benchmark(iterations, persons);

			long iterationTime = System.currentTimeMillis() - startTime;
			totalTime += iterationTime;
			System.out.println("Iteration " + i + " finished in " + iterationTime + " milliseconds with checksum " + Long.toHexString(checksum));
		}
		System.out.println("TOTAL time: " + totalTime);
	}

	static long benchmark(int iterations, Person[] persons) {
		long checksum = 1;
		for (int i = 0; i < iterations; ++i) {
			double result = getValue(persons);

			checksum = checksum * 31 + (long) result;
		}
		return checksum;
	}

	/*
	 * The actual stream expression that we want to benchmark.
	 */
	public static double getValue(Person[] persons) {
		return Arrays.stream(persons)
				.filter(p -> p.getEmployment() == Employment.EMPLOYED)
				.filter(p -> p.getSalary() > 100_000)
				.mapToInt(Person::getAge)
				.filter(age -> age >= 40).average()
				.getAsDouble();
	}
}

enum Employment {
	EMPLOYED, UNEMPLOYED
}

class Person {
	private final Employment employment;
	private final int age;
	private final int salary;

	public Person(Employment employment, int height, int age) {
		this.employment = employment;
		this.salary = height;
		this.age = age;
	}

	public int getSalary() {
		return salary;
	}

	public int getAge() {
		return age;
	}

	public Employment getEmployment() {
		return employment;
	}
}
```

Compile it using below command:

![user input](../images/userinput.png)

`$ javac Streams.java`

And then create the native binary executable using the following command:

![user input](../images/userinput.png)

`$ native-image --no-server --no-fallback Streams`

Run the native binary executable:

![user input](../images/userinput.png)

`$ ./streams 100000 200`

The above command line will create an array of 200 Person objects, with 100K iterations to calculate the average age that meet the criteria.

The output is similar to the following:

```
Iteration 1 finished in 264 milliseconds with checksum e6e0b70aee921601
Iteration 2 finished in 244 milliseconds with checksum e6e0b70aee921601
Iteration 3 finished in 244 milliseconds with checksum e6e0b70aee921601
Iteration 4 finished in 254 milliseconds with checksum e6e0b70aee921601
Iteration 5 finished in 238 milliseconds with checksum e6e0b70aee921601
Iteration 6 finished in 239 milliseconds with checksum e6e0b70aee921601
Iteration 7 finished in 233 milliseconds with checksum e6e0b70aee921601
Iteration 8 finished in 232 milliseconds with checksum e6e0b70aee921601
Iteration 9 finished in 236 milliseconds with checksum e6e0b70aee921601
Iteration 10 finished in 219 milliseconds with checksum e6e0b70aee921601
Iteration 11 finished in 223 milliseconds with checksum e6e0b70aee921601
Iteration 12 finished in 226 milliseconds with checksum e6e0b70aee921601
Iteration 13 finished in 235 milliseconds with checksum e6e0b70aee921601
Iteration 14 finished in 229 milliseconds with checksum e6e0b70aee921601
Iteration 15 finished in 230 milliseconds with checksum e6e0b70aee921601
Iteration 16 finished in 234 milliseconds with checksum e6e0b70aee921601
Iteration 17 finished in 237 milliseconds with checksum e6e0b70aee921601
Iteration 18 finished in 220 milliseconds with checksum e6e0b70aee921601
Iteration 19 finished in 223 milliseconds with checksum e6e0b70aee921601
Iteration 20 finished in 226 milliseconds with checksum e6e0b70aee921601
TOTAL time: 4686
```

The result is **4686 miliseconds**, and that would be the designated throughput result before we optimize the `streams` binary executable application using PGO.

Next we will create a PGO file and create a new `streams` binary executable application.

There are 2 methods for creating a PGO file:

* `java -Dgraal.PGOInstrument`
* `native-image --pgo-instrument`


##### Generating PGO file via `java -Dgraal.PGOInstrument`

In this exercise we will create a PGO file named `streams.iprof` via `java -Dgraal.PGOInstrument`, we can complete that by executing below command:

![user input](../images/userinput.png)

`$ java -Dgraal.PGOInstrument=streams.iprof Streams 100000 200`

Feel free to `more streams.iprof` to see the contents of the file.

The contents is similar to this:

```
{
  "version": "0.1.0",
  "types": [
    { "id": 0, "typeName": "int" },
    { "id": 1, "typeName": "char" },
    { "id": 2, "typeName": "java.lang.String" },
    { "id": 3, "typeName": "void" },
    { "id": 4, "typeName": "java.lang.Object" },
    { "id": 5, "typeName": "boolean" },
    { "id": 6, "typeName": "java.util.Locale" },
    { "id": 7, "typeName": "java.util.stream.Sink" },
    { "id": 8, "typeName": "java.util.stream.AbstractPipeline" },
    { "id": 9, "typeName": "java.util.stream.ReferencePipeline$2" },
    { "id": 10, "typeName": "java.util.stream.IntPipeline$9" },
    { "id": 11, "typeName": "java.util.stream.ReferencePipeline$4" },
    { "id": 12, "typeName": "jdk.internal.org.objectweb.asm.ByteVector" },
    { "id": 13, "typeName": "[C" },
    { "id": 14, "typeName": "[B" },
    { "id": 15, "typeName": "sun.nio.cs.UTF_8$Encoder" },
    { "id": 16, "typeName": "java.util.stream.Sink$ChainedReference" },
    { "id": 17, "typeName": "java.util.stream.IntPipeline$9$1" },
    { "id": 18, "typeName": "java.util.stream.ReferencePipeline$2$1" },
    { "id": 19, "typeName": "java.util.stream.ReferencePipeline$4$1" },
    { "id": 20, "typeName": "java.util.stream.StreamShape" },
    { "id": 21, "typeName": "java.util.stream.ReferencePipeline$StatelessOp" },
    { "id": 22, "typeName": "java.util.stream.IntPipeline$StatelessOp" },
    { "id": 23, "typeName": "long" },
    { "id": 24, "typeName": "java.util.function.Consumer" },
    { "id": 25, "typeName": "java.util.Spliterators$ArraySpliterator" },
    { "id": 26, "typeName": "Streams$$Lambda$bcba0c9074f907ff1118ccf4b20382b375b44963" },
    { "id": 27, "typeName": "Streams$$Lambda$c53cfc0c6f6864e593fb5fc8f47a4c561a797150" },
    { "id": 28, "typeName": "java.util.Spliterator" },
    { "id": 29, "typeName": "java.util.stream.StreamOpFlag" },
    { "id": 30, "typeName": "[LPerson;" },
    { "id": 31, "typeName": "double" },
    { "id": 32, "typeName": "Streams" },
    { "id": 33, "typeName": "java.util.OptionalDouble" },
    { "id": 34, "typeName": "java.util.stream.IntPipeline" },
    { "id": 35, "typeName": "[Ljava.lang.Object;" },
    { "id": 36, "typeName": "java.util.Spliterators" },
    { "id": 37, "typeName": "java.util.stream.TerminalOp" },
    { "id": 38, "typeName": "java.util.stream.ReduceOps$7" },
    { "id": 39, "typeName": "java.util.stream.PipelineHelper" },
    { "id": 40, "typeName": "java.util.stream.ReduceOps$ReduceOp" },
    { "id": 41, "typeName": "Streams$$Lambda$eae7de59100ee7efdaf17ed2cdd0bde92ce7cd36" },
    { "id": 42, "typeName": "Streams$$Lambda$05225ea80029b82a7c73c194f3554dc78ecdb5db" },
    { "id": 43, "typeName": "java.util.stream.ReduceOps$7ReducingSink" },
    { "id": 44, "typeName": "java.util.stream.IntPipeline$$Lambda$28f2139532a62de6690b06ac907ce20a1b664ed0" },
    { "id": 45, "typeName": "java.lang.CharacterDataLatin1" },
    { "id": 46, "typeName": "java.lang.CharacterData" }
  ],
  "methods": [
    { "id": 0, "methodName": "charAt", "signature": [ 2, 1, 0 ] },
    { "id": 1, "methodName": "<init>", "signature": [ 4, 3 ] },
    { "id": 2, "methodName": "hashCode", "signature": [ 2, 0 ] },
    { "id": 3, "methodName": "indexOf", "signature": [ 2, 0, 0, 0 ] },
    { "id": 4, "methodName": "equals", "signature": [ 2, 5, 4 ] },
    { "id": 5, "methodName": "toUpperCase", "signature": [ 2, 2, 6 ] },
    { "id": 6, "methodName": "wrapSink", "signature": [ 8, 7, 7 ] },
    { "id": 7, "methodName": "putUTF8", "signature": [ 12, 12, 2 ] },
    { "id": 8, "methodName": "encode", "signature": [ 15, 0, 13, 0, 0, 14 ] },

...

```

Next we can then re-create the `topten` binary executable with our PGO `streams.iprof`, type the following command:

![user input](../images/userinput.png)

`$ native-image --no-server --no-fallback --pgo=streams.iprof Streams`

Then we execute the same benchmark again:

![user input](../images/userinput.png)

`$ ./streams 100000 200`

The result is:

```
Iteration 1 finished in 183 milliseconds with checksum e6e0b70aee921601
Iteration 2 finished in 157 milliseconds with checksum e6e0b70aee921601
Iteration 3 finished in 152 milliseconds with checksum e6e0b70aee921601
Iteration 4 finished in 148 milliseconds with checksum e6e0b70aee921601
Iteration 5 finished in 160 milliseconds with checksum e6e0b70aee921601
Iteration 6 finished in 162 milliseconds with checksum e6e0b70aee921601
Iteration 7 finished in 149 milliseconds with checksum e6e0b70aee921601
Iteration 8 finished in 137 milliseconds with checksum e6e0b70aee921601
Iteration 9 finished in 141 milliseconds with checksum e6e0b70aee921601
Iteration 10 finished in 151 milliseconds with checksum e6e0b70aee921601
Iteration 11 finished in 140 milliseconds with checksum e6e0b70aee921601
Iteration 12 finished in 133 milliseconds with checksum e6e0b70aee921601
Iteration 13 finished in 151 milliseconds with checksum e6e0b70aee921601
Iteration 14 finished in 142 milliseconds with checksum e6e0b70aee921601
Iteration 15 finished in 133 milliseconds with checksum e6e0b70aee921601
Iteration 16 finished in 144 milliseconds with checksum e6e0b70aee921601
Iteration 17 finished in 151 milliseconds with checksum e6e0b70aee921601
Iteration 18 finished in 137 milliseconds with checksum e6e0b70aee921601
Iteration 19 finished in 138 milliseconds with checksum e6e0b70aee921601
Iteration 20 finished in 148 milliseconds with checksum e6e0b70aee921601
TOTAL time: 2957
```

The new benchmark (as a result of PGO) shows a better throughput of **2957 milliseconds** compared to **4686 miliseconds** (with the non-PGO version) which is **37% better throughput**.


##### Generating PGO file via `native-image --pgo-instrument`

Another way of creating a PGO file is using `native-image --pgo-instrument`.

This will create a `default.iprof` file from the `native-image` tool directly. Execute the command: 

![user input](../images/userinput.png)

`$ native-image --pgo-instrument Streams`

Note that `default.iprof` PGO file is not immediately created after you ran the command.

You need to run it with the newly created binary `streams` executable file again. Execute below command:

![user input](../images/userinput.png)

`$ ./streams 100000 200`

Once finished, you can see the `default.iprof` file is created. Once again, execute `more default.iprof` to see the contents.

Final step is to create an optimized `TopTen` native binary executable using the following command:

![user input](../images/userinput.png)

`$ native-image --pgo Streams`

And re-run our benchmark test again:

![user input](../images/userinput.png)

`$ ./streams 100000 200`

You will see more or less this output result (could be slightly different based on your system) :

```
Iteration 1 finished in 44 milliseconds with checksum e6e0b70aee921601
Iteration 2 finished in 37 milliseconds with checksum e6e0b70aee921601
Iteration 3 finished in 33 milliseconds with checksum e6e0b70aee921601
Iteration 4 finished in 32 milliseconds with checksum e6e0b70aee921601
Iteration 5 finished in 28 milliseconds with checksum e6e0b70aee921601
Iteration 6 finished in 36 milliseconds with checksum e6e0b70aee921601
Iteration 7 finished in 33 milliseconds with checksum e6e0b70aee921601
Iteration 8 finished in 28 milliseconds with checksum e6e0b70aee921601
Iteration 9 finished in 29 milliseconds with checksum e6e0b70aee921601
Iteration 10 finished in 28 milliseconds with checksum e6e0b70aee921601
Iteration 11 finished in 27 milliseconds with checksum e6e0b70aee921601
Iteration 12 finished in 30 milliseconds with checksum e6e0b70aee921601
Iteration 13 finished in 35 milliseconds with checksum e6e0b70aee921601
Iteration 14 finished in 31 milliseconds with checksum e6e0b70aee921601
Iteration 15 finished in 28 milliseconds with checksum e6e0b70aee921601
Iteration 16 finished in 29 milliseconds with checksum e6e0b70aee921601
Iteration 17 finished in 29 milliseconds with checksum e6e0b70aee921601
Iteration 18 finished in 28 milliseconds with checksum e6e0b70aee921601
Iteration 19 finished in 32 milliseconds with checksum e6e0b70aee921601
Iteration 20 finished in 36 milliseconds with checksum e6e0b70aee921601
TOTAL time: 633
```

The latest benchmark shows even better throughput of **633 milliseconds** compared to the initial **4686 miliseconds** which results in more than **86% better throughput**.

We have demonstrated how to optimize an AOT binary executable file using PGO.

The `native-image` tool has some
[restrictions](https://github.com/oracle/graal/blob/master/substratevm/LIMITATIONS.md)
such as all classes having to be available during compilation, and some
limitations around reflection. It has some additional advantages over basic
compilation as well in that static initializers are run during compilation, so
you can reduce the work required each time an application loads.

Of course, you can use GraalVM native image as a means to distribute and run
your existing Java programs with low-footprint and fast-startup features. It also frees you from configuration issues such as locating the right jar files at runtime,
and allows you to create smaller container images.

---<a href="../ex03/">    <img src="../images/noun_Next_511450_100.png"/></a>