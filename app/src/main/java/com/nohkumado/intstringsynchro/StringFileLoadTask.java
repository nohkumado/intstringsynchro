package com.nohkumado.intstringsynchro;
import android.os.*;
import android.util.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;
import java.util.*;
import org.xmlpull.v1.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 *
 * AsyncTask to Load the ressource files without impacting the UI-thread 
 */
public class StringFileLoadTask extends AsyncTask<StringFile,Integer,Void>
{
  /** the table data */
  protected TreeMapTable<String,StringEntry> data;
  /** the context */
  protected MainActivity context;
  /** needed for Log.d */
  public static final String TAG = "SFL";
  /**
   * CTOR
   */
  public StringFileLoadTask(TreeMapTable<String,StringEntry> data, MainActivity context)
  {
    this.data = data;
    this.context = context;

  }//CTOR
  /**
   * the worker thread code
   */
  @Override
  protected Void doInBackground(StringFile[] p1)
  {
    for (StringFile aFile: p1) loadStringsXmlFile(aFile);
    return null;
  }//protected Void doInBackground(StringFile[] p1)
  /**
   * load one xml file and parse it
   */
  protected void loadStringsXmlFile(StringFile aFile)
  {
    Log.d(TAG, "loading " + aFile.getAbsolutePath());
    String langTok = aFile.lang();
    ArrayList<StringEntry> entries = new ArrayList<>(); 
    //XmlPullParser parser =  Xml.newPullParser();
    InputStream stream = null;
    StringXmlParser parser = new StringXmlParser();
    try
    {
      stream = new FileInputStream(aFile);
      entries = parser.parse(stream);
    }//try
    catch (XmlPullParserException e)
    {}
    catch (IOException e)
    {}
    // Makes sure that the InputStream is closed after the app is
    // finished using it.
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }//try
        catch (IOException e)
        {}
      }//if (stream != null)
    }//finally
    for (StringEntry anEntry: entries) data.set(anEntry.token, langTok, anEntry);
  }//protected void loadStringsXmlFile(StringFile aFile)
  /** 
   * finished, time to call the callback
   */
  @Override
  protected void onPostExecute(Void result)
  {
    super.onPostExecute(result);
    context.buildTableView();
  }//protected void onPostExecute(Void result)
}//public class StringFileLoadTask extends AsyncTask<StringFile,Integer,Void>
