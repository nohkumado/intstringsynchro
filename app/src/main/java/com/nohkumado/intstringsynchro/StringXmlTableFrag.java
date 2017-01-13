package com.nohkumado.intstringsynchro;
import android.app.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.nohutils.collection.*;
import java.util.*;
import java.util.regex.*;

import android.view.View.OnClickListener;

public class StringXmlTableFrag extends Fragment implements OnEditorActionListener, OnClickListener,
DialogFragAddLang.AddLangDialogListener, DialogFragAddToken.AddTokenDialogListener
{
  private static final String TAG="SXF";
  protected ArrayList<String> langList;
  protected TreeMapTable<String,StringEntry> data;
  protected Pattern simple = Pattern.compile("([a-z]{2})");
  protected Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");
  protected Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");
  protected  HashMap<String,Boolean> hidden = new HashMap<>();

  //protected ArrayList<StringEntry> rest;
  //protected HashMap<String, ArrayList<StringEntry>> rest;
  //protected TreeMapTable<String, StringEntry> rest;
  protected TableLayout tokenTable;

  protected MainActivity context;

  private ArrayList<String> tmpLangList;
  /**
   CTOR

   */
  //public StringXmlTableFrag(ArrayList<String> langList, TreeMapTable<String, String> data, HashMap<String, ArrayList<StringEntry>> rest, MainActivity context)
  public StringXmlTableFrag()
  {
    super();
    hidden.put("default", false);
  }

  public StringXmlTableFrag(ArrayList<String> langList, TreeMapTable<String, StringEntry> data, MainActivity context)
  {
    this();
    this.langList = langList;
    this.data = data;
    this.context = context;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View v = super.onCreateView(inflater, container, savedInstanceState);
    if (v == null)
    {
      v = inflater.inflate(R.layout.string_xml_frag, container, false);
    }
    //Log.d(TAG, "view is " + v);
    tokenTable = (TableLayout)v.findViewById(R.id.table);
    if (tokenTable != null) Log.d(TAG, "found tokentable");

    buildTitleRow();
    //tokenTable.addView(buildTitleRow());

    return v;
  }

  private TableRow buildTitleRow() throws Resources.NotFoundException
  {
    TableRow title = (TableRow)tokenTable.findViewById(R.id.title_line);
    if (title == null)
    {
      title = new TableRow(context);
      title.setBackground(context.getDrawable(R.drawable.border));

      TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
      llp.setMargins(0, 0, 2, 0);//2px right-margin

      //title.setBackgroundColor(Color.parseColor("#b7eeff"));
      title.setLayoutParams(llp);
      tokenTable.addView(title);
    }

    title.removeAllViews();
    title.addView(createButton(context.getResources().getString(R.string.table_tok), "token"));
    //title.addView(createButton(context.getResources().getString(R.string.fallback), "default"));
    for (String lang: langList)
    {
      Log.d(TAG, "case lang " + lang);
      if (!hidden.get(lang))title.addView(createButton(lang, lang));
      else Log.d(TAG, "is hidden");
    }
    title.addView(createButton("...", "..."));
    title.invalidate();

    return title;
  }


  /**
   * buildTableView
   */
  public void buildTableView()
  {
    //View title = tokenTable.findViewById(R.id.title_line);
    Log.d(TAG, "removing all views");
    tokenTable.removeAllViews();
    //tokenTable.addView(title);
    buildTitleRow();
    //tr.setBackgroundColor(Color.BLACK);
    //tr.setPadding(0, 0, 0, 2); //Border between rows

    /*TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
     llp.setMargins(0, 0, 2, 0);//2px right-margin

     for (String token : data)
     {
     TableRow newRow = new TableRow(context);
     newRow.setLayoutParams(llp);
     newRow.addView(createTextView(llp, token, "token"));
     for (String lang : langList)
     {
     String someContent = data.get(token, lang);
     if (someContent == null) someContent = "";

     newRow.addView(createEditView(llp, someContent, token + ":" + lang));
     }
     tokenTable.addView(newRow);
     }
     */

    for (String token : data)
    {
      //Log.d(TAG, "add rest stuff for " + token + " vla " + entry.getValue());
      StringEntry someContent = data.get(token, "default");//.get(lang);
      if (someContent != null)
      {
        if (someContent instanceof PluralEntry) createPluralTable(token);
        else if (someContent instanceof ArrayEntry)  createArrayTable(token, (ArrayEntry) someContent);
        else if (someContent instanceof StringEntry) createStringRow(token);
      }

    }
    tokenTable.invalidate();
  }//buildTableView
  /**
   * createTextView
   *
   * @param layoutparms
   * @param string to print
   * @param hint
   * @return the build up text view
   */
  private TextView createTextView(TableRow.LayoutParams llp, String someContent, String hintTxt)
  {
    TextView tv = new TextView(context);
    tv.setLayoutParams(llp);
    tv.setText(someContent);
    tv.setBackground(getResources().getDrawable(R.drawable.border));
    tv.setPadding(0, 0, 4, 3);
    tv.setHint(hintTxt);
    return tv;
  }//createTextView

  /**
   * createEditView
   * @param layoutparms
   * @param string to print
   * @param hint
   * @return the build up text view
   */
  private EditText createEditView(TableRow.LayoutParams llp, String someContent, String hintTxt)
  {
    EditText tv = new EditText(context);
    tv.setLayoutParams(llp);
    tv.setText(someContent);
    tv.setBackground(getResources().getDrawable(R.drawable.border));
    tv.setPadding(0, 0, 4, 3);
    tv.setHint(hintTxt);
    tv.setOnEditorActionListener(this);
    return tv;
  }//createEditView
  /**
   * onEditorAction
   * @param v,
   * @param actionId
   * @param event
   * @return success or not
   */
  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  {
    //Log.d(TAG,"Editor action! "+event+"  id"+actionId);

    if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    {
      String posHint = v.getHint().toString();
      String[] pos = posHint.split(":");
      //Log.d(TAG,"change["+pos[0]+":"+pos[1]+"] text "+v.getText().toString());
      if (pos.length == 2)
        data.set(pos[0], pos[1], new StringEntry(pos[0], v.getText().toString()));
      else
      {
        try
        {
          int num =  Integer.parseInt(pos[2]);  
          //numeric
          //Log.d(TAG, "adding array");
          ArrayEntry aEntry = (ArrayEntry) data.get(pos[0], pos[1]);
          if (aEntry == null)
          {
            aEntry = new ArrayEntry(pos[0]);
            data.set(pos[0], pos[1], aEntry);
          }

          int diff = num - aEntry.array.size();

          if (diff >= 0)
          {
            for (int i = 0; i <= diff; i++) aEntry.array.add("");
            buildTableView();
          }
          aEntry.array.set(num, v.getText().toString());
        }
        catch (NumberFormatException e)
        {
          //Log.d(TAG, "adding plural");
          PluralEntry aEntry = (PluralEntry) data.get(pos[0], pos[1]);
          if (aEntry == null)
          {
            aEntry = new PluralEntry(pos[0]);
            data.set(pos[0], pos[1], aEntry);
          }

          aEntry.hashmap.put(pos[2], new StringEntry(pos[2], v.getText().toString()));
        }
      }//else
      //Log.d(TAG, "changed[" + pos[0] + ":" + pos[1] + "] : " + data.get(pos[0], pos[1]));

      return true;
    }
    return false;
  }//onEditorAction


  private void createPluralTable(String token)
  {
    TableRow newRow = new TableRow(context);
    newRow.setBackground(context.getDrawable(R.drawable.border));

    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    llp.setMargins(0, 0, 2, 0);//2px right-margin
    String[] quant = new String[] {"zero","one","two","few","many","other"};

    newRow.setBackgroundColor(Color.parseColor("#b7eeff"));
    newRow.setLayoutParams(llp);
    newRow.addView(createTextView(llp, token, "token"));

    //complete with empty
    for (String lang : langList) if (!hidden.get(lang))newRow.addView(createTextView(llp, "", token + ":" + lang));
    tokenTable.addView(newRow);


    //Log.d(TAG, "plural :" + someContent);
    //tokenTable.addView(newRow);

    for (String quantity : quant)
    {
      newRow = new TableRow(context);
      newRow.setBackground(context.getDrawable(R.drawable.border));
      newRow.setBackgroundColor(Color.parseColor("#b7eeff"));
      newRow.setLayoutParams(llp);
      TextView tv = createTextView(llp, quantity, "");
      tv.setGravity(Gravity.RIGHT);
      newRow.addView(tv);

      for (String lang : langList)
      {
        if (hidden.get(lang))  continue;
        
        //if (lang.equals("default")) continue;
        if (data.get(token, lang) != null)
        {
          PluralEntry rec = (PluralEntry) data.get(token, lang);
          StringEntry line = null;
          for (String key : rec.hashmap.keySet())
          {
            StringEntry aline = rec.hashmap.get(key);
            if (aline.token.equals(quantity)) 
            {
              line = aline;
              break;
            }
          }
          if (line != null) newRow.addView(createEditView(llp, line.text, token + ":" + lang + ":" + quantity));
          else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + quantity));
        }
        else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + quantity));
      }
      tokenTable.addView(newRow);
    }
  }//private void createPluralTable(String token)


  private void createArrayTable(String token, ArrayEntry toDisp)
  {

    TableRow newRow = new TableRow(context);
    newRow.setBackground(context.getDrawable(R.drawable.border));

    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    llp.setMargins(0, 0, 2, 0);//2px right-margin
    newRow.setBackgroundColor(Color.parseColor("#abe666"));
    newRow.setLayoutParams(llp);
    newRow.addView(createTextView(llp, token, "token"));

    //Log.d(TAG, "array :" + someContent);
    int line;
    for (line = 0; line < toDisp.array.size(); line++)
    {
      for (String lang : langList)
      {
        if (hidden.get(lang))  continue;
        //if (lang.equals("default")) continue;
        if (data.get(token, lang) != null)
        {
          ArrayEntry rec = (ArrayEntry) data.get(token, lang);
          if (rec.array.size() > line)
          {
            newRow.addView(createEditView(llp, rec.array.get(line), token + ":" + lang + ":" + line));
          }
          else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + line));
        }
        else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + line));
      }
      tokenTable.addView(newRow);
      newRow = new TableRow(context);
      newRow.setBackground(context.getDrawable(R.drawable.border));
      newRow.setBackgroundColor(Color.parseColor("#abe666"));
      newRow.setLayoutParams(llp);
      newRow.addView(createTextView(llp, "", ""));
    }
    //and an empty row
    newRow = new TableRow(context);
    newRow.setBackground(context.getDrawable(R.drawable.border));
    newRow.setBackgroundColor(Color.parseColor("#abe666"));
    newRow.setLayoutParams(llp);
    newRow.addView(createTextView(llp, "", ""));
    for (String lang : langList)
    {
      if (hidden.get(lang))  continue;
      newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + line));
    }
    tokenTable.addView(newRow);
  }//  private void createArrayTable(String token)


  private void createStringRow(String token)
  {
    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    llp.setMargins(0, 0, 2, 0);//2px right-margin

    TableRow newRow = new TableRow(context);
    newRow.setBackground(context.getDrawable(R.drawable.border));
    newRow.setLayoutParams(llp);
    newRow.addView(createTextView(llp, token, "token"));
    for (String lang : langList)
    {
      if (hidden.get(lang))  continue;
      //Log.d(TAG, "adding string col " + lang + " tok " + token);
      StringEntry someContent = data.get(token, lang);
      String text = "";
      if (someContent != null) text = someContent.text;
      newRow.addView(createEditView(llp, text, token + ":" + lang));
    }
    tokenTable.addView(newRow);
  }  

  public void addNewLang(String sanitized)
  {
    Log.d(TAG, "asked to add " + sanitized);
    if (!langList.contains(sanitized))
    {
      langList.add(sanitized);
      hidden.put(sanitized, false);
      if (tokenTable == null)
      {
        //not yet initialized
        if (tmpLangList == null) tmpLangList = new ArrayList<>();
        tmpLangList.add(sanitized);
        return;
      }
    }//if
    else Log.d(TAG, "allready in  " + Arrays.toString(langList.toArray(new String[langList.size()])));
    buildTableView();
  }//addNewLang


  private Button createButton(String oneLang, String hint)
  {
    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    llp.setMargins(0, 0, 2, 0);//2px right-margin
    Button tv = new Button(context);
    tv.setText(oneLang);
    tv.setPadding(0, 0, 4, 3);
    tv.setOnClickListener(this);
    tv.setHint(hint);
    return tv;
  }
  @Override
  public void onClick(View p1)
  {
    Button clicked = (Button) p1;

    if (clicked.getHint().equals("token"))
    {
      Toast.makeText(context, "clicked otken ", Toast.LENGTH_SHORT).show();
      showAddTokenDialog();
    }
    else if (clicked.getHint().equals("..."))
    {
      Toast.makeText(context, "clicked show selection", Toast.LENGTH_SHORT).show();
      showAddLangDialog();
    }      
    else 
    {
      Toast.makeText(context, "clicked on lang " + clicked.getHint(), Toast.LENGTH_SHORT).show();
      boolean toggle = hidden.get(clicked.getHint());
      hidden.put(clicked.getHint().toString(), !toggle);
      buildTableView();
    }      

  }//public void onClick(View p1)
  public void showAddLangDialog()
  {
    FragmentManager fm = getFragmentManager();
    DialogFragAddLang editNameDialog = new DialogFragAddLang();
    editNameDialog.setAddLangDialogListener(this);
    editNameDialog.show(fm, "fragment_add_lang");
  }

  public void showAddTokenDialog()
  {
    FragmentManager fm = getFragmentManager();
    DialogFragAddToken editNameDialog = new DialogFragAddToken();
    editNameDialog.setAddTokenDialogListener(this);
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
          Toast.makeText(context, "Can't extract lang from " + sanitized + " valid examples: de, de-DE or de-rDE!", Toast.LENGTH_SHORT).show();
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
      //langList.add(sanitized);
      Toast.makeText(context, "Added lang, " + sanitized, Toast.LENGTH_SHORT).show();
      addNewLang(sanitized);
      buildTableView();
    }
  }

  @Override
  public void onFinishAddTokenDialog(String inputText, String defaultVal)
  {
    Toast.makeText(context, "Added Token, " + inputText, Toast.LENGTH_SHORT).show();
    data.set(inputText.trim(), "default", new StringEntry(inputText.trim(), defaultVal.trim()));
    buildTableView();
    //if(stringDataAdapter != null) stringDataAdapter.notifyDataSetChanged();
  }

}//class
