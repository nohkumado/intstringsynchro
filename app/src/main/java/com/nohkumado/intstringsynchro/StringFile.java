package com.nohkumado.intstringsynchro;
import java.io.*;
import java.net.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * a file subclass that holds the additional info about which langugage it represents 
 */
public class StringFile extends File
{
  /** the lang for which we store this file */
  String lang;

  /**
   *  Creates a new File instance from a parent abstract pathname and a child pathname string.
   */
  public StringFile(File parent, String child, String la)
  {
    super(parent, child);
    lang = la;
  }//CTOR
  /** 
   * Creates a new File instance by converting the given pathname string into an abstract pathname.
   */
  public StringFile(String pathname, String la)
  {
    super(pathname);
    lang = la;
  }//CTOR
  /**
   * Creates a new File instance from a parent pathname string and a child pathname string.
   */
  public StringFile(String parent, String child, String la)
  {
    super(parent, child);
    lang = la;
  }//CTOR
  /**
   * Creates a new File instance by converting the given file: URI into an abstract pathname.
   */
  public StringFile(URI uri, String la)
  {
    super(uri);
    lang = la;
  }//CTOR
  /**
   * setter getter for the language
   */
  public void lang(String lang)
  {
    this.lang = lang;
  }//public void lang(String lang)
  /** getter */
  public String lang()
  {
    return lang;
  }//public String lang()
}//public class StringFile extends File
