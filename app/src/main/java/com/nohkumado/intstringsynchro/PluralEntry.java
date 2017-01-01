package com.nohkumado.intstringsynchro;

import java.util.*;

public class PluralEntry extends StringEntry
{
  protected ArrayList<String> array;

  public PluralEntry(String n, ArrayList<String> a)
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
