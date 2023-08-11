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

<details>
<summary>Lexicon이란?</summary>
<div markdown="1">

> java.lang.Cloneable, java.util.Map, java.io.Serializable 인터페이스를 구현한 클래스이며,
> HashMap 클래스를 상속받아 주어진 데이터 셋에서 유일한 단어들을 저장하는 자료구조이다.

• https://www.cs.cmu.edu/~youngwoo/projects/textminer/textminer-docs/doc/textminer/ds/Lexicon.html
</div>
</details>

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

두 예시 모두 하나의 사전 인스턴스로 여러 언어 사전 기능 요구에 대응해야할 때 실효성과 유연성이 떨어진다.

## 의존 객체 주입: 인스턴스 생성 시 파라미터로 자원 넘겨주기
다음 과정을 통해 보다 유연하고 테스트 용이한 객체 생성이 가능하다.
1) Dictionary 를 인터페이스로 정의
```java
public interface Dictionary {

  boolean contains(String word);
  
  List<String> closeWordsTo(String typo);

}
```
2) 기본 구현체인 DefaultDictionary 를 구현
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
3)  DI 가 가능한 SpellChecker 구현
```java
public class DISpellChecker {
  private final Dictionary dictionary;
  
  public DISpellChecker(Dictionary dictionary) {
    this.dictionary = dictionary;
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
4) 원하는 상황에 맞게 의존하는 자원(== 객체) 주입
```java
public class SpellCheckerTest {

  @Test
  void isValid() {
    DISpellChecker diSpellChecker = new DISpellChecker(new DefaultDictionary());
    diSpellChecker.isValid("test");
  }
}
```

## 의존 객체 주입 결론
 장점: 유연성과 테스트 용이성을 개선해준다
 
단점: 의존성이 늘어날 수록 연관관계를 파악하며 개발하는 것이 어려워진다
 
해결 방법 예시: Spring 과 같은 의존 객체 주입 프레임워크
&rarr; IoC 를 통해 DI 를 프레임워크에 위임한다(ex. @Configuration)