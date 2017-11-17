package com.nohkumado.intstringsynchro;

import java.util.*;
import java.util.regex.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * code representation of the string ressource plural
 */
public class PluralEntry extends StringEntry
{
  private static final String TAG="Plur"; /** needed for Log.d */
  private HashMap<String,StringEntry> hashmap; /** the map for the different plural strings */
  Pattern availableKEys;
  
  /** CTOR */
  public PluralEntry(String n)
  {
    this(n, null);
  }//public PluralEntry(String n)
  /** CTOR */
  public PluralEntry(String n, HashMap<String,StringEntry> a)
  {
    super(n, new CdataString());
    if (a != null) hashmap = a;
    else hashmap = new HashMap<>();
  }

  public StringEntry get(String key)
  {
    return hashmap.get(key);
  }

  public Collection keySet()
  {
    return hashmap.keySet();
  }

  /**
   * make a string representation, for pretty printing
   */
  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("Plural[").append(token).append("]");
    if (hashmap != null) sb.append(hashmap.toString());
    return sb.toString();
  }//public String toString()
  /**
   * create the xml representation, the stuff needed to write into strings.xml
   example 
   <plurals name="numberOfSongsAvailable">
   <item quantity="one">Znaleziono %d piosenkę.</item>
   <item quantity="few">Znaleziono %d piosenki.</item>
   <item quantity="other">Znaleziono %d piosenek.</item>
   </plurals>
   */
  public String toXml(String indent)
  {
    StringBuilder  sb = new StringBuilder();
    sb.append(indent).append("<plurals name=\"").append(token).append("\">\n");
    for (String quant : hashmap.keySet()) 
      sb.append(indent).append(indent).append("<item quantity=\"").append(quant).append("\">").append(hashmap.get(quant).toXml(indent)).append("</item>\n");
    sb.append(indent).append("</plurals>\n");
    return sb.toString();
  }//public String toXml(String indent)
  public void put(String key, CdataString value)
  {
    if(availableKEys == null) availableKEys = Pattern.compile("zero|one|two|few|many|other");
    Matcher m = availableKEys.matcher(key);
    if(m.find())
    {
      hashmap.put(key,new StringEntry(key,value));
    }
  }//public PluralEntry(String n, HashMap<String,StringEntry> a)
  
}//public class PluralEntry extends StringEntry
