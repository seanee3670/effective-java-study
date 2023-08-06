# 정적 팩터리 메서드(static factory method)

> 생성 패턴 중 하나로, 인스턴스를 생성하고 반환하는 클래스 메서드

## 5가지 장점
### 1) 이름을 가질 수 있음
생성자만으로는 어떤 객체가 반환되는지 예측하기엔 어려움이 있다. 정적 팩터리 메서드는 메서드명을 통해 반환될 객체의 특성을 묘사하는 것이 가능하다.
```java
public class Order {
  private boolean prime;
  private boolean urgent;
  private Product product;


  public static Order primeOrder(Product product) { // 명시적인 정적 팩터리 메서드
    Order order = new Order();
    order.prime = true;
    order.product = product;
    return order;
  }

  public static Order urgentOrder(Product product) {
    Order order = new Order();
    order.urgent = true;
    order.product = product;
    return order;
  }

//  public Order(Product product, boolean prime) { // prime 을 파라미터로 받는 생성자
//    this.product = product;
//    this.prime = prime;
//  }

//  public Order(Product product, boolean urgent) { // 같은 갯수와 타입의 생성자는 오버로딩 불가
//    this.product = product;
//    this.urgent = urgent;
//  }

//  public Order(boolean urgent, Product product) { // 파라미터 순서 바꾸면 가능.. but 명시적이지 못함
//    this.urgent = urgent;
//    this.product = product;
//  }
}
```

### 2) 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다
`private`으로 생성자를 선언하여 외부에서 인스턴스 생성을 방지하고, 클래스 필드에 `private final`로 인스턴스를 생성하여 싱글톤으로 관리되도록 한다.
```java
public class Settings {

  private boolean useAutoSteering;
  private boolean useABS;
  private Settings() {} // 외부에서 생성자 접근 방지

  private static final Settings SETTINGS = new Settings(); //
  public static Settings newInstance() {
    return SETTINGS;
  }
}
```

`Flyweight Pattern` 은 디자인 패턴이며, 자주 사용하는 값들을 미리 캐싱하고 꺼내 쓰는 방식으로 사용된다.

### 3) 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다


### 4) 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다
```java
public class HelloServiceFactory {

  public static HelloService of(String lang) { // 파라미터에 따라 다른 타입을 반환할 수 있다
    if (lang.equals("ko")) {
      return new KoreanHelloService();
    } else {
      return new EnglishHelloService();
    }
  }
}
```
혹은 인터페이스로 인스턴스 생성 역시 가능하다.
```java
public interface HelloService {

  String hello();

  static HelloService of(String lang) { // java 8 부터 정적 메서드 사용 가능
    if (lang.equals("ko")) {
      return new KoreanHelloService();
    } else {
      return new EnglishHelloService();
    }
  }
}
```
### 5) 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다
```java
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
```
## 2가지 단점

### 1) 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다
```java
public class Settings {

  private boolean useAutoSteering;
  private boolean useABS;
  private Settings() {} // 외부에서 생성자 접근 방지 == 하위 클래스에게 상속 불가능!

  private static final Settings SETTINGS = new Settings(); //
  public static Settings newInstance() {
    return SETTINGS;
  }
}
```
물론 원한다면 이를 해결할 수 있는 방법이 존재한다.

하나는, 생성자를 protected 이상으로 열어주는 방법. 이상하다고 생각될 수 있겠지만, `List` 에선, 생성자와 정적 팩터리 메서드 모두 제공하고 있다.
```java
public class ListDemo {

  public static void main(String[]args){
    List<String> list1 = new ArrayList<>();
    List<String> list2 = List.of("seok", "seeyun");
  }
}
```

### 2) 정적 팩터리 메서드는 프로그래머가 찾기 어렵다
javadoc은 기본적으로 생성자는 api 리스트에 등록해주지만, 정적 팩터리 메서드를 등록해주지 않는다.
(참고로, javadoc 은 명령어 `mvn javadoc:javadoc` 을 통해 생성하고 `target/site/apidocs/index.html` 파일을 브라우저 형태로 열면 확인할 수 있다.)
