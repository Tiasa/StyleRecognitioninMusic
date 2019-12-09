


import java.util.*;

public class Grammar {

  private Vector _rules = new Vector();
  private String _algorithm = "";
  private int grammarSize = 0;
  private long _axiom = 0;
  
  // Number of terminals
  private int sigmaSize;
  
  public Grammar(String a) {
	  this._algorithm = a;
  }
  
  @SuppressWarnings("unchecked")
public Vector<Rule> getRules() {
	  return (Vector<Rule>)_rules.clone();
  }
  public long getAxiom() {
	  return _axiom;
  }
  public int getSigmaSize() {
	  return this.sigmaSize;
  }
  public void setSigmaSize(int size) {
	  this.sigmaSize = size;
  }
  @SuppressWarnings("unchecked")
public void addRule(long leftPart, Long[] rightPart)
  {
    if(leftPart<0)
    {
      Rule c = new Rule(new Long(leftPart),rightPart);
      int index = Collections.binarySearch(_rules,c);
      if(index<0)
      {
        _rules.add((-index)-1,c);
        
        
      } else {
    	  this.grammarSize -= ((Long[]) (((Rule)_rules.elementAt(index)).snd())).length;
    	  ((Rule)_rules.get(index)).setSnd(rightPart);
    	  
      }
      this.grammarSize += rightPart.length;
    }
  }

  public boolean containsRule(long leftPart)
  {
    if(leftPart<0)
    {
      Rule c = new Rule(new Long(leftPart),null);
      int index = Collections.binarySearch(_rules,c);
      if(index>=0)
      {
        return true;
      }
    }
    return false;
  }


  @SuppressWarnings("unchecked")
public Long[] getRule(long leftPart)
  {
    if(leftPart<0)
    {
      Rule c = new Rule(new Long(leftPart),null);
      int index = Collections.binarySearch(_rules,c);
      if(index>=0)
      {
        return (Long[]) (((Rule)_rules.elementAt(index)).snd());
      }
    }
    return new Long[0];
  }

  
//private int getGrammarSize() {
//	int result = 0;
//	for (Rule rule: this.getRules()) {
//		Long [] rightPart = (Long [])(rule.snd());
//		result += rightPart.length;
//	}
//	return result;
//}
  @SuppressWarnings("unchecked")
public void setAxiom(long axiom)
  {
    int index = Collections.binarySearch(_rules,new Rule(new Long(axiom),null));
    if(index>=0)
    {
      _axiom = axiom;
    }
  }


  public double KolmogorovComplexity()
  {
	  // Size defined as Moses Charikar et al
	  
	  return (this._rules.size()+this.grammarSize) * Math.log(this._rules.size()+sigmaSize);
		  
  }
  public String toString()
  {
    return toString(false);
  }

  public String toString(boolean printRules)
  {
    String result = "";
    int numRules = _rules.size();
    for(int i=numRules-1;i>=0;i--)
    {
      Rule c = (Rule) _rules.elementAt(i);
      long LHS = ((Long)c.fst()).longValue();
      Long[] RHS = (Long[])c.snd();
      
      if (printRules)
      {
        result += "T"+(-LHS)+" -> ";
        for(int j=0;j<RHS.length;j++)
        {

          if(RHS[j]>=0)
          {
            result += "'" + RHS[j].intValue()+"' "; 
          }
          else
          {
        	  if(RHS[j]==-1) {
        		  result+="'$'";
        	  } else {
        		  result += "T"+(-RHS[j])+" ";
        	  }
          }
        }
        result +="\n";
      }
    }
    result +="Algorithm : "+ this._algorithm +"\n" + "Size : "+ this.grammarSize +"\n" + "Rules : "+ numRules + "\n" + "Sigma : "+ this.sigmaSize + "\n"  +"Complexity : " + String.format("%.2f", KolmogorovComplexity());
    return result;
  }

}