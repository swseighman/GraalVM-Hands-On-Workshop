## Exercise 3: Polyglot - Combine JavaScript, Java, and R

GraalVM includes implementations of JavaScript, Ruby, R and Python on JVM. These are written using a new language implementation framework called
_**Truffle**_ that makes it possible to implement language interpreters that are
both simple and performant. When you write a language interpreter using
Truffle, Truffle will automatically use GraalVM on your behalf and provide you a JIT
compiler for your language. So GraalVM is not only a JIT compiler and
ahead-of-time native compiler for Java, but it can also provide a JIT compiler for
JavaScript, Ruby, R and Python through Truffle.

The languages in GraalVM aim to be drop-in replacements for your existing
languages. For example, we can install a Node.js module:

![user input](../images/userinput.png)

`$ $GRAALVM_HOME/bin/npm install color`

```
...
+ color@3.1.1
added 6 packages from 6 contributors and audited 7 packages in 6.931s
```

We can write a simple program using this module to convert an RGB HTML color to
HSL (Hue, Saturation and Lightness):

```javascript
var Color = require('color');

process.argv.slice(2).forEach(function (val) {
  console.log(Color(val).hsl().string());
});
```

Then we can run that in the usual way:

![user input](../images/userinput.png)

```
$ $GRAALVM_HOME/bin/node color.js '#42aaf4'
  hsl(204.89999999999998, 89%, 60.8%)
```

The languages in GraalVM work together and there's an API which allows you to run code
from one language in another enabling creation of polyglot programs, programs
written in more than one language.

You might want to do this because you want to write the majority of your application in one language, but there's a library in another language's ecosystem that you'd like to use. For example, JavaScript doesn't have a great solution for arbitrarily-large integers. I found several modules like `big-integer` but these are all inefficient as they store components of the number as JavaScript floating point numbers. Java's `BigInteger` class is more efficient so let's use that instead to do some arbitrarily-large integer arithmetic.

JavaScript also doesn't include any built-in support for drawing graphs, where R includes excellent support. Let's use R's `svg` module to draw a 3D scatter plot of a trigonometric function.

In both cases we can use GraalVM's polyglot API, and we can just compose the results from these other languages into JavaScript.

First, let's install the express npm package:

![user input](../images/userinput.png)

`$ $GRAALVM_HOME/bin/npm install express`

Next, let's run the following program:

![user input](../images/userinput.png)

```js
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

![user input](../images/userinput.png)

`$ $GRAALVM_HOME/bin/node --jvm --polyglot polyglot.js`

Open http://localhost:3000/ in your browser to see the result.

![polyglot.js](../images/polyglot.png)

That's the third thing we can do with GraalVM - run programs written in multiple
languages and use modules from those languages together. We think of this as a
kind of commoditisation of languages and modules - you can use whichever
language you think is best for your problem at hand, and whichever library you
want, no matter which language it came from.