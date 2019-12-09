import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;;
public class PatriciaTree {
	private SuffixTree<Long> generalized;
	List<Long> concatenated;
	Grammar grammar;
	private final long sentinel = 200; // Midi numbers range from 0 to 128
	public PatriciaTree(Grammar G) {
		grammar = G;
		concatenated = new ArrayList<Long>();
		for (Rule rule: grammar.getRules()) {
			concatenated.addAll(Arrays.asList((Long[])rule.snd()));
			concatenated.add(new Long(sentinel+(-1*((Long)rule.fst()).longValue()))); // unique separator, smartly encode the non terminal too
		  }
		generalized = new SuffixTree<Long>(concatenated.toArray(new Long[concatenated.size()]));
	}
	
	public Pattern<Long> getMostFrequentPattern () {
		Pattern<Long> result = generalized.getRoot().getLongestMostFrequentPattern(0);
		return result;
	}
	public Pattern<Long> getGreedyPattern() {
		Pattern<Long> result = generalized.getRoot().getGreedyPattern(0);
		return result;
	}
	public Pattern<Long> getLongestPattern() {
		Pattern<Long> result = generalized.getRoot().getLongestAtleastTwiceOccuringPattern(0);
		return result;
	}
	public Pattern<Long> findPatternOccurences(Long[] p) {
		if (p!=null && p.length!=0 && generalized.getRoot().get(p[0])!=null) {
			List<Integer> occ = generalized.getRoot().get(p[0]).getOccurence(p);
			if (occ == null) {
				return null;
			} else {
				return new Pattern<Long>(p.length,occ);
			}
		} else {
			return null;
		}
	}
	public Long[] smartReplace(Pattern<Long> p, long NT) {
		Long[] RHS = new Long[p.length()];
		// Copy the pattern before making any change
		// since we didn't store that info in the 
		// Pattern class for fast computation
		int anyStartPoint = p.getOccurences().get(0);
		for(int l=0; l<RHS.length ; l++) {
			RHS[l]=new Long(concatenated.get(l+anyStartPoint).longValue());
		}
		Long[] replacedRightSides = replace(concatenated.toArray(new Long[concatenated.size()]),p,NT);
		int ruleStart = 0;
		for (int i=0;i<replacedRightSides.length;i++) {
			if(replacedRightSides[i].longValue()>=sentinel) {
				Long leftPart = new Long((-1)*(replacedRightSides[i].longValue()-sentinel));
				grammar.addRule(leftPart, Arrays.copyOfRange(replacedRightSides,ruleStart,i));
				ruleStart = i+1;
			}
		}
		return RHS;
		
	}
	private Long[] replace (Long [] rule, Pattern<Long> pattern, Long nt) {
		Long[] temp = new Long[rule.length - ((pattern.length()-1)*pattern.frequency())];
		for(Integer i:pattern.getOccurences()) {
			rule[i.intValue()] = new Long(nt);
		}
		int i,j;
		i = j= 0;
		while (i<temp.length && j<rule.length) {
			temp[i] = new Long(rule[j].longValue());
			i++;
			if(rule[j].longValue()==nt) {
				j+= pattern.length();
			} else {
				j++;
			}
		}
		return temp;
	}
	
}
