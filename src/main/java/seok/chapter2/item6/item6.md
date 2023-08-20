# 불필요한 객체 생성을 피하라

객체 생성은 비싸니 싱글턴이 좋다는 이야기가 절대 아니다.
객체 생성할 필요가 없는데 굳이 매번 객체를 생성하진 말라는 말이다.

## 재사용이 가능한 불변객체

```java
String str1=new String("hello"); // 매번 객체 생성
    String str2="hello"; // 재활용 가능
```

문자열은 인스턴스 생성 후 리터럴로 캐싱되어 같은 값을 사용하는 모든 코드가 동일한 인스턴스를 재사용하게 된다.

### 리터럴(literal) vs 상수(constant)

리터럴: 고정된 값을 계산하지 않고 나타내는 표기법.
데이터 그 자체를 말하며, 숫자, 문자, 문자열, 부울, null 리터럴 등이 있다.

상수: 한 번 할당된 후에는 값을 변경할 수 없는 변수.
프로그램 실행 동안 고정되도록 보장하는 방법이다.
일반적으로 자바에선 `final` 키워드를 사용해서 상수를 정의한다.

## 정적 팩터리 메서드

item1 에서 소개한 `정적 팩터리 메서드` 을 사용하면 `static final` 로 선언한 인스턴스를 재사용할 수 있다.

```java
Boolean flag1=new Boolean("true");
    Boolean flag2=Boolean.valueOf("true");
```

참고로 위에 사용한 `Boolean(String)` API는 java 9 버전부터 deprecated 되어 사용이 권장되지 않는다.

## 생성 비용이 비싼 객체

객체 중에는 생성 비용이 비싼 경우도 있을 것이다.
책에서 소개된 예시는 정규표현식용 객체인 `Pattern` 이다.

```java
public class RomanNumerals {

  // BAD
  static boolean isRomanNumeralSlow(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
        + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
  }

  private static final Pattern ROMAN = Pattern.compile(
      "^(?=.)M*(C[MD]|D?C{0,3})"
          + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

  static boolean isRomanNumeralFast(String s) {
    return ROMAN.matcher(s).matches();
  }
}
```

문자열 하나로 정규식을 표현한다면 `Pattern` 메서드 내에서 최적화가 되어 상관없겠지만,
보통은 한 번 쓰여지고 GC에 의해 메모리 회수된다.
따라서, 해당 패턴을 `static final` 로 정적 초기화 과정에서 미리 생성하여 재사용하는 것이 효율적이다.

추가적으로, 복잡한 자료구조를 가지고 있거나 DB 또는 네트워크 커넥션을 수행하는 객체들 역시 생성 비용이 비쌀 것이다.
코딩테스트의 경우에도 일반적으로 자료구조 인스턴스를 매번 생성하는 것보다 clear 해주어 재사용하는 편이 훨씬 효율적이라는 것을 알 수 있다.

## 어댑터

뷰(view) 라고도 불리는 어댑터는 제 2의 인터페이스 역할을 하며, 실제 작업을 뒷단 객체에 위임한다.
예를 들어, `Map` 인터페이스인 `keySet` 는 Map 의 값들에 대한 Collection view 이다

```java
public class KeySetTest {

  @Test
  void keySetTest() {
    Map<String, Integer> map = new HashMap<>();
    map.put("apple", 1);
    map.put("pear", 2);

    Set<String> keySet1 = map.keySet();
    Set<String> keySet2 = map.keySet();

    Assertions.assertEquals(keySet1, keySet2); // 테스트 통과
  }
}
```

따라서, 같은 Map 에 대한 view 라면 동일한 인스턴스를 사용한다.

## 오토박싱(auto-boxing)

> 컴파일러가 기본 타입을 그에 대응하는 래퍼 클래스의 객체로 변환해주는 행위

```java
public class AutoBoxingPractice {

  private static long sum() {
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++) {
      sum += i; // 불필요한 오토박싱 발생
    }
    return sum;
  }
}
```
오토박싱이 발생할때마다 해당 래퍼 클래스의 인스턴스가 생성되니 항상 유의해서 사용하자.

##