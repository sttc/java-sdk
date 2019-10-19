<img src="http://img.stateful.co/pomegranate.svg" width="64px" height="64px"/>

[![Managed by Zerocracy](https://www.zerocracy.com/badge.svg)](https://www.zerocracy.com)
[![DevOps By Rultor.com](https://www.rultor.com/b/sttc/java-sdk)](https://www.rultor.com/p/sttc/java-sdk)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/sttc/java-sdk.svg?branch=master)](https://travis-ci.org/sttc/java-sdk)
[![Build status](https://ci.appveyor.com/api/projects/status/g2r57nw43nxqb29h?svg=true)](https://ci.appveyor.com/project/yegor256/java-sdk)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/co.stateful/java-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/co.stateful/java-sdk)
[![Javadoc](http://www.javadoc.io/badge/co.stateful/java-sdk.svg)](http://www.javadoc.io/doc/co.stateful/java-sdk)

This is Java SDK for [Stateful](https://www.stateful.co).

The JavaDoc is [here](http://java-sdk.stateful.co/).

First, register an account at [Stateful](https://www.stateful.co). Then,
you can create a counter and increment it:

```java
import co.stateful.Counter;
import co.stateful.Counters;
import co.stateful.Sttc;
import co.stateful.RtSttc;
import com.jcabi.urn.URN;
public class Main {
  public static void main(String... args) {
    Sttc sttc = new RtSttc(
      new URN("urn:github:526301"),
      "9FF3-41E0-73FB-F900"
    );
    String name = "test-123";
    Counters counters = sttc.counters();
    Counter counter = counters.create(name);
    long value = counter.incrementAndGet(1L);
    System.out.println("new value: " + value);
    counters.delete(name);
  }
}
```

Here is how you can use a lock:

```java
Locks locks = sttc.locks();
Lock lock = locks.get("test-lock");
new Atomic(lock).call(
  new Callable<Void>() {
    @Override
    public void call() {
      // perfectly synchronized code
      return null;
    }
  }
);
```

