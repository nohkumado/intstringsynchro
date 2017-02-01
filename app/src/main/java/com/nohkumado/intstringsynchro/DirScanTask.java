package com.nohkumado.intstringsynchro;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 *
 * AsyncTask to Load the ressource files without impacting the UI-thread 
 */


import android.os.*;
import android.util.*;
import java.util.*;
import android.app.*;

public class DirScanTask extends AsyncTask<String,Integer,Void>
{
  private ArrayList<StringFile> toLoad = new ArrayList<StringFile>(); 
  private MainActivity mA;

  public final static String TAG = "ScanDirTask";
  private ProgressDialog waitSymbol;

  public DirScanTask(MainActivity mainActivity, String startPath)
  {
    this.mA = mainActivity;
    waitSymbol = new ProgressDialog(mA);
    waitSymbol.setTitle("Scanning "+startPath);
    waitSymbol.show();
  }  
  @Override
  protected Void doInBackground(String[] p1)
  {
    StringBuilder error = new StringBuilder();
    DirectoryScanner scanner = new DirectoryScanner();
    for(String fName : p1) 
    {
      //Log.d(TAG,"calling find string file on "+fName);
      for(StringFile aFile: scanner.findStringFiles(fName,error)) toLoad.add(aFile);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result)
  {
    super.onPostExecute(result);
    waitSymbol.dismiss();
    mA.directoriesScanned(toLoad);
  }//  protected Void doInBackground(String[] p1)
  
}//public class DirScanTask extends AsyncTask<String,Integer,Void>



