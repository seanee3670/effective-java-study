package seok.chapter2.item9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesHandlingException {
  static String firstLineOfFile(String path) throws IOException {
    try(BufferedReader br = new BadBufferedReader(new FileReader(path))) {
      return br.readLine();
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println(firstLineOfFile("pom.xml"));
  }
}
