package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;

public class DialogFragAddToken  extends DialogFragment implements OnEditorActionListener
{

  private Spinner spinner;
  /**
  * an interface to be able to pass back the data
  */
  public interface AddTokenDialogListener
  {
    void onFinishAddTokenDialog(StringEntry input);
  }
  
  public static final String TAG = "TokFrag";

  private DialogFragAddToken.AddTokenDialogListener listener;
 
  private EditText mEditText,dEditText;

  public DialogFragAddToken()
  {
    // Empty constructor required for DialogFragment
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.frag_add_tok, container);
    mEditText = (EditText) view.findViewById(R.id.txt_add_tokname);
    dEditText = (EditText) view.findViewById(R.id.txt_add_tokdef);
    getDialog().setTitle(R.string.add_tok);
    // Show soft keyboard automatically
    mEditText.requestFocus();
    getDialog().getWindow().setSoftInputMode(
      WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    mEditText.setOnEditorActionListener(this);
    dEditText.setOnEditorActionListener(this);
    spinner = (Spinner) view.findViewById(R.id.type_spin);    
    return view;
  }//createView

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  {
    //Log.d(TAG,"Editor action! "+event+"  id"+actionId);
    if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    {
      // Return input text to activity
      //AddTokenDialogListener activity = (AddTokenDialogListener) getActivity();
      if(listener != null)
      {
        
        StringEntry input = null;
        switch (spinner.getSelectedItemPosition())
        {
          case 1:
            input = new ArrayEntry(mEditText.getText().toString().trim());
            ((ArrayEntry)input).array.add(dEditText.getText().toString().trim());
            break;
          case 2:
            input = new PluralEntry(mEditText.getText().toString().trim());
            break;
            default:
            Log.e(TAG,"spinner has unknown case: "+spinner.getSelectedItemPosition());
          case 0:
            input = new StringEntry(mEditText.getText().toString().trim(),dEditText.getText().toString().trim());
            break;
            
        }
         
        listener.onFinishAddTokenDialog(input);
        
      }
      this.dismiss();
      return true;
    }
    return false;
  }
  
  public void setAddTokenDialogListener(AddTokenDialogListener listCand)
  {
    listener = listCand;
  }
}
