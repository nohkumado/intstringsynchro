package com.nohkumado.intstringsynchro;

import java.util.regex.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 */

public class LangNameNormalizer
{
  protected Pattern simple = Pattern.compile("([a-z]{2})");
  protected Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");
  protected Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");
  

  public String normalizeLangName(String sanitized)
  {
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
        else
        {
          sanitized = null;
        }//else
      }//else
    }//else

    //if (inputText.length() > 2) sanitized = inputText.substring(0, 2);
    //sanitized = sanitized.toLowerCase();
    if (region.length() > 0)
    {
      sanitized = lang + "-r" + region;
    }
    else sanitized = lang;
    return sanitized;
  }//private String normalizeLangName(String sanitized)
}//class
