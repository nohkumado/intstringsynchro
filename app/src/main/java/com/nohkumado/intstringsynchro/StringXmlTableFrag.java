package com.nohkumado.intstringsynchro;
import android.app.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.nohutils.collection.*;
import java.util.*;


import android.view.View.OnClickListener;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 */

public class StringXmlTableFrag extends Fragment implements OnEditorActionListener, OnClickListener,
DialogFragAddLang.AddLangDialogListener, DialogFragAddToken.AddTokenDialogListener, DialogTokenMenu.TokenDialogListener
{
  private static final String TAG="SXF";
  protected ArrayList<String> langList;
  protected TreeMapTable<String,StringEntry> data;
  LangNameNormalizer normalizer = new LangNameNormalizer();
  //protected Pattern simple = Pattern.compile("([a-z]{2})");
  //protected Pattern complete = Pattern.compile("([a-z]{2})\\-r([a-zA-Z]{2})");
  //protected Pattern iso = Pattern.compile("([a-z]{2})\\-([a-zA-Z]{2})");
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
    if (data == null) data = new TreeMapTable<>();
    if (langList == null) 
    {
      langList = new ArrayList<String>();
      langList.add("default"); //can't use addNewLang, i think, default is in the layout anyway
    }
  }

  public StringXmlTableFrag(MainActivity context)
  {
    this();
    this.context = context;
  }

  public ArrayList<String> getLangList()
  {
    return langList;
  }

  public TreeMapTable<String, StringEntry> getData()
  {
    return data;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    hidden.put("default", false);
    if (savedInstanceState != null)
    {
      //Log.d(TAG, "coming back!" + data);

      if (context == null) context = (MainActivity) getActivity();
    }
    //else
    //  Log.d(TAG, "new inst!");
  }
  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    // TODO: Implement this method
    super.onSaveInstanceState(outState);
    //Log.d(TAG, "in save state " + data);
  }




  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    //Log.d(TAG, "in create view...");
    View v = super.onCreateView(inflater, container, savedInstanceState);
    if (context == null)
    {
      context = (MainActivity) getActivity();
    }

    if (v == null)
    {
      v = inflater.inflate(R.layout.string_xml_frag, container, false);
    }
    //Log.d(TAG, "view is " + v);
    tokenTable = (TableLayout)v.findViewById(R.id.table);
    //if (tokenTable != null) Log.d(TAG, "found tokentable");

    buildTitleRow();
    //tokenTable.addView(buildTitleRow());
    buildTableView();

    return v;
  }

  private TableRow buildTitleRow() throws Resources.NotFoundException
  {
    if (langList == null)
    {
      //Log.e(TAG,"called buildTitlerow with null langlist..");
      return null;
    }
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

    //title.addView(createImageButton("", android.R.drawable.ic_delete));
    title.addView(createButton(context.getResources().getString(R.string.table_tok), "token"));
    //title.addView(createButton(context.getResources().getString(R.string.fallback), "default"));
    for (String lang: langList)
    {
      //Log.d(TAG, "case lang " + lang);
      if (!hidden.get(lang))title.addView(createButton(lang, lang));
      //else Log.d(TAG, "is hidden");
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
    tokenTable.removeAllViews();
    buildTitleRow();

    synchronized (data)
    {

      for (String token : data)
      {
        //Log.d(TAG, "add rest stuff for " + token + " vla " + entry.getValue());
        StringEntry someContent = data.get(token, "default");//.get(lang);
        if (someContent != null)
        {
          if (someContent instanceof PluralEntry) createPluralTable(token);
          else if (someContent instanceof ArrayEntry)  createArrayTable(token, (ArrayEntry) someContent);
          else if (someContent instanceof StringEntry) createStringRow(token);
        }// if (someContent != null)
      }//for (String token : data)

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
    tv.setOnClickListener(this);
    return tv;
  }//createTextView
  /**
   * createEditView
   * @param layoutparms
   * @param string to print
   * @param hint
   * @return the build up text view
   */
  private TextView createEditView(TableRow.LayoutParams llp, String someContent, String hintTxt)
  {
    /*
     TextView tv = new TextView(context);
     tv.setLayoutParams(llp);
     tv.setText(someContent);
     tv.setBackground(getResources().getDrawable(R.drawable.border));
     tv.setPadding(0, 0, 4, 3);
     tv.setHint(hintTxt);
     tv.setOnClickListener(this);
     tv.setSingleLine(false);
     tv.setWidth(0);
     */

    EditText tv = new EditText(context);
    tv.setLayoutParams(llp);
    tv.setText(someContent);
    tv.setBackground(getResources().getDrawable(R.drawable.border));
    tv.setPadding(0, 0, 4, 3);
    tv.setHint(hintTxt);
    tv.setSingleLine(false);
    tv.setWidth(0);
    //tv.setInputType(EditText.
    //tv.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
    tv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
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
    String content = v.getText().toString().trim();
    String cleaned = TextUtils.htmlEncode(content);
    Log.d(TAG, "Editor action! " + event + "  id" + actionId + " " + content + " clean *" + cleaned + "'");

    if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    {
      String posHint = v.getHint().toString().trim();
      String[] pos = posHint.split(":");
      //Log.d(TAG, "change[" + pos[0] + ":" + pos[1] + "] text " + content);
      //Toast.makeText(context, "change[" + pos[0] + ":" + pos[1] + "] text " + content, Toast.LENGTH_SHORT).show();

      if (pos.length == 2)
      {
        StringEntry aEntry = new StringEntry(pos[0], v.getText().toString());
        data.set(pos[0], pos[1], aEntry);
      } 
      else
      {
        try
        {
          String token = pos[0];
          String lang = pos[1];

          int num =  Integer.parseInt(pos[2]);  
          //numeric
          Log.d(TAG, "adding array");
          ArrayEntry aEntry = (ArrayEntry) data.get(token, lang);
          if (aEntry == null)
          {
            aEntry = new ArrayEntry(token);
            data.set(token, lang, aEntry);
          }
          aEntry.set(num, v.getText().toString().trim());
          //Toast.makeText(context, "setting on  " + aEntry + " " + num + " to " + content, Toast.LENGTH_SHORT).show();
          buildTableView();

          /*int diff = num - aEntry.array.size();

           if (diff >= 0)
           {
           for (int i = 0; i <= diff; i++) aEntry.array.add("");
           aEntry.array.set(num, v.getText().toString());
           buildTableView();
           }
           else aEntry.array.set(num, v.getText().toString());
           */
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

    TableRow.LayoutParams llp = new TableRow.LayoutParams(
      TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
    llp.setMargins(0, 0, 2, 0);//2px right-margin
    String[] quant = new String[] {"zero","one","two","few","many","other"};

    newRow.setBackgroundColor(Color.parseColor("#b7eeff"));
    newRow.setLayoutParams(llp);
    //newRow.addView(createImageButton(token, android.R.drawable.ic_delete));
    newRow.addView(createTextView(llp, token, "token"));

    //newRow.addView(createTokenField(token));

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
      //newRow.addView(createTextView(llp, "", ""));//del but
      TextView tv = createTextView(llp, quantity, "");//token but
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
  }

  /*private RelativeLayout createTokenField(String token)
   {
   LayoutInflater inflater = LayoutInflater.from(context);
   RelativeLayout tokenCont = (RelativeLayout) inflater.inflate(R.layout.token_field,null);
   ImageButton tv = (ImageButton) tokenCont.findViewById(R.id.tok_del_but);
   tv.setOnClickListener(this);
   TextView text = (TextView)    tokenCont.findViewById(R.id.token_label);
   text.setText(token);

   return tokenCont;
   }*/


  private void createArrayTable(String token, ArrayEntry toDisp)
  {
    //Log.d(TAG, "create arraytable tok:" + token + " vs " + toDisp);
    //tv.invalidate();
    //Log.d(TAG, "added view " + tv.getText());

    //Log.d(TAG, "array :" + someContent);
    TableRow newRow;
    int line;
    for (line = 0; line <= toDisp.size(); line++)
    {
      newRow = new TableRow(context);
      newRow.setBackground(context.getDrawable(R.drawable.border));

      TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
      llp.setMargins(0, 0, 2, 0);//2px right-margin
      newRow.setBackgroundColor(Color.parseColor("#abe666"));
      newRow.setLayoutParams(llp);
      //if (line == 0)
      //  newRow.addView(createImageButton(token, android.R.drawable.ic_delete));
      //else
      //  newRow.addView(createImageButton(token + ":" + line, android.R.drawable.ic_delete));

      //newRow.addView(createTokenField(token));
      TextView tv;
      if (line == 0)
        tv = createTextView(llp, token, "token");
      else tv = createTextView(llp, "", "token:" + token + ":" + line);
      newRow.addView(tv);

      for (String lang : langList)
      {
        if (hidden.get(lang))  continue;
        //if (lang.equals("default")) continue;
        if (data.get(token, lang) != null)
        {
          ArrayEntry rec = (ArrayEntry) data.get(token, lang);
          if (rec.size() > line)
          {
            newRow.addView(createEditView(llp, rec.get(line), token + ":" + lang + ":" + line));
          }
          else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + line));
        }
        else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + line));
      }
      tokenTable.addView(newRow);
    }
  }//  private void createArrayTable(String token)


  private void createStringRow(String token)
  {
    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    llp.setMargins(0, 0, 2, 0);//2px right-margin

    TableRow newRow = new TableRow(context);
    newRow.setBackground(context.getDrawable(R.drawable.border));
    newRow.setLayoutParams(llp);
    //newRow.addView(createTokenField(token));
    //newRow.addView(createImageButton(token, android.R.drawable.ic_delete));

    //newRow.addView(createTokenField(token));

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
    //Log.d(TAG, "asked to add " + sanitized);
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
    //else Log.d(TAG, "allready in  " + Arrays.toString(langList.toArray(new String[langList.size()])));
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
  /* private DelTokImageButton createImageButton(String name, int icon_id)
   {
   TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
   llp.setMargins(0, 0, 2, 0);//2px right-margin
   DelTokImageButton tv = new DelTokImageButton(name, context);
   tv.setImageResource(icon_id);
   tv.setPadding(0, 0, 4, 3);
   tv.setOnClickListener(this);
   return tv;
   }*/


  /**
   * onClick
   * @argument  p1 the button clicked
   */
  @Override
  public void onClick(View p1)
  {
    if (p1 instanceof Button)
    {
      Button clicked = (Button) p1;

      if (clicked.getHint().equals("token"))
      {
        //Toast.makeText(context, "clicked otken ", Toast.LENGTH_SHORT).show();
        showAddTokenDialog();
      }
      else if (clicked.getHint().equals("..."))
      {
        //Toast.makeText(context, "clicked show selection", Toast.LENGTH_SHORT).show();
        showAddLangDialog();
      }      
      else 
      {
        //Toast.makeText(context, "clicked on lang " + clicked.getHint(), Toast.LENGTH_SHORT).show();
        boolean toggle = hidden.get(clicked.getHint());
        hidden.put(clicked.getHint().toString(), !toggle);
        buildTableView();
      } 
    }//if Button
    /*else if (p1 instanceof DelTokImageButton)
     {
     DelTokImageButton tv = (DelTokImageButton) p1;
     String token = tv.getToken();
     if (token.contains(":"))
     {
     String[] pos = token.split(":");
     token = pos[0];
     try
     {
     int line = Integer.parseInt(pos[1]);
     for (String lang: langList)
     {
     StringEntry aE = data.get(token, lang);
     if (aE != null && aE instanceof ArrayEntry)
     {
     ArrayEntry rec = (ArrayEntry) aE;
     rec.remove(line);
     }
     }
     //Toast.makeText(context, "removed line  " + line + " of " + token, Toast.LENGTH_SHORT).show();
     }//try
     catch (NumberFormatException e)
     {
     Toast.makeText(context, "couldn't extract linenum out of  " + tv.getToken(), Toast.LENGTH_SHORT).show();
     }//catch
     }//if (token.contains(":"))
     else deleteToken(tv.getToken());
     buildTableView();
     }//else if (p1 instanceof DelTokImageButton)
     */
    else if (p1 instanceof TextView)
    {
      //Toast.makeText(context, "hit token " + ((TextView)p1).getText(), Toast.LENGTH_SHORT).show();
      if ("token".equals(((TextView)p1).getHint().toString()))
      {
        String token = ((TextView)p1).getText().toString();
        if (token == null || token.length() <= 0) token =  ((TextView)p1).getHint().toString();
        //Toast.makeText(context, "hit token " + token, Toast.LENGTH_SHORT).show();

        DialogTokenMenu tokenActionDia = new DialogTokenMenu(getActivity(), p1, token);
        tokenActionDia.setTokenDialogListener(this);
        tokenActionDia.getMenuInflater().inflate(R.menu.token_menu, tokenActionDia.getMenu());
        tokenActionDia.setOnMenuItemClickListener(tokenActionDia);
        tokenActionDia.show();
      }
      else
      {
        //String token = ((TextView)p1).getText().toString();
        //Toast.makeText(context, "hit text " + token, Toast.LENGTH_SHORT).show();

      }//else

    }//else if (p1 instanceof TextView)

  }//public void onClick(View p1)

  private void deleteToken(String token)
  {
    data.remove(token);
    buildTableView();
  }//public void onClick(View p1)
  public void showAddLangDialog()
  {
    FragmentManager fm = getFragmentManager();
    DialogFragAddLang editNameDialog = new DialogFragAddLang();
    editNameDialog.setAddLangDialogListener(this);
    editNameDialog.setData(langList, hidden);
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
    if (hidden.get(sanitized) != null)
    {
      hidden.put(sanitized, false);
      buildTableView();
      return;
    }

    sanitized = normalizer.normalizeLangName(sanitized);

    if (!langList.contains(sanitized))
    {
      //langList.add(sanitized);
      //Toast.makeText(context, "Added lang, " + sanitized, Toast.LENGTH_SHORT).show();
      addNewLang(sanitized);
      buildTableView();
    }
    else if (hidden.get(sanitized) != null)
    {
      boolean status = hidden.get(sanitized);
      hidden.put(sanitized, !status);
      buildTableView();
    }//else 
  }//

  /*private String normalizeLangName(String sanitized)
   {
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
   }//

   }

   //if (inputText.length() > 2) sanitized = inputText.substring(0, 2);
   //sanitized = sanitized.toLowerCase();
   if (region.length() > 0)
   {
   sanitized = lang + "-r" + region;
   }
   else sanitized = lang;
   return sanitized;
   }*/

  @Override
  public void onFinishAddTokenDialog(StringEntry input)
  {
    //Toast.makeText(context, "Added Token, " + input.token +" val= "+input, Toast.LENGTH_SHORT).show();
    //Log.d(TAG,"Added Token, " + input.token +" val= "+input);
    data.set(input.token, "default", input);

    buildTableView();
    //if(stringDataAdapter != null) stringDataAdapter.notifyDataSetChanged();
  }
  public void onFinishTokenDialog(String token, String action)
  {
    //Toast.makeText(context, "Asked Token " + token + " mode= " + action, Toast.LENGTH_SHORT).show();
    //Log.d(TAG,"Added Token, " + input.token +" val= "+input);
    //data.set(input.token, "default", input);
    switch (action)
    {
      case "delete": 

        if (token.contains(":"))
        {
          String[] pos = token.split(":");
          token = pos[0];
          try
          {
            int line = Integer.parseInt(pos[1]);
            for (String lang: langList)
            {
              StringEntry aE = data.get(token, lang);
              if (aE != null && aE instanceof ArrayEntry)
              {
                ArrayEntry rec = (ArrayEntry) aE;
                rec.remove(line);
              }
            }
            //Toast.makeText(context, "removed line  " + line + " of " + token, Toast.LENGTH_SHORT).show();
          }//try
          catch (NumberFormatException e)
          {
            Toast.makeText(context, "couldn't extract linenum out of  " + token, Toast.LENGTH_SHORT).show();
          }//catch
        }//if (token.contains(":"))
        else deleteToken(token);
        break;
      case "rename":
        Toast.makeText(context, "rename of " + token + " not yet supported  ", Toast.LENGTH_SHORT).show();
        break;
      case "copy":
        Toast.makeText(context, "copy  " + token + " not yet supported  ", Toast.LENGTH_SHORT).show();
        break;
    }//    switch (action)


    buildTableView();

  }//public void onFinishTokenDialog(String inputText)

}//class
