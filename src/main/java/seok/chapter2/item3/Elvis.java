package seok.chapter2.item3;

public class Elvis implements IElvis {

  public static final Elvis INSTANCE = new Elvis();
  private static boolean created; // 생성 여부 체크

  private Elvis() {
    if (created) { // 생성자로 인한 중복 생성을 막는다
      throw new UnsupportedOperationException("can't be created by constructor.");
    }
    created = true;
  }


  @Override
  public void leaveTheBuilding() {
    System.out.println("Whoa baby, I'm outta here!");
  }

  @Override
  public void sing() {
    System.out.println("I'll have a blue Christmas without you~");
  }
}
