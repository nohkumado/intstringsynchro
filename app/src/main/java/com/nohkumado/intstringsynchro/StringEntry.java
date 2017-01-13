package com.nohkumado.intstringsynchro;

import java.util.*;

public class StringEntry
{
  protected String token;
  
  protected String text;
  

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
  
  public String toXml(String indent)
  {
    StringBuilder  sb = new StringBuilder();
    sb.append(indent).append("<string name=\"").append(token).append("\">");
    sb.append(text).append("</string>\n");
    return sb.toString();
  }
}
