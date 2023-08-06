package seok.chapter2.item1;

import java.util.Optional;
import java.util.ServiceLoader;

public class HelloServiceLoaderDemo {
  // HelloService 인터페이스의 구현체가 없다고 가정
  public static void main(String[] args) {
    // 임의의 구현체를 가져온다.
    // CAUTION: dependency 에 타 패키지에 존재하는 구현체를 추가해야함
    ServiceLoader<HelloService> loader = ServiceLoader.load(HelloService.class);
    Optional<HelloService> helloServiceOptional = loader.findFirst();
    helloServiceOptional.ifPresent(h -> {
      System.out.println(h.hello());
    });

  }
}
