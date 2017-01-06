package com.nohkumado.intstringsynchro;
import android.app.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.nohutils.collection.*;
import java.util.*;

public class StringXmlTableFrag extends Fragment implements OnEditorActionListener
{
  private static final String TAG="SXF";
  protected ArrayList<String> langList;
  protected TreeMapTable<String,String> data;
  //protected ArrayList<StringEntry> rest;
  protected HashMap<String, ArrayList<StringEntry>> rest;
  protected TableLayout tokenTable;

  protected MainActivity context;
  /**
   CTOR

   */
  public StringXmlTableFrag(ArrayList<String> langList, TreeMapTable<String, String> data, HashMap<String, ArrayList<StringEntry>> rest, MainActivity context)
  {
    this.langList = langList;
    this.data = data;
    this.rest = rest;
    this.context = context;
  }

  /**
   * buildTableView
   */
  private void buildTableView()
  {
    View title = tokenTable.findViewById(R.id.title_line);
    tokenTable.removeAllViews();
    tokenTable.addView(title);

    //tr.setBackgroundColor(Color.BLACK);
    //tr.setPadding(0, 0, 0, 2); //Border between rows

    TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
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
      data.set(pos[0], pos[1], v.getText().toString());
      return true;
    }
    return false;
  }//onEditorAction

}//class
