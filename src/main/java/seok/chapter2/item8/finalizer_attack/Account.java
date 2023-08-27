package seok.chapter2.item8.finalizer_attack;

import java.math.BigDecimal;

public class Account { // final 을 통해 상속 금지 가능

  private String accountId;

  public Account(String accountId) {
    this.accountId = accountId;

    if (accountId.equals("hackerId")) {
      throw new IllegalArgumentException("account with hackerId can not be proceeded");
    }
  }

  public void transfer(BigDecimal amount, String to) {
    System.out.printf("transfer %f from %s to %s\n", amount, accountId, to);
  }

}
