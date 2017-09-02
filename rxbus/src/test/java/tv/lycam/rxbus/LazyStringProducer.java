package tv.lycam.rxbus;

import tv.lycam.rxbus.annotation.Produce;

public class LazyStringProducer {
  public String value = null;

  @Produce
  public String produce() {
    return value;
  }
}
