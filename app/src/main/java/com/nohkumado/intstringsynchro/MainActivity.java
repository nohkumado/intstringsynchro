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

public class MainActivity extends Activity implements OnClickListener, 
DialogSelectionListener//, OnEditorActionListener
{
  //protected Spinner langSpin;
  protected Button openProject;//addLang, addToken, 
  protected ImageButton moveUpBut,saveBut;

  protected ArrayList<String> langList;
  protected TreeMapTable<String,StringEntry> data;
  protected String actProjectPath = "";
  protected int mode;
  //protected ArrayList<StringEntry> rest;
  //protected TreeMapTable<String,StringEntry> rest;

  //protected HashMap<String, ArrayList<StringEntry>> rest;
  //protected ArrayList<StringEntry> tokenList;
  //protected ListView tokenTable;
  //protected TableLayout tokenTable;
  //protected StringEntryAdapter stringDataAdapter;

  //private static final int PROJECT_CHOOSED = 99;

  private static final String TAG="MA";
  public static final int MODE_ADD=1;
  public static final int MODE_DEL=2;
  public static final int MODE_UNKNOWN=3;
  //protected Pattern simple = Pattern.compile("([a-z]{2})");
  //protected Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");
  //protected Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");

  protected StringXmlTableFrag tokenTable;

  private Bundle intentArgs;

  public final int MISSING_PATH = 10;
  public final int MISSING_MODE = 11;
  public final int MISSING_VALUE = 12;
  public final int MISSING_TOKEN = 13;

  public final String ERROR = "com.nohkumado.intstringsynchro.ERROR";
  public final String ACTION = "com.nohkumado.intstringsynchro.ACTION";
  

  public MainActivity()
  {

  }


  /**
   *   onCreate
   * @arg savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
    if (prefs.contains("actprojectpath")) 
    {
      actProjectPath = prefs.getString("actprojectpath", "");
    }
    // Get the intent that started this activity
    Intent intent = getIntent();


    // Figure out what to do based on the intent type
    if (intent != null && !intent.getAction().equals(Intent.ACTION_MAIN))
    {
      Uri data = intent.getData();
      Intent returnInt = new Intent();
      returnInt.setAction(ACTION);
      ArrayList<String> sb = new ArrayList<>();
      ArrayList<Integer> error_codes = new ArrayList<>();

      Log.d(TAG, "received intent " + intent + " of action " + intent.getAction() + " of type " + intent.getType());

      //check validity of send data
      Bundle args = intent.getExtras();
      if (checkIntentArgs(args, sb, error_codes))
      {
        Log.d(TAG, "has data " + intent.getData() + " of type " + intent.getType());
        String farPath = args.getString("path");
        String farmode = args.getString("mode");
        switch (farmode)
        {
          case("add"):
            mode = MODE_ADD;
            intentArgs = args;
            onSelectedFilePaths(new String[] {farPath + "/values"});
            break;
          case("del"):
            mode = MODE_DEL;
            onSelectedFilePaths(new String[] {farPath + "/values"});
            break;
          case "edit":
          case "list":
            break;
          default:
            error_codes.add(MODE_UNKNOWN);
            sb.add(getResources().getString(R.string.mode_unknown));
        }//switch
      }//if (checkIntentArgs(args, returnInt))

      if (error_codes.size() > 0)
      {
        returnInt.setAction(ERROR);
        returnInt.putExtra("codes", error_codes);
        returnInt.putExtra("msg", sb); 
        // Create intent to deliver some kind of result data
        //Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        setResult(Activity.RESULT_CANCELED, returnInt);
        finish();
      }//if (error_codes.size() > 0)
    }//if (intent != null && !intent.getAction().equals(Intent.ACTION_MAIN))  


    // find the retained fragment on activity restarts
    FragmentManager fm = getFragmentManager();
    tokenTable = (StringXmlTableFrag) fm.findFragmentByTag("data");
    // create the fragment and data the first time
    if (tokenTable == null)
    {
      // add the fragment
      tokenTable = new StringXmlTableFrag(this);

      fm.beginTransaction().add(tokenTable, "data").replace(R.id.table, tokenTable).commit();
      //FragmentTransaction ft = fm.beginTransaction();
      //ft.replace(R.id.table, tokenTable);
      //ft.commit();

    }
    data = tokenTable.getData();
    langList = tokenTable.getLangList();

    if (savedInstanceState != null)
    {
      Log.d(TAG, "#############################  restart ###################################");  
    }
    else
    {
      Log.d(TAG, "#############################  start ###################################");  
    }

    //Log.d(TAG,"data : "+data);
    /*langSpin = (Spinner) findViewById(R.id.lang_selector);

     ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
     android.R.layout.simple_spinner_item, langList);
     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     langSpin.setAdapter(dataAdapter);
     langSpin.setOnItemSelectedListener(new LangSpinnerItemSelected());

     addLang = (Button) findViewById(R.id.addLangBut);
     addLang.setOnClickListener(this);

     addToken = (Button) findViewById(R.id.addTokBut);
     addToken.setOnClickListener(this);
     */
    openProject  = (Button) findViewById(R.id.addProjBut);
    openProject.setOnClickListener(this);

    moveUpBut   = (ImageButton) findViewById(R.id.moveUpBut);
    moveUpBut.setOnClickListener(this);

    saveBut = (ImageButton) findViewById(R.id.saveBut);
    saveBut.setOnClickListener(this);
    //tokenList = new ArrayList<>();
    //tokenTable = (ListView) findViewById(R.id.stringListView);
    //tokenTable = (TableLayout) findViewById(R.id.table);
    //tokenTable.setBackground(getResources().getDrawable(R.drawable.border);
    //stringDataAdapter = new StringEntryAdapter(this, data);
    //tokenTable.setAdapter(stringDataAdapter);
    if (actProjectPath != null && actProjectPath.length() > 0 && data.size() <= 0) 
      onSelectedFilePaths(new String[] {actProjectPath + "/values"});
  }

  private boolean checkIntentArgs(Bundle args, ArrayList<String> sb, ArrayList<Integer> error_codes)
  {
    if (args.getString("path") == null)
    {
      error_codes.add(MISSING_PATH);
      sb.add(getResources().getString(R.string.missing_path));
    }
    if (args.getString("mode") == null)
    {
      error_codes.add(MISSING_MODE);
      sb.add(getResources().getString(R.string.missing_mode));
    }
    else
    {
      if (args.getString("mode").equals("add") && args.getString("value") == null)
      {
        error_codes.add(MISSING_VALUE);
        sb.add(getResources().getString(R.string.missing_value));

      }
    }
    if (args.getString("token") == null)
    {
      error_codes.add(MISSING_TOKEN);
      sb.add(getResources().getString(R.string.missing_token));
    }
    if (error_codes.size() > 0) 
      return false;
    return true;
  }//onCreate

  /*public String getLang()
   {
   if (langSpin != null) return String.valueOf(langSpin.getSelectedItem());
   return("default");
   }//public String getLang()
   */
  public void checkAndLoadFiles(String[] p1)
  {
    //Log.d(TAG,"select file "+data);
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
          }
        }
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
          }
          catch (IOException e)
          {
            Log.e(TAG, "coouldn't set path to " + resDir.getAbsolutePath());
          }
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
            }
            else Log.e(TAG, "wrng pattern " + mlang);

          }
        }
        else
          error.append("no strings.xml found in").append(actProjectPath);

      }
      else
        error.append("Dir not found: " + actProjectPath);
    }
    else
      error.append("select the values dir");

    if (error.toString().length() > 0) Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    if (toLoad.size() > 0)
    {
      //for (StringFile aFile: toLoad)
      //  Log.d(TAG, "about to load :" + aFile.toString() + " of " + aFile.lang());
      StringFileLoadTask task = new StringFileLoadTask(data, this);
      task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
    }
  }//  public void onSelectedFilePaths(String[] p1)

  @Override
  public void onClick(View p1)
  {
    //if (p1 == addLang) showEditDialog();
    //if (p1 == addLang) tokenTable.showAddLangDialog();
    //else if (p1 == addToken)  tokenTable.showAddTokenDialog();      
    //else 
    if (p1 == openProject)  
    {
      //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      //intent.addCategory(Intent.CATEGORY_OPENABLE);
      //intent.setType("*/*"); 
      //startActivityForResult(intent, PROJECT_CHOOSED);
      showFileChooser();
    }      
    else if (p1 == moveUpBut)
    {
      File actDir = new File(actProjectPath, "../");
      try
      {
        actProjectPath = actDir.getCanonicalPath();
      }
      catch (IOException e)
      {}
    }
    else if (p1 == saveBut)
    {
      saveFiles();
    }


  }//public void onClick(View p1)

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
  }

  @Override
  public void onSelectedFilePaths(String[] p1)
  {
    //Log.d(TAG,"select file "+data);
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
          }
        }
        if (found)
        {
          //ok we have the right directory
          File resDir = new File(resValuesDir, "../");

          if (mode <= 0)
          {
            //save the actual value of the path
            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.contains("actprojectpath")) actProjectPath = prefs.getString("actprojectpath", "");

            SharedPreferences.Editor editor = prefs.edit();
            try
            {
              editor.putString("actprojectpath", resDir.getCanonicalPath());
            }
            catch (IOException e)
            {
              Log.e(TAG, "coouldn't set path to " + resDir.getAbsolutePath());
            }
            editor.apply();
          }
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
            }
            else Log.e(TAG, "wrng pattern " + mlang);

          }
        }
        else
          error.append("no strings.xml found in").append(actProjectPath);

      }
      else
        error.append("Dir not found: " + actProjectPath);
    }
    else
      error.append("select the values dir");

    if (error.toString().length() > 0) Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    if (toLoad.size() > 0)
    {
      //for (StringFile aFile: toLoad)
      //  Log.d(TAG, "about to load :" + aFile.toString() + " of " + aFile.lang());
      StringFileLoadTask task = new StringFileLoadTask(data, this);
      task.execute(toLoad.toArray(new StringFile[toLoad.size()]));
    }
  }//  public void onSelectedFilePaths(String[] p1)

  /**
   * callback needed by the loader task to signify the data is ready
   */
  public void buildTableView()
  {
    if (mode > 0)
    {
      switch (mode)
      {
        case(MODE_ADD):
          LangNameNormalizer normalizer = new LangNameNormalizer();

          String langCand = null;
          for (String key: intentArgs.keySet())
          {
            if (key.equals("value")) langCand = "default";
            else if (key.startsWith("value")) langCand = normalizer.normalizeLangName(key.replace("value-", ""));
            else langCand = null; //ignoring the rest

            if (langCand != null)
            {
              String token = intentArgs.getString("token");
              StringEntry newEntry = new StringEntry(token, intentArgs.get(key).toString());
              data.set(token, langCand, newEntry);
            }
          }//for(String key: intentArgs.keySet())
          break;
        case(MODE_DEL):
          data.remove(intentArgs.getString("token"));
          break;
      }
      saveFiles();
    }
    else
      tokenTable.buildTableView();
  }//public void buildTableView()

  /**
   * @return boolean  if it was a success
   * saveFiles
   */  
  protected boolean saveFiles()
  {

    boolean result = true;
    SaveStringXmlTask task = new SaveStringXmlTask(data, this);
    task.execute(langList.toArray(new String[langList.size()]));
    return result;
  }


  public String getProjectPath()
  {
    return actProjectPath;
  }

  public void broadcastIntent(View view)
  {
    Intent intent = new Intent();
    intent.setAction("com.nohkumado.EDIT_STRINGXML");
    sendBroadcast(intent);
  }

  public void filesSaved()
  {
    if (mode > 0)
    {
      Intent returnInt = new Intent();
      returnInt.setAction(ACTION);
      // Create intent to deliver some kind of result data
        //Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        setResult(Activity.RESULT_OK, returnInt);
        finish();
      }//if (error_codes.size() > 0)
  }// if (mode > 0)
}
