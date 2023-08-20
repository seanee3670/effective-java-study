package seok.chapter2.item6;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeySetTest {

  @Test
  void keySetTest() {
    Map<String, Integer> map = new HashMap<>();
    map.put("apple", 1);
    map.put("pear", 2);

    Set<String> keySet1 = map.keySet();
    Set<String> keySet2 = map.keySet();

    Assertions.assertEquals(keySet1, keySet2);

  }
}
