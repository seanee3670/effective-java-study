package seok.chapter2.item8.finalizer_attack;

import java.math.BigDecimal;

public class HackerAccount extends Account {
  public HackerAccount(String accountId) {
    super(accountId);
  }

  @Override
  protected void finalize() throws Throwable {
    this.transfer(BigDecimal.valueOf(100), "chris");
    System.out.println("hacker with blockedId has transferred the money");
  }
}
