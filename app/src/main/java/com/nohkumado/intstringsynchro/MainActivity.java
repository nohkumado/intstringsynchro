package com.nohkumado.intstringsynchro;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.github.angads25.filepicker.controller.*;
import com.github.angads25.filepicker.model.*;
import com.github.angads25.filepicker.view.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class MainActivity extends Activity implements OnClickListener, 
DialogFragAddLang.AddLangDialogListener, DialogFragAddToken.AddTokenDialogListener,
DialogSelectionListener
{
  Spinner langSpin;
  Button addLang, addToken, openProject;
  private String actProjectPath = "";
  ArrayList<String> langList;

  private static final int PROJECT_CHOOSED = 99;

  private static final String TAG="MA";

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    langSpin = (Spinner) findViewById(R.id.lang_selector);
    langList = new ArrayList<String>();
    langList.add("default");
    langList.add("de");
    langList.add("fr");
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
  }//protected void onCreate(Bundle savedInstanceState)

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
    Log.d(TAG,"adding lang "+inputText);
    if (inputText == null || inputText.length() <= 0) return;
    String sanitized = inputText.trim();

    Log.d(TAG,"analyzing "+sanitized);
    Pattern simple = Pattern.compile("([a-z]{2})");
    Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");
    Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");
    Matcher m = complete.matcher(sanitized);
    String lang = "", region = "";
    if (m.find())
    {
      lang = m.group(1).toLowerCase();
      region = m.group(2).toUpperCase();
    }
    else
    {
      Log.d(TAG,"not "+m+" of "+complete);
      m = iso.matcher(sanitized);
      if (m.find())
      {
        lang = m.group(1).toLowerCase();
        region = m.group(2).toUpperCase();
      }
      else
      {
        Log.d(TAG,"not "+m+" of "+iso);
        m = simple.matcher(sanitized);
        if (m.find())
        {
          sanitized = sanitized.substring(0, 2);
          lang = sanitized.toLowerCase();
        }
        else
        {
          Log.d(TAG,"not "+m+" of "+simple);
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
   
    Log.d(TAG,"done analyzing "+sanitized);
    
    
    if (!langList.contains(sanitized))
    {
      langList.add(sanitized);
      Toast.makeText(this, "Added lang, " + sanitized, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onFinishAddTokenDialog(String inputText)
  {
    Toast.makeText(this, "Added Token, " + inputText, Toast.LENGTH_SHORT).show();
  }
  private void showFileChooser()
  {
    DialogProperties properties=new DialogProperties();
    properties.selection_mode = DialogConfigs.SINGLE_MODE;
    properties.selection_type = DialogConfigs.DIR_SELECT;
    if (actProjectPath.length() > 0)  properties.root = new File(actProjectPath, "../");
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
            //Toast.makeText(this, "found strings.xml", Toast.LENGTH_LONG).show();
            found = true;
            break;
          }
        }
        if (found)
        {
          //ok we have the right directory
          File resDir = new File(resValuesDir, "../");
          //select allready selected languages
          String[] files = resDir.list(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name)
              {
                return name.matches("values-\\[a-z\\-]{2,}");
              }
            });

          for (String aLang : files)
          {

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

//    for(String aPath : p1)
//    {
//      Toast.makeText(this, "Selected file: "+aPath, Toast.LENGTH_LONG).show();
//    }
  }



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent resultData)
  {
    super.onActivityResult(requestCode, resultCode, resultData);
    if (requestCode == PROJECT_CHOOSED && resultCode == Activity.RESULT_OK)
    {
      // The document selected by the user won't be returned in the intent.
      // Instead, a URI to that document will be contained in the return intent
      // provided to this method as a parameter.
      // Pull that URI using resultData.getData().
      Uri uri = null;
      if (resultData != null)
      {
        uri = resultData.getData();
        //Uri pwd = new Uri(uri, ".");
        File strpointer = new File(uri.getPath());
        File dirPtr = new File(strpointer, "..");

        Log.i(TAG, "Uri: " + uri.toString() + " " + strpointer.getAbsolutePath());
        //Toast.makeText(this, "Retrieved URI " + strpointer.getAbsolutePath(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "proj dir " + dirPtr.getAbsolutePath(), Toast.LENGTH_LONG).show();


        //showImage(uri);
      }
    }
  }//protected void onActivityResult(int requestCode, int resultCode, Intent resultData)

  private String readTextFromUri(Uri uri) throws IOException
  {
    InputStream inputStream = getContentResolver().openInputStream(uri);
    BufferedReader reader = new BufferedReader(new InputStreamReader(
                                                 inputStream));
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null)
    {
      stringBuilder.append(line);
    }
    inputStream.close();
    return stringBuilder.toString();
  }
  private static final int WRITE_REQUEST_CODE = 43;
  private void createFile(String mimeType, String fileName)
  {
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

    // Filter to only show results that can be "opened", such as
    // a file (as opposed to a list of contacts or timezones).
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    // Create a file with the requested MIME type.
    intent.setType(mimeType);
    intent.putExtra(Intent.EXTRA_TITLE, fileName);
    startActivityForResult(intent, WRITE_REQUEST_CODE);
  }
  private void alterDocument(Uri uri)
  {
    try
    {
      ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
      FileOutputStream fileOutputStream =
        new FileOutputStream(pfd.getFileDescriptor());
      fileOutputStream.write(("Overwritten by MyCloud at " +
                             System.currentTimeMillis() + "\n").getBytes());
      // Let the document provider know you're done by closing the stream.
      fileOutputStream.close();
      pfd.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

}
