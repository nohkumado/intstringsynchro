package com.nohkumado.intstringsynchro;

import android.util.*;
import java.io.*;
import java.util.*;
import org.xmlpull.v1.*;
import org.apache.http.entity.*;

public class StringXmlParser
{
  // We don't use namespaces
  private static final String ns = null;
  private static final String TAG="Parser";


  public ArrayList<StringEntry> parse(InputStream in) throws XmlPullParserException, IOException
  {
    try
    {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(in, null);
      parser.nextTag();
      return readResources(parser);
    }
    finally
    {
      in.close();
    }
  }
  private ArrayList<StringEntry> readResources(XmlPullParser parser) throws XmlPullParserException, IOException
  {
    ArrayList<StringEntry> entries = new ArrayList<>();
    //Log.d(TAG, "etering parse");
    parser.require(XmlPullParser.START_TAG, ns, "resources");
    while (parser.next() != XmlPullParser.END_TAG)
    {
      if (parser.getEventType() != XmlPullParser.START_TAG)
      {
        continue;
      }
      String name = parser.getName();
      //Log.d(TAG, "parsing " + name);
      // Starts by looking for the entry tag
      if (name.equals("string"))
      {
        entries.add(readString(parser));
      }
      else if (name.equals("string-array"))
      {
        entries.add(readStringArray(parser));
      }
      else if (name.equals("plurals"))
      {
        entries.add(readPlurals(parser));
      }
      else
      {
        Log.d(TAG, "skipping tag " + name);
        skip(parser);
      }
    }
    //Log.d(TAG, "returning (" + entries.size() + ")" + Arrays.toString(entries.toArray(new StringEntry[entries.size()])));
    return entries;
  }

  private StringEntry readPlurals(XmlPullParser parser) throws IOException, XmlPullParserException
  {
    //Log.d(TAG, "entering read plurals");
    parser.require(XmlPullParser.START_TAG, ns, "plurals");
    ArrayList<StringEntry> newList = new ArrayList<>();
    StringEntry result = new PluralEntry(parser.getAttributeValue("", "name"), newList);
   
    while (parser.next() != XmlPullParser.END_TAG)
    {
      if (parser.getEventType() != XmlPullParser.START_TAG)
      {
        continue;
      }
      String name = parser.getName();
      if (name.equals("item")) newList.add(readPluralItem(parser));
    }
    //Log.d(TAG, " new plural " + result);
    return result;    
    /*
     <plurals
     name="plural_name">
     <item
     quantity=["zero" | "one" | "two" | "few" | "many" | "other"]
     >text_string</item>
     </plurals>

     <plurals name="numberOfSongsAvailable">
     <!--
     As a developer, you should always supply "one" and "other"
     strings. Your translators will know which strings are actually
     needed for their language. Always include %d in "one" because
     translators will need to use %d for languages where "one"
     doesn't mean 1 (as explained above).
     -->
     <item quantity="one">%d song found.</item>
     <item quantity="other">%d songs found.</item>
     </plurals>
     */
  }

  private StringEntry readStringArray(XmlPullParser parser) throws IOException, XmlPullParserException
  {
    parser.require(XmlPullParser.START_TAG, ns, "string-array");
    ArrayList<String> newList = new ArrayList<>();
    String tag = parser.getAttributeValue(null, "name");
    /* example
     <string-array name="planets_array">
     <item>Mercury</item>
     <item>Venus</item>
     <item>Earth</item>
     <item>Mars</item>
     </string-array>
     */
    while (parser.next() != XmlPullParser.END_TAG)
    {
      if (parser.getEventType() != XmlPullParser.START_TAG)
      {
        continue;
      }
      String name = parser.getName();
      if (name.equals("item")) newList.add(readItem(parser));
    }
    StringEntry result = new ArrayEntry(tag, newList);
    //Log.d(TAG, " new array " + result);
    return result;    
  }

  private String readItem(XmlPullParser parser) throws IOException, XmlPullParserException
  {
    parser.require(XmlPullParser.START_TAG, ns, "item");
    String summary = "";

    while (parser.next() != XmlPullParser.END_TAG)  summary = parser.getText();

    return summary;
  }
  private StringEntry readPluralItem(XmlPullParser parser) throws IOException, XmlPullParserException
  {
    parser.require(XmlPullParser.START_TAG, ns, "item");
    String summary = "";
    StringEntry result = new StringEntry(parser.getAttributeValue(null, "quantity"), "");

    while (parser.next() != XmlPullParser.END_TAG)  summary = parser.getText();
    result.text = summary;
    return result;
  }

  // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
  private StringEntry readString(XmlPullParser parser) throws XmlPullParserException, IOException
  {
    parser.require(XmlPullParser.START_TAG, ns, "string");
    String tag = parser.getAttributeValue(null, "name");

    String summary = parser.getText();

    while (parser.next() != XmlPullParser.END_TAG)
    {
      summary = parser.getText();
    }
    StringEntry result = new StringEntry(tag, summary);
    //Log.d(TAG, " new string " + result);
    return result;
  }

  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
  {
    if (parser.getEventType() != XmlPullParser.START_TAG)
    {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0)
    {
      switch (parser.next())
      {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }
}
