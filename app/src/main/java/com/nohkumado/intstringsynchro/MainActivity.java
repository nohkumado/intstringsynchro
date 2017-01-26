package com.nohkumado.intstringsynchro;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.github.angads25.filepicker.controller.*;
import com.github.angads25.filepicker.model.*;
import com.github.angads25.filepicker.view.*;
import com.nohkumado.nohutils.collection.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.xmlpull.v1.*;

import android.view.View.OnClickListener;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * main entry point, mainly just to fire up the right fragment and manage the file loaders 
 */
public class MainActivity extends Activity implements OnClickListener, 
DialogSelectionListener//, OnEditorActionListener
{
  protected Button openProject;/** fire up the filebrowser */ 
  protected ImageButton moveUpBut,saveBut, helpBut; /** different buttons for overall function */

  //protected ArrayList<String> langList; /** list of loaded languages */
  protected TreeMapTable<String,StringEntry> data; /** the table with the data */
  protected String actProjectPath = ""; /** the actual project path */
  protected int mode = -1; /** if called by intent, what mode was used */

  private static final String TAG="MA"; /** needed for Log.d */
  public static final int MODE_ADD=1; /** mode enum , but since android doesn't like enums... */
  public static final int MODE_DEL=2;
  public static final int MODE_UNKNOWN=3;

  protected StringXmlTableFrag tokenTable; /** the fragment that does the real work */

  private Bundle intentArgs; /** just to hold the arguments of the incoming bundle */

  public static final int MISSING_PATH = 10; /** error code enum */
  public static final int MISSING_MODE = 11;
  public static final int MISSING_VALUE = 12;
  public static final int MISSING_TOKEN = 13;
  public static final int PATH_INVALID = 14;
  public static final int MODE_INVALID = 15;
  /** urls to call this intent with */
  public static final String ERROR = "com.nohkumado.intstringsynchro.ERROR";
  public static final String ACTION = "com.nohkumado.intstringsynchro.ACTION";
  /** the link to the ehm "docu" */
  private static final String manualUrl = "https://sites.google.com/site/nohkumado/home/projects/intstringsynchro";
  /** CTOR */
  public MainActivity()
  {
  }
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
    setContentView(R.layout.main);
    SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
    if (prefs.contains("actprojectpath")) actProjectPath = prefs.getString("actprojectpath", "");
    // Get the intent that started this activity
    Intent intent = getIntent();

    //Log.d(TAG, "received intent of action " + intent.getAction() + " vs " + Intent.ACTION_MAIN);
    // Figure out what to do based on the intent type
    if (!intent.getAction().equals(Intent.ACTION_MAIN))
    {
      //Toast.makeText(this, "headless mode " + intent + " of action " + intent.getAction() + " of type " + intent.getType(), Toast.LENGTH_LONG).show();
      data = new TreeMapTable<>(); //headless mode!! 
      //langList = new ArrayList<>();
      //Log.d(TAG,"got called by an intent");
      //Uri data = intent.getData();
      Intent returnInt = new Intent();
      returnInt.setAction(ACTION);
      ArrayList<String> sb = new ArrayList<>();
      ArrayList<Integer> error_codes = new ArrayList<>();

      //Log.d(TAG, "received intent " + intent + " of action " + intent.getAction() + " of type " + intent.getType()+" "+intent.getExtras());
      //Toast.makeText(this, "received intent " + intent + " of action " + intent.getAction() + " of type " + intent.getType(), Toast.LENGTH_LONG).show();
      //check validity of send data
      Bundle args = intent.getExtras();
      String farmode = "";
      if (args != null)
      {
        farmode = args.getString("mode");
        if (farmode == null || farmode.length() <= 0) farmode = intent.getAction();
        farmode = farmode.toLowerCase().trim();
        args.putString("mode", farmode);
      }//if (args != null)
      //if (checkIntentArgs(args, sb, error_codes))
      
      IntentArgsChecker checker = new IntentArgsChecker(this);
      if (checker.check(args, sb, error_codes))
      {
        //Log.d(TAG, "has data " + intent.getData() + " of type " + intent.getType());
        // Toast.makeText(this, "has data " + intent.getData() + " of type " + intent.getType(), Toast.LENGTH_LONG).show();

        String farPath = args.getString("path");
        PathChecker pCheck = new PathChecker(this);
        farPath = pCheck.check(farPath, error_codes, sb, returnInt);
        if(farPath == null) bailOut(returnInt, error_codes, sb);
        
        //farPath = checkPath(farPath, error_codes, sb, returnInt);
        //Log.d(TAG,"farpath now "+farPath);
        actProjectPath = farPath;

        switch (farmode)
        {
          case("add"):
            mode = MODE_ADD;
            intentArgs = args;
            onSelectedFilePaths(new String[] {farPath + "/values"});
            break;
          case("del"):
            mode = MODE_DEL;
            intentArgs = args;
            onSelectedFilePaths(new String[] {farPath + "/values"});
            break;
          case "edit":
            actProjectPath = farPath;
            //TODO check if token is present, center on it and set the focus into it
            break;
          default:
            error_codes.add(MODE_UNKNOWN);
            sb.add(getResources().getString(R.string.mode_unknown));
            bailOut(returnInt, error_codes, sb);
        }//switch
        //just to test if we are able to bail out...
        //bailOut(returnInt, error_codes, sb);
      }//if (checkIntentArgs(args, returnInt))
      else bailOut(returnInt, error_codes, sb);
    }//if (intent != null && !intent.getAction().equals(Intent.ACTION_MAIN))  
    //else Log.d(TAG, "got called standalone ");
    //else  Toast.makeText(this, "called from launcher " + intent + " of action " + intent.getAction() + " of type " + intent.getType(), Toast.LENGTH_LONG).show();
    // find the retained fragment on activity restarts
    FragmentManager fm = getFragmentManager();
    tokenTable = (StringXmlTableFrag) fm.findFragmentByTag("data");
    // create the fragment and data the first time
    if (tokenTable == null)
    {
      // add the fragment
      tokenTable = new StringXmlTableFrag(this);
      fm.beginTransaction().add(tokenTable, "data").replace(R.id.table, tokenTable).commit();
    }//if (tokenTable == null)
    data = tokenTable.getData();
    //langList = tokenTable.getLangList();

//    if (savedInstanceState != null)
//    {
//      Log.d(TAG, "#############################  restart ###################################");  
//    }
//    else
//    {
//      Log.d(TAG, "#############################  start ###################################");  
//    }

    //Log.d(TAG,"data : "+data);
    openProject  = (Button) findViewById(R.id.addProjBut);
    openProject.setOnClickListener(this);

    moveUpBut   = (ImageButton) findViewById(R.id.moveUpBut);
    moveUpBut.setOnClickListener(this);

    saveBut = (ImageButton) findViewById(R.id.saveBut);
    saveBut.setOnClickListener(this);

    helpBut = (ImageButton) findViewById(R.id.helpBut);
    helpBut.setOnClickListener(this);

    if (actProjectPath != null && actProjectPath.length() > 0 && data.size() <= 0) 
    {
      //Log.d(TAG, "calling load on " + actProjectPath + "/values");
      onSelectedFilePaths(new String[] {actProjectPath + "/values"});
    }//if (actProjectPath != null && actProjectPath.length() > 0 && data.size() <= 0) 
  }//protected void onCreate(Bundle savedInstanceState)
  /**
   * check if the path is the path to a res dir, with a values folder inside and there a strings.xml
   */
  /*private String checkPath(String farPath, ArrayList<Integer> error_codes, ArrayList<String> sb, Intent returnInt) 
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
        error_codes.add(PATH_INVALID);
        sb.add(tstit.getAbsolutePath() + " " + getResources().getString(R.string.missing_xml));
        bailOut(returnInt, error_codes, sb);
      }//else
    }//if (tstit.exists())
    else 
    {
      error_codes.add(PATH_INVALID);
      sb.add(tstit.getAbsolutePath() + " " + getResources().getString(R.string.cd_does_not_exist));
      bailOut(returnInt, error_codes, sb);
    }//else
    return farPath;
  }//private String checkPath(String farPath, ArrayList<Integer> error_codes, ArrayList<String> sb, Intent returnInt) 
  */
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
  /**
   * check out if the arguments given with the intent match what we expect...
   */
  /*private boolean checkIntentArgs(Bundle args, ArrayList<String> sb, ArrayList<Integer> error_codes)
  {
    boolean result = true;
    if (args == null) return false;
    if (args.getString("path") == null)
    {
      error_codes.add(MISSING_PATH);
      sb.add(getResources().getString(R.string.missing_path));
      result = false; //critical
    }//if (args.getString("path") == null)
    if (args.getString("mode") == null)
    {
      error_codes.add(MISSING_MODE);
      sb.add(getResources().getString(R.string.missing_mode));
      result = false; //critical
    }//if (args.getString("mode") == null)
    else
    {
      if (!args.getString("mode").matches("edit|add|del"))
      {
        error_codes.add(MODE_INVALID);
        sb.add(getResources().getString(R.string.mode_unknown));
        result = false;    
      }//if (!args.getString("mode").matches("edit|add|del"))
      else if (args.getString("mode").equals("add") && args.getString("value") == null)
      {
        error_codes.add(MISSING_VALUE);
        sb.add(getResources().getString(R.string.missing_value));
        result = false; 
      }//else if (args.getString("mode").equals("add") && args.getString("value") == null)
    }//else
    if (args.getString("token") == null && args.getString("mode") != null && !args.getString("mode").equals("edit"))
    {
      error_codes.add(MISSING_TOKEN);
      sb.add(getResources().getString(R.string.missing_token));
      result = false;
    }//if (args.getString("token") == null && args.getString("mode") != null && !args.getString("mode").equals("edit"))
    return result;
  }//private boolean checkIntentArgs(Bundle args, ArrayList<String> sb, ArrayList<Integer> error_codes)
*/
  /**
   * check if the files are there and load them
   */
  public void checkAndLoadFiles(String[] p1)
  {
    ArrayList<StringFile> toLoad = new ArrayList<StringFile>(); 
    StringBuilder error = new StringBuilder();
    if (p1.length == 1)
    {
      actProjectPath = p1[0];
      File resValuesDir = new File(actProjectPath);
      if (resValuesDir.exists())
      {
        boolean found = false;
        for (String aFile: resValuesDir.list())
        {
          if ("strings.xml".equals(aFile))
          {
            //TODO for later ArrayList<String> list = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.strings)));//where R.array.strings is a reference to your resource
            //Toast.makeText(this, "found strings.xml", Toast.LENGTH_LONG).show();
            found = true;
            toLoad.add(new StringFile(resValuesDir, aFile, "default"));
            //loadStringsXmlFile(new File(resValuesDir, aFile), "default");

            break;
          }//if ("strings.xml".equals(aFile))
        }//for (String aFile: resValuesDir.list())
        if (found)
        {
          //ok we have the right directory
          File resDir = new File(resValuesDir, "../");

          //save the actual value of the path
          SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
          if (prefs.contains("actprojectpath")) actProjectPath = prefs.getString("actprojectpath", "");

          SharedPreferences.Editor editor = prefs.edit();
          try
          {
            editor.putString("actprojectpath", resDir.getCanonicalPath());
          }//try
          catch (IOException e)
          {
            Log.e(TAG, "coouldn't set path to " + resDir.getAbsolutePath());
          }//catch (IOException e)
          editor.apply();
          final Pattern p = Pattern.compile("values-[a-z\\-]{2,}");
          //select allready selected languages
          String[] files = resDir.list(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name)
              {
                Matcher m = p.matcher(name);
                return m.find();
                //return name.matches("values-\\[a-z\\-]{2,}");
              }
            });
          //Log.d(TAG, "checking parent directory " + Arrays.toString(files));
          Pattern onlyLang = Pattern.compile("^values-(.*)$");
          for (String aLang : files)
          {
            //Log.d(TAG, "checking alternate " + aLang);
            Matcher mlang = onlyLang.matcher(aLang);
            if (mlang.find())
            {
              String sanitized = mlang.group(1);
              tokenTable.addNewLang(sanitized);
              StringFile resLangFile = new StringFile(resDir, aLang + "/strings.xml", sanitized);

              if (resLangFile.exists()) toLoad.add(resLangFile);
              //loadStringsXmlFile(resLangFile, sanitized);    
            }//if (mlang.find())
            else Log.e(TAG, "wrng pattern " + mlang);
          }//for (String aLang : files)
        }//if (found)
        else error.append("no strings.xml found in").append(actProjectPath);
      }//if (resValuesDir.exists())
      else error.append("Dir not found: " + actProjectPath);
    }//if (p1.length == 1)
    else error.append("select the values dir");

    if (error.toString().length() > 0) Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    if (toLoad.size() > 0)
    {
      StringFileLoadTask task = new StringFileLoadTask(data, this);
      task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
    }//if (toLoad.size() > 0)
  }//  public void onSelectedFilePaths(String[] p1)
  /**
   * one of the buttons was clicked 
   */
  @Override
  public void onClick(View p1)
  {
    if (p1 == openProject)  
    {
      //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      //intent.addCategory(Intent.CATEGORY_OPENABLE);
      //intent.setType("*/*"); 
      //startActivityForResult(intent, PROJECT_CHOOSED);
      showFileChooser();
    }//if (p1 == openProject)  
    else if (p1 == moveUpBut)
    {
      File actDir = new File(actProjectPath, "../");
      try
      {
        actProjectPath = actDir.getCanonicalPath();
      }
      catch (IOException e)
      {}
    }//else if (p1 == moveUpBut)
    else if (p1 == saveBut) saveFiles();
    else if (p1 == helpBut)
    {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(manualUrl));
      startActivity(browserIntent);
    }
  }//public void onClick(View p1)
  /**
   * fire up a file chooser
   */
  private void showFileChooser()
  {
    DialogProperties properties=new DialogProperties();
    properties.selection_mode = DialogConfigs.SINGLE_MODE;
    properties.selection_type = DialogConfigs.DIR_SELECT;
    if (actProjectPath.length() > 0)  properties.root = new File(actProjectPath);
    else properties.root = new File(DialogConfigs.DEFAULT_DIR);

    properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
    properties.extensions = null;

    FilePickerDialog dialog = new FilePickerDialog(this, properties);
    dialog.setTitle(getResources().getString(R.string.open_stringxml));
    dialog.setDialogSelectionListener(this);
    dialog.show();

//    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//    //intent.setType("*/*");      //all files
//    intent.setType("text/xml");   //XML file only
//    intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//    try
//    {
//      startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.open_stringxml)), PROJECT_CHOOSED);
//    }
//    catch (android.content.ActivityNotFoundException ex)
//    {
//      // Potentially direct the user to the Market with a Dialog
//      Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//    }
  }//private void showFileChooser()
  /**
   * return from the filechooserdialog
   */
  @Override
  public void onSelectedFilePaths(String[] p1)
  {
    ArrayList<StringFile> toLoad = null;
    StringBuilder error = new StringBuilder();
    StringFileLoadTask task = new StringFileLoadTask(data, this);
    if (p1.length == 1) toLoad = task.findStringFiles(p1[0],error);
    else error.append("select the values dir");
    if (toLoad.size() > 0)
    {
      savePathToPrefs(p1);
      task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
    }//if (toLoad.size() > 0)
    
/*    
    String pathToLoad;
    ArrayList<StringFile> toLoad = new ArrayList<StringFile>(); 
    StringBuilder error = new StringBuilder();
    if (p1.length == 1)
    {
      pathToLoad = p1[0];
      File resValuesDir = new File(pathToLoad);
      if (resValuesDir.exists())
      {
        boolean found = false;
        for (String aFile: resValuesDir.list())
        {
          if ("strings.xml".equals(aFile))
          {
            //TODO for later ArrayList<String> list = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.strings)));//where R.array.strings is a reference to your resource
            found = true;
            toLoad.add(new StringFile(resValuesDir, aFile, "default"));
            break;
          }//if ("strings.xml".equals(aFile))
        }//for (String aFile: resValuesDir.list())
        if (found)
        {
          //ok we have the right directory
          File resDir = new File(resValuesDir, "../");

          if (mode <= 0)
          {
            //save the actual value of the path
            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.contains("actprojectpath")) pathToLoad = prefs.getString("actprojectpath", "");

            SharedPreferences.Editor editor = prefs.edit();
            try
            {
              editor.putString("actprojectpath", resDir.getCanonicalPath());
            }//try
            catch (IOException e)
            {
              Log.e(TAG, "coouldn't set path to " + resDir.getAbsolutePath());
            }//catch (IOException e)
            editor.apply();
          }//if (mode <= 0)
          final Pattern p = Pattern.compile("values-[a-z\\-]{2,}");
          //select allready selected languages
          String[] files = resDir.list(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name)
              {
                Matcher m = p.matcher(name);
                return m.find();
                //return name.matches("values-\\[a-z\\-]{2,}");
              }
            });
          //Log.d(TAG, "checking parent directory " + Arrays.toString(files));
          Pattern onlyLang = Pattern.compile("^values-(.*)$");
          for (String aLang : files)
          {
            //Log.d(TAG, "checking alternate " + aLang);
            Matcher mlang = onlyLang.matcher(aLang);
            if (mlang.find())
            {
              String sanitized = mlang.group(1);
              if (tokenTable != null) tokenTable.addNewLang(sanitized);
              StringFile resLangFile = new StringFile(resDir, aLang + "/strings.xml", sanitized);

              if (resLangFile.exists()) toLoad.add(resLangFile);
              //loadStringsXmlFile(resLangFile, sanitized);    
            }//if (mlang.find())
            else Log.e(TAG, "wrng pattern " + mlang);
          }//for (String aLang : files)
        }//if(found)
        else error.append("no strings.xml found in").append(pathToLoad);
      }//if (resValuesDir.exists())
      else error.append("Dir not found: " + pathToLoad);
    }//if (p1.length == 1)
    else error.append("select the values dir");

    if (error.toString().length() > 0) Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    if (toLoad.size() > 0)
    {
      StringFileLoadTask task = new StringFileLoadTask(data, this);
      task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
    }//if (toLoad.size() > 0)
    */
  }

  private void savePathToPrefs(String[] p1)
  {
    if (mode <= 0)
    {
      //save the actual value of the path
      SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
      //if (prefs.contains("actprojectpath")) p1[0] = prefs.getString("actprojectpath", "");
      File resDir = new File(new File(p1[0]), "../");
      SharedPreferences.Editor editor = prefs.edit();
      try
      {
        editor.putString("actprojectpath", resDir.getCanonicalPath());
      }//try
      catch (IOException e)
      {
        Log.e(TAG, "coouldn't set path to " + resDir.getAbsolutePath());
      }//catch (IOException e)
      editor.apply();
    }//if (mode <= 0)
  }//  public void onSelectedFilePaths(String[] p1)
  /**
   * callback needed by the loader task to signify the data is ready
   * cleans everything and then rebuild the UI
   */
  public void buildTableView()
  {/** headless mode */
    if (mode > 0)
    {
      //Log.d(TAG, "back from loading");
      switch (mode)
      {
        case(MODE_ADD):
          //Log.d(TAG, "case add");
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
              StringEntry newEntry = new StringEntry(token, intentArgs.get(key).toString());
              data.set(token, langCand, newEntry);
            }//if (langCand != null)
            else Log.d(TAG, "not a value " + key);
          }//for(String key: intentArgs.keySet())
          break;
        case(MODE_DEL):
          Log.d(TAG, "case del");
          data.remove(intentArgs.getString("token"));
          break;
      }//switch (mode)
      saveFiles();
    }//if (mode > 0)
    else tokenTable.buildTableView();
  }//public void buildTableView()
  /**
   * @return boolean  if it was a success
   * saveFiles
   */  
  protected boolean saveFiles()
  {
    boolean result = true;
    //Log.d(TAG, "in save files " + actProjectPath);
    SaveStringXmlTask task = new SaveStringXmlTask(data, this, actProjectPath);
    task.execute(new String[0]);
    return result;
  }//protected boolean saveFiles()
  /** 
   getter for the actual stored path
   */
  public String getProjectPath()
  {
    return actProjectPath;
  }
  /** 
   @deprecated 
   send out a broadcasintent to ask for editing
   */
  public void broadcastIntent(View view)
  {
    Intent intent = new Intent();
    intent.setAction("com.nohkumado.EDIT_STRINGXML");
    sendBroadcast(intent);
  }
  /** 
   * callback for when the files were saved
   */
  public void filesSaved()
  {
    Log.d(TAG, "files saved");
    if (mode > 0)
    {
      Intent returnInt = new Intent();
      returnInt.setAction(ACTION);
      setResult(Activity.RESULT_OK, returnInt);
      Log.d(TAG, "headless save end " + returnInt + " " + returnInt.getExtras());
      finish();
    }//if (mode > 0)
    else
      Toast.makeText(this, getResources().getString(R.string.files_saved) + " '" + actProjectPath+"'", Toast.LENGTH_SHORT).show();
  }//public void filesSaved()
  /**
  * when back is pressed we need to catch it, control, if we were called through intent
  * and if needed send back an answer intent
  */
  @Override
  public void onBackPressed()
  {
    super.onBackPressed();
    if (getIntent() != null && !getIntent().getAction().equals(Intent.ACTION_MAIN))
    {
      Intent returnInt = new Intent();
      returnInt.setAction(ACTION);
      setResult(Activity.RESULT_OK, returnInt);
      //Log.d(TAG, "normal end of back on intent call " + returnInt + " " + returnInt.getExtras()); 
      finish();
    }//if (error_codes.size() > 0)
    //else Log.d(TAG, "standalino back"); 
  }//public void onBackPressed()
}//public class MainActivity extends Activity implements OnClickListener, 
