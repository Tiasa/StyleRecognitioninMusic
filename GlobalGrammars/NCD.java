

import java.io.*;

// Strictly assuming the input file is a music pitch file

public class NCD {
	public NCD() {
		
	}
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
		//Grammar longest = CFGGlobalAlgorithm.compress(x, Constants.Longest);
		if(greedy.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			//if (longest.KolmogorovComplexity() < greedy.KolmogorovComplexity()) {
			//	smallest = longest;
			//} else {
				smallest = greedy;
			//}
		} else {
			//if (longest.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			//	smallest = longest;
			//} else {
				smallest = mostFrequent;
			//}
		}
		// For testing the longest pattern scheme
		// Delete later
		//return CFGGlobalAlgorithm.compress(x, Constants.Longest);
		//return CFGGlobalAlgorithm.compress(x,Constants.Greedy);
		return smallest;
	}
	public static Grammar getSmallestCFG(String x, Grammar g) {
		Grammar smallest = null;
		Grammar greedy = CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.Greedy);
		Grammar mostFrequent = CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.MostFrequent);
		//Grammar longest = CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		if(greedy.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			//if (longest.KolmogorovComplexity() < greedy.KolmogorovComplexity()) {
			//	smallest = longest;
			//} else {
				smallest = greedy;
			//}
		} else {
			//if (longest.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			//	smallest = longest;
			//} else {
				smallest = mostFrequent;
			//}
		}
		//return CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.Greedy);
		return smallest;
	}
	// Using the definition NCD(x,y) = (C(xy)-min{C(x), C(y)})  / max{C(x), C(y)}
	public static double MingVityaniNCDApproximation(String file1Name, String file2Name) {

		FileReader file1 = null;
		FileReader file2 = null;
		
		double ncd = 0;
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
	    try {
	    
			String string1 = read (file1);
			String string2 = read(file2);
			String combinedString = string1 + " " + string2;
			// Getting the smallest grammar for C(xy)
		    Grammar combinedSmallest = getSmallestCFG(combinedString);
		    Grammar string1Smallest = getSmallestCFG (string1);
		    //System.out.println(file1Name);
		    //System.out.println(string1Smallest.toString(true));
		    Grammar string2Smallest = getSmallestCFG(string2);
		    //System.out.println(file2Name);
		    //System.out.println(string2Smallest.toString(true));
		    ncd = (combinedSmallest.KolmogorovComplexity() - Math.min(string1Smallest.KolmogorovComplexity(), string2Smallest.KolmogorovComplexity())) / Math.max(string1Smallest.KolmogorovComplexity(), string2Smallest.KolmogorovComplexity());
	    }
	    catch (Exception e3) {
			System.out.println(e3.getMessage());
			System.exit(1);
		}
		return ncd;
	    

	}
	
	// Using the definition NCD(x,y) = max {C(x|y*),C(y|x*)} / max{C(x), C(y)}
	public static double MingVityaniNCD (String file1Name, String file2Name) {
		double ncd = 0;
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
	    try {
	    	String string1 = read (file1);
			String string2 = read(file2);
			Grammar string1Smallest = getSmallestCFG (string1);
		    Grammar string2Smallest = getSmallestCFG(string2);
		    
		    Grammar string1Cond = getSmallestCFG(string1,string2Smallest);
		    Grammar string2Cond = getSmallestCFG(string2, string1Smallest);
		    ncd = (Math.max(string1Cond.KolmogorovComplexity(), string2Cond.KolmogorovComplexity())) / (Math.max(string1Smallest.KolmogorovComplexity(), string2Smallest.KolmogorovComplexity()));
			
			
	    } catch (Exception e3) {
			System.out.println(e3.getMessage());
			System.exit(1);
		}
		return ncd;
	}
	
	// Using the definition NCD(x,y) = (C(x|y*) + C(y|x*))/C(x,y)
	public static double MingVityaniSumOfConditionalsNCD (String file1Name, String file2Name, String separator) {
		double ncd = 0;
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
	    try {
	    	String string1 = read (file1);
			String string2 = read(file2);
			
			Grammar string1Smallest = getSmallestCFG (string1);
		    Grammar string2Smallest = getSmallestCFG(string2);
		    
		    Grammar string1Cond = getSmallestCFG(string1,string2Smallest);
		    Grammar string2Cond = getSmallestCFG(string2, string1Smallest);
		    
		    
			String combinedSeparableString = string1 + separator+ string2;
		    Grammar combinedSeparableStringSmallest = getSmallestCFG(combinedSeparableString);
		    
		    
		    ncd = (string1Cond.KolmogorovComplexity()+string2Cond.KolmogorovComplexity())/combinedSeparableStringSmallest.KolmogorovComplexity();
			
			
	    } catch (Exception e3) {
			System.out.println(e3.getMessage());
			System.exit(1);
		}
	    
		return ncd;
	}
	// Using the definition NCD(x,y) = (C(x|y*) + C(y|x*))/C(x)+C(y)
	public static double MySumOfConditionalsNCD(String file1Name, String file2Name) {
		double ncd = 0;
		FileReader file1 = null;
		FileReader file2 = null;
	    try {
      	  file1 = new FileReader(file1Name);

        }       
        catch(FileNotFoundException e2)
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
	    try {
	    
	    	String string1 = read (file1);
			String string2 = read(file2);
			
			Grammar string1Smallest = getSmallestCFG(string1);
		    Grammar string2Smallest = getSmallestCFG(string2);
		    
		    Grammar string1Cond = getSmallestCFG(string1,string2Smallest);
		    Grammar string2Cond = getSmallestCFG(string2, string1Smallest);
		    
		    
		    ncd = (string1Cond.KolmogorovComplexity()+string2Cond.KolmogorovComplexity())/(string1Smallest.KolmogorovComplexity()+string2Smallest.KolmogorovComplexity());
			
			
	    }
	    catch (Exception e3) {
			System.out.println(e3.getMessage());
			System.exit(1);
		}
		return ncd;
	}

}
