

package approximations;

import java.io.*;
import java.util.*;

public class Sequential {

  public Sequential() {
  }

  public static Grammar compress(String input)
  {
    Grammar result = new Grammar();
      long currentNT = -2;
      Vector AxRHS = new Vector();
      Tree NTProductions = new Tree();
      int step = 0;

      for(int i=0;i<input.length();i++)
      {
        // Partie non algorithmique, juste pour faire patienter l'utilisateur
        if (Config.showProgress)
        {
          int currentStep = (i*Config.numProgressStep)/input.length();
          if(currentStep!=step)
          {
            for(int p=step;p<currentStep;p++)
            System.err.print(".");
            step = currentStep;
          }
        }
        Tree tmp = NTProductions;
        boolean fini = false;
        int j = 0;
        long candidate = 0;
        int candidateSize = 0;
        while(!fini)
        {
          if(j+i<input.length())
          {
            tmp = tmp.getSon(input.charAt(j+i));
            if(tmp == null)
            {fini = true;}
            else
            {
              if (tmp.getNT()!=0)
              {
                candidate = tmp.getNT();
                candidateSize = j;
              }
            }
          }
          else
          { fini = true; }
          j++;
        }
        if (candidate == 0)
        { AxRHS.add(new Long(input.charAt(i))); }
        else
        {
          AxRHS.add(new Long(candidate));
          i += candidateSize;
        }
        if(AxRHS.size()>3)
        {
          long avantDernier = ((Long)AxRHS.elementAt(AxRHS.size()-2)).longValue();
          long dernier = ((Long)AxRHS.elementAt(AxRHS.size()-1)).longValue();
          fini = false;
          for(int k=0;(k<AxRHS.size()-3)&&(!fini);k++)
          {
            long l = ((Long)AxRHS.elementAt(k)).longValue();
            if(l==avantDernier)
            {
              l = ((Long)AxRHS.elementAt(k+1)).longValue();
              if(l==dernier)
              {
                long[] RHS = new long[2];
                RHS[0] = avantDernier;
                RHS[1] = dernier;
                fini = true;
                int backupSize = AxRHS.size();
                AxRHS.remove(backupSize-1);
                AxRHS.remove(backupSize-2);
                AxRHS.add(new Long(currentNT));

                AxRHS.remove(k+1);
                AxRHS.remove(k);
                AxRHS.add(k,new Long(currentNT));

                result.addRule(currentNT, RHS);

                String lang = result.getLanguageFor(currentNT);
                tmp = NTProductions;
                for(int m=0;m<lang.length();m++)
                {
                  tmp = tmp.addSon(lang.charAt(m));
                }
                tmp.setNT(currentNT);

                currentNT--;
              }
            }
          }
        }
      }
      long[] RHS = new long[AxRHS.size()];
      for(int i=0;i<AxRHS.size();i++)
      {
        RHS[i] = ((Long)AxRHS.elementAt(i)).longValue();
      }
      result.addRule(-1, RHS);
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
    result.setAxiom(-1);
    return result;
  }

  public static class Tree
  {
    Vector _sons = new Vector();
    Vector _ranges = new Vector();
    long _NT = 0;

    public Tree()
    {
    }

    public Vector getSons()
    {
      return _sons;
    }

    public void setNT(long nt)
    {
      _NT = nt;
    }

    public long getNT()
    {
      return _NT;
    }
   
    public Tree addSon(int n)
    {
      int index = Collections.binarySearch(_sons,new Couple(new Integer(n),null));
      if(index<0)
      {
        Tree t = new Tree();
        _sons.add((-index)-1,new Couple(new Integer(n),t));
        return t;
      }
      else
      return ((Tree)((Couple)_sons.elementAt(index)).snd());
    }


    public Tree getSon(int n)
    {
      int index = Collections.binarySearch(_sons,new Couple(new Integer(n),null));
      if(index<0)
      {
        return null;
      }
      else
      return ((Tree)((Couple)_sons.elementAt(index)).snd());
    }

  }
}