# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
자원을 직접 명시 == 자원을 직접 생성(ex. new 연산자 사용)

## 정적 유틸리티 클래스를 잘못 사용한 예

```java
import java.util.Dictionary;

public class SpellChecker {

  private static final Dictionary dictionary = new Dictionary();

  private SpellChecker() {
  } // 인스턴스화 방지

  public static boolean isValid(String word) {...}

  public static List<String> suggestions(String typo) {...}

  // 사용 예시
  // SpellChecker.isValid(...);
}
```

## 싱글턴을 잘못 사용한 예

```java
import java.util.Dictionary;

public class SpellChecker {

  private final Dictionary dictionary = new Dictionary();

  private SpellChecker() {
  }

  public static SpellChecker INSTANCE = new SpellChecker(...);

  public static boolean isValid(String word) {...}

  public static List<String> suggestions(String typo) {...}
  // 사용 예시
  // SpellChecker.INSTANCE.isValid(...);
}
```

두 예시 모두 하나의 사전 인스턴스로 여러 언어  사전 기능 요구에 대응해야할 때 실효성과 유연성이 떨어진다.

## 의존 객체 주입: 인스턴스 생성 시 파라미터로 자원 넘겨주기
다음 과정을 통해 보다 유연하고 테스트 용이한 객체 생성이 가능하다.
#### 1) Dictionary 를 인터페이스로 정의
```java
public interface Dictionary {

  boolean contains(String word);
  
  List<String> closeWordsTo(String typo);

}
```
#### 2) 기본 구현체인 DefaultDictionary 를 구현
```java
public class DefaultDictionary implements Dictionary {
  
  @Override
  public boolean contains(String word) {
    return false;
  }

  @Override
  public List<String> closeWordsTo(String typo) {
    return null;
  }
}
```
#### 3)  DI 가 가능한 SpellChecker 구현
```java
public class DISpellChecker {

  private final Dictionary dictionary;

  public DISpellChecker(Dictionary dictionary) {
    this.dictionary = dictionary;
  }
  // 파라미터에 함수형 인터페이스가 넘어오면 supplier 로 감싸서 객체 반환
  public DISpellChecker(Supplier<Dictionary> dictionarySupplier) {
    this.dictionary = dictionarySupplier.get();
  }

  public boolean isValid(String word) {
    // TODO SpellCheck Logic
    return dictionary.contains(word);
  }

  public List<String> suggestions(String typo) {
    // TODO SpellCheck Logic
    return dictionary.closeWordsTo(typo);
  }
}

```
#### 4) 원하는 상황에 맞게 의존하는 자원(== 객체) 주입

```java
import org.junit.jupiter.api.Test;

public class SpellCheckerTest {

  @Test
  void isValid1() {
    DISpellChecker diSpellChecker = new DISpellChecker(new DefaultDictionary());
    diSpellChecker.isValid("test");
  }

  @Test
  void isValid2() {
   DISpellChecker diSpellChecker = new DISpellChecker(() -> new DefaultDictionary());
   // 혹은
   // DISpellChecker diSpellChecker = new DISpellChecker(DefaultDictionary::new);
   diSpellChecker.isValid("test");
  }
}
```
#### Supplier 로 구현한 DISPellChecker 생성자의 내부 실행 순서
1. DISpellChecker의 인스턴스를 생성 (diSpellChecker)
2. DISpellChecker의 생성자를 DefaultDictionary::new 메소드 참조와 함께 호출
    3. DISpellChecker 생성자 내부:
        4. dictionarySupplier.get()를 호출:
            5. DefaultDictionary의 생성자를 실행
            6. DefaultDictionary의 인스턴스 생성
        7. 생성된 DefaultDictionary 인스턴스를 dictionary 필드에 할당
8. 이제 diSpellChecker 인스턴스는 생성된 DefaultDictionary 인스턴스를 참조


## 의존 객체 주입 결론
장점: 유연성과 테스트 용이성을 개선해준다
 
단점: 의존성이 늘어날 수록 연관관계를 파악하며 개발하는 것이 어려워진다
 
해결 방법 예시: Spring 과 같은 의존 객체 주입 프레임워크
&rarr; IoC 를 통해 DI 를 프레임워크에 위임한다(ex. @Configuration)