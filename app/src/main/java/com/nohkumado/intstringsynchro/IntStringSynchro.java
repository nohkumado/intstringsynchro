package com.nohkumado.intstringsynchro;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.nohkumado.nohutils.collection.*;
import java.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * secondary entry point, to handle add and delete of tokens called by intent from other apps 
 */
public class IntStringSynchro extends Activity
{
  protected ArrayList<String> langList; /** list of loaded languages */
  protected TreeMapTable<String,StringEntry> data; /** the table with the data */
  protected String actProjectPath = ""; /** the actual project path */
  protected int mode = -1; /** if called by intent, what mode was used */

  private static final String TAG="MA"; /** needed for Log.d */
  public static final int MODE_ADD=1; /** mode enum , but since android doesn't like enums... */
  public static final int MODE_DEL=2;
  public static final int MODE_UNKNOWN=3;
  private Bundle intentArgs; /** just to hold the arguments of the incoming bundle */

  public final int MISSING_PATH = 10; /** error code enum */
  public final int MISSING_MODE = 11;
  public final int MISSING_VALUE = 12;
  public final int MISSING_TOKEN = 13;
  public final int PATH_INVALID = 14;
  public final int MODE_INVALID = 15;
  /** urls to call this intent with */
  public final String ERROR = "com.nohkumado.intstringsynchro.ERROR";
  public final String ACTION = "com.nohkumado.intstringsynchro.ACTION";

  /**
   *   onCreate
   * @arg savedInstanceState
   *
   * testet the incoing intent with this:
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
   //intent.putExtra("type", "string|array|plural");
   //intent.putExtra("token", "testit");
   //intent.putExtra("value", "a test");
   //intent.putExtra("value-de", "ein Test");
   //testing remove
   //intent.setAction("DEL");
   //intent.putExtra("path", "AppProjects/IntStringSynchro/app/src/main/res");//relative
   //intent.putExtra("mode", "del");
   //intent.putExtra("token", "testit");
   */
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    //Log.d(TAG,"§§§§§§§§§§§§§§§§§§§§§§§§§§§§ headless §§§§§§§§§§§§§§§§§§§§§§§§§");
    //setRetainInstance(true);
    // Get the intent that started this activity
    Intent intent = getIntent();
    //Toast.makeText(this, "headless mode " + intent + " of action " + intent.getAction() + " of type " + intent.getType(), Toast.LENGTH_LONG).show();
    data = new TreeMapTable<>(); //headless mode!! 
    langList = new ArrayList<>();
    Intent returnInt = new Intent();
    returnInt.setAction(ACTION);
    ArrayList<String> sb = new ArrayList<>();
    ArrayList<Integer> error_codes = new ArrayList<>();

    //check validity of send data
    Bundle args = intent.getExtras();
    String farmode = intent.getAction();
    if (farmode != null ) farmode = farmode.toLowerCase().trim();
    if (args != null) args.putString("mode", farmode);

    IntentArgsChecker checker = new IntentArgsChecker(this);
    if (checker.check(args, sb, error_codes))
    {
      //Log.d(TAG, "intent is  " + intent.getAction() + " with " + intent.getExtras());
      // Toast.makeText(this, "has data " + intent.getData() + " of type " + intent.getType(), Toast.LENGTH_LONG).show();
      String farPath = args.getString("path");
      PathChecker pCheck = new PathChecker(this);
      farPath = pCheck.check(farPath, error_codes, sb, returnInt);
      if (farPath == null) bailOut(returnInt, error_codes, sb);
      //Log.d(TAG,"farpath now "+farPath);
      actProjectPath = farPath;

      switch (farmode)
      {
        case("add"):
          mode = MODE_ADD;
          intentArgs = args;
          loadFiles(farPath + "/values");
          LangNameNormalizer normalizer = new LangNameNormalizer();

          String langCand = null;
          for (String key: intentArgs.keySet())
          {
            if (key.equals("value")) langCand = "default";
            else if (key.startsWith("value")) langCand = normalizer.normalizeLangName(key.replace("value-", ""));
            else langCand = null; //ignoring the rest

            if (langCand != null)
            {
              //Log.d(TAG, "found langCand " + langCand);
              String token = intentArgs.getString("token");
              String type = intentArgs.getString("type");
              if(type == null) type = "string";
              
              StringEntry newEntry = null;
              switch(type)
              {
                case "string":
					  CdataString txt = new CdataString(intentArgs.get(key).toString());
                  newEntry = new StringEntry(token, txt);
                  break;
                case "array":
                  newEntry = new ArrayEntry(token);
					  ((ArrayEntry)newEntry).add(new CdataString(intentArgs.get(key).toString().trim()));
                  break;
                case "plural":
                  newEntry = new PluralEntry(token);
					  ((PluralEntry)newEntry).put("one",new CdataString(intentArgs.get(key).toString().trim()));
                  
                  break;
              }
              
              data.set(token, langCand, newEntry);
            }//if (langCand != null)
          }//for(String key: intentArgs.keySet())
          break;
        case("del"):
          mode = MODE_DEL;
          intentArgs = args;
          loadFiles(farPath + "/values");
          data.remove(intentArgs.getString("token"));
          
          break;
        default:
          error_codes.add(MODE_UNKNOWN);
          sb.add(getResources().getString(R.string.mode_unknown));
          bailOut(returnInt, error_codes, sb);
      }//switch
      //Log.d(TAG,"saving");
      SaveStringXmlTask task = new SaveStringXmlTask(data, this, farPath);
      task.saveData();
      //just to test if we are able to bail out...
      //bailOut(returnInt, error_codes, sb);
    }//if (checkIntentArgs(args, returnInt))
    else bailOut(returnInt, error_codes, sb);
    setResult(Activity.RESULT_OK,returnInt);
    //Log.d(TAG,"set the result"+returnInt+" and finishing");
    //Log.d(TAG,"§§§§§§§§§§§§§§§§§§§§§§§§§§§§ headless end §§§§§§§§§§§§§§§§§§§§§§§§§");
    finish();
  }
/**
* loadFiles
* analyze the filename, check there are value directories in it and 
* launch the task that will load the files
*/
  private void loadFiles(String fName)
  {
    ArrayList<StringFile> toLoad = null;
    StringBuilder error = new StringBuilder();
    StringFileLoadTask task = new StringFileLoadTask(data, this);
    if (fName != null) toLoad = task.findStringFiles(fName, error);
    else error.append("select the values dir");
    if (toLoad.size() > 0)
    {
      for(StringFile aFile: toLoad) task.loadStringsXmlFile(aFile);
    }
    //here we do everything on hte main thread... otherwise we can't send back the result...
      //task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
  }//protected void onCreate(Bundle savedInstanceState)
  /**
   * bailout
   * something bad happened, lets leave it at that...
   */
  private void bailOut(Intent returnInt, ArrayList<Integer> error_codes, ArrayList<String> sb)
  {
    Toast.makeText(this, "we have errors, bailing out :" + sb, Toast.LENGTH_LONG).show();

    returnInt.setAction(ERROR);
    returnInt.putExtra("codes", error_codes);
    returnInt.putExtra("msg", sb); 
    // Create intent to deliver some kind of result data
    //Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));

    setResult(Activity.RESULT_CANCELED, returnInt);
    //Log.d(TAG, "we have errors, bailing out " + returnInt + " ext:" + returnInt.getExtras());
    finish();
  }//private void bailOut(Intent returnInt, ArrayList<Integer> error_codes, ArrayList<String> sb)
  
}
