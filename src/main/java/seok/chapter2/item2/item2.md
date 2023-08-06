# 생성자에 매개변수가 많다면 빌더를 고려하라
## 개요
item1에서 보았던 정적 팩터리나 생성자은 결국 "선택적 매개변수가 많을 때 적절히 대응하기가 어렵다" 는 한계를 가지고 있다.

## 점층적 생성자 패턴과 한계
선배 프로그래머들은 이에 대응하기 위해 생성자가 연쇄적으로 또 다른 생성자를 호출하는 `점층적 생성자 패턴(telescoping constructor pattern)`을 즐겨 사용했다. 예시는 다음과 같다.
```java
public class NutritionFacts {
  private final int servingSize; // 필수 필드
  private final int servings; // 필수 필드
  private final int calories; // 선택 필드
  private final int fat; // 선택 필드

  
  public NutritionFacts(int servingSize, int servings) {
    this(servingSize, servings, 0); // 아래있는 생성자를 호출.
  }

  public NutritionFacts(int servingSize, int servings, int calories) {
    this(servingSize, servings, calories, 0);
  }

  public NutritionFacts(int servingSize, int servings, int calories, int fat) {
    this.servingSize = servingSize;
    this.servings = servings;
    this.calories = calories;
    this.fat = fat;
  }
}
```

하지만 보이는바와 같이 매개변수가 많아질수록 클라이언트 코드를 작성하거나 읽기가 어려워진다는 것을 쉽게 유추할 수 있다.
(실제로 코드 작성 시 파라미터를 헷갈려서 생성자를 잘못 작성했는데도 컴파일 시점에선 전혀 눈치채지 못했다..)

### 팁
인텥리제이에서는 `ctrl(or cmd) + p` 단축키를 통해 해당 메서드의 매개변수를 확인할 수 있다.

## 자바빈즈 패턴와 한계
`자바빈즈(JavaBeans)`란 JSP 에서 사용할 수 있는 자바 클래스이며, 생성자, 멤버 변수(필드)와 setter, getter 메서드를 가지고 있다.

매개변수가 많은 경우 setter 를 사용하면 보다 명시적으로 원하는 매개변수의 값을 설정할 수 있다.
```java
public class NutritionFacts {
  private int servingSize;
  public NutrionFacts() { };
  public void setServingSize(int val) {
    servingSize = val;  
  }
}
```
```java
  public static void main(String[] args) {
    NutritionFacts coke = new NutrionFacts();
    coke.setServing(240);
  }
```
하지만 자바빈즈 패턴에선 객체 하나를 만들기 위해 여러 메서드를 호출해줘야 하며, 그 전까진 객체는 일관성이 깨진 상태가 된다. 따라서, 자바빈즈 패턴으론 클래스를 불변으로 만드는 것이 불가능하다.

## 빌더 패턴
`빌더 패턴(Builder Pattern)`은 앞서 말한 점층적 생성자 패턴의 안전성(불변성)과 자바빈즈 패턴의 가독성을 모두 갖춘 특징을 가지고 있다.
빌더 패턴의 동작 순서는 다음과 같다.
* 클라이언트는 빌더 객체를 통해 객체를 생성
* 클라이언트는 빌더 객체를 최초로 생성 시 필수 파라미터 주입
* 클라이언트가 선택 파라미터를 주입하려면, 빌더 객체의 setter와 유사한 메서드를 호출
* 클라이언트는 객체를 얻기 위해 빌드 완성 메서드를 호출
```java
public class NutritionFacts {
	private final int servingSize;
	private final int servings;
	private final int calories;
	private final int fat;
	private final int sodium;
	private final int carbohydrate;

	public static class Builder {
		private final int servingSize;  // 필수
		private final int servings;     // 필수
		private int calories = 0;
		private int fat = 0;
		private int sodium = 0;
		private int carbohydrate = 0;

		public Builder(int servingSize, int servings) {
			this,servingSize = serginsSize;
			this.servings = servings;
		}

		public Builder fat(int val) {
			fat = val;
			return this;
		}

		public Builder sodium(int val) {
			sodium = val;
			return this;
		}

		public Builder carbohydrate(int val) {
			carbohydrate = val;
			return this;
		}

		public NutritionFacts build() {
			return new NutritionFacts(this);
		}
	}

	private NutirionFacts(Builder builder) {
		servingSize = builder.servingSize;
		servings = builder.servings;
		calories = builder.calories;
		fat = builder.fat;
		sodium = builder.fat;
		carbohydrate = builder.carbohydrate;
	}
}
```
```java
public static void main(String[]args){
    NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
        .calories(100)
        .sodium(35)
        .carbohydrate(27)
        .build();
    }
```