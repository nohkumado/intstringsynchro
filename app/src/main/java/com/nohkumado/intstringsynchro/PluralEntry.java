package com.nohkumado.intstringsynchro;

import android.util.*;
import java.util.*;

public class PluralEntry extends StringEntry
{
  private static final String TAG="Plur";
  protected ArrayList<StringEntry> array;

  public PluralEntry(String n, ArrayList<StringEntry> a)
  {
    super(n, "");
    array = a;
  }
  @Override
  public String toString()
  {
    StringBuilder  sb = new StringBuilder();
    sb.append("Plural[").append(token).append("]");
    try
    {
      
    if (array != null) sb.append(Arrays.toString(array.toArray(new StringEntry[array.size()])));
    }
    catch(ArrayStoreException e)
    {
      Log.e(TAG,"failde to store "+array);
      for(StringEntry anEn : array)
      {
        Log.e(TAG,"content  "+anEn);
      }
    }
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
    for (StringEntry iem : array) 
      sb.append(indent).append(indent).append("<item quantity=\"").append(iem.token).append("\">").append(iem.text).append("</item>\n");
    sb.append("</plurals>\n");
    return sb.toString();
  }
}
