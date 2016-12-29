package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.intstringsynchro.*;

public class DialogFragAddToken  extends DialogFragment implements OnEditorActionListener
{
  public static final String TAG = "TokFrag";
  public interface AddTokenDialogListener
  {
    void onFinishAddTokenDialog(String inputText,String defaultVal);
  }
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
    
    return view;
  }//createView

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  {
    //Log.d(TAG,"Editor action! "+event+"  id"+actionId);
    if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    {
      // Return input text to activity
      AddTokenDialogListener activity = (AddTokenDialogListener) getActivity();
      activity.onFinishAddTokenDialog(mEditText.getText().toString(),dEditText.getText().toString());
      this.dismiss();
      return true;
    }
    return false;
  }
}
