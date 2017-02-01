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
import org.apache.http.*;
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
    data.clear();
    for (StringFile aFile: p1) loadStringsXmlFile(aFile);
    return null;
  }//protected Void doInBackground(StringFile[] p1)
  /**
   * load one xml file and parse it
   */
  protected void loadStringsXmlFile(StringFile aFile)
  {
    //Log.d(TAG, "loading " + aFile.getAbsolutePath());
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
    //Log.d(TAG,"donw loading, reporting back "+data.size());
    if (context instanceof MainActivity)
    {
      MainActivity mA =  (MainActivity)context;

      mA.buildTableView();
    }
      
  }//protected void onPostExecute(Void result)
  /**
   * return from the filechooserdialog
   */
  public ArrayList<StringFile> findStringFiles(String pathToLoad, StringBuilder error)
  {
    ArrayList<StringFile> toLoad = new ArrayList<StringFile>(); 

    //Log.d(TAG, "findStringFiles in " + pathToLoad);
    File resValuesDir = new File(pathToLoad);
    //ok check if the xml file was selected and use its parent dir
    if (pathToLoad.endsWith(".xml"))
    {
      resValuesDir =  new File(pathToLoad);
      resValuesDir = new File(resValuesDir.getAbsolutePath());
      resValuesDir = resValuesDir.getParentFile();
      if (resValuesDir == null) resValuesDir = Environment.getExternalStorageDirectory();
    }
    else 
    {
      ArrayList<File> tmp = new ArrayList<>();
      tmp.add(resValuesDir);
      
      tmp = scanDirs(tmp);
      if(tmp.size() <= 0) resValuesDir = Environment.getExternalStorageDirectory();
      else if(tmp.size() == 1) resValuesDir = tmp.get(0);
      else
      {
        //ambigouus path....
        if(!(context instanceof MainActivity))
        {
          resValuesDir = tmp.get(0);
        }
        else if(context instanceof MainActivity)
        {
          String[] cands = new  String[tmp.size()];
          int n = 0;
          for(File aF : tmp) 
          {
            cands[n] = aF.getAbsolutePath();
            n++;
          }
          //TODO check if this i sstill relevant
          //((MainActivity)context).callProjectSelect(cands);
          //Log.e(TAG,"should call mainactivity load project "+cands);
        }
      }
    }
    //Log.d(TAG, "got back res dir " + resValuesDir.getAbsolutePath());
    if (context instanceof MainActivity) ((MainActivity)context).savePathToPrefs(resValuesDir.getAbsolutePath());


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
  }
  private ArrayList<File> scanDirs(ArrayList<File> list)
  {
    ArrayList<File> stringsFiles = new ArrayList<>();
    ArrayList<File> toExplore = new ArrayList<>();
    for (File aDir: list)
    {
      if (aDir.exists() && aDir.isDirectory()) 
      {
        for (File aFile: aDir.listFiles())
        {
          if (aFile.exists())
          {
            if (aFile.getAbsolutePath().endsWith("strings.xml"))  
            {
              //here we are... allready there
              aFile = new File(aFile.getAbsolutePath());
              aFile = aFile.getParentFile();
              if (aFile == null) aFile = Environment.getExternalStorageDirectory();
              stringsFiles.add(aFile);
            }//if (aFile.getAbsolutePath().endsWith("strings.xml"))  
            else if (aFile.isDirectory())
            {
              File stringF = new File(aFile, "strings.xml");
              if (stringF.exists()) stringsFiles.add(aFile);
              else toExplore.add(aFile);
            }//else if (aFile.isDirectory())
          }//if (aFile.exists())
        }//if (aDir.exists() && aDir.isDirectory()) 
      }//if(aDir.exists() && aDir.isDirectory()) 
    }

    if (toExplore.size() > 0) 
    {
      toExplore = scanDirs(toExplore);
      for (File aF: toExplore) stringsFiles.add(aF);
    }
    return stringsFiles;//all ok, we have it
  }//  public void findStringFiles(String[] p1)

}//public class StringFileLoadTask extends AsyncTask<StringFile,Integer,Void>
