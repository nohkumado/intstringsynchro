package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import java.util.*;

public class IntentArgsChecker
{
  Activity context;

  public IntentArgsChecker(Activity context)
  {
    this.context = context;
  }
  /**
   * check out if the arguments given with the intent match what we expect...
   */
  public boolean check(Bundle args, ArrayList<String> sb, ArrayList<Integer> error_codes)
  {
    boolean result = true;
    if (args == null) return false;
    if (args.getString("path") == null)
    {
      error_codes.add(MainActivity.MISSING_PATH);
      sb.add(context.getResources().getString(R.string.missing_path));
      result = false; //critical
    }//if (args.getString("path") == null)
    if (args.getString("mode") == null)
    {
      error_codes.add(MainActivity.MISSING_MODE);
      sb.add(context.getResources().getString(R.string.missing_mode));
      result = false; //critical
    }//if (args.getString("mode") == null)
    else
    {
      if (!args.getString("mode").matches("edit|add|del"))
      {
        error_codes.add(MainActivity.MODE_INVALID);
        sb.add(context.getResources().getString(R.string.mode_unknown));
        result = false;    
      }//if (!args.getString("mode").matches("edit|add|del"))
      else if (args.getString("mode").equals("add") && args.getString("value") == null)
      {
        error_codes.add(MainActivity.MISSING_VALUE);
        sb.add(context.getResources().getString(R.string.missing_value));
        result = false; 
      }//else if (args.getString("mode").equals("add") && args.getString("value") == null)
    }//else
    if (args.getString("token") == null && args.getString("mode") != null && !args.getString("mode").equals("edit"))
    {
      error_codes.add(MainActivity.MISSING_TOKEN);
      sb.add(context.getResources().getString(R.string.missing_token));
      result = false;
    }//if (args.getString("token") == null && args.getString("mode") != null && !args.getString("mode").equals("edit"))
    return result;
  }//private boolean checkIntentArgs(Bundle args, ArrayList<String> sb, ArrayList<Integer> error_codes)
}
