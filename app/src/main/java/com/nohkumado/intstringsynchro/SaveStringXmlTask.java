package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.util.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;
import java.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 *
 * the class that saves the table data into the different ressource files 
 * asynctask as to not inpound I/O onto the main UI thread...
 */
public class SaveStringXmlTask extends AsyncTask<String,Integer,Void>
{
  private static final String TAG="SaveF"; /** needed for Log.d */

  protected TreeMapTable<String,StringEntry> data; /** the tabular data */
  protected Activity context; /** callback */
  String savePath; /** the path to save to */
  /** CTOR */
  public SaveStringXmlTask(TreeMapTable<String,StringEntry> rest, Activity context, String path)
  {
    this.data = rest;
    this.context = context;
    savePath = path;
  }//public SaveStringXmlTask(TreeMapTable<String,StringEntry> rest, MainActivity context, String path)
  /**
   * the worker thread
   */
  @Override
  protected Void doInBackground(String[] p1)
  {
    boolean result = true;
    StringBuilder sb ;
    if (savePath == null || savePath.length() <= 0) 
      if (context instanceof MainActivity)  savePath = ((MainActivity)context).getProjectPath(); 
    saveData();//for (String lang : p1)
    return null;
  }

  public void saveData()
  {
    for (Map.Entry<Integer,String> keyVal : data.header().entrySet())
    {
      String lang = keyVal.getValue();
      //Log.d(TAG, "printing for lang : " + lang);
      StringBuilder sb = new StringBuilder();
      sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
      String indent =  "   ";
      for (String token : data)
      {
        StringEntry msg = data.get(token, lang);
        if (msg != null) sb.append(msg.toXml(indent));  
      }//for (String token : data)
      sb.append("</resources>");

      File saveFile;
      if (lang.equals("default")) saveFile = new File(savePath, "values/strings.xml");
      else saveFile = new File(savePath, "values-" + lang + "/strings.xml");
      //Log.d(TAG, "saveFile = " + saveFile.getAbsolutePath());
      saveFile.mkdirs();

      //File saveFile = new File(context.getExternalFilesDir(null), "strings-" + lang + ".xml");
      try
      {
        BufferedWriter  os = new BufferedWriter(new FileWriter(saveFile));
        //Log.d(TAG, "writing into " + saveFile + " " + sb);
        os.write(sb.toString());
        os.close();
        //Log.d(TAG,"wroten file "+saveFile);
      }//try
      catch (FileNotFoundException e)
      {
        Log.e(TAG, "file not found :" + e);
      }
      catch (IOException f)
      {
        Log.e(TAG, "IO ex :" + f);
      }//catch (IOException f)
    }
  }//save
  /**
   * the worker thread finished, now lets call the callback
   */
  @Override
  protected void onPostExecute(Void result)
  {
    super.onPostExecute(result);
    if (context instanceof MainActivity) ((MainActivity)context).filesSaved();
  }//protected void onPostExecute(Void result)
}//class StringFileLoadTask
