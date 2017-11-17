package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;

/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 *
 * dialog to add a new token 
 */
public class DialogFragAddToken  extends DialogFragment implements OnEditorActionListener
{
	/** the spinner of the type of token to add */
	private Spinner spinner;
	/**
	 * an interface to be able to pass back the data
	 */
	public interface AddTokenDialogListener
	{
		void onFinishAddTokenDialog(StringEntry input);
	}//public interface AddTokenDialogListener

	public static final String TAG = "TokFrag";/** log d necessity */

	private DialogFragAddToken.AddTokenDialogListener listener; /** the callback */

	private EditText mEditText,dEditText; /** token field, default value field */
	/**
	 * CTOR
	 * Empty constructor required for DialogFragment
	 */
	public DialogFragAddToken()
	{
		// 
	}
	/**
	 * create view
	 */
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
	/**
	 * catch the enter key hits on the edittext
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		//Log.d(TAG,"Editor action! "+event+"  id"+actionId);
		if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		{
			// Return input text to activity
			//AddTokenDialogListener activity = (AddTokenDialogListener) getActivity();
			if (listener != null)
			{
				StringEntry input = null;
				switch (spinner.getSelectedItemPosition())
				{
					case 1:
						input = new ArrayEntry(mEditText.getText().toString().trim());
						((ArrayEntry)input).add(new CdataString(dEditText.getText().toString().trim()));
						break;
					case 2:
						input = new PluralEntry(mEditText.getText().toString().trim());
						break;
					default:
						Log.e(TAG, "spinner has unknown case: " + spinner.getSelectedItemPosition());
					case 0:
						CdataString txt = new  CdataString(dEditText.getText().toString().trim());
						input = new StringEntry(mEditText.getText().toString().trim(), txt);
						break;
				}//switch (spinner.getSelectedItemPosition())

				listener.onFinishAddTokenDialog(input);
			}//if (listener != null)

			this.dismiss();
			return true;
		}//if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		return false;
	}//public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	/**
	 * add the listener
	 */
	public void setAddTokenDialogListener(AddTokenDialogListener listCand)
	{
		listener = listCand;
	}//public void setAddTokenDialogListener(AddTokenDialogListener listCand)
}//public class DialogFragAddToken  extends DialogFragment implements OnEditorActionListener
