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
  //protected Button testBut, resetBut;/** fire up the filebrowser */ 
  protected Button resetBut;/** reset the path */ 
  protected ImageButton saveBut, helpBut; /** different buttons for overall function */

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

  public final static int ASK_STRINGMOD = 10;

  private TextView pathView;
  /** CTOR */
  public MainActivity()
  {
  }
  /**
   *   onCreate
   * @arg savedInstanceState
   *
   * testet the incoing intent with this:
   */
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    /*if (savedInstanceState != null)
     {
     Log.d(TAG, "#############################  restart ###################################");  
     }
     else
     {
     Log.d(TAG, "#############################  start ###################################");  
     }*/
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
        if (farPath == null) bailOut(returnInt, error_codes, sb);

        //farPath = checkPath(farPath, error_codes, sb, returnInt);
        //Log.d(TAG,"farpath now "+farPath);
        actProjectPath = farPath;

        switch (farmode)
        {
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

    //Log.d(TAG,"data : "+data);

    saveBut = (ImageButton) findViewById(R.id.saveBut);
    saveBut.setOnClickListener(this);

    helpBut = (ImageButton) findViewById(R.id.helpBut);
    helpBut.setOnClickListener(this);

    //testBut = (Button) findViewById(R.id.addTokBut);
    //testBut.setOnClickListener(this);

    resetBut = (Button) findViewById(R.id.reset_path);
    resetBut.setOnClickListener(this);


    pathView = (TextView) findViewById(R.id.path_view);
    pathView.setText(actProjectPath);
    pathView.setOnClickListener(this);
    Log.d(TAG,"got here trying to load '"+actProjectPath+"'");
    if (actProjectPath == null || actProjectPath.length() <= 0 || tryToFindADefaultDir().equals(actProjectPath))
    {
      Log.d(TAG,"trying to load '"+tryToFindADefaultDir() + "/AppProjects"+"'");
      
      callProjectSelect(tryToFindADefaultDir() + "/AppProjects");
    }//if(actProjectPath == null || tryToFindADefaultDir().equals(actProjectPath))
    else if (actProjectPath != null && actProjectPath.length() > 0 && data.size() <= 0) 
    {
      //Log.d(TAG, "calling load on " + actProjectPath + "/values");

      onSelectedFilePaths(new String[] {actProjectPath });
      pathView.setText(actProjectPath);
    }//if (actProjectPath != null && actProjectPath.length() > 0 && data.size() <= 0) 
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
    if (p1 == pathView)  
    {
      //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      //intent.addCategory(Intent.CATEGORY_OPENABLE);
      //intent.setType("*/*"); 
      //startActivityForResult(intent, PROJECT_CHOOSED);
      showFileChooser();
    }//if (p1 == openProject)  
    else if (p1 == saveBut) saveFiles();
    else if (p1 == helpBut)
    {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(manualUrl));
      startActivity(browserIntent);
    }
    else if (p1 == resetBut)
    {
      tokenTable.clear();
      actProjectPath = tryToFindADefaultDir();
      savePathToPrefs(actProjectPath);
      pathView.setText(actProjectPath);
      buildTableView();
    }
    /*
     else if (p1 == testBut)
     {
     String tstpathToProject = "AppProjects/IntStringIntentTester/app/src/main/res";

     //IntStringSynchro testit = new IntStringSynchro();
     //testit.onCreate(savedInstanceState);

     //Intent callIt = new Intent(this, IntStringSynchro.class);
     Intent callIt = new Intent();
     callIt.setClassName("com.nohkumado.intstringsynchro", "IntStringSynchro");
     //callIt.setComponent(new ComponentName("com.nohkumado.intstringsynchro", "./IntStringSynchro"));
     callIt.setComponent(new ComponentName("com.nohkumado.intstringsynchro", "com.nohkumado.intstringsynchro.IntStringSynchro"));
     callIt.addCategory("android.intent.category.EMBED");
     callIt.setAction("ADD");
     //call.setClassName("com.nohkumado.intstringsynchro","IntStringSynchro");

     //Intent callIt = new Intent(name);
     //callIt.setPackage("com.nohkumado.intstringsynchro");
     callIt.putExtra("token", "testtoto");
     callIt.putExtra("value", "toto");
     callIt.putExtra("value-de", "tata");
     callIt.setAction("ADD");
     callIt.putExtra("path", tstpathToProject);//relative
     //intent.putExtra("mode", "add");
     //intent.putExtra("value", "a test");
     //intent.putExtra("value-de", "ein Test");
     Log.d(TAG, "sending intent " + callIt + " " + callIt.getExtras());

     try
     {
     //sendBroadcast(callIt);
     //startActivity(callIt);
     startActivityForResult(callIt, ASK_STRINGMOD);
     }
     catch (ActivityNotFoundException e)
     {
     Toast.makeText(this, "no available activity " + callIt, Toast.LENGTH_SHORT).show();
     }
     }*/
  }

  private String tryToFindADefaultDir()
  {
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state))
    {
      // We can read and write the media
      return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
    {
      // We can only read the media
      return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    else
    {
      return getExternalFilesDir(null).getAbsolutePath();
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
    properties.root = new File(tryToFindADefaultDir());

    if (actProjectPath.length() > 0)  properties.offset = new File(actProjectPath);
    else properties.offset = new File(DialogConfigs.DEFAULT_DIR);

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
  public void onSelectedFilePaths(String[] selectedStartDirs)
  {
    ArrayList<StringFile> toLoad = new ArrayList<>();
    StringBuilder error = new StringBuilder();
    //Log.d(TAG, "onSelected " + Arrays.toString(p1));
    StringFileLoadTask task = new StringFileLoadTask(data, this);
    DirectoryScanner scanner;
    if (selectedStartDirs.length > 0) 
    {
      scanner = new DirectoryScanner();
      for (String fName : selectedStartDirs) 
      {
        //Log.d(TAG,"calling find string file on "+fName);
        for (StringFile aFile: scanner.findStringFiles(fName, error,this)) toLoad.add(aFile);
      }
      if (scanner.isAmbiguous())
      {
        callProjectSelect(selectedStartDirs);
        return;
      }
      //toLoad = task.findStringFiles(p1[0], error);
      //callProjectSelect(p1);
      //Log.e(TAG, "calling on call project, restore after debug " + Arrays.toString(p1)+"\nfiles found "+toLoad);
    }
    else error.append("selected the values dir");

    if (toLoad.size() > 0)
    {
      //Log.d(TAG,"saving "+p1[0]+" "+toLoad.get(0).getParentFile().getParent());
      tokenTable.clear();
      savePathToPrefs(toLoad.get(0).getParentFile().getParent());
      task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
    }//if (toLoad.size() > 0)
  }

  public void callProjectSelect(String p1)
  {
    callProjectSelect(new String[] {p1});
  }

  public void callProjectSelect(String[] p1)
  {
    //Log.d(TAG, "calling scanTask with " + Arrays.toString(p1));
    DirScanTask scanTask = new DirScanTask(this, p1[0]);  
    scanTask.execute(p1);
  }

  public void savePathToPrefs(String p1)
  {
    //Log.d(TAG,"save prefs "+p1); 
    if (mode <= 0)
    {
      //save the actual value of the path
      SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
      //if (prefs.contains("actprojectpath")) p1[0] = prefs.getString("actprojectpath", "");
      File resDir = new File(new File(p1), "");
      SharedPreferences.Editor editor = prefs.edit();
      try
      {
        actProjectPath = resDir.getCanonicalPath();
        editor.putString("actprojectpath", actProjectPath);
        pathView.setText(actProjectPath);
        //Log.d(TAG, "saved path " + actProjectPath + " to prefs");
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
				CdataString txt = new CdataString(intentArgs.get(key).toString());
				StringEntry newEntry = new StringEntry(token, txt);
              data.set(token, langCand, newEntry);
            }//if (langCand != null)
            //else Log.d(TAG, "not a value " + key);
          }//for(String key: intentArgs.keySet())
          break;
        case(MODE_DEL):
          //Log.d(TAG, "case del");
          data.remove(intentArgs.getString("token"));
          break;
      }//switch (mode)
      saveFiles();
    }//if (mode > 0)
    else 
    {
      //Log.d(TAG, "done loading rebuilding the view "+data.size());
      pathView.setText(actProjectPath);
      tokenTable.buildTableView(); 
    }
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
    //Log.d(TAG, "files saved");
    if (mode > 0)
    {
      Intent returnInt = new Intent();
      returnInt.setAction(ACTION);
      setResult(Activity.RESULT_OK, returnInt);
      //Log.d(TAG, "headless save end " + returnInt + " " + returnInt.getExtras());
      finish();
    }//if (mode > 0)
    else
      Toast.makeText(this, getResources().getString(R.string.files_saved) + " '" + actProjectPath + "'", Toast.LENGTH_SHORT).show();
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
  /*@Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    //StringBuilder sb = new StringBuilder();
    //sb.append("got intent back ");
    //sb.append(data);
    //if (data != null)
    //  sb.append(": ").append(data.getExtras());
    //Log.d(TAG,  sb.toString());
    // Check which request we're responding to
    if (requestCode == ASK_STRINGMOD) 
    {

      // Make sure the request was successful
      if (resultCode == RESULT_OK)
      {
        // The user picked a contact.
        // The Intent's data Uri identifies which contact was selected.

        // Do something with the contact here (bigger example below)
      }
    }
  }*/

  public void directoriesScanned(ArrayList<StringFile> toLoad)
  {
    ArrayList<StringFile> onlyDef = new ArrayList<>();

    //StringBuilder debug = new StringBuilder();
    //StringBuilder debug2 = new StringBuilder();

    //debug.append("scan result");
    //debug.append("leaving ");
    //Log.d(TAG,"return of dir scan "+toLoad.size());
    for (StringFile aF: toLoad)
    {
      //debug.append(aF.getAbsolutePath()).append("\n");
      if ("default".equals(aF.lang()))
      {
        onlyDef.add(aF.getParentFile());
        //debug2.append(aF.getParentFile().getAbsolutePath()).append("\n");
      }
    }
    //Log.d(TAG, "coming back from scan " + debug);
    //Log.d(TAG, "cleansed " + debug2);

    if (onlyDef.size() <= 0)
    {
      actProjectPath = tryToFindADefaultDir();
      savePathToPrefs(actProjectPath);
    }
    else if (onlyDef.size() == 1)
    {
      actProjectPath = onlyDef.get(0).getAbsolutePath();
      savePathToPrefs(actProjectPath);
    }
    else
    {
      FragmentManager fm = getFragmentManager();
      DialogFragSelectProject sp = new DialogFragSelectProject();
      sp.setDialogSelectionListener(this);
      sp.setData(onlyDef);
      sp.show(fm, "fragment_sel_proj");
    }
  }

}//public class MainActivity extends Activity implements OnClickListener, 
