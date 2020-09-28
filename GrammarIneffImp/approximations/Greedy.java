

package approximations;

import java.io.*;
import java.util.*;


public class Greedy {

  public Greedy() {
  }

  public static Grammar compress(String input)
  {
    Grammar result = new Grammar();
    boolean plusDeReductionsPossibles = false;
    Vector mainRule = new Vector();
    String[] inputArray = input.trim().split("\\s+");
    for(int k=0;k<inputArray.length;k++)
    {
	int item = Integer.parseInt(inputArray[k]);
      mainRule.add(new Long(item));
    }
    mainRule.add(new Long(-1));
    Vector tmpOcc = new Vector();
    long currentNT = -3;

    int nbPasse = 1;

    while(!plusDeReductionsPossibles)
    {
      if (Config.showProgress)
      {
        System.err.println("Passe n#"+nbPasse+" Taille Axiome : "+mainRule.size());
      }
      int bestTaille = 0;
      int bestDebut = 0;
      int bestFin = 0;
      Vector bestOcc = null;

      long amount = (mainRule.size()*(mainRule.size()-1))/4;
      long progress = 0;
      int step = 0;

      for(int debut = 0; debut <= mainRule.size()/2;debut++)
      {
        for(int fin = debut+1; fin <= 1 + mainRule.size()/2;fin++)
        {
          // Partie non algorithmique, juste pour faire patienter l'utilisateur
          if (Config.showProgress)
          {
            progress++;
            int currentStep = (int)((progress*Config.numProgressStep)/amount);
            if(currentStep!=step)
            {
              for(int p=step;p<currentStep;p++)
              System.err.print(".");
              step = currentStep;
            }
          }
          int nbOccurencesMotif = 0;
          tmpOcc.clear();

          for(int i = 0; i < mainRule.size()-(fin-debut+1)+1;i++)
          {
            boolean fini = false;
            int j=0;
            while (!fini)
            {
              if(j>fin-debut)
              {
                nbOccurencesMotif++;
                fini = true;
                tmpOcc.add(new Integer(i));
                i = i+j-1;
              }
              else
              {
            	  if (debut+j>=mainRule.size()) {
            		  nbOccurencesMotif++;
                      fini = true;
                      tmpOcc.add(new Integer(i));
                      i = i+j-1;
            	  } else {
            		  fini = (!mainRule.elementAt(i+j).equals(mainRule.elementAt(debut+j)));
            	  }
              }
              j++;
            }
          }
          int tmpTailleIntervalle = (fin-debut+1);
          int tmpTaille = nbOccurencesMotif*tmpTailleIntervalle - nbOccurencesMotif - tmpTailleIntervalle;
          if(tmpTaille>bestTaille)
          {
            bestTaille = tmpTaille;
            bestDebut = debut;
            bestFin = fin;
            bestOcc = (Vector) tmpOcc.clone();
          }
        }
      }
      if (bestTaille<=0)
      {
        plusDeReductionsPossibles = true;
      }
      else
      {
        if (Config.GREEDY_verbose)
        {
          Config.GREEDY_out.println("L'intervalle le plus prometteur est : ("+bestDebut+","+bestFin+")  "+bestOcc.size()+" fois");
          Config.GREEDY_out.print(""+(-currentNT)+" ->");
        }
        long[] RHS = new long[(bestFin-bestDebut)+1];
        for(int i = 0; i<=bestFin-bestDebut;i++)
        {
          RHS[i] = ((Long)mainRule.elementAt(bestDebut+i)).longValue();
          if (Config.GREEDY_verbose)
            Config.GREEDY_out.print(" "+((RHS[i]>0)?(""+(char)RHS[i]):("X"+(-RHS[i]))));
        }
        if (Config.GREEDY_verbose) Config.GREEDY_out.println("");
        result.addRule(currentNT,RHS);
        for(int i = bestOcc.size()-1; i>=0;i--)
        {
          int offset = ((Integer)bestOcc.elementAt(i)).intValue();
          for (int j=bestFin-bestDebut;j>=0;j--)
          {
            mainRule.remove(offset+j);
          }
          mainRule.add(offset,new Long(currentNT));
        }
        currentNT--;
      }
      if (Config.showProgress)
      {
        System.err.println("");
      }
    }
    long[]  mainRuleV = new long[mainRule.size()];
    for(int i =0;i<mainRuleV.length;i++)
    {
      mainRuleV[i] = ((Long)mainRule.elementAt(i)).longValue();
    }
    result.addRule(-2,mainRuleV);
    result.setAxiom(-2);
    return result;
  }
}
