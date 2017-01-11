package com.nohkumado.intstringsynchro;
import java.io.*;
import java.net.*;

public class StringFile extends File
{
  String lang;

  /**
   Creates a new File instance from a parent abstract pathname and a child pathname string.
   */
  public StringFile(File parent, String child, String la)
  {
    super(parent, child);
    lang = la;
  }
  /** 
   Creates a new File instance by converting the given pathname string into an abstract pathname.

   */
  public StringFile(String pathname, String la)
  {
    super(pathname);
    lang = la;
  }/**
   Creates a new File instance from a parent pathname string and a child pathname string.

   */
  public StringFile(String parent, String child, String la)
  {
    super(parent, child);
    lang = la;
  }/**
   Creates a new File instance by converting the given file: URI into an abstract pathname.

   */
  public StringFile(URI uri, String la)
  {
    super(uri);
    lang = la;
  }

/**
* setter getter for the language
*/
  public void lang(String lang)
  {
    this.lang = lang;
  }

  public String lang()
  {
    return lang;
  }

}//class