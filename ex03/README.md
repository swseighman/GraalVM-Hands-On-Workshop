## Exercise 3: Polyglot - Combine JavaScript, Java, and R

GraalVM includes implementations of JavaScript, Ruby, R and Python on JVM. These
are written using a new language implementation framework called
[**Truffle**](https://www.graalvm.org/22.3/graalvm-as-a-platform/language-implementation-framework/)
that makes it possible to implement language interpreters that are both simple
and performant.

When you write a language interpreter using Truffle, Truffle will automatically
use GraalVM on your behalf and provide you a JIT compiler for your language. So
GraalVM is not only a JIT compiler and ahead-of-time native compiler for Java,
but it can also provide a JIT compiler for JavaScript, Ruby, R and Python
through Truffle.

The languages in GraalVM aim to be drop-in replacements for your existing
languages. The core distribution of GraalVM includes the JVM, the GraalVM
compiler, the LLVM runtime, and Node.js JavaScript runtime. Having downloaded
and installed GraalVM, you can already run Java, Node.js, JavaScript, and
LLVM-based applications.

Use the
[GraalVM Updater](https://www.graalvm.org/22.3/reference-manual/graalvm-updater/)
to list all available components.

![User Input](../images/userinput.png)

```shell
$ gu available
ComponentId              Version             Component name                Stability                     Origin
---------------------------------------------------------------------------------------------------------------------------------
espresso                 22.3.0              Java on Truffle               Experimental                  gds.oracle.com
js                       22.3.0              Graal.js                      Experimental                  gds.oracle.com
llvm                     22.3.0              LLVM Runtime Core             Experimental                  gds.oracle.com
llvm-toolchain           22.3.0              LLVM.org toolchain            Experimental                  gds.oracle.com
native-image             22.3.0              Native Image                  Experimental                  gds.oracle.com
nodejs                   22.3.0              Graal.nodejs                  Experimental                  gds.oracle.com
python                   22.3.0              GraalVM Python                Experimental                  gds.oracle.com
ruby                     22.3.0              TruffleRuby                   Experimental                  gds.oracle.com
visualvm                 22.3.0              VisualVM EE                   Experimental                  gds.oracle.com
```

Use the GraalVM Updater to list all installed components.

```shell
$ gu list
ComponentId              Version             Component name                Stability                     Origin
---------------------------------------------------------------------------------------------------------------------------------
graalvm                  22.3.0              GraalVM Core                  Experimental
js                       22.3.0              Graal.js                      Experimental                  gds.oracle.com
llvm                     22.3.0              LLVM Runtime Core             Experimental                  gds.oracle.com
native-image             22.3.0              Native Image                  Experimental                  gds.oracle.com
nodejs                   22.3.0              Graal.nodejs                  Experimental                  gds.oracle.com
```

The above shows four components.  In this example we will be using the _nodejs_
and _r_ GraalVM components.  Make sure that both the _nodejs_ the _r_ GraalVM
components before proceeding.

![User Input](../images/userinput.png)

```shell
$ gu install nodejs
```

![User Input](../images/userinput.png)

```shell
$ gu install r
```

**Kindly note that r is not available on the [AArch64](https://en.wikipedia.org/wiki/AArch64) architecture**.

We can also install a Node.js module using the npm that is part of the GraalVM
nodejs component.

Make sure that the environment variable `GRAALVM_HOME` exists and points to the
GraalVM installation directory.

![User Input](../images/userinput.png)

```shell
$ echo ${GRAALVM_HOME}
~/.sdkman/candidates/java/graalee-22.3-17
```

The output of the above command will vary depending where GraalVM is installed.
Set the environment variable if missing.

![User Input](../images/userinput.png)

```
$ export GRAALVM_HOME=<GRAALVM-HOME-PATH>
```

Let install the [`color` NPM package](https://www.npmjs.com/package/color)

![User Input](../images/userinput.png)

```shell
$ ${GRAALVM_HOME}/bin/npm install color
```

```
...
+ color@3.1.1
added 6 packages from 6 contributors and audited 7 packages in 6.931s
```

We can write a simple program using this module to convert an RGB HTML color to
HSL (Hue, Saturation and Lightness). Create a file called `color.js` and add the
following code:

![User Input](../images/userinput.png)

```javascript
var Color = require('color');

process.argv.slice(2).forEach(function (val) {
  console.log(Color(val).hsl().string());
});
```

Then we can run that in the usual way:

![User Input](../images/userinput.png)

```shell
$ ${GRAALVM_HOME}/bin/node color.js '#42aaf4'
  hsl(204.89999999999998, 89%, 60.8%)
```

The languages in GraalVM work together and there's an API which allows you to
run code from one language in another enabling creation of polyglot programs.

The use case for polyglot programming often boils down to some feature missing
in the language you're using to solve an issue. For example, JavaScript doesn't
have a great solution for arbitrarily-large integers. Modules like `big-integer`
exist but they tend to be inefficient as they store components of the number as
JavaScript floating point numbers. Java's `BigInteger` class is more efficient
for performing arbitrarily-large integer arithmetic.

JavaScript also doesn't include any built-in support for drawing graphs, where R
includes excellent support. Let's use R's `svg` module to draw a 3D scatter plot
of a trigonometric function.

In both cases we can use GraalVM's polyglot API, and we can just compose the
results from these other languages into JavaScript.

First, let's install the `express` npm package:

![User Input](../images/userinput.png)

```shell
$ ${GRAALVM_HOME}/bin/npm install express
```

Next, in your favorite IDE, create a new file called `polyglot.js`:

![User Input](../images/userinput.png)

```javascript
const express = require('express')
const app = express()

const BigInteger = Java.type('java.math.BigInteger')

app.get('/', function (req, res) {
  var text = 'Hello World from Graal.js!<br> '

  // Using Java standard library classes
  text += BigInteger.valueOf(10).pow(100)
          .add(BigInteger.valueOf(43)).toString() + '<br>'

  // Using R interoperability to create graphs
  text += Polyglot.eval('R',
    `svg();
     require(lattice);
     x <- 1:100
     y <- sin(x/10)
     z <- cos(x^1.3/(runif(1)*5+10))
     print(cloud(x~y*z, main="cloud plot"))
     grDevices:::svg.off()
    `);

  res.send(text)
})

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
})
```

Now let's run the program:

![User Input](../images/userinput.png)

```shell
$ ${GRAALVM_HOME}/bin/node --jvm --polyglot polyglot.js
```

Open [http://localhost:3000/](http://localhost:3000/) in your browser to see the
result.

_(NOTE: If your're using WSL2, you'll need to specify the IP address of your environment)_

![polyglot.js](../images/polyglot.png)

That's another option we have available with GraalVM, the ability to run
programs written in multiple languages and use modules from those languages
together. We think of this as a kind of commoditization of languages and modules
- you can use whichever language you think is best for your use case and
whichever library you want, no matter which language.

---

<a href="../ex04/"><img src="../images/noun_Next_511450_100.png"/></a>
