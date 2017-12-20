package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.nohutils.collection.*;

public class DialogRenameFrag extends DialogFragment implements OnEditorActionListener
{
	public static final String TAG = "DiaRenameFrag";

	private TextView label;

	private StringXmlTableFrag callback;

	/**
	 * interface to allow callback
	 */
	public interface RenameDialogListener
	{
		/** the method the callback needs to implement */
		void onFinishAddLangDialog(String inputText);
	}
	private RenameDialogListener listener; /**the callback, when finished result is pushed there */
	private TreeMapTable<String,StringEntry> data; /** list of avilable languages */
	private String token; /** list of hidden/visible languages */
	private EditText mEditText; /** token field */
	/**
	 * CTOR
	 * Empty constructor required for DialogFragment
	 */
	public DialogRenameFrag()
	{
		// 
	}//public DialogRenameFrag()
	/**
	 * set the data to be used, no defaults setted, this is really needed...
	 * @param langs the list of languages
	 * @param the map of cols to show
	 */
	public DialogRenameFrag setData(TreeMapTable<String,StringEntry> data, String notDisplayed)
	{
		this.data = data;
		token = notDisplayed;
		return this;
	}
	/**
	 * creates the view for this thing
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.frag_rename, container);
		label = (TextView) view.findViewById(R.id.tok_to_rename);
		label.setText(token);
		mEditText = (EditText) view.findViewById(R.id.txt_new_tokname);
		getDialog().setTitle(R.string.rename_tok);
		// Show soft keyboard automatically
		mEditText.requestFocus();
		getDialog().getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mEditText.setOnEditorActionListener(this);

		return view;
	}//createView
	/**
	 * this is called when the enter key is called on the edittext
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		boolean result = false;
		//Log.d(TAG, "Editor action! " + event + "  id" + actionId);
		if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		{
			if (!"".equals(token) && token != null)
			{
				//Log.d(TAG, "renaming from " + token + " to " + mEditText.getText());
				result = data.rename(token, mEditText.getText().toString());
				if (result && callback != null) callback.buildTableView();				
			}//if(!"".equals(token) && token != null)
			this.dismiss();
		}//if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		//Log.d(TAG, "renaming ended with  " + result);
		return result;
	}//public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	/**
	 * setAddLangDialogListener
	 * set the listener that will be called
	 */
	public void setAddLangDialogListener(RenameDialogListener listCand)
	{
		listener = listCand;
	}//public void setAddLangDialogListener(AddLangDialogListener listCand)
	public DialogRenameFrag setCaller(StringXmlTableFrag p0)
	{
		callback = p0;
		return this;
	} /** needed for logging */
}//public class DialogRenameFrag extends DialogFragment implements OnEditorActionListener, OnClickListener

