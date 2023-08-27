# `try-finally` 보다는 `try-with-resources` 를 사용하라

자바 라이브러리엔 close 메서드를 호출해서 명시적으로 자원을 닫아야하는 경우가 많다.

`InputStream`, `OutputStream`, `java.sql.Connection` 등의 예시들을 책에서 들고 있는데,

`BufferedReader` 와 `BufferedWriter` 을 써봤다면 이해가 갈 거라고 생각한다.

물론 GC가 메모리 회수를 시도하겠지만, 여러 자원이 사용된다면 정상적으로 동작하지 않을 가능성이 있다.

이런 이유로 자원을 명시적으로 닫기 위해 `try-finally` 가 전통적으로 사용되어져 왔는데,
저자는 `try-with-resources`를 더욱 권장한다. `try-finally` 는 과연 무엇이 문제였을까?

## try-finally 단점

### 자원 수와 반비례하는 가독성

```java
public class TryFinallyExample {

  static final int BUFFER_SIZE = 999;

  static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
      OutputStream out = new FileOutputStream(dst);
      try {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) >= 0) {
          out.write(buf, 0, n);
        }
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }
  }

}
```
닫아줘야할 자원이 늘어날수록 코드의 depth 가 증가하며, 이에 따라 close 메서드를 제대로 구현하는 것 역시 어려워진다.

### 예외가 "먹힌다"

```java
public class BadBufferedReader extends BufferedReader {
  public BadBufferedReader(Reader in, int sz) {
    super(in, sz);
  }

  public BadBufferedReader(Reader in) {
    super(in);
  }

  @Override
  public String readLine() throws IOException {
    throw new CharConversionException();
  }

  @Override
  public void close() throws IOException {
    throw new StreamCorruptedException();
  }
}
```
BadBufferedReader 이 위와 같이 정의되어 있다면, 아래 코드에서는 과연 어떤 예외가 보여질까?
```java
public class TopLine {
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
```

정답은 br.close 할때 발생되는 StreamCorruptedException() 가 보여진다.

그렇다면 br.readLine() 시에 발생되는 charConversionException() 은 어디간걸까?

이것이 책에서 언급하는 try-finally 의 미묘한 결점이다. 가장 마지막에 실행되는 예외만 보여지고, 처음에 발생한 예외는 "먹혀진다."

try-finally 형식으로 이 문제를 해결하려면 코드가 다소 난잡해질 것이다.

그렇다면 try-with-resources 는 어떨까?
```java
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
```

우리가 자연스레 예상하는대로 가장 첫번째 예외가 먼저 보여지며, 그 다음 예외 역시 Stack Trace 에서 발견할 수 있다.

`try-with-resources` 를 사용하면서도 catch 와 finally 역시 사용할 수 있으니 훌륭한 상위호환이라고 할 수 있다.

참고로 `AutoCloseable` 를 구현한 자원과 함께 `try-with-resources` 를 사용할때는 명시적으로 close 메서드를 호출하지 않아도 자원을 닫아주며,

위 예제에서 쓰인 `BufferedReader` 은 `Reader` 을 상속받았으며, `Reader` 은 'Closeable' 을 구현하고 있고,  'Closeable' 은 'AutoCloseable' 을 상속받는다.

`Cloesable` 은 I/O 자원을 다룰때 주로 쓰이며, 일반적인 예외인 `Exception`을 던지는 `AutoCloseable` 과 달리 보다 I/O 자원을 사용하는 상황에 적절한 `IOException` 을 던진다.

