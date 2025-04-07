<img src="http://img.stateful.co/pomegranate.svg" width="64px" height="64px"/>

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/sttc/java-sdk)](https://www.rultor.com/p/sttc/java-sdk)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/sttc/java-sdk/actions/workflows/mvn.yml/badge.svg)](https://github.com/sttc/java-sdk/actions/workflows/mvn.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/co.stateful/java-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/co.stateful/java-sdk)
[![Javadoc](https://www.javadoc.io/badge/co.stateful/java-sdk.svg)](https://www.javadoc.io/doc/co.stateful/java-sdk)

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
