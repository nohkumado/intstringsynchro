package com.nohkumado.intstringsynchro;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.nohkumado.intstringsynchro.*;
import java.io.*;
import java.util.*;

public class PathChecker
{
  private static final String TAG="PC"; /** needed for Log.d */
  
  Activity context;

  public PathChecker(Activity context)
  {
    this.context = context;
  }
  /**
   * check if the path is the path to a res dir, with a values folder inside and there a strings.xml
   */
  public String check(String farPath, ArrayList<Integer> error_codes, ArrayList<String> sb, Intent returnInt) 
  {
    File tstit;
    if (farPath.startsWith("/")) tstit = new File(farPath); 
    else  tstit = new File(Environment.getExternalStorageDirectory(),  farPath); 
    if (tstit.exists())
    {
      //Log.d(TAG, "path exists! " + tstit.getAbsolutePath());
      try
      {
        farPath = tstit.getCanonicalPath();
      }
      catch (IOException e)
      { Log.e(TAG, "something went wrong extracting the path from " + tstit.getAbsolutePath());}

      tstit = new File(tstit.getAbsoluteFile(), "values/strings.xml");
      if (tstit.exists()) return farPath;
      else
      {
        error_codes.add(MainActivity.PATH_INVALID);
        sb.add(tstit.getAbsolutePath() + " " + context.getResources().getString(R.string.missing_xml));
        return null;
      }//else
    }//if (tstit.exists())
    else 
    {
      error_codes.add(MainActivity.PATH_INVALID);
      sb.add(tstit.getAbsolutePath() + " " + context.getResources().getString(R.string.cd_does_not_exist));
      return null;
    }//else
  }//private String checkPath(String farPath, ArrayList<Integer> error_codes, ArrayList<String> sb, Intent returnInt) 
}
