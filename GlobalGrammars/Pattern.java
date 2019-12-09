
import java.util.List;

public class Pattern<Item> {
	private int patternLength = 0; // The pattern it self
	private List<Integer>  occurences;
	public Pattern(int p, List<Integer> o) {
		this.patternLength = p;
		this.occurences = o;
	}
	
	public int frequency() {
		if(this.occurences == null) {
			return 0;
		}
		return this.occurences.size();
	}
	public int compressionSize() {
		return ((this.patternLength-1)*(this.frequency()-1)) - 2;
	}
	public int length() {
		return this.patternLength;
	}
	public List<Integer> getOccurences() {
		return this.occurences;
	}
	
}
