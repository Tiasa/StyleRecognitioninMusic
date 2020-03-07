import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class ModelContemplation {
	private static long axiom = -1; // axiom meaning the starting rule
	public ModelContemplation() {}
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
	private static int countSigma(Long[] s) {
		HashSet<Integer> hs = new HashSet<Integer>();
		for (Long l:s) {			
			hs.add(l.intValue());
		}
		return hs.size();
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
		Grammar[] greedy = ModelContemplation.compressWithPreviousGrammar(x, g, Constants.Greedy);
		Grammar[] mostFrequent = ModelContemplation.compressWithPreviousGrammar(x, g, Constants.MostFrequent);
		Grammar[] longest = ModelContemplation.compressWithPreviousGrammar (x, g, Constants.Longest);
		if(greedy[0].KolmogorovComplexity() < mostFrequent[0].KolmogorovComplexity()) {
			if (longest[0].KolmogorovComplexity() < greedy[0].KolmogorovComplexity()) {
				smallest = longest[0];
			} else {
				smallest = greedy[0];
			}
		} else {
			if (longest[0].KolmogorovComplexity() < mostFrequent[0].KolmogorovComplexity()) {
				smallest = longest[0];
			} else {
				smallest = mostFrequent[0];
			}
		}
		//return CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		return smallest;
	}
	public static Grammar getSmallestModelCFG(String x, Grammar g) {
		Grammar smallest = null;
		Grammar[] greedy = ModelContemplation.compressWithPreviousGrammar(x, g, Constants.Greedy);
		Grammar[] mostFrequent = ModelContemplation.compressWithPreviousGrammar(x, g, Constants.MostFrequent);
		Grammar[] longest = ModelContemplation.compressWithPreviousGrammar (x, g, Constants.Longest);
		if(greedy[0].KolmogorovComplexity() < mostFrequent[0].KolmogorovComplexity()) {
			if (longest[0].KolmogorovComplexity() < greedy[0].KolmogorovComplexity()) {
				smallest = longest[1];
			} else {
				smallest = greedy[1];
			}
		} else {
			if (longest[0].KolmogorovComplexity() < mostFrequent[0].KolmogorovComplexity()) {
				smallest = longest[1];
			} else {
				smallest = mostFrequent[1];
			}
		}
		//return CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		return smallest;
	}
	private static void buildCompressionDictionaryForOneRule(ArrayList<Long> dict, Grammar input , Long leftPart) {
		Long[] RHS = input.getRule(leftPart);
		for (Long i:RHS) {
			if (i < 0 && !dict.contains(i)) {
				buildCompressionDictionaryForOneRule(dict,input,i);
			}
		}
		// For now we are removing the starting rule that can regenerate xy
		// because then you need constant bits to reconstruct x and the dictionary 
		// is not useful
		if (leftPart != input.getAxiom() && !dict.contains(leftPart)) {
			dict.add(leftPart);
		}
	}
	private static void buildCompressionDictionary(ArrayList<Long> dict, Grammar input) {
		Vector<Rule> allRules = input.getRules();
		for (Rule r: allRules) {
			Long leftPart =  (Long)r.fst();
			if (!dict.contains(leftPart)) {
				buildCompressionDictionaryForOneRule(dict, input ,leftPart);
			}
		}
	}
	public static Grammar[] compressWithPreviousGrammar(String input, Grammar inputGrammar, String algorithm)
	  {
		Grammar result = null;
		Grammar allRulesGrammar = new Grammar("Mixed");
	    switch (algorithm.toUpperCase()) {
    	case Constants.Greedy:
	  		result = new Grammar("Greedy");
	  		break;
    	case Constants.MostFrequent:
	  		result = new Grammar("Most Frequent");
	  		break;
	  	case Constants.Longest:
	  		result = new Grammar("Longest Recurring");
	  		break;
	  	default:
  		System.out.println("Wrong algorithm");
  		System.exit(1);
	    }
	    
	    
	    String[] inputArray = input.trim().split("\\s+"); // For music
	    result.addRule(axiom, new Long[inputArray.length]);
	    
	    for (int k=0;k<inputArray.length;k++) {
	    	result.getRule(axiom)[k] = new Long(Integer.parseInt(inputArray[k])); // For music
	    }
	    result.setSigmaSize(countSigma(result.getRule(axiom)));
	    
	  // First compress as much as possible with the given grammar
	  // Better to implement as a stack
	  ArrayList<Long> dictionary = new ArrayList<Long>();
	  buildCompressionDictionary(dictionary, inputGrammar);
	  Iterator<Long> dictionaryIterator = dictionary.iterator();
	  // Exhaust the dictionary
	  // Here we need to check how much can we compress with the given grammar
	  long currentNT = axiom-1;
	  long inputGrammarLastNT = currentNT;
	  
	  while(dictionaryIterator.hasNext()) {
		  currentNT = dictionaryIterator.next();
		  Rule inputRule = inputGrammar.getRuleObject(currentNT);
		  Long[] RHS = (Long []) inputRule.snd();
		  PatriciaTree tree = new PatriciaTree(result);
		  Pattern<Long> pattern = tree.findPatternOccurences(RHS);
		  if (pattern!= null && pattern.frequency() > 1) {
			  tree.smartReplace(pattern, currentNT);
			  allRulesGrammar.addRule(currentNT, RHS, inputRule.getNumberOfGrammarsUsingThisRule()+1);
		  } else {
			  allRulesGrammar.addRule(currentNT, RHS, inputRule.getNumberOfGrammarsUsingThisRule());
		  }
		  if (currentNT < inputGrammarLastNT) {
			  inputGrammarLastNT = currentNT;
		  }
	  }
	  
	  boolean moreReductionPossible = true;
	  currentNT = inputGrammarLastNT - 1; // we want to start where input grammar left off
	  while(moreReductionPossible) {
	    	PatriciaTree tree = new PatriciaTree(result); // Patricia tree
	    	Pattern<Long> pattern = null;
	    	switch (algorithm.toUpperCase()) {
	    	case Constants.Greedy:
	    		pattern = tree.getGreedyPattern();
	    		break;
	    	case Constants.MostFrequent:
	    		pattern = tree.getMostFrequentPattern();
	    		break;
	    	case Constants.Longest:
	    		pattern = tree.getLongestPattern();
	    		break;
	    	}
	    	if (pattern == null) {
	    		moreReductionPossible = false;
	    	} else {
	    		int totalReductionInGrammarSize = (pattern.length()-1) * (pattern.frequency()-1) - 2;
	    		if (totalReductionInGrammarSize <=0) {
	    			moreReductionPossible = false;
	    		} else { 
		    		Long[] RHS = tree.smartReplace(pattern, currentNT);
		    		result.addRule(currentNT, RHS);
		    		allRulesGrammar.addRule(currentNT, RHS, 1);
		    		currentNT--;
	    		}
	    	}
	    }
	    result.setAxiom(axiom);
	    allRulesGrammar.setAxiom(axiom);
	    return new Grammar[] {result, allRulesGrammar};
	  }
	// Threshold is expressed in percentage
	public static Grammar ConstrainedMaximumLikelyModel(String corpus, int threshold) {
		File folder = new File(corpus);
		File[] listOfFiles = folder.listFiles();
		Grammar result = null;
		FileReader fileReader = null;
		for (File file: listOfFiles) {
				if (file.getAbsolutePath().contains(".pitch")) {
				String modelSetElementString = null;
				try {
					fileReader = new FileReader(file.getAbsolutePath());
					modelSetElementString = read(fileReader);
		        }         
		        catch(FileNotFoundException e1)
		        {
		            System.out.println("file not found \""+file.getAbsolutePath()+"\" !");
		            System.exit(1);
		        } catch (IOException e2) {
					System.out.println(e2.getMessage());
					System.exit(1);
				}
				
				if (result == null) {
					result = getSmallestCFG(modelSetElementString);
				} else {
					result = getSmallestModelCFG(modelSetElementString, result); 
				}
			}
		}
		
		double numOfElementsToBeConsidered = Math.floor((listOfFiles.length * threshold)/100);
		Grammar temp = new Grammar("Mixed");
		Vector<Rule> allRules = result.getRules();
		for (Rule r: allRules) {
			if (r.getNumberOfGrammarsUsingThisRule()>=numOfElementsToBeConsidered) {
				temp.addRule((Long)r.fst(), (Long [])r.snd(), r.getNumberOfGrammarsUsingThisRule());
			}
		}
		result = temp;
		return result;
	}
}
