package com.nohkumado.intstringsynchro;

import android.os.*;
import android.util.*;
import android.widget.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 */

public class SaveStringXmlTask extends AsyncTask<String,Integer,Void>
{
  private static final String TAG="SaveF";

  protected TreeMapTable<String,StringEntry> data;
  protected MainActivity context;
  String savePath;

  public SaveStringXmlTask(TreeMapTable<String,StringEntry> rest, 
                            MainActivity context, String path)
  {
    this.data = rest;
    this.context = context;
    savePath = path;
  }

  @Override
  protected Void doInBackground(String[] p1)
  {
    boolean result = true;
    StringBuilder sb ;
    if(savePath == null || savePath.length() <= 0) savePath = context.getProjectPath(); 
    for (String lang : p1)
    {
      //Log.d(TAG, "printing for lang : " + lang);
      sb = new StringBuilder();
      sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
      String indent =  "   ";
      for (String token : data)
      {
        StringEntry msg = data.get(token, lang);
        if (msg != null) sb.append(msg.toXml(indent));  
      }
//      ArrayList<StringEntry> otherStrucs = rest.get(lang);
//      if (otherStrucs != null)
//      {
//        for (StringEntry record : otherStrucs)
//        {
//          sb.append(record.toXml(indent));  
//        }  
//      }
      sb.append("</resources>");

      File saveFile;
      if(lang.equals("default")) saveFile = new File(savePath, "values/strings.xml");
      else saveFile = new File(savePath, "values-" + lang + "/strings.xml");
      Log.d(TAG,"saveFile = "+saveFile.getAbsolutePath());
      saveFile.mkdirs();
      
      //File saveFile = new File(context.getExternalFilesDir(null), "strings-" + lang + ".xml");
      try
      {
        BufferedWriter  os = new BufferedWriter(new FileWriter(saveFile));
        //Log.d(TAG, "writing into " + saveFile + " " + sb);
        os.write(sb.toString());
        os.close();
        //Log.d(TAG,"wroten file "+saveFile);
      }
      catch (FileNotFoundException e)
      {
        Log.e(TAG, "file not found :" + e);
      }
      catch (IOException f)
      {
        Log.e(TAG, "IO ex :" + f);
      }
      //Log.d(TAG, "done writing ");


    }
    return null;
  }//save

  @Override
  protected void onPostExecute(Void result)
  {
    super.onPostExecute(result);
    context.filesSaved();
    
  }//List<StringEntry> loadStringsXmlFile(String aFile)


}//class StringFileLoadTask
