package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity implements OnClickListener, DialogFragAddLang.AddLangDialogListener
{

  Spinner langSpin;
  Button addLang;
  private String m_Text = "";
  ArrayList<String> langList;

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
  }//protected void onCreate(Bundle savedInstanceState)

  public String getLang()
  {
    if (langSpin != null) return String.valueOf(langSpin.getSelectedItem());
    return("default");
  }//public String getLang()


  @Override
  public void onClick(View p1)
  {
    if (p1 == addLang)
    {
      showEditDialog();      
    }
  }//public void onClick(View p1)

  private void showEditDialog()
  {
    FragmentManager fm = getFragmentManager();
    DialogFragAddLang editNameDialog = new DialogFragAddLang();
    editNameDialog.show(fm, "fragment_add_lang");
  }

  @Override
  public void onFinishAddLangDialog(String inputText)
  {
    if (inputText == null || inputText.length() <= 0) return;
    String sanitized = inputText; 
    if (inputText.length() > 2) sanitized = inputText.substring(0, 2);
    sanitized = sanitized.toLowerCase();

    if (!langList.contains(sanitized))
    {
      langList.add(sanitized);
      Toast.makeText(this, "Added lang, " + sanitized, Toast.LENGTH_SHORT).show();
    }
  }

}
