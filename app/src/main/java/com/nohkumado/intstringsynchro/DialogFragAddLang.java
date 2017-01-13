package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import android.util.*;

public class DialogFragAddLang extends DialogFragment implements OnEditorActionListener

{

  public static final String TAG = "DiaFrag";
  AddLangDialogListener listener;
  
  
  public interface AddLangDialogListener
  {
    void onFinishAddLangDialog(String inputText);
  }
  private EditText mEditText;

  public DialogFragAddLang()
  {
    // Empty constructor required for DialogFragment
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
        listener.onFinishAddLangDialog(mEditText.getText().toString());
      }
      //AddLangDialogListener activity = (AddLangDialogListener) getActivity();
      this.dismiss();
      return true;
    }
    return false;
  }
  
  public void setAddLangDialogListener(AddLangDialogListener listCand)
  {
    listener = listCand;
  }
}
