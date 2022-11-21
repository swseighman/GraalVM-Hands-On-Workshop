## Installing GraalVM

The installation instructions for GraalVM can be found [here](https://docs.oracle.com/en/graalvm/enterprise/22/docs/getting-started/installation-linux/).

The prerequisites for getting the native image component working are described in the [docs](https://docs.oracle.com/en/graalvm/enterprise/22/docs/reference-manual/enterprise-native-image/).

### Native Image Prerequisites

Native Image requires the installation of `glibc-devel`, `zlib-devel`, and `gcc` libraries on your MacOS or Linux system. Install those libraries using the package manager available in your OS:

Oracle Linux using `yum` package manager

```shell
$ sudo yum install gcc glibc-devel zlib-devel
```

Ubuntu Linux using `apt-get` package manager

```shell
$ sudo apt-get install build-essential libz-dev zlib1g-dev
```

Other Linux using `rpm` package manager

```shell
$ sudo dnf install gcc glibc-devel zlib-devel libstdc++-static
```

MacOS

`$ xcode-select --install`

>As mentioned, this workshop does not include Windows examples but if your're curious, feel free to review [this document](https://swseighman.github.io/Native-Image-Windows/) on installing GraalVM and the native image module on Windows 10.

**Congratulations!** You have successfully installed GraalVM Enterprise Edition along with the Native Image, LLVM toolchain, and R components.

Now we're ready to begin the exercises.

---<a href="../ex01/">    <img src="../images/noun_Next_511450_100.png"/></a>