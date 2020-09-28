
package approximations;


public class Couple implements Comparable
{
  private Object _first = null;
  private Object _second = null;



  public Couple(Object a, Object b)
  {
    super();
    _first = a;
    _second = b;
  }


  public Object fst()
  {return _first;}


  public Object snd()
  {return _second;}

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
    Couple to = (Couple)o;
    Comparable x1 = (Comparable) _first;
    Comparable x2 = (Comparable) to.fst();
    return x1.compareTo(x2);
  }
}