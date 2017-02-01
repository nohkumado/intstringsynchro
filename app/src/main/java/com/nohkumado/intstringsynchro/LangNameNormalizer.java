package com.nohkumado.intstringsynchro;

import java.util.regex.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * normalize the language ressource, we have a language part and a region part
 * in eISO format or android format, this tries to parse any sense in the incoming string 
 * and spits an android lang string
 */
public class LangNameNormalizer
{
  protected Pattern simple = Pattern.compile("([a-z]{2})"); /** 2 char lang field */
  protected Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");/** android style */
  protected Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");/** iso style */

  /** 
   * normalize the incoming string and spit it back
   */
  public String normalizeLangName(String sanitized)
  {
    if(sanitized == null) return "";
    Matcher m = complete.matcher(sanitized);
    String lang = "", region = "";
    if (m.find())
    {
      lang = m.group(1).toLowerCase();
      region = m.group(2).toUpperCase();
    }//if (m.find())
    else
    {
      m = iso.matcher(sanitized);
      if (m.find())
      {
        lang = m.group(1).toLowerCase();
        region = m.group(2).toUpperCase();
      }//if
      else
      {
        m = simple.matcher(sanitized);
        if (m.find())
        {
          sanitized = sanitized.substring(0, 2);
          lang = sanitized.toLowerCase();
        }//if
        else sanitized = null;
      }//else
    }//else

    if (region.length() > 0) sanitized = lang + "-r" + region;
    else sanitized = lang;
    return sanitized;
  }//private String normalizeLangName(String sanitized)
}//public class LangNameNormalizer
