package com.nohkumado.intstringsynchro;

import android.os.*;
import android.util.*;
import android.widget.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;

public class SaveStringXmlTask extends AsyncTask<String,Integer,Void>
{
  private static final String TAG="SaveF";

  protected TreeMapTable<String,StringEntry> data;
  protected MainActivity context;

  public SaveStringXmlTask(TreeMapTable<String,StringEntry> rest, 
                            MainActivity context)
  {
    this.data = rest;
    this.context = context;
  }

  @Override
  protected Void doInBackground(String[] p1)
  {
    boolean result = true;
    StringBuilder sb ;

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

      File saveFile = new File(context.getExternalFilesDir(null), "test-strings-" + lang + ".xml");
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
    String msg = context.getResources().getString(R.string.files_saved);
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    
  }//List<StringEntry> loadStringsXmlFile(String aFile)


}//class StringFileLoadTask
