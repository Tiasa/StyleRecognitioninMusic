
package approximations;

import java.util.*;

public class Grammar {

  private Vector _rules = new Vector();

  private long _axiom = 0;
  
  // Number of terminals
  private int sigmaSize = 17; // For now because of Music

  public Grammar() {
  }


  public void addRule(long partieGauche, long[] partieDroite)
  {
    if(partieGauche<0)
    {
      Couple c = new Couple(new Long(partieGauche),partieDroite);
      int index = Collections.binarySearch(_rules,c);
      if(index<0)
      {
        _rules.add((-index)-1,c);
        if (_axiom==0){_axiom = partieGauche;}
      }
    }
  }

  public boolean containsRule(long partieGauche)
  {
    if(partieGauche<0)
    {
      Couple c = new Couple(new Long(partieGauche),null);
      int index = Collections.binarySearch(_rules,c);
      if(index>=0)
      {
        return true;
      }
    }
    return false;
  }


  public long[] getRule(long partieGauche)
  {
    if(partieGauche<0)
    {
      Couple c = new Couple(new Long(partieGauche),null);
      int index = Collections.binarySearch(_rules,c);
      if(index>=0)
      {
        return (long[]) (((Couple)_rules.elementAt(index)).snd());
      }
    }
    return new long[0];
  }

  public void alterRule(long partieGauche, long[] partieDroite)
  {
    if(partieGauche<0)
    {
      Couple c = new Couple(new Long(partieGauche),partieDroite);
      int index = Collections.binarySearch(_rules,c);
      if(index>=0)
      {
        ((Couple)_rules.elementAt(index)).setSnd(partieDroite);
      }
    }
  }

  public void setAxiom(long axiom)
  {
    int index = Collections.binarySearch(_rules,new Couple(new Long(axiom),null));
    if(index>=0)
    {
      _axiom = axiom;
    }
  }

  public String getLanguageFor(long nt)
  {
    String result = "";
    if(nt<0)
    {
      Couple c = new Couple(new Long(nt),null);
      int index = Collections.binarySearch(_rules,c);
      if(index>=0)
      {
        Vector sequence = new Vector();
        sequence.add(new Long(nt));
        int i = 0;
        while(i<sequence.size())
        {
          Long l = (Long) sequence.elementAt(i);
          if (l.longValue()<0)
          {
            sequence.remove(i);
            index = Collections.binarySearch(_rules,new Couple(l,null));
            if (index>=0)
            {
              long[] RHS = (long[])((Couple) _rules.elementAt(index)).snd();
              for(int j=RHS.length-1;j>=0;j--)
              { sequence.add(i,new Long(RHS[j])); }
            }
            else
            {
              result += "\nNonTerminal Symbol X"+(-l.intValue())+" is not defined !\n";
            }
          }
          else
          {
            result+=""+(char)l.intValue();
            i++;
          }
        }
      }
    }
    return result;
  }

  public String getLanguage()
  {
    String result = "";
    Vector sequence = new Vector();
    sequence.add(new Long(_axiom));
    int i = 0;
    while(i<sequence.size())
    {
      Long l = (Long) sequence.elementAt(i);
      if (l.longValue()<0)
      {
        sequence.remove(i);
        int index = Collections.binarySearch(_rules,new Couple(l,null));
        if (index>=0)
        {
          long[] RHS = (long[])((Couple) _rules.elementAt(index)).snd();
          for(int j=RHS.length-1;j>=0;j--)
          { sequence.add(i,new Long(RHS[j])); }
        }
        else
        {
          result += "[Error : NT Symbol X"+(-l.intValue())+" is not defined]";
        }
      }
      else
      {
        result+=""+(char)l.intValue();
        i++;
      }
    }
    return result;
  }
  public String KolmogorovComplexity(int size, int numrules)
  {
	  // Size defined as Moses Charikar et al
	  double k = (sigmaSize + numrules +size) * Math.log(numrules+sigmaSize);
	  return String.format("%.2f", k);
		  
  }
  public String toString()
  {
    return toString(false);
  }

  public String toString(boolean printRules)
  {
    String result = "";
    int size=0;
    int numRules = _rules.size();
    for(int i=0;i<numRules;i++)
    {
      Couple c = (Couple) _rules.elementAt(i);
      long LHS = ((Long)c.fst()).longValue();
      long[] RHS = (long[])c.snd();
      size += RHS.length;
      if (printRules)
      {
        result += "X"+(-LHS)+" -> ";
        for(int j=0;j<RHS.length;j++)
        {

          if(RHS[j]>=0)
          {
            if(RHS[j]>=20)
            result += (int)RHS[j]+" ";
            else
            result += (int)RHS[j]+" ";
          }
          else
          {
            result += "X"+(-RHS[j])+" ";
          }
        }
        result +="\n";
      }
    }
    result +="Size : "+size +"\n" + "Rules : "+ numRules + "\n" + "Complexity : " + KolmogorovComplexity(size, numRules);
    return result;
  }

}