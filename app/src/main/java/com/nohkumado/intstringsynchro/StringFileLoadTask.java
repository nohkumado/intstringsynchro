package com.nohkumado.intstringsynchro;
import android.os.*;
import android.util.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;
import java.util.*;
import org.xmlpull.v1.*;

public class StringFileLoadTask extends AsyncTask<StringFile,Integer,Void>
{
  //protected HashMap<String, ArrayList<StringEntry>> rest;
  protected TreeMapTable<String,StringEntry> data;
  
  protected MainActivity context;

  public static final String TAG = "SFL";

  public StringFileLoadTask(TreeMapTable<String,StringEntry> data, MainActivity context)
  {
    this.data = data;
    this.context = context;
    
  }

  @Override
  protected Void doInBackground(StringFile[] p1)
  {
    for (StringFile aFile: p1)
    {
      loadStringsXmlFile(aFile);
    }
    return null;
  }

  protected void loadStringsXmlFile(StringFile aFile)
  {
    Log.d(TAG,"loading "+aFile.getAbsolutePath());
    String langTok = aFile.lang();
    ArrayList<StringEntry> entries = new ArrayList<>(); 
    //XmlPullParser parser =  Xml.newPullParser();
    InputStream stream = null;
    StringXmlParser parser = new StringXmlParser();
    try
    {
      stream = new FileInputStream(aFile);
      entries = parser.parse(stream);
    }
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
        }
        catch (IOException e)
        {}
      }
    }
    for (StringEntry anEntry: entries) data.set(anEntry.token, langTok, anEntry);

    //if(stringDataAdapter != null) stringDataAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onPostExecute(Void result)
  {
    super.onPostExecute(result);
    context.buildTableView();
  }//List<StringEntry> loadStringsXmlFile(String aFile)


}//class StringFileLoadTask
