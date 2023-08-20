# 다 쓴 객체 참조를 해제하라

## 가비지 컬렉터(GC)

> 메모리 영역 중 더 이상 사용되지 않는 영역을 자동으로 탐지하여 해제하는 역할을 한다.

## 메모리 누수

> 프로그램이 작동하며 할당됐던 메모리가 더 이상 사용되지 않는 시점에서도 반환되지 않는 현상.

메모리 누수가 계속된다면 프로그램에 할당 가능한 메모리가 적어지면서 성능 저하 및 프로그램 비정상 작동과 같은 문제들이 발생할 수 있다.

GC를 통해 메모리 회수 대상이 되려면 해당 객체의 참조가 해제되어야 한다.

## 객체 참조 해제 방법

### 1) null 처리

```java
public class Stack {

  private Object[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;

  public Stack() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY];
  }

  public void push(Object e) {
    ensureCapacity();
    elements[size++] = e;
  }

  public Object pop() {
    if (size == 0) {
      throw new EmptyStackException();
    }
    Object result = elements[--size];
    elements[size] = null; // 다 쓴 객체 참조 해제
    return result;
  }

  /**
   * 원소를 위한 공간을 적어도 하나 이상 확보한다.
   * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
   */
  private void ensureCapacity() {
    if (elements.length == size)
      elements = Arrays.copyOf(elements, 2 * size + 1);
  }
}
```

### 2) WeakReference 사용

약한 참조(Weak Reference)
> java.lang.ref.WeakReference class 를 이용해서 생성된 참조를 의미한다.

약한 참조에서는 강한 참조가 없다면 약한 참조가 있더라도 GC에 의해 메모리가 수거된다.

참고로 강한 참조란 우리가 일반적으로 객체를 생성하고 변수에 할당하는 참조를 의미한다.

책에서는 `WeakHashMap`을 사용해서 캐시의 메모리 누수를 방지하는 법을 이야기한다.



```java
import java.lang.ref.WeakReference;

public class WeakReferenceExample {
  public static void main(String[] args) {
    // 객체와 해당 객체를 가리키는 약한 참조를 생성
    Object data = new Object();
    WeakReference<Object> weakRef = new WeakReference<>(data);

    // 강한 참조를 null로 설정하여 약한 참조만 객체를 가리키게 함
    data = null;

    // 이 시점에서 메모리가 부족하면 객체가 가비지 컬렉션될 수 있음
    // 명시적으로 가비지 컬렉션을 트리거하려 할 수 있지만 보장되는 것은 아님
    System.gc();

    // 약한 참조에서 객체를 검색
    Object retrieved = weakRef.get();

    if (retrieved == null) {
      System.out.println("객체가 가비지 컬렉션되었습니다."); // 해당 메세지가 출력된다.
    } else {
      System.out.println("객체는 여전히 접근 가능합니다.");
    }
  }
}
```

### 3) 유효 범위(scope) 밖으로 밀어내기
모든 변수들은 동작하는 유효 범위를 지닌다.
#### 지역변수(로컬변수):
메서드 내부에서만 동작

#### 멤버변수(인스턴스 변수)
클래스 내부에서 사용, private 이 아닌 경우 다른 클래스에서 참조변수로 사용 가능

#### 정적변수(클래스 변수):
클래스 내부에서 사용, private 이 아닌 경우 다른 클래스에서 클래스 이름으로 사용 가능

또한 생명주기 역시 다르다.
#### 지역변수(로컬변수):
메서드 호출 시 생성, 메서드 종료 시 소멸

#### 멤버변수(인스턴스 변수)
인스턴스 생성 시 힙 영역에 생성, GC가 메모리 수거 시 소멸

#### 정적변수(클래스 변수):
프로그램 시작과 동시에 데이터 영역에 생성, 프로그램 종료 시 소멸




