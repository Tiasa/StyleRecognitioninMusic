
package approximations;

import java.io.*;
import java.util.*;

public class Bisection {

  public Bisection() {
  }

  public static int pow2(int n)
  {
    if(n>0)
    return 1<<n;
    else
    return 1;

  }


  public static int log2(int n)
  {
    int k = 0;
    n = n/2;
    while(n>1)
    {
      k++;
      n = n/2;
    }
    return k;
  }

  public static Grammar compress(String input)
  {
    Grammar result = new Grammar();
      long currentNT = -2;
      if(input.length()<2)
      {
        long[] l = new long[input.length()];
        for(int i=0;i<l.length;i++)
        { l[i] = input.charAt(i); }
        result.addRule(-1,l);
        result.setAxiom(-1);
        for(int p=0;p<Config.numProgressStep;p++)
            System.err.print(".");
      }
      else
      {
        Stack ranges = new Stack();
        ranges.push(new Range(0,input.length()-1,-1));
        Tree dictionnaire = new Tree();

        int step = 0;
        long amount = input.length();
        long numEtape = 0;

        
        while(!ranges.empty())
        {
          // Partie non algorithmique, juste pour faire patienter l'utilisateur
          if (Config.showProgress)
          {
            numEtape++;
            int currentStep = (int)((numEtape*(long)Config.numProgressStep)/amount);
            if(currentStep!=step)
            {
              for(int p=step;p<currentStep;p++)
              System.err.print(".");
              step = currentStep;
            }
          }

          Tree tmp = dictionnaire;
          // On extrait un intervalle
          Range r = (Range) ranges.pop();
          if (Config.BISECTION_verbose) Config.BISECTION_out.println("On observe l'intervalle "+r);
          long[] l = new long[2];
          // On coupe cet intervalle en deux intervalles
          int startFils1 = r.start;

         
          int endFils1 = r.start + pow2(log2((1+r.end-r.start)))-1;
          if (Config.BISECTION_verbose) Config.BISECTION_out.println("  "+r+" -> ("+startFils1+","+endFils1+") ("+(endFils1+1)+","+r.end+")");
          if((endFils1-startFils1)>0)
          {
            
            for(int k=startFils1;k<=endFils1;k++)
            {
              tmp = tmp.addSon(input.charAt(k));
            }
          
            if (tmp.getNT()==0)
            {
              if (Config.BISECTION_verbose) Config.BISECTION_out.println("  ("+startFils1+","+endFils1+")"+(-currentNT));
              tmp.setNT(currentNT);
              Range r1 = new Range(startFils1,endFils1,currentNT);
              ranges.push(r1);
              currentNT--;
            }
            else
            {
              if (Config.BISECTION_verbose) Config.BISECTION_out.println("  ("+startFils1+","+endFils1+")"+(-tmp.getNT()));
            }
            l[0] = tmp.getNT();
          }
          else
          {
          
            if (Config.BISECTION_verbose) Config.BISECTION_out.println("  ("+startFils1+","+endFils1+")");
            l[0] = (int) input.charAt(endFils1);
          }
         
          tmp = dictionnaire;
          int startFils2 = endFils1+1;
          int endFils2 = r.end;
          if((endFils2-startFils2)>0)
          {
            for(int k=startFils2;k<=endFils2;k++)
            {
              tmp = tmp.addSon(input.charAt(k));
            }
            if (tmp.getNT()==0)
            {
              if (Config.BISECTION_verbose) Config.BISECTION_out.println("  ("+startFils2+","+endFils2+")"+(-currentNT));
              tmp.setNT(currentNT);
              Range r2 = new Range(startFils2,endFils2,currentNT);
              ranges.push(r2);
              currentNT--;
            }
            else
            {
              if (Config.BISECTION_verbose) Config.BISECTION_out.println("  ("+startFils2+","+endFils2+")"+(-tmp.getNT()));
            }
            l[1] = tmp.getNT();
          }
          else
          {
            if (Config.BISECTION_verbose) Config.BISECTION_out.println("  ("+startFils2+","+endFils2+")");
            l[1] = input.charAt(endFils2);
          }
          
          if (Config.BISECTION_verbose) Config.BISECTION_out.println((-r.NT)+" -> "+((l[0]>=0)?(""+(char)l[0]):("X"+(-l[0]))) +" " +((l[1]>=0)?(""+(char)l[1]):("X"+(-l[1])))+""+r );
          result.addRule(r.NT,l);
        }
        result.setAxiom(-1);
        if (Config.showProgress)
        {
          numEtape++;
          int currentStep = Config.numProgressStep;
          if(currentStep!=step)
          {
            for(int p=step;p<currentStep;p++)
            System.err.print(".");
            step = currentStep;
          }
          System.err.println("");
        }
      }
    return result;
  }

  public static class Range
  {
    public int start=0;
    public int end=0;
    public long NT = 0;

    public Range (int _start,int _end, long _nt)
    {
      start = _start;
      end = _end;
      NT = _nt;
    }
    public String toString()
    {
      return ("Range : ("+start+";"+end+")");
    }
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
  }
}