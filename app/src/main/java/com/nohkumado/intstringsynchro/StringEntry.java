package com.nohkumado.intstringsynchro;

import java.util.*;

public class StringEntry
{
  String token;
  
  String text;
  

  public StringEntry(String name, String text)
  {
    token = name;
    this.text = text;
  }

  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("String[").append(token).append("]");
    sb.append(text);
    
    return sb.toString();
  }
}
