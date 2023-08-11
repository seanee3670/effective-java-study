# private 생성자나 열거 타입으로 싱글턴임을 보장하라

## 싱글턴(singleton)
> 인스턴스를 오직 하나만 생성할 수 있는 클래스

## 싱글턴의 장점
### 메모리 효율성
하나의 인스턴스를 재활용함으로써 메모리 할당 및 회수를 줄일 수 있다.(Item1. `정적 팩터리 메서드`의 장점에서도 설명함)
### 간단한 접근과 제어
단 하나의 접근점(single point of access)을 허락하기 때문에 애플리케이션의 모든 컴포넌트가 동일한 인스턴스를 사용한다는 것을 보장할 수 있다.

## 싱글턴의 단점
### 확장성과 테스트의 한계
전역(public)으로 선언된 클래스인만큼 싱글턴 클래스를 상속하는 모든 자식 클래스들은 반드시 public 으로 선언되어야 하며 메서드 오버라이딩 범위 역시 마찬가지이다.
따라서, 코드 변경은 애플리케이션 전역에 영향을 끼치므로 확장성 높은 설계가 어렵다.

또한, 테스트는 하나의 기능에 대해 각각 독립적으로 수행되어야 하는데, 전역으로 선언된 싱글턴의 의존성들이 어디서 어떻게 작용하는지 전부 파악하고 테스트해보는 것 역시 쉽지 않다.
(혹자는 싱글턴은 안티패턴이라고 한다.)

## 싱글턴 생성 방식
### public static final 필드 방식
```java
public class Elvis {

  public static final Elvis INSTANCE = new Elvis();
  private Elvis() { ... }
  
  public void leaveTheBuilding() { ... }
}
```
장점: 간결하며, 싱글턴임이 API에 명백히 들어남 (javadoc에서 확인 가능)

단점1: 인터페이스 타입을 정의하여 구현한 싱글톤이 아니라면 클라이언트 테스트가 어려움
```java
public interface IElvis {

  void leaveTheBuilding();
  void sing();

}
```
```java
public class Elvis implements IElvis { // 인터페이스를 구현한 싱글턴 클래스

  public static final Elvis INSTANCE = new Elvis();
  
  private Elvis() {}

  @Override
  public void leaveTheBuilding() {
    System.out.println("Whoa baby, I'm outta here!");
  }

  @Override
  public void sing() {
    System.out.println("I'll have a blue Christmas without you~");
  }
}
```
```java
class Test {
  @Test
  void perform() {
    MockElvis mockElvis = new MockElvis(); //인터페이스를 구현한 Mock Elvis 객체 (클래스는 작성했다고 가정)
    assertTrue(...) // 테스트 로직
  }
}
```

#### 단점2: `리플렉션`으로 private 생성자 호출 가능

<details>
<summary>리플렉션이란?</summary>
<div markdown="1">

> 클래스로더를 통해 읽어온 클래스 정보(거울에 반사”된 정보)를 사용하는 기술

• 리플렉션을 사용해 클래스를 읽어오거나, 인스턴스를 만들거나, 메소드를 실행하거나,
필드의 값을 가져오거나 변경하는 것이 가능
</div>
</details>
예방 방법: 생성자를 수정하여 두 번째 객체가 생성될 때 예외 던지기 

```java
public class Elvis implements IElvis {

  public static final Elvis INSTANCE = new Elvis();
  private static boolean created; // 생성 여부 체크

  private Elvis() {
    if (created) { // 생성자로 인한 중복 생성을 막는다
      throw new UnsupportedOperationException("can't be created by constructor.");
    }
    created = true;
  }


  @Override
  public void leaveTheBuilding() {
    System.out.println("Whoa baby, I'm outta here!");
  }

  @Override
  public void sing() {
    System.out.println("I'll have a blue Christmas without you~");
  }
}
```


#### 단점3: 역직렬화 할 때 새로운 인스턴스 생길 수 있음
<details>
<summary>역직렬화란?</summary>
<div markdown="1">

> 특정 포맷의 데이터를 객체로 변환하는 행위

</div>
</details>

예방 방법: 모든 인스턴스 필드를 일시적(transient)라고 선언하고 readResolve 메서드를 제공(오버라이딩과 비슷하게 동작)

```java
import java.io.Serializable;

public class Elvis implements IElvis, Serializable { // Serializable 구현 필수

  public static final Elvis INSTANCE = new Elvis();
  private static boolean created; // 생성 여부 체크

  private Elvis() {
    if (created) { // 생성자로 인한 중복 생성을 막는다
      throw new UnsupportedOperationException("can't be created by constructor.");
    }
    created = true;
  }
  private transient String name; // transient 로 직렬화/역직렬화 시 null로 처리

  private Elvis() {
  }

  private Object readResolve() {
    return INSTANCE;
  }

}
```

### private 생성자 + 정적 팩터리 방식
```java
public class Elvis {
  private static final Elvis INSTANCE = new Elvis();
  private Elvis() { ... }
  public static Elvis getInstance() { return INSTANCE; }
//  public static Elvis getInstance() { return new Elvis(); } // 장점1: API (클라이언트 코드) 변경 없이 싱글턴이 아니게 변경 가능
  
}
```
단점: public static final 필드 방식와 동일

장점1: API를 바꾸지 않고도 싱글턴이 아니게 변경 가능
장점2: 정적 팩터리를 제네릭 싱글턴 팩터리로 변경 가능 -> 타입에 대한 유연성 제공
```java
public class GenElvis<T> {

  private static final GenElvis<Object> INSTANCE = new GenElvis<>();

  private GenElvis() {
  }

  @SuppressWarnings("unchecked")
  public static <T> GenElvis<T> getInstance() { // 여기서 <T> 2개는 GenElvis 클래스의 <T>와는 scope가 다르다
    return (GenElvis<T>) INSTANCE;
  }
}
```

장점3: 정적 팩터리의 메서드 참조를 공급자(Supplier)로 사용 가능(추후 내용 추가 필요)

### 열거 타입 방식
```java
public enum EnumElvis implements IElvis { // 인터페이스 구현 가능
  INSTANCE;


  @Override
  public void leaveTheBuilding() {
  }

  @Override
  public void sing() {
  }
}
```
대부분 상황에서 싱글턴을 만드는 가장 좋은 방법. 앞서 언급한 문제들의 예방 방법들을 도입할 필요 없이 enum이 자체적으로 방지한다.
