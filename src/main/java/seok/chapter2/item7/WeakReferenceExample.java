package seok.chapter2.item7;

import java.lang.ref.WeakReference;

public class WeakReferenceExample {
  public static void main(String[] args) {
    // 객체와 해당 객체를 가리키는 약한 참조를 생성
    Object data = new Object();
    WeakReference<Object> weakRef = new WeakReference<>(data);

    // 강한 참조를 null로 설정하여 약한 참조만 객체를 가리키게 함
    data = null;

    // 이 시점에서 메모리가 부족하면 객체가 가비지 컬렉션될 수 있음
    // 명시적으로 가비지 컬렉션을 트리거하려 할 수 있지만 보장되는 것은 아님
    System.gc();

    // 약한 참조에서 객체를 검색
    Object retrieved = weakRef.get();

    if (retrieved == null) {
      System.out.println("객체가 가비지 컬렉션되었습니다.");
    } else {
      System.out.println("객체는 여전히 접근 가능합니다.");
    }
  }
}
