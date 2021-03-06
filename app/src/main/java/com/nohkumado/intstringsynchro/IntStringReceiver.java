package com.nohkumado.intstringsynchro;
import android.content.*;
import android.widget.*;
import android.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * legacy, was trying to find the best way to be callable from another activity
 * but this is way too slow...
 * but in case it should be still implemented, still here, is published in Manifest...
 * so if we remove it here, we should also remove it there...
 */
public class IntStringReceiver extends BroadcastReceiver
{
  public static final String TAG = "Receiver"; /** needed for Log.d */
  /**
   * intent broadcasted detected
   */
  @Override
  public void onReceive(Context context, Intent intent)
  {
    //Toast.makeText(context, "Intent Detected."+intent.getAction(), Toast.LENGTH_LONG).show();

    switch (intent.getAction())
    {
      case("com.nohkumado.intstringsynchro.ADD_STRINGXML"):
        {
          Toast.makeText(context, "add token " + intent.getExtras(), Toast.LENGTH_LONG).show();

          break;
        }
      case("com.nohkumado.intstringsynchro.DEL_STRINGXML"):
        {
          Toast.makeText(context, "del token " + intent.getExtras(), Toast.LENGTH_LONG).show();

          break;
        }
      default:
        {
          Toast.makeText(context, "no idea what to do with  " + intent, Toast.LENGTH_LONG).show();
          //Log.d(TAG, "no idea what to do with  " + intent);

        }//default
    }//    switch (intent.getAction())
  }//  public void onReceive(Context context, Intent intent)
}//public class IntStringReceiver extends BroadcastReceiver
