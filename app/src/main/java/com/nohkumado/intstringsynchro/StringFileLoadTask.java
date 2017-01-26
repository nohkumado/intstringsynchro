package com.nohkumado.intstringsynchro;
import android.app.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
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
  protected Activity context;
  /** needed for Log.d */
  public static final String TAG = "SFL";
  /**
   * CTOR
   */
  public StringFileLoadTask(TreeMapTable<String,StringEntry> data, Activity context)
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
    if(context instanceof MainActivity)
      ((MainActivity)context).buildTableView();
  }//protected void onPostExecute(Void result)
  /**
   * return from the filechooserdialog
   */
  public ArrayList<StringFile> findStringFiles(String pathToLoad,StringBuilder error)
  {
    ArrayList<StringFile> toLoad = new ArrayList<StringFile>(); 

    File resValuesDir = new File(pathToLoad);
    if (resValuesDir.exists())
    {
      boolean found = false;
      for (String aFile: resValuesDir.list())
      {
        if ("strings.xml".equals(aFile))
        {
          found = true;
          toLoad.add(new StringFile(resValuesDir, aFile, "default"));
          break;
        }//if ("strings.xml".equals(aFile))
      }//for (String aFile: resValuesDir.list())
      if (found)
      {
        //ok we have the right directory
        File resDir = new File(resValuesDir, "../");

        final Pattern p = Pattern.compile("values-[a-z\\-]{2,}");
        //select allready selected languages
        String[] files = resDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
              Matcher m = p.matcher(name);
              return m.find();
              //return name.matches("values-\\[a-z\\-]{2,}");
            }
          });
        //Log.d(TAG, "checking parent directory " + Arrays.toString(files));
        Pattern onlyLang = Pattern.compile("^values-(.*)$");
        for (String aLang : files)
        {
          //Log.d(TAG, "checking alternate " + aLang);
          Matcher mlang = onlyLang.matcher(aLang);
          if (mlang.find())
          {
            String sanitized = mlang.group(1);
            StringFile resLangFile = new StringFile(resDir, aLang + "/strings.xml", sanitized);

            if (resLangFile.exists()) toLoad.add(resLangFile);
          }//if (mlang.find())
        }//for (String aLang : files)
      }//if(found)
      else error.append("no strings.xml found in").append(pathToLoad);
    }//if (resValuesDir.exists())
    else error.append("Dir not found: " + pathToLoad);

    if (error.toString().length() > 0) Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
    return(toLoad);
  }//  public void findStringFiles(String[] p1)

}//public class StringFileLoadTask extends AsyncTask<StringFile,Integer,Void>
