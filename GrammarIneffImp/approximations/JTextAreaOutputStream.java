
/**
 * Title:        Approximations Simulation<p>
 * Description:  Simulation sur des exemples de divers algorithmes d'approximation pour la compression de chaînes de caractères utilisant des grammaires non contextuelles.<p>
 * Copyright:    Copyright (c) Cécile Malinaud/Yann Ponty<p>
 * Company:      <p>
 * @author Cécile Malinaud/Yann Ponty
 * @version 1.0
 */

package approximations;

import java.io.*;
import java.awt.*;
import javax.swing.*;

public class TextAreaPrintStream extends PrintStream
{

  JTextArea _child ;


  public boolean checkError()
  {return false;}
  public void close()
  {}
  public void flush()
  {}
  public void print(boolean b)
  {print(String.valueOf(b));}
  public void print(char c)
  {print(String.valueOf(c));}
  public void print(char[] s)
  {print(new String(s));}
  public void print(double d)
  {print(String.valueOf(d));}
  public void print(float f)
  {print(String.valueOf(f));}
  public void print(int i)
  {print(String.valueOf(i));}
  public void print(long l)
  {print(String.valueOf(l));}
  public void print(Object obj)
  {print(obj.toString());}
  public void print(String s)
  {_child.append(s);}

  public void println(boolean b)
  {println(String.valueOf(b));}
  public void println(char c)
  {println(String.valueOf(c));}
  public void println(char[] s)
  {println(new String(s));}
  public void println(double d)
  {println(String.valueOf(d));}
  public void println(float f)
  {println(String.valueOf(f));}
  public void println(int i)
  {println(String.valueOf(i));}
  public void println(long l)
  {println(String.valueOf(l));}
  public void println(Object obj)
  {println(obj.toString());}
  public void println()
  {println("");}
  public void println(String x)
  {_child.append(x+'\n');}

  public TextAreaPrintStream(JTextArea txt)
  {
    super(new ByteArrayOutputStream());
    _child = txt;
  }
}