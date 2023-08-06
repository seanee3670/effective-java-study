package seok.chapter2.item1;

public class HelloServiceFactory {

  public static HelloService of(String lang) { // 파라미터에 따라 다른 타입을 반환할 수 있다
    if (lang.equals("ko")) {
      return new KoreanHelloService();
    } else {
      return new EnglishHelloService();
    }
  }
}
