# intstringsynchro
A small android helper to keep the string.xml ressources in sync 
between translations

at the moment i found 2 projects that try to chaieve this, but both are 
directed to commercial translators, who want to translate the stuff 
that wasn't translated, want time accounting and that sort of stuff...

as a dev, i just want a table with the available tokens and see where a
translation is missing...

just starting BTW...

thanks to https://android-arsenal.com/details/1/3950 for the diretory picker

BTW to validate a field you have to hit the enter key, otherwise the input is not taken, even if it is still written there
its a "feature" at least on my kbd the IME_ACTION_DONE is not triggered when i hit the TAB key...

to use it include the instringsynchro jar into your path, add the 


import com.nohkumado.intstringsynchro.*;


create a tablefragment:

protected StringXmlTableFrag tokenTable;

an in MainActivity create do:

   // find the retained fragment on activity restarts
    FragmentManager fm = getFragmentManager();
    tokenTable = (StringXmlTableFrag) fm.findFragmentByTag("data");
    // create the fragment and data the first time
    if (tokenTable == null)
    {
      // add the fragment
      tokenTable = new StringXmlTableFrag(this);
      fm.beginTransaction().add(tokenTable, "data").replace(R.id.table, tokenTable).commit();
    }



    Receiver:

    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.nohkumado.intstringsynchro" >

        <application
            android:allowBackup="true"
            android:icon="@drawable/ic_launcher"
            android:label="@string/app_name"
            android:theme="@style/AppTheme"
            android:resizeableActivity = "true">
            <activity
                android:name=".MainActivity"
                android:label="@string/app_name" >
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />
                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>
            <receiver android:name=".IntStringReceiver" android:exported="true"  android:enabled="true">
                <intent-filter>
                    <action android:name="com.nohkumado.intstringsynchro.EDIT_STRINGXML"/>
                    <category android:name="android.intent.category.DEFAULT"/>
                </intent-filter>
                <intent-filter>
                    <action android:name="com.nohkumado.intstringsynchro.ADD_STRINGXML"/>
                    <action android:name="com.nohkumado.intstringsynchro.DEL_STRINGXML"/>
                    <category android:name="android.intent.category.DEFAULT"/>
                    <data android:mimeType="text/plain"/>
                </intent-filter>
            </receiver>
        </application>
    </manifest>

    package com.nohkumado.intstringsynchro;
    import android.content.*;
    import android.widget.*;
    import android.util.*;

    public class IntStringReceiver extends BroadcastReceiver
    {
      public static final String TAG = "Receiver";
      @Override
      public void onReceive(Context context, Intent intent)
      {
        Toast.makeText(context, "Intent Detected:"+intent.getAction(), Toast.LENGTH_LONG).show();

        switch (intent.getAction())
        {
          case "com.nohkumado.intstringsynchro.EDIT_STRINGXML":
            {
              Intent intentStartMainActivity = new Intent(context, MainActivity.class);
              intentStartMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              context.startActivity(intentStartMainActivity);
              break;
            }
          case("com.nohkumado.intstringsynchro.ADD_STRINGXML"):
            {
              Toast.makeText(context, "add token "+intent.getExtras(), Toast.LENGTH_LONG).show();
              break;
            }
          case("com.nohkumado.intstringsynchro.DEL_STRINGXML"):
            {
              Toast.makeText(context, "del token "+intent.getExtras(), Toast.LENGTH_LONG).show();
              break;
            }
          default:
            {
              Toast.makeText(context, "no idea what to do with  "+intent, Toast.LENGTH_LONG).show();
              Log.d(TAG,"no idea what to do with  "+intent);
            }//default
        }//    switch (intent.getAction())
      }//  public void onReceive(Context context, Intent intent)
    }//class

    Tester:

    package com.nohkumado.istester;

    import android.app.*;
    import android.content.*;
    import android.net.*;
    import android.os.*;
    import android.view.*;
    import android.widget.*;

    public class MainActivity extends Activity 
    {
      @Override
      protected void onCreate(Bundle savedInstanceState)
      {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      }//onCreate
      public void callIntString()
      {
        callIntString(null);
      }
      public void callIntString(View but)
      {
        Toast.makeText(this, "call int string", Toast.LENGTH_SHORT).show(); 

        String name="com.nohkumado.intstringsynchro.EDIT_STRINGXML";
        Intent callIt = new Intent(name);
        try
        {
          startActivity(callIt);
        }
        catch (ActivityNotFoundException e)
        {
          Toast.makeText(this, "no available activity"+callIt, Toast.LENGTH_SHORT).show();
      
          //callGooglePlayStore();
        }
      }

      private void callGooglePlayStore()
      {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.vending");
        ComponentName comp = new ComponentName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity"); // package name and activity
        launchIntent.setComponent(comp);
        launchIntent.setData(Uri.parse("market://details?id=com.nohkumado.intstringsynchro"));

        startActivity(launchIntent);
      }//callIntString
    }
