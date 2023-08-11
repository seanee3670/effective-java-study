package seok.chapter2.item3;

public class GenElvis<T> {

  private static final GenElvis<Object> INSTANCE = new GenElvis<>();

  private GenElvis() {
  }

  @SuppressWarnings("unchecked")
  public static <T> GenElvis<T> getInstance() { // 1번째 <T>: 메서드 타입,  2번째 <T>: 리턴 타입
    return (GenElvis<T>) INSTANCE;
  }
}
