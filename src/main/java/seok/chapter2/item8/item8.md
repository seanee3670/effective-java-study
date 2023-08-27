# finalizer 와 cleaner 사용을 피해라
## `finalizer`와 `cleaner`
> 자바에서 지원하는 객체 소멸자로, 객체의 더 이상 사용되지 않는 자원을 해제하고 메모리 누수를 방지하기 위해 만들어졌다.

## 한계
하지만, `finalizer` 은 호출 시점이 보장되지 않아 언제 객체가 메모리에서 해제되는지 알기가 힘들다.

또한, `finalizer` 동작 중 발생한 예외는 무시되며, 처리할 작업이 남았더라도 종료된다.

java9 부터 도입된 `cleaner` 는 `finalizer` 대안으로 등장했는데, `cleaner`의 경우 자신의 스레드를 통제하기에
예외가 무시되는 문제가 발생하진 않지만, 그래도 여전히 예측이 힘들며 일반적으로 불필요하다.

둘 다 즉시 수행이 보장되지 않는다는 점은 동일하며, 따라서 상태를 영구적으로 수정하는 작업엔 절대 이들을 사용해선 안된다.

예시로, DB와 같은 공유자원에 영구 락(lock)을 해제를 이들에게 맡기면 전체 시스템이 서서히 멈출 가능성이 높다.

따라서 둘 다 사용이 권장되지 않는다..

이쯤되면 더 이상 알아보기도 싫어지는데, `finalizer`을 통해 보안 시스템을 우회할 수 있는 방법이 있다고 한다.

### `finalizer` 을 통한 보안 공격
```java
public class Account {

  private String accountId;

  public Account(String accountId) {
    this.accountId = accountId;

    if (accountId.equals("hackerId")) {
      throw new IllegalArgumentException("account with hackerId can not be proceeded");
    }
  }

  public void transfer(BigDecimal amount, String to) {
    System.out.printf("transfer %f from %s to %s\n", amount, accountId, to);
  }

}
```
일반적으로 계정이 hackerId 를 넘겨받게 되면 `IllegalArgumentException`가 발생한다.

`finalizer`을 통해 이를 우회할 수 있는 방법은 다음과 같다.

```java
public class HackerAccount extends Account {
  public HackerAccount(String accountId) {
    super(accountId);
  }

  @Override
  protected void finalize() throws Throwable {
    this.transfer(BigDecimal.valueOf(100), "chris");
    System.out.println("hacker with blockedId has transferred the money");
  }
}
```

```java
public class AccountTest {
  @Test
  void regular_account() {
    Account account = new Account("chris");
    account.transfer(BigDecimal.valueOf(10.4),"hello");
  }

  @Test
  void hacker_account() throws InterruptedException {
    Account hackerAccount = null;
    try {
      hackerAccount = new HackerAccount("hackerId");
    } catch (Exception exception) {
      System.out.println("exception occurred");
    }

    System.gc(); // GC 동작과 함께 hacker 의 finalizer 가 실행되어 (송금 기능을 하는)내부 코드가 실행된다.
    Thread.sleep(3000L);
  }
}
```

테스트 실행 결과, hackerId 를 통해 예외가 발생했음에도 불구하고 여전히 송금을 할 수 있었다.

막는 방법은 크게 2가지가 있겠다.

### 방어 방법1: `final`을 통해 객체 상속 금지
앞서 사용한 객체인 Account 를 final 제어자를 통해 상속을 금지시킬 수 있다.

따라서 hackerAccount 와 같은 하위 객체 생성이 불가능해지나, 확장성을 제한한다는 큰 단점이 있다.

### 방어 방법2:  `finalizer`  을 `final` 로 선언 
`final` 로 선언된 `finalizer`은 외부에서 변경이 불가능하며, 어떤 동작도 안하도록 설정해두면
`finalizer`을 통한 공격을 방어할 수 있다.


## finalize, cleaner 활용방법

### 1) 안정망(safety net)
`finalizer` 와 `cleaner`을 사용하면 자원 소요자가 close 메서드를 호출하지 않았을때 한번 더 GC 에게 자원을 회수할 수 있는 기회를 준다.
```java
public class Room implements AutoCloseable{
  private static final Cleaner cleaner = Cleaner.create();

  // 청소가 필요한 자원. 절대 Room을 참조해서는 안됨
  private static class State implements Runnable {
    int numJunkPiles; // 방(Room) 안의 쓰레기 수

    public State(int numJunkPiles) {
      this.numJunkPiles = numJunkPiles;
    }

    // close 메소드나 cleaner가 호출한다. -> 이 예제에선 메모리 해제 역할이라고 봐도 무방하다
    @Override
    public void run() {
      System.out.println("방 청소");
      numJunkPiles = 0;
    }
  }

  // 방의 상태. cleanable와 공유한다.
  private final State state;

  // cleanable 객체. 수거 대상이 되면 방을 청소한다.
  private final Cleaner.Cleanable cleanable;

  public Room(State state, Cleaner.Cleanable cleanable) {
    this.state = new State(state.numJunkPiles);
    this.cleanable = cleaner.register(this,state); // 룸 생성과 동시에 cleaner 에 state 등록
  }

  @Override
  public void close() throws Exception { 
    cleanable.clean();
  }
}
```

위 코드에서 State 의 run은 메모리를 정리하는 작업으로 해석해야한다. 그런 의미에서, State 의 run 메서드가 호출되는 상황은 크게 두 가지이다.
1) Room 의 close 메서드가 호출 되었을 때
2) (1번이 아니라면) GC 가 Room 을 호수할 때 cleaner 가 run 호출


참고로, State 인스턴스가 Room 인스턴스를 참조하게 될 경우, 순환참조가 생겨 GC가 Room 인스턴스를 회수하지 못한다.

해당 State 클래스가 중첩 클래스라면 자동적으로 바깥 객체의 참조를 갖게 되므로, 여기에서는 정적 중첩 클래스로 정의해주었다.



### 2) 네이티브 피어(native peer)
네이티브 피어는 자바 클래스 내부에서 네이티브 메서드를 호출하여 생성되는 네이티브 객체로, 기능을 위임하는 역할을 한다.
하지만 네이티브 피어는 일반 자바 객체와는 다르며, GC(Garbage Collector)가 이를 인식하지 못해 자바 피어만 회수하고 네이티브 객체까지는 회수하지 못하는 문제가 발생할 수 있다.

이러한 상황에서는 `finalizer`나 `cleaner`를 사용하여 문제를 해결할 수 있으나 성능에 저하가 발생할 수 있다.

## 대안

`AutoCloseable` 인터페이스를 구현한 자원을 `try-with-resources` 형식으로 닫아주자.

해당 내용은 다음 아이템인 Item9 에서 이어서 기록하겠다.