package seok.chapter2.item8.finalizer_attack;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class AccountTest {
  @Test
  void regular_account() {
    Account account = new Account("chris");
    account.transfer(BigDecimal.valueOf(10.4),"hello");
  }

  @Test
  void hacker_account() throws InterruptedException {
    Account hackerAccount = null;
    try {
      hackerAccount = new HackerAccount("hackerId");
    } catch (Exception exception) {
      System.out.println("exception occurred");
    }

    System.gc(); // GC 동작과 함께 hacker 의 finalizer 가 실행되어 내부 코드가 실행된다.
    Thread.sleep(3000L);
  }
}
