package com.nohkumado.intstringsynchro;

import android.util.*;
import java.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 */

public class PluralEntry extends StringEntry
{
  private static final String TAG="Plur";
  protected HashMap<String,StringEntry> hashmap;

  public PluralEntry(String n)
  {
    this(n, null);
  }
  public PluralEntry(String n, HashMap<String,StringEntry> a)
  {
    super(n, "");
    if(a != null) hashmap = a;
    else hashmap = new HashMap<>();
  }
  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("Plural[").append(token).append("]");
      
    if (hashmap != null) sb.append(hashmap.toString());

    return sb.toString();
  }
  /**
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
      sb.append(indent).append(indent).append("<item quantity=\"").append(quant).append("\">").append(hashmap.get(quant).text).append("</item>\n");
    sb.append(indent).append("</plurals>\n");
    return sb.toString();
  }
}
