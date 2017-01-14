package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.intstringsynchro.*;
import java.util.*;

public class DialogFragAddLang extends DialogFragment implements OnEditorActionListener, OnClickListener
{
  public static final String TAG = "DiaFrag";
  AddLangDialogListener listener;
  ArrayList<String> langList;
  HashMap<String, Boolean> hidden;
  Spinner spin;

  private ImageButton okButton;
  
  public interface AddLangDialogListener
  {
    void onFinishAddLangDialog(String inputText);
  }
  private EditText mEditText;

  public DialogFragAddLang()
  {
    // Empty constructor required for DialogFragment
  }

  public void setData(ArrayList<String> availableLangs, HashMap<String, Boolean> notDisplayed)
  {
       langList = availableLangs;
       hidden = notDisplayed;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.frag_add_lang, container);
    mEditText = (EditText) view.findViewById(R.id.txt_add_lang);
    getDialog().setTitle(R.string.add_lang);
    // Show soft keyboard automatically
    mEditText.requestFocus();
    getDialog().getWindow().setSoftInputMode(
      WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    mEditText.setOnEditorActionListener(this);

    ArrayList<String> list = new ArrayList<String>();
    list.add(getActivity().getResources().getString(R.string.none));
    for(String lang : langList)
    {
      if(hidden.get(lang) != null && hidden.get(lang) == true) list.add(lang);
    }
    
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                                                                android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    
    
    
    spin = (Spinner)view.findViewById(R.id.stored_lang_spin);
    spin.setAdapter(dataAdapter);

    okButton = (ImageButton) view.findViewById(R.id.addlang_ok);
    okButton.setOnClickListener(this);
    
    return view;
  }//createView

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  {
    //Log.d(TAG,"Editor action! "+event+"  id"+actionId);
    if (EditorInfo.IME_ACTION_DONE == actionId|| (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    {
      // Return input text to activity
      if(listener != null)
      {
        String selection = spin.getSelectedItem().toString();
        if(!selection.equals(getActivity().getResources().getString(R.string.none)))
          listener.onFinishAddLangDialog(selection);
          else 
            listener.onFinishAddLangDialog(mEditText.getText().toString());
      }
      //AddLangDialogListener activity = (AddLangDialogListener) getActivity();
      this.dismiss();
      return true;
    }
    return false;
  }

  @Override
  public void onClick(View p1)
  {
    // Return input text to activity
    if(listener != null)
    {
      String selection = spin.getSelectedItem().toString();
      if(!selection.equals(getActivity().getResources().getString(R.string.none)))
        listener.onFinishAddLangDialog(selection);
      else 
        listener.onFinishAddLangDialog(mEditText.getText().toString());
    }
    //AddLangDialogListener activity = (AddLangDialogListener) getActivity();
    this.dismiss();
  }


  
  public void setAddLangDialogListener(AddLangDialogListener listCand)
  {
    listener = listCand;
  }
}
