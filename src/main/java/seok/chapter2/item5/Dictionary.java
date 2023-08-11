package seok.chapter2.item5;

import java.util.List;

public interface Dictionary {

  boolean contains(String word);

  List<String> closeWordsTo(String typo);

}
