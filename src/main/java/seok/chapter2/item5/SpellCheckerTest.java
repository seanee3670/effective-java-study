package seok.chapter2.item5;

import org.junit.jupiter.api.Test;

public class SpellCheckerTest {

  @Test
  void isValid() {
    DISpellChecker diSpellChecker = new DISpellChecker(new DefaultDictionary());
    diSpellChecker.isValid("test");
  }
}
