package seok.chapter2.item1;

public interface HelloService {

  String hello();

  static HelloService of(String lang) { // java 8 부터 가능
    if (lang.equals("ko")) {
      return new KoreanHelloService();
    } else {
      return new EnglishHelloService();
    }
  }
}
