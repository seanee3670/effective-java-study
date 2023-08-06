package seok.chapter2.item1;

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
