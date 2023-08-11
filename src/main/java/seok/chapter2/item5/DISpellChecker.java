package seok.chapter2.item5;

import java.util.List;

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
