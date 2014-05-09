[![Build Status](https://travis-ci.org/sttc/java-sdk.svg?branch=master)](https://travis-ci.org/sttc/java-sdk)

Java SDK of Stateful.co

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

The only dependency you need is this:

```xml
<dependency>
  <groupId>co.stateful</groupId>
  <artifactId>java-sdk</artifactId>
  <version>0.1</version>
</dependency>
```
