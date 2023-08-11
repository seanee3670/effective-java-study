package seok.chapter2.item5;

import java.util.List;

public class SpellChecker {

  private static final Dictionary dictionary = new DefaultDictionary();
  private SpellChecker() { }

  public static boolean isValid(String word) {
    // TODO SpellCheck Logic
    return dictionary.contains(word);
  }

  public static List<String> suggestions(String typo) {
    // TODO SpellCheck Logic
    return dictionary.closeWordsTo(typo);
  }

}
