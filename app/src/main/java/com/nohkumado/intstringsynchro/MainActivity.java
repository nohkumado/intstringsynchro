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
DialogFragAddLang.AddLangDialogListener, DialogFragAddToken.AddTokenDialogListener,
DialogSelectionListener//, OnEditorActionListener
{
  protected Spinner langSpin;
  protected Button addLang, addToken, openProject;
  protected ImageButton moveUpBut,saveBut;
  protected String actProjectPath = "";
  protected ArrayList<String> langList;
  protected TreeMapTable<String,StringEntry> data;
  //protected ArrayList<StringEntry> rest;
  //protected TreeMapTable<String,StringEntry> rest;
  
  //protected HashMap<String, ArrayList<StringEntry>> rest;
  //protected ArrayList<StringEntry> tokenList;
  //protected ListView tokenTable;
  //protected TableLayout tokenTable;
  //protected StringEntryAdapter stringDataAdapter;

  private static final int PROJECT_CHOOSED = 99;

  private static final String TAG="MA";

  protected Pattern simple = Pattern.compile("([a-z]{2})");
  protected Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");
  protected Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");

  protected StringXmlTableFrag tokenTable;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "#############################  start ###################################");
    setContentView(R.layout.main);
    data = new TreeMapTable<>();
    //rest = new  TreeMapTable<>();
    //rest = new  HashMap<String, ArrayList<StringEntry>>();
    //rest.put("default", new ArrayList<StringEntry>());
    //rest = new ArrayList<StringEntry>() ; 



    //Log.d(TAG,"data : "+data);
    langSpin = (Spinner) findViewById(R.id.lang_selector);
    langList = new ArrayList<String>();
    langList.add("default"); //can't use addNewLang, i think, default is in the layout anyway
    //langList.add("de");
    //langList.add("fr");
    SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
    if (prefs.contains("actprojectpath")) 
    {
      actProjectPath = prefs.getString("actprojectpath", "");
      
    }
    //Log.d(TAG,"retrieved default path '"+actProjectPath+"'");

    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                                                                android.R.layout.simple_spinner_item, langList);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    langSpin.setAdapter(dataAdapter);
    langSpin.setOnItemSelectedListener(new LangSpinnerItemSelected());

    addLang = (Button) findViewById(R.id.addLangBut);
    addLang.setOnClickListener(this);

    addToken = (Button) findViewById(R.id.addTokBut);
    addToken.setOnClickListener(this);

    openProject  = (Button) findViewById(R.id.addProjBut);
    openProject.setOnClickListener(this);

    moveUpBut   = (ImageButton) findViewById(R.id.moveUpBut);
    moveUpBut.setOnClickListener(this);

    saveBut = (ImageButton) findViewById(R.id.saveBut);
    saveBut.setOnClickListener(this);
    //tokenList = new ArrayList<>();
    //tokenTable = (ListView) findViewById(R.id.stringListView);
    tokenTable = new StringXmlTableFrag(langList, data, this);
    FragmentManager fm = getFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    ft.replace(R.id.table, tokenTable);
    ft.commit();

    //tokenTable = (TableLayout) findViewById(R.id.table);
    //tokenTable.setBackground(getResources().getDrawable(R.drawable.border);
    //stringDataAdapter = new StringEntryAdapter(this, data);
    //tokenTable.setAdapter(stringDataAdapter);
    if(actProjectPath != null && actProjectPath.length() > 0) onSelectedFilePaths(new String[] {actProjectPath+"/values"});
  }

  public String getLang()
  {
    if (langSpin != null) return String.valueOf(langSpin.getSelectedItem());
    return("default");
  }//public String getLang()


  @Override
  public void onClick(View p1)
  {
    if (p1 == addLang) showEditDialog();
    else if (p1 == addToken)  showAddTokenDialog();      
    else if (p1 == openProject)  
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

  private void showEditDialog()
  {
    FragmentManager fm = getFragmentManager();
    DialogFragAddLang editNameDialog = new DialogFragAddLang();
    editNameDialog.show(fm, "fragment_add_lang");
  }

  private void showAddTokenDialog()
  {
    FragmentManager fm = getFragmentManager();
    DialogFragAddToken editNameDialog = new DialogFragAddToken();
    editNameDialog.show(fm, "fragment_add_token");
  }

  @Override
  public void onFinishAddLangDialog(String inputText)
  {
    if (inputText == null || inputText.length() <= 0) return;
    String sanitized = inputText.trim();

    Matcher m = complete.matcher(sanitized);
    String lang = "", region = "";
    if (m.find())
    {
      lang = m.group(1).toLowerCase();
      region = m.group(2).toUpperCase();
    }
    else
    {
      m = iso.matcher(sanitized);
      if (m.find())
      {
        lang = m.group(1).toLowerCase();
        region = m.group(2).toUpperCase();
      }
      else
      {
        m = simple.matcher(sanitized);
        if (m.find())
        {
          sanitized = sanitized.substring(0, 2);
          lang = sanitized.toLowerCase();
        }
        else
        {
          Toast.makeText(this, "Can't extract lang from " + sanitized + " valid examples: de, de-DE or de-rDE!", Toast.LENGTH_SHORT).show();
        }
      }
    }

    //if (inputText.length() > 2) sanitized = inputText.substring(0, 2);
    //sanitized = sanitized.toLowerCase();
    if (region.length() > 0)
    {
      sanitized = lang + "-r" + region;
    }
    else sanitized = lang;

    if (!langList.contains(sanitized))
    {
      langList.add(sanitized);
      Toast.makeText(this, "Added lang, " + sanitized, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onFinishAddTokenDialog(String inputText, String defaultVal)
  {
    Toast.makeText(this, "Added Token, " + inputText, Toast.LENGTH_SHORT).show();
    data.set(inputText.trim(), "default", new StringEntry(inputText.trim(),defaultVal.trim()));
    //if(stringDataAdapter != null) stringDataAdapter.notifyDataSetChanged();
  }
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
              addNewLang(sanitized);
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

  public void buildTableView()
  {
    tokenTable.buildTableView();
  }//public void buildTableView()


  private void addNewLang(String sanitized)
  {
    //Log.d(TAG, "asked to add " + sanitized);
    if (!langList.contains(sanitized))
    {
      langList.add(sanitized);

      tokenTable.addNewLang(sanitized);

      //rest.put(sanitized, new ArrayList<StringEntry>());
      Toast.makeText(this, "Added lang, " + sanitized, Toast.LENGTH_SHORT).show();

    }//if
  }//add-newLang

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
  
}
