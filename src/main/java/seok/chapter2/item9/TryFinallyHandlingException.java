package seok.chapter2.item9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryFinallyHandlingException {
  // 코드 9-1 try-finally - 더 이상 자원을 회수하는 최선의 방책이 아니다! (47쪽)
  static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
      return br.readLine();
    } finally {
      br.close();
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(firstLineOfFile("pom.xml"));
  }
}