<img src="http://img.stateful.co/pomegranate.svg" width="64px" height="64px"/>

[![Made By Teamed.io](http://img.teamed.io/btn.svg)](http://www.teamed.io)
[![DevOps By Rultor.com](http://www.rultor.com/b/sttc/java-sdk)](http://www.rultor.com/p/sttc/java-sdk)

[![Build Status](https://travis-ci.org/sttc/java-sdk.svg?branch=master)](https://travis-ci.org/sttc/java-sdk)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/co.stateful/java-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/co.stateful/java-sdk)
[![Dependencies](https://www.versioneye.com/user/projects/561ac6a1a193340f2800116d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/561ac6a1a193340f2800116d)

## Java SDK of Stateful.co

JavaDoc is here: [java-sdk.stateful.co](http://java-sdk.stateful.co/)

First, register an account at [stateful.co](http://www.stateful.co). Then,
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

