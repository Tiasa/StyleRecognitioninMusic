import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class GrammarTest {
	private static String read(Reader reader) throws IOException
	  {
	      boolean over = false;
	      String content = "";
	      int bufferSize = 1024;
	      char[] buffer = new char[bufferSize];
	      while(!over)
	      {
	        int r = reader.read(buffer);
	        if (r!=-1)
	        {
	          String tmp = String.valueOf(buffer,0,r);
	          content = content + tmp;
	        }
	        if (r<bufferSize) over = true;
	      }
	      return content;
	  }
	public static Grammar getSmallestCFG (String x) {
		Grammar smallest = null;
		Grammar greedy = CFGGlobalAlgorithm.compress(x,Constants.Greedy);
		Grammar mostFrequent = CFGGlobalAlgorithm.compress(x, Constants.MostFrequent);
		Grammar longest = CFGGlobalAlgorithm.compress(x, Constants.Longest);
		if(greedy.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			if (longest.KolmogorovComplexity() < greedy.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = greedy;
			}
		} else {
			if (longest.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = mostFrequent;
			}
		}
		// For testing the longest pattern scheme
		// Delete later
		//return CFGGlobalAlgorithm.compress(x, Constants.Longest);
		return smallest;
	}
	public static Grammar getSmallestCFG(String x, Grammar g) {
		Grammar smallest = null;
		Grammar greedy = CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.Greedy);
		Grammar mostFrequent = CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.MostFrequent);
		Grammar longest = CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		if(greedy.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			if (longest.KolmogorovComplexity() < greedy.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = greedy;
			}
		} else {
			if (longest.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = mostFrequent;
			}
		}
		//return CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		return smallest;
	}
	public static double KofxANDyWithDef(int num, String file1Name, String file2Name, String separator) {
		double result = 0;
		FileReader file1 = null;
		FileReader file2 = null;
		try {
	      	  file1 = new FileReader(file1Name);

        }       
        catch(FileNotFoundException e1)
        {
            System.out.println("file not found \""+file1Name+"\" !");
            System.exit(1);
        }
	    try {
      	  file2 = new FileReader(file2Name);
        }       
        catch(FileNotFoundException e2)
        {
            System.out.println("file not found \""+file2Name+"\" !");
            System.exit(1);
        }
		switch (num) {
		// K(x,y) = K(x1000y)
		case 1:
			try {
		    	String string1 = read (file1);
				String string2 = read(file2);
				String combinedSeparableString = string1 + separator + string2;
			    Grammar combinedSeparableStringSmallest = getSmallestCFG(combinedSeparableString);
			    System.out.println(combinedSeparableStringSmallest.toString(true));
			    result = combinedSeparableStringSmallest.KolmogorovComplexity();
				
		    } catch (Exception e3) {
				System.out.println(e3.getMessage());
				System.exit(1);
			}
			break;
		// K(x,y) = K(x)+K(y)
		case 2:
			try {
		    	String string1 = read (file1);
				String string2 = read(file2);
				
				Grammar string1Smallest = getSmallestCFG (string1);
				System.out.println(string1Smallest.toString(true));
			    Grammar string2Smallest = getSmallestCFG(string2);
			    System.out.println(string2Smallest.toString(true));
			    result = string1Smallest.KolmogorovComplexity() + string2Smallest.KolmogorovComplexity();			
				
		    } catch (Exception e3) {
				System.out.println(e3.getMessage());
				System.exit(1);
			}
			break;
			
		}
		return result;
	}
	public static void main(String[] args) {
		if(args.length != 2)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
//		final String folder = args[0];
		final String fileName1 = args[0];
		final String fileName2 = args[1];
		try {
			KofxANDyWithDef(1,fileName1,fileName2," 160 ");		
			KofxANDyWithDef(2,fileName1,fileName2," 160 ");	
		//Grammar smallest1 = getSmallestCFG(string1);
//		Grammar smallest2 = getSmallestCFG(string2);
//		System.out.println(CFGGlobalAlgorithm.compress(string1, Constants.Greedy).toString(true));
//		System.out.println(CFGGlobalAlgorithm.compress(string1, Constants.MostFrequent).toString(true));
//		System.out.println(CFGGlobalAlgorithm.compress(string1, Constants.Longest).toString(true));
//		System.out.println(smallest2.toString(true));
//		Grammar mostFrequent = CFGGlobalAlgorithm.compress(string1, Constants.MostFrequent);
//		System.out.println(mostFrequent.toString(true));
//		Grammar longest = CFGGlobalAlgorithm.compress(string1, Constants.Longest);
//		System.out.println(longest.toString(true));
		//Grammar cond1 = getSmallestCFG(string1,smallest2);
		//Grammar cond2 = getSmallestCFG(string2,smallest1);
		//System.out.println(cond1.toString(true));
		//System.out.println(cond2.toString(true));
		
//			for (int step = 0; step < 3; step ++) {
//				for (int def=1;def<=2;def++) {
//					int numSteps = 1400 + (step * 100);
//					String fileName1 = folder + "/numSteps" + Integer.toString(numSteps) +"/1_dt.pitch";
//					String fileName2 = folder + "/numSteps" + Integer.toString(numSteps) +"/2_dt.pitch";
//					KofxANDyWithDef(def,fileName1,fileName2," 160 ");
//			}
//		}
//		System.out.println(Double.toString(KofxANDyWithDef(Integer.parseInt(args[0]),args[1],args[2]," 160  ")));
		} catch(Exception e) {
			System.out.println();
		}

	}

}
