package com.nohkumado.intstringsynchro;
import android.content.*;
import android.widget.*;

public class IntStringReceiver extends BroadcastReceiver
{

  @Override
  public void onReceive(Context context, Intent intent)
  {
    Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
    Intent intentStartMainActivity = new Intent(context,MainActivity.class);
    intentStartMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intentStartMainActivity);
  }
  
}
