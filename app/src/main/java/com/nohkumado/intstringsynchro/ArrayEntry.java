package com.nohkumado.intstringsynchro;

import java.util.*;

public class ArrayEntry extends StringEntry
{
  protected ArrayList<String> array;

  public ArrayEntry(String n, ArrayList<String> a)
  {
    super(n,"");
    array = a;
  }
  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("Plural[").append(token).append("]");
    if(array != null) sb.append(Arrays.toString(array.toArray(new String[array.size()])));
    return sb.toString();
  }
}
