package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
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
 * a class for a language dialog, here will be asked what language to display
 */
public class DialogFragAddLang extends DialogFragment implements OnEditorActionListener, OnClickListener
{
  public static final String TAG = "DiaFrag"; /** needed for logging */
  /**
   * interface to allow callback
   */
  public interface AddLangDialogListener
  {
    /** the method the callback needs to implement */
    void onFinishAddLangDialog(String inputText);
  }
  private AddLangDialogListener listener; /**the callback, when finished result is pushed there */
  private TreeMapTable<String,StringEntry> data; /** list of avilable languages */
  private HashMap<String, Boolean> hidden; /** list of hidden/visible languages */
  private Spinner spin; /** spinner to choose the var type */
  private ImageButton okButton; /** hit this when you are finished */
  private EditText mEditText; /** token field */
  /**
   * CTOR
   * Empty constructor required for DialogFragment
   */
  public DialogFragAddLang()
  {
    // 
  }//public DialogFragAddLang()
  /**
   * set the data to be used, no defaults setted, this is really needed...
   * @param langs the list of languages
   * @param the map of cols to show
   */
  public void setData(TreeMapTable<String,StringEntry> data, HashMap<String, Boolean> notDisplayed)
  {
    this.data = data;
    hidden = notDisplayed;
  }
  /**
   * creates the view for this thing
   */
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
    for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
    {
      String lang = keyVal.getValue();
      if (hidden.get(lang) != null && hidden.get(lang) == true) list.add(lang);
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
  /**
   * this is called when the enter key is called on the edittext
   */
  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  {
    //Log.d(TAG,"Editor action! "+event+"  id"+actionId);
    if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    {
      // Return input text to activity
      if (listener != null)
      {
        String selection = spin.getSelectedItem().toString();
        if (!selection.equals(getActivity().getResources().getString(R.string.none)))
          listener.onFinishAddLangDialog(selection);
        else 
          listener.onFinishAddLangDialog(mEditText.getText().toString());
      }//if (listener != null)
      //AddLangDialogListener activity = (AddLangDialogListener) getActivity();
      this.dismiss();
      return true;
    }//if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
    return false;
  }//public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  /**
   * onClick
   *called when hitting a button, here the ok button
   * calls, if there is one the callback to send back the acquired data
   */
  @Override
  public void onClick(View p1)
  {
    // Return input text to activity
    if (listener != null)
    {
      String selection = spin.getSelectedItem().toString();
      if (!selection.equals(getActivity().getResources().getString(R.string.none)))
        listener.onFinishAddLangDialog(selection);
      else 
        listener.onFinishAddLangDialog(mEditText.getText().toString());
    }//if (listener != null)
    //AddLangDialogListener activity = (AddLangDialogListener) getActivity();
    this.dismiss();
  }//public void onClick(View p1)
  /**
   * setAddLangDialogListener
   * set the listener that will be called
   */
  public void setAddLangDialogListener(AddLangDialogListener listCand)
  {
    listener = listCand;
  }//public void setAddLangDialogListener(AddLangDialogListener listCand)
}//public class DialogFragAddLang extends DialogFragment implements OnEditorActionListener, OnClickListener
