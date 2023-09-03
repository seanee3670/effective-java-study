# equals는 일반 규약을 지켜 재정의 하라

### public boolean equals(Object obj)
> Indicates whether some other object is "equal to" this one.

말 그대로 객체가 비교 대상인 객체와 동일한지 확인하는 메서드이다.
```java
public boolean equals(Object obj) {
        return (this == obj);
}
```
#### ==
> compares the memory location or the references of the two objects stored in the heap memory

모든 자바 객체는 Object 를 상속하고 있으니, equals 메서드 사용이 가능하다.

때에 따라서 재정의(override) 해야할 수 있는데, 그 이전에 재정의 하지 않는게 권장되는 경우부터 살펴보자.

## equals 를 재정의 하지 않는 것이 최선인 경우

#### 1) 각 인스턴스가 본질적으로 고유하다
ex) Thread

#### 2) 인스턴스의 '논리적 통치성(logical equality)'을 검사할 일이 없다
ex) java.util.regex.Pattern &rarr; 논리적 동치성을 필요하지 않다면 equals 재정의하지 않아도 된다.

#### 3) 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다
ex) Set 은 AbstractSet 에서 구현한 equals 를 상속받아서 쓴다.

#### 4) 클래스가 private이나 package-private이고 equals를 호출할 일이 없다
사실 호출을 못한다.

## equals 를 재정의해야 하는 경우

객체 식별성(object identity)이 아닌 '**논리적 동치성**'을 확인해야 하는 경우

Integer 이나 String 과 같은 값 클래스가 여기에 해당한다.

동치관계(equivalence relation): 두 객체가 질적으로 서로 같다는 것을 의미. 반사성, 대칭성, 추이성을 만족해야한다.

#### 반사성(reflexivity)
> null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true다.
#### 대칭성(symmetry)
> null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true다.
#### 추이성(transitivity)
> null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고, y.equals(z)도 true면 x.equals(z)도 true다.
#### 일관성(consistency)
> null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true이거나 false다.
#### null-아님
> null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다.

## 대안
#### 1) 구글의 AutoValue 프레임워크

클래스를 작성할 때 getter, equals(), hashCode(), toString() 과 같은 메서드를 반복적으로 정의 또는 재정의해줘야 하는 경우 생긴다.

이를 소위 말하는 보일러플레이트(boiler plate) 코드라고 한다.

책에서 소개된 AutoValue 는 이러한 보일러플레이트를 줄여줄 오픈소스 프레임워크다.
에너테이션 프로세서 기반으로 새로운 POJO 클래스를 생성하는 식으로 동작한다.

```java
package autovalue.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Person {
    public abstract String lastName();

    public abstract String firstName();

    public abstract Integer age();

    public static Person create(String lastName, String firstName, Integer age) {
        return builder().lastName(lastName).firstName(firstName).age(age).build();
    }

    public static Builder builder() {
        return new AutoValue_Person.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder lastName(String lastName);
        public abstract Builder firstName(String firstName);
        public abstract Builder age(Integer age);

        public abstract Person build();
    }
}
```
아래에 설명할 lombok 보다 코드양이 더 많은 것을 한눈에 알 수 있다.

다만, 빌더 생성 시 메서드명을 정할 수 있는 등 유연성을 제공한다.

#### 2) lombok
마찬가지 이유에서 사용되는 오픈소스이며, 스프링과 함께 사용되는 경우가 많다.

단, AutoValue 와 다르게 해당 클래스의 bytecode 를 변경해서 코드를 대신 직접 작성해준다. 

```java
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Person {
  @NonNull
  @Getter
  private final String lastName;
  @NonNull
  @Getter
  private final String firstName;
  @NonNull
  @Getter
  private final Integer age;
}
```

#### 3) 자바 Record
https://www.baeldung.com/java-record-keyword#5-tostring
자바 14 버전에서 소개되었고 16 버전부터 공식 기능으로 지원하는 기능이다.

Record는 마찬가지로 보일러플레이트 코드를 줄이고자 만들어진 Object 를 상속하는 특별한 클래스이다.

Object 를 상속받는 추상클래스 Record 는 아래와 같은 3개의 추상 메서드를 제공한다.

또한, 모든 필드를 사용하는 생성자, 게터, 모든 필드의 타입과 값을 비교해서 검증하는 equals, hashCode