
package approximations;
import java.io.*;


public class Config {
  public static PrintStream LZ78_out = System.out;
  public static boolean LZ78_verbose = false;

  public static PrintStream BISECTION_out = System.out;
  public static boolean BISECTION_verbose = false;

  public static PrintStream SEQUENTIAL_out = System.out;
  public static boolean SEQUENTIAL_verbose = false;

  public static PrintStream GREEDY_out = System.out;
  public static boolean GREEDY_verbose = false;

  public static boolean showProgress = false;
  public static final int numProgressStep = 60;


  public Config() {
  }
}