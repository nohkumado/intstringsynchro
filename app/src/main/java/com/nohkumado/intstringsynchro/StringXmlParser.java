package com.nohkumado.intstringsynchro;

import android.util.*;
import java.io.*;
import java.util.*;
import org.xmlpull.v1.*;

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

    parser.require(XmlPullParser.START_TAG, ns, "resources");
    while (parser.next() != XmlPullParser.END_TAG)
    {
      if (parser.getEventType() != XmlPullParser.START_TAG)
      {
        continue;
      }
      String name = parser.getName();
      // Starts by looking for the entry tag
      if (name.equals("string"))
      {
        entries.add(readString(parser));
      }
      else
      {
        skip(parser);
      }
    }
    return entries;
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
      //Log.d(TAG,"att count: "+parser.getAttributeCount());
      //for(int i = 0; i < parser.getAttributeCount(); i++)
      //{
      //  Log.d(TAG,"attr: "+parser.getAttributeName(i)+" : "+parser.getAttributeValue(i));
      //}
      
      summary = parser.getText();
    }
    return new StringEntry(tag, summary);
  }
  
  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
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
