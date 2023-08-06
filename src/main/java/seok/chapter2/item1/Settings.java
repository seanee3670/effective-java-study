package seok.chapter2.item1;

public class Settings {

  private boolean useAutoSteering;
  private boolean useABS;
  private Settings() {} // 외부에서 생성자 접근 방지

  private static final Settings SETTINGS = new Settings(); //
  public static Settings newInstance() {
    return SETTINGS;
  }
}
