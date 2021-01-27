public class HelloReflection {
  
  public static void foo() {
    System.out.println("Running foo");
  }
 
  public static void bar() {
    System.out.println("Running bar");
  }
    
  public static void main(String[] args) {
    for (String arg : args) {
      try {
        HelloReflection.class.getMethod(arg).invoke(null);
      } catch (ReflectiveOperationException ex) {
        System.out.println("Exception running " + arg + ": " + ex.getClass().getSimpleName());
      }
    }
  }
}
