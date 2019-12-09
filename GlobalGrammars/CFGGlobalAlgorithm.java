import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CFGGlobalAlgorithm {
	//private static long startingNT = -3;
	private static long axiom = -1; // axiom meaning the starting rule
	//private static long sentinel = -1;
	public CFGGlobalAlgorithm() {}
	// Order the non-terminals
	private static void buildCompressionDictionary(ArrayList<Long> dict, Grammar input , Long leftPart) {
		Long[] RHS = input.getRule(leftPart);
		for (Long i:RHS) {
			if (i < 0 && !dict.contains(i)) {
				buildCompressionDictionary(dict,input,i);
			}
		}
		// For now we are removing the starting rule that can regenerate xy
		// because then you need constant bits to reconstruct x and the dictionary 
		// is not useful
		if (leftPart != input.getAxiom() && !dict.contains(leftPart)) {
			dict.add(leftPart);
		}
	}
	// Meticulously counting the number of different element
	// needed to describe the object. Theoretically an object needing more
	// elements to describe will have larger K complexity
	private static int countSigma(Long[] s) {
		HashSet<Integer> hs = new HashSet<Integer>();
		for (Long l:s) {			
			hs.add(l.intValue());
		}
		return hs.size();
	}
	
	public static Grammar compressWithExistingGrammar(String input, Grammar inputGrammar, String algorithm)
	  {
		Grammar result = null;
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
	    //char[] inputArray = input.trim().toCharArray(); // for DNA
	    result.addRule(axiom, new Long[inputArray.length]);
	    for (int k=0;k<inputArray.length;k++) {
	    	result.getRule(axiom)[k] = new Long(Integer.parseInt(inputArray[k])); // For music
	    	//mainRule[k] = new Long(inputArray[k]); // For DNA
	    }
	    result.setSigmaSize(countSigma(result.getRule(axiom)));
	    
	  // First compress as much as possible with the given grammar
	  // Better to implement as a stack
	  ArrayList<Long> dictionary = new ArrayList<Long>();
	  buildCompressionDictionary(dictionary, inputGrammar, inputGrammar.getAxiom());
	  Iterator<Long> dictionaryIterator = dictionary.iterator();
	  // Exhaust the dictionary
	  // Here we need to check how much can we compress with the given grammar
	  long currentNT = axiom-1;
	  long inputGrammarLastNT = currentNT;
	  
	  while(dictionaryIterator.hasNext()) {
		  currentNT = dictionaryIterator.next();
		  Long[] RHS = inputGrammar.getRule(currentNT);
		  PatriciaTree tree = new PatriciaTree(result);
		  Pattern<Long> pattern = tree.findPatternOccurences(RHS);
		  if (pattern!= null && pattern.frequency() > 1) {
			  tree.smartReplace(pattern, currentNT);
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
		    		currentNT--;
	    		}
	    	}
	    }
	    result.setAxiom(axiom);
	    return result;
	  }
	public static Grammar compress(String input, String algorithm) {
		
		Grammar result = null;
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
	    //char[] inputArray = input.trim().toCharArray(); // for DNA
	    result.addRule(axiom, new Long[inputArray.length]);
	    for (int k=0;k<inputArray.length;k++) {
	    	result.getRule(axiom)[k] = new Long(Integer.parseInt(inputArray[k])); // For music
	    	//mainRule[k] = new Long(inputArray[k]); // For DNA
	    }
	    
	    // Using a negative number for the sentinel character 
	    // since the ASCII number 36 can be used as a pitch number
	    // 
	    //mainRule[inputArray.length] = new Long(sentinel);// really need this sentinel character to properly build tree
	    result.setSigmaSize(countSigma(result.getRule(axiom)));
	    boolean moreReductionPossible = true;
	    long currentNT = axiom-1;
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
		    		currentNT--;
	    		}
	    	}
	    }
	    result.setAxiom(axiom);
	    return result;

	}
}
