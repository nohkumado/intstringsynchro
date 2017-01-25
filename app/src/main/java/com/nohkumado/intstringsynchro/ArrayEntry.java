package com.nohkumado.intstringsynchro;

import java.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 
 Representation of a string ressource of type array 

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
  /** the data structure holding the data */
  private ArrayList<String> array;
  /** 
   * CTOR
   * @argument name the token name
   */
  public ArrayEntry(String n)
  {
    this(n, null);
  }
  /** 
   * CTOR
   * @argument name the token name
   * @argument list with an arraylist to use instead of the internal one
   */
  public ArrayEntry(String n, ArrayList<String> a)
  {
    super(n, "");
    if (a != null) array = a;
    else array = new ArrayList<>();
  }//public ArrayEntry(String n, ArrayList<String> a)
  /**
   * remove an entry at 
   * @argument index
   */
  public void remove(int line)
  {
    array.remove(line);
  }// public void remove(int line)
  /**
   * make a string representation of this object
   */
  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("Array[").append(token).append("]");
    if (array != null) sb.append(Arrays.toString(array.toArray(new String[array.size()])));
    return sb.toString();
  }//public String toString()
  /**
   * the real purpose of this thing, make an xml representation, ready to be printed into a string.xml file
   * @argument indent the indentation to use even if it is machine generated a bit of pretty printing doesn't hurt
   */
  public String toXml(String indent)
  {
    StringBuilder  sb = new StringBuilder();
    sb.append(indent).append("<string-array name=\"").append(token).append("\">\n");
    for (String iem : array) sb.append(indent).append(indent).append("<item>").append(iem).append("</item>\n");

    sb.append(indent).append("</string-array>\n");
    return sb.toString();
  }//public String toXml(String indent)
  /**
   * set a string at index n, autoexpands the list if the index is bigger than the container
   * @arg num the line to set it to
   * @arg msg the string to store at that position
   */
  public void set(int num, String msg)
  {
    while (num >= array.size()) array.add("");

    array.set(num, msg);
  }//public void set(int num, String msg)
  /**
   * add a line to the end of this array
   * @arg msg the strriiing to add
   */
  public void add(String msg)
  {
    array.add(msg);
  }//public void add(String msg)
  /**
  * delegated method for the array size
  */
  public int size()
  {
    if (array != null)  return array.size();
    return -1;
  }//public int size()
  /**
  * delegated method for getting at index n
  */
  public String get(int line)
  {
    if (array != null) return array.get(line);
    return null;
  }//public String get(int line)
}//class
