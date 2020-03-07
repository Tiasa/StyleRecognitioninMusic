



public class Rule implements Comparable
{
  private Object _first = null;
  private Object _second = null;
  // For Classification
  private int _numberOfGrammarsUsingThisRule = 1;

  public Rule(Object a, Object b, int num) {
	  super();
	  _first = a;
	  _second = b;
	  _numberOfGrammarsUsingThisRule = num;
  }
  public Rule(Object a, Object b)
  {
    this(a,b,1);
  }


  public Object fst()
  {return _first;}


  public Object snd()
  {return _second;}
  
  //For Classification
  public int getNumberOfGrammarsUsingThisRule() {
	  return this._numberOfGrammarsUsingThisRule;
  }
  public void setNumberOfGrammarsUsingThisRule(int num) {
	  this._numberOfGrammarsUsingThisRule = num;
  }
  /////////
  public void setFst(Object o)
  {_first = o;}

  public void setSnd(Object o)
  { _second = o;}

  public String toString()
  {
    return "("+_first.toString()+";"+_second.toString()+")";
  }

  public int compareTo(Object o)
  {
    Rule to = (Rule)o;
    Comparable x1 = (Comparable) _first;
    Comparable x2 = (Comparable) to.fst();
    return x1.compareTo(x2);
  }
}