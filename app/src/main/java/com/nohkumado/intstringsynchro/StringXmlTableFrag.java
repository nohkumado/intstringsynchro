package com.nohkumado.intstringsynchro;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.nohutils.collection.*;
import java.text.*;
import java.util.*;

public class StringXmlTableFrag extends Fragment implements OnEditorActionListener
{

  private static final String TAG="SXF";
  protected ArrayList<String> langList;
  protected TreeMapTable<String,StringEntry> data;
  //protected ArrayList<StringEntry> rest;
  //protected HashMap<String, ArrayList<StringEntry>> rest;
  //protected TreeMapTable<String, StringEntry> rest;
  protected TableLayout tokenTable;

  protected MainActivity context;
  /**
   CTOR

   */
  //public StringXmlTableFrag(ArrayList<String> langList, TreeMapTable<String, String> data, HashMap<String, ArrayList<StringEntry>> rest, MainActivity context)
  public StringXmlTableFrag(ArrayList<String> langList, TreeMapTable<String, StringEntry> data, MainActivity context)
  {
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
    return v;
  }


  /**
   * buildTableView
   */
  public void buildTableView()
  {
    View title = tokenTable.findViewById(R.id.title_line);
    //Log.d(TAG, "removing all viees");
    tokenTable.removeAllViews();
    tokenTable.addView(title);

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
            for(int i = 0; i <= diff; i++) aEntry.array.add("");
            //TODO need to redraw!!
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
    for (String lang : langList) newRow.addView(createTextView(llp, "", token + ":" + lang));
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
      TableRow title = (TableRow)tokenTable.findViewById(R.id.title_line);
      TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
      llp.setMargins(0, 0, 2, 0);//2px right-margin
      TextView tv = new TextView(context);
      tv.setText(sanitized);
      tv.setPadding(0, 0, 4, 3);

      title.addView(tv);
    }//if
  }//addNewLang


}//class
