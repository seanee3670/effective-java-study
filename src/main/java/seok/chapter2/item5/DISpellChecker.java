package seok.chapter2.item5;

import java.util.List;
import java.util.function.Supplier;

public class DISpellChecker {
  private final Dictionary dictionary;
  public DISpellChecker(Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  // 함수형 인터페이스로 파라미터를 넘겨받아 객체 생성
  public DISpellChecker(Supplier<Dictionary> dictionarySupplier) {
    this.dictionary = dictionarySupplier.get();
  }

  // 와일드카드를 이용해서 하위 클래스까지 허용
  //   public DISpellChecker(Supplier<? extends DefaultDictionary> dictionarySupplier) {
  //    this.dictionary = dictionarySupplier.get();
  //  }

  public boolean isValid(String word) {
    // TODO SpellCheck Logic
    return dictionary.contains(word);
  }

  public List<String> suggestions(String typo) {
    // TODO SpellCheck Logic
    return dictionary.closeWordsTo(typo);
  }


}
