package com.nohkumado.intstringsynchro;

import java.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 *
 * code representation for a String type ressource 
 */
public class StringEntry
{
  /** the token used in the code */
  protected String token;
  /** the text associated with that token */
  private CdataString text;
  /** CTOR */
  public StringEntry(String name, CdataString text)
  {
    token = name;
    this.text = text;
  }

  public boolean isCdata()
  {
	  if(text != null) return text.isCdata();
	  return false;
  }//public StringEntry(String name, String text)
  /**
   * make a string representation, for pretty printing
   */
  public String asString()
  {
    return text.toString();
  }//public String toString()
	/**
	 * make a string representation, for pretty printing
	 */
	@Override
	public String toString()
	{
		StringBuilder  sb = new StringBuilder();
		sb.append("String[").append(token).append("]");
		sb.append(text);
		return sb.toString();
	}//public String toString()
	
  /**
   * create the xml representation, the stuff needed to write into strings.xml
   example 
   <string name="oyoyoy">something will be printed</string>
   */
  public String toXml(String indent)
  {
    StringBuilder  sb = new StringBuilder();
    sb.append(indent).append("<string name=\"").append(token).append("\">");
    sb.append(text.toXml()).append("</string>\n");
    return sb.toString();
  }//public String toXml(String indent)
}//public class StringEntry
