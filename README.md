# intstringsynchro
A small android helper to keep the string.xml ressources in sync 
between translations

at the moment i found 2 projects that try to achieve this, but both are 
directed to commercial translators, who want to translate the stuff 
that wasn't translated, want time accounting and that sort of stuff...

as a dev, i just want a table with the available tokens and see where a
translation is missing...

thanks to https://android-arsenal.com/details/1/3950 for the diretory picker
it is buggy, since it can't backnavigate once a path was given, but it works reasonably well

BTW to validate a field you have to hit the enter key, otherwise the input is not taken, even if it is still written there
its a "feature" at least on my kbd the IME_ACTION_DONE is not triggered when i hit the TAB key...
You are responsible on what you write into, NO CHECKING on the data is done, so beware!!


# include it into your own code

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

# call it through intent

    //testing edit
    //intent.setAction("EDIT");
    //intent.putExtra("mode", "edit");
    //intent.putExtra("path","/at/timbouktou");//wrong
    //intent.putExtra("path", "AppProjects/IntStringSynchro/app/src/main/res");//relative
    //intent.putExtra("path","/storage/emulated/0/AppProjects/IntStringSynchro/app/src/main/res");//absolute

    //testing add
    //intent.setAction("ADD");
    //intent.putExtra("path", "AppProjects/IntStringSynchro/app/src/main/res");//relative
    //intent.putExtra("mode", "add");
    //intent.putExtra("token", "testit");
    //intent.putExtra("value", "a test");
    //intent.putExtra("value-de", "ein Test");
    //testing remove
    //intent.setAction("DEL");
    //intent.putExtra("path", "AppProjects/IntStringSynchro/app/src/main/res");//relative
    //intent.putExtra("mode", "del");
    //intent.putExtra("token", "testit");
    
    
at the moment it is possible to call the app that way, the add and rm work, but my tester never got 
neither the error messages nor the result intent, so if anyone spots whats going wrong here....  
the test project:  

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
