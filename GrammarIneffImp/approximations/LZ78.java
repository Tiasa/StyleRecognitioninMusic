
package approximations;

import java.io.*;
import java.util.*;

public class LZ78 {

  public LZ78() {
  }

  public static Grammar compress(String input)
  {
      Grammar result = new Grammar();
      Vector mainRule = new Vector();
      LZ78.Trie mainTrie = new LZ78.Trie(-1);
      long currentNT = -2;
      LZ78.Trie tmpTrie = mainTrie;
      int step = 0;
      for(int k=0;k<input.length();k++)
      {
        // Partie non algorithmique, juste pour faire patienter l'utilisateur
        if (Config.showProgress)
        {
          int currentStep = (k*Config.numProgressStep)/input.length();
          if(currentStep!=step)
          {
            for(int p=step;p<currentStep;p++)
            System.err.print(".");
            step = currentStep;
          }
        }
        char c = input.charAt(k);
        String tmp = ""+(char)c;
        if(tmpTrie.containsTransitionFor(c))
        {
          tmpTrie = tmpTrie.getSonAccessibleWith(c);
          if (Config.LZ78_verbose) Config.LZ78_out.println(tmp +(-tmpTrie.getRoot()));
        }
        else
        {
          long[] partieDroiteRegle = null;
          if (tmpTrie.getRoot()!=-1)
          {
            partieDroiteRegle = new long[2];
            partieDroiteRegle[0] = tmpTrie.getRoot();
            partieDroiteRegle[1] = c;
            if (Config.LZ78_verbose)
            {
              Config.LZ78_out.println(tmp +" admet le mot issu de X"+(-tmpTrie.getRoot())+(-currentNT)+" -> X"+(-tmpTrie.getRoot())+" "+(char)c);
              tmp ="";
            }
          }
          else
          {
            partieDroiteRegle = new long[1];
            partieDroiteRegle[0] = c;
            if (Config.LZ78_verbose)
            {
              Config.LZ78_out.println(tmp+(-currentNT)+" -> X"+(-tmpTrie.getRoot())+" "+(char)c);
              tmp ="";
            }
          }
          tmpTrie.add(c,currentNT);
          result.addRule(currentNT,partieDroiteRegle);
          mainRule.add(new Long(currentNT));
          currentNT--;
          tmpTrie = mainTrie;
          }
      }
      if (Config.showProgress)
      {
        int currentStep = Config.numProgressStep;
        if(currentStep!=step)
        {
          for(int p=step;p<currentStep;p++)
          System.err.print(".");
          step = currentStep;
        }
        System.err.println("");
      }

      if (tmpTrie.getRoot()!=-1)
      { mainRule.add(new Long(tmpTrie.getRoot())); }

      long[] mainRuleV = new long[mainRule.size()];
      for(int i=0;i<mainRule.size();i++)
      { mainRuleV[i] = ((Long)mainRule.elementAt(i)).longValue();}
      result.addRule(-1,mainRuleV);
      result.setAxiom(-1);
      return result;
}




  public static class Trie
  {
    Vector _sons = new Vector();
    long _root = -1;

    public Trie(long root)
    {
      _root = root;
    }

    public long getRoot()
    { return _root; }

   
    public Trie add(int n, long root)
    {
      int index = Collections.binarySearch(_sons,new Couple(new Integer(n),null));
      if(index<0)
      {
        Trie t = new Trie(root);
        _sons.add((-index)-1,new Couple(new Integer(n),t));
        return t;
      }
      else
      return null;
    }

    public boolean containsTransitionFor(int n)
    {
      int index = Collections.binarySearch(_sons,new Couple(new Integer(n),null));
      return !(index < 0);
    }

    public Trie getSonAccessibleWith(int n)
    {
      int index = Collections.binarySearch(_sons,new Couple(new Integer(n),null));
      if (index >= 0)
      { return (Trie)((Couple) _sons.elementAt(index)).snd(); }
      return null;
    }
  }
}