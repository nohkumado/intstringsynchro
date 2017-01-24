package com.nohkumado.intstringsynchro;

import java.util.*;
/**

 example 
 <string-array name="planets_array">
 <item>Mercury</item>
 <item>Venus</item>
 <item>Earth</item>
 <item>Mars</item>
 </string-array>

 */
public class ArrayEntry extends StringEntry
{
  private ArrayList<String> array;

  public ArrayEntry(String n)
  {
    this(n,null);
  }
  public ArrayEntry(String n, ArrayList<String> a)
  {
    super(n, "");
    if(a != null) array = a;
    else array = new ArrayList<>();
    
  }

  public void remove(int line)
  {
    array.remove(line);
  }

  
  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("Array[").append(token).append("]");
    if (array != null) sb.append(Arrays.toString(array.toArray(new String[array.size()])));
    return sb.toString();
  }
  public String toXml(String indent)
  {
    StringBuilder  sb = new StringBuilder();
    sb.append(indent).append("<string-array name=\"").append(token).append("\">\n");
    for (String iem : array) sb.append(indent).append(indent).append("<item>").append(iem).append("</item>\n");

    sb.append(indent).append("</string-array>\n");
    return sb.toString();
  }

  public void set(int num, String msg)
  {
    while(num >= array.size()) array.add("");

    array.set(num, msg);
  }

  public void add(String msg)
  {
    array.add(msg);
  }

  public int size()
  {
   if(array != null)  return array.size();
   return -1;
  }
  public String get(int line)
  {
    if(array != null) return array.get(line);
    return null;
  }
  
}//class
