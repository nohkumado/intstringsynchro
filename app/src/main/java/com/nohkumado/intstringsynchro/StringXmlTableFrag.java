package com.nohkumado.intstringsynchro;
import android.app.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;
import com.nohkumado.nohutils.collection.*;
import java.util.*;


import android.view.View.OnClickListener;
import java.util.regex.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 *
 * the "main" class of this thing, the thing is build monolithically since i wanted 
 * to make it easy to include it into other programs...
 * displays the table and handles the events on it
 */
public class StringXmlTableFrag extends Fragment implements OnEditorActionListener, OnClickListener,
DialogFragAddLang.AddLangDialogListener, DialogFragAddToken.AddTokenDialogListener, DialogTokenMenu.TokenDialogListener, DialogArray.ArrayDialogListener
{
	private static final String TAG="SXF";
	//private ArrayList<String> langList;
	private TreeMapTable<String,StringEntry> data;
	private LangNameNormalizer normalizer = new LangNameNormalizer();
	private HashMap<String,Boolean> hidden = new HashMap<>();

	private TableLayout tokenTable;

	protected MainActivity context;
	private ArrayList<String> tmpLangList;
	/**
	 * default CTOR
	 * needed e.g. when coming back from sleep...
	 */
	public StringXmlTableFrag()
	{
		super();
		if (data == null) 
		{
			data = new TreeMapTable<>();
			data.addCol("default");
			hidden.put("default", false);
		}

		/*if (langList == null) 
		 {
		 langList = new ArrayList<String>();
		 langList.add("default"); //can't use addNewLang, i think, default is in the layout anyway
		 }//if (langList == null)
		 */
	}//CTOR
	/**
	 * CTOR
	 */
	public StringXmlTableFrag(MainActivity context)
	{
		this();
		this.context = context;
	}//CTOR
	/**
	 * getter for the lang list
	 */
	/*public ArrayList<String> getLangList()
	 {
	 return langList;
	 }//public ArrayList<String> getLangList()
	 */
	/**
	 * getter for the table data
	 */
	public TreeMapTable<String, StringEntry> getData()
	{
		return data;
	}//public TreeMapTable<String, StringEntry> getData()
	/**
	 * create this thing
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		hidden.put("default", false);
		if (savedInstanceState != null && context == null) context = (MainActivity) getActivity();
	}//public void onCreate(Bundle savedInstanceState)
	/**
	 * create the view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (context == null) context = (MainActivity) getActivity();

		if (v == null) v = inflater.inflate(R.layout.string_xml_frag, container, false);
		tokenTable = (TableLayout)v.findViewById(R.id.table);

		buildTitleRow();
		buildTableView();

		return v;
	}//public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	/**

	 * build the title row
	 */
	private TableRow buildTitleRow() throws Resources.NotFoundException
	{
		if (data.header() == null) return null;
		TableRow title = (TableRow)tokenTable.findViewById(R.id.title_line);
		if (title == null)
		{
			title = new TableRow(context);
			title.setBackground(context.getDrawable(R.drawable.border));

			TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			llp.setMargins(0, 0, 2, 0);//2px right-margin

			//title.setBackgroundColor(Color.parseColor("#b7eeff"));
			title.setLayoutParams(llp);
			tokenTable.addView(title);
		}

		title.removeAllViews();

		title.addView(createButton(context.getResources().getString(R.string.table_tok), "token"));
		for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
		{
			String lang = keyVal.getValue();
			if (hidden.get(lang) == null)
			{
				//Log.d(TAG,"ehhhm forgot to adde "+lang+" to hidden??");
				hidden.put(lang, false);
			}
			if (!hidden.get(lang))title.addView(createButton(lang, lang));
		}//for (String lang: langList)

		title.addView(createButton("...", "..."));
		title.invalidate();

		return title;
	}//private TableRow buildTitleRow() throws Resources.NotFoundException
	/**
	 * buildTableView
	 */
	public void buildTableView()
	{
		//Log.d(TAG,"building table "+data.size());
		tokenTable.removeAllViews();
		buildTitleRow();

		synchronized (data)
		{
			for (String token : data)
			{
				StringEntry someContent = data.get(token, "default");//.get(lang);
				if (someContent != null)
				{
					//Log.d(TAG,"building line for "+token);
					if (someContent instanceof PluralEntry) createPluralTable(token);
					else if (someContent instanceof ArrayEntry)  createArrayTable(token, (ArrayEntry) someContent);
					else if (someContent instanceof StringEntry) createStringRow(token);
				}// if (someContent != null)
			}//for (String token : data)
		}//synchronized (data)
		tokenTable.invalidate();
		//Log.d(TAG,"done building table");
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
		setFontSize(tv);

		tv.setBackground(getResources().getDrawable(R.drawable.border));
		tv.setPadding(0, 0, 4, 3);
		tv.setHint(hintTxt);
		tv.setOnClickListener(this);
		return tv;
	}

	private void setFontSize(TextView tv)
	{
		float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		tv.setTextSize(pixels);
	}//createTextView
	/**
	 * createEditView
	 * @param layoutparms
	 * @param string to print
	 * @param hint
	 * @return the build up text view
	 */
	private TextView createEditView(TableRow.LayoutParams llp, String someContent, String hintTxt)
	{
		/*
		 TextView tv = new TextView(context);
		 tv.setLayoutParams(llp);
		 tv.setText(someContent);
		 tv.setBackground(getResources().getDrawable(R.drawable.border));
		 tv.setPadding(0, 0, 4, 3);
		 tv.setHint(hintTxt);
		 tv.setOnClickListener(this);
		 tv.setSingleLine(false);
		 tv.setWidth(0);
		 */

		EditText tv = new EditText(context);
		tv.setLayoutParams(llp);
		tv.setText(someContent);
		tv.setBackground(getResources().getDrawable(R.drawable.border));
		tv.setPadding(0, 0, 4, 3);
		tv.setHint(hintTxt);
		tv.setSingleLine(false);
		tv.setWidth(0);
		setFontSize(tv);
		//tv.setInputType(EditText.
		//tv.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
		tv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
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
		String content = v.getText().toString().trim();
		String cleaned = TextUtils.htmlEncode(content);
		//Log.d(TAG, "Editor action! " + event + "  id" + actionId + " " + content + " clean *" + cleaned + "'");
		if (event == null)
		{
			if (EditorInfo.IME_ACTION_DONE == actionId)
			{
				validateNewLine(v);//else
				return true;
			}//if (EditorInfo.IME_ACTION_DONE == actionId)
			Toast.makeText(context, "Invalid event, Please send me a mail with : 'event = null, CODE = " + actionId + "'", Toast.LENGTH_LONG).show();
			return false;
		}//if(event == null)

		if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		{
			validateNewLine(v);//else
			return true;
		}//if (EditorInfo.IME_ACTION_DONE == actionId || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		return false;
	}//public boolean onEditorAction(TextView v, int actionId, KeyEvent event)

	private void validateNewLine(TextView v)
	{
		String posHint = v.getHint().toString().trim();
		String[] pos = posHint.split(":");

		if (pos.length == 2)
		{
			CdataString txt = new CdataString(v.getText().toString());

			StringEntry aEntry = new StringEntry(pos[0], txt);
			data.set(pos[0], pos[1], aEntry);
		}//if (pos.length == 2) 
		else
		{
			try
			{
				String token = pos[0];
				String lang = pos[1];

				int num =  Integer.parseInt(pos[2]);  
				//numeric
				ArrayEntry aEntry = (ArrayEntry) data.get(token, lang);
				if (aEntry == null)
				{
					aEntry = new ArrayEntry(token);
					data.set(token, lang, aEntry);
				}//if (aEntry == null)
				aEntry.set(num, new CdataString(v.getText().toString().trim()));
				buildTableView();
			}//try
			catch (NumberFormatException e)
			{
				//Log.d(TAG, "adding plural");
				PluralEntry aEntry = (PluralEntry) data.get(pos[0], pos[1]);
				if (aEntry == null)
				{
					aEntry = new PluralEntry(pos[0]);
					data.set(pos[0], pos[1], aEntry);
				}//if (aEntry == null)
				aEntry.put(pos[2], new CdataString(v.getText().toString()));
			}//catch (NumberFormatException e)
		}
	}//onEditorAction
	/**
	 * create the table entry for a plural entr
	 */
	private void createPluralTable(String token)
	{
		TableRow newRow = new TableRow(context);
		newRow.setBackground(context.getDrawable(R.drawable.border));

		TableRow.LayoutParams llp = new TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
		llp.setMargins(0, 0, 2, 0);//2px right-margin
		String[] quant = new String[] {"zero","one","two","few","many","other"};

		newRow.setBackgroundColor(Color.parseColor("#b7eeff"));
		newRow.setLayoutParams(llp);
		newRow.addView(createTextView(llp, token, "token"));

		//complete with empty
		for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
		{
			String lang = keyVal.getValue();
			if (!hidden.get(lang))newRow.addView(createTextView(llp, "", token + ":" + lang));
		}

		tokenTable.addView(newRow);

		for (String quantity : quant)
		{
			newRow = new TableRow(context);
			newRow.setBackground(context.getDrawable(R.drawable.border));
			newRow.setBackgroundColor(Color.parseColor("#b7eeff"));
			newRow.setLayoutParams(llp);
			TextView tv = createTextView(llp, quantity, "");//token but
			tv.setGravity(Gravity.RIGHT);
			newRow.addView(tv);
			for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
			{
				String lang = keyVal.getValue();
				if (hidden.get(lang))  continue;

				if (data.get(token, lang) != null)
				{
					PluralEntry rec = (PluralEntry) data.get(token, lang);
					StringEntry line = null;
					for (String key : rec.keySet())
					{
						StringEntry aline = rec.get(key);
						if (aline.token.equals(quantity)) 
						{
							line = aline;
							break;
						}//if (aline.token.equals(quantity)) 
					}//for (String key : rec.hashmap.keySet())
					if (line != null) 
					{
						TextView newCell =  createEditView(llp, line.asString(), token + ":" + lang + ":" + quantity);
						newRow.addView(newCell);
						if (line.isCdata()) newCell.setBackgroundColor(android.R.color.holo_orange_light);
					}
					else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + quantity));

				}//if (data.get(token, lang) != null)
				else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + quantity));
			}//for (String lang : langList)
			tokenTable.addView(newRow);
		}//for (String quantity : quant)
	}//private void createPluralTable(String token)
	/**
	 * create the UI for a array entry
	 */
	private void createArrayTable(String token, ArrayEntry toDisp)
	{
		TableRow newRow;
		int line;
		for (line = 0; line <= toDisp.size(); line++)
		{
			newRow = new TableRow(context);
			newRow.setBackground(context.getDrawable(R.drawable.border));

			TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			llp.setMargins(0, 0, 2, 0);//2px right-margin
			newRow.setBackgroundColor(Color.parseColor("#abe666"));
			newRow.setLayoutParams(llp);
			TextView tv;
			if (line == 0) tv = createTextView(llp, token, "token");
			else tv = createTextView(llp, "", "token:" + token + ":" + line);
			newRow.addView(tv);
			for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
			{
				String lang = keyVal.getValue();
				if (hidden.get(lang))  continue;
				if (data.get(token, lang) != null)
				{
					ArrayEntry rec = (ArrayEntry) data.get(token, lang);

					TextView newCell;
					CdataString someContent = rec.get(line);
					if (rec.size() > line) newCell = createEditView(llp, someContent.toString(), token + ":" + lang + ":" + line);
					else newCell = createEditView(llp, "", token + ":" + lang + ":" + line);
					newRow.addView(newCell);
					if (someContent != null && someContent.isCdata()) 
					{
						newCell.setBackgroundColor(android.R.color.holo_orange_light);
						//debug("set background for " + token);
					}
				}//if (data.get(token, lang) != null)
				else newRow.addView(createEditView(llp, "", token + ":" + lang + ":" + line));
			}//for (String lang : langList)
			tokenTable.addView(newRow);
		}
	}//private void createArrayTable(String token)


	private void debug(String msg)
	{
		Log.d(TAG, msg);
	}//private void method(String msg)	
	/**
	 * create a string row
	 */
	private void createStringRow(String token)
	{
		TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 0, 2, 0);//2px right-margin

		TableRow newRow = new TableRow(context);
		newRow.setBackground(context.getDrawable(R.drawable.border));
		newRow.setLayoutParams(llp);

		newRow.addView(createTextView(llp, token, "token"));
		for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
		{
			String lang = keyVal.getValue();
			if (hidden.get(lang) == null) hidden.put(lang, false);
			if (hidden.get(lang))  continue;
			StringEntry someContent = data.get(token, lang);
			String text = "";
			if (someContent != null) text = someContent.asString();
			TextView newCell = createEditView(llp, text, token + ":" + lang);
			if (someContent != null && someContent.isCdata()) 
			{
				//debug("set background for " + token);
				//Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)
				//newCell.setBackgroundColor(android.R.color.holo_orange_light);
				newCell.setBackgroundColor(R.color.colorAccent);
			}
			newRow.addView(newCell);
		}//for (String lang : langList)
		tokenTable.addView(newRow);
	}//private void createStringRow(String token)
	/**
	 * add or make visible a language
	 */
	public void addNewLang(String sanitized)
	{
		//Log.d(TAG, "asked to add " + sanitized);
		if (!data.hasCol(sanitized))
		{
			data.addCol(sanitized);
			hidden.put(sanitized, false);
			if (tokenTable == null)
			{
				//not yet initialized
				//Log.d(TAG,"no table to display yet");
				if (tmpLangList == null) tmpLangList = new ArrayList<>();
				tmpLangList.add(sanitized);
				return;
			}
		}//if
		//Log.d(TAG,"calling table view");
		//else Log.d(TAG, "allready in  " + Arrays.toString(langList.toArray(new String[langList.size()])));
		buildTableView();
	}//addNewLang
	/**
	 * create a button, add this as listener, set the hint to find it later etc...
	 */
	private Button createButton(String oneLang, String hint)
	{
		TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 0, 2, 0);//2px right-margin
		Button tv = new Button(context);
		tv.setText(oneLang);
		tv.setPadding(0, 0, 4, 3);
		tv.setOnClickListener(this);
		tv.setHint(hint);
		return tv;
	}//private Button createButton(String oneLang, String hint)
	/**
	 * onClick
	 * @argument  p1 the button clicked
	 */
	@Override
	public void onClick(View p1)
	{
		if (p1 instanceof Button)
		{
			Button clicked = (Button) p1;

			if (clicked.getHint().equals("token")) showAddTokenDialog();
			else if (clicked.getHint().equals("...")) showAddLangDialog();
			else 
			{
				//Toast.makeText(context, "clicked on lang " + clicked.getHint(), Toast.LENGTH_SHORT).show();
				boolean toggle = hidden.get(clicked.getHint());
				hidden.put(clicked.getHint().toString(), !toggle);
				buildTableView();
			} 
		}//if Button
		else if (p1 instanceof TextView)
		{
			//Toast.makeText(context, "hit token " + ((TextView)p1).getText(), Toast.LENGTH_SHORT).show();
			String token = ((TextView)p1).getHint().toString();
			if (token == null) token = "";
			//Log.d(TAG, "text '" + ((TextView)p1).getText() + "' token '" + token + "'");
			if ("token".equals(token))
			{
				//Log.d(TAG, "we have array title");
				token = ((TextView)p1).getText().toString();
				if (token == null || token.length() <= 0) token =  ((TextView)p1).getHint().toString();

				DialogTokenMenu tokenActionDia = new DialogTokenMenu(getActivity(), p1, token);
				tokenActionDia.setTokenDialogListener(this);
				tokenActionDia.getMenuInflater().inflate(R.menu.token_menu, tokenActionDia.getMenu());
				tokenActionDia.setOnMenuItemClickListener(tokenActionDia);
				tokenActionDia.show();
			}//if ("token".equals(((TextView)p1).getHint().toString()))
			else if (token.startsWith("token"))
			{
				//Log.d(TAG, "we have array line");
				//"token:" + token + ":" + line)
				DialogArray tokenActionDia = new DialogArray(getActivity(), p1, token);
				tokenActionDia.setTokenDialogListener(this);
				tokenActionDia.getMenuInflater().inflate(R.menu.array_menu, tokenActionDia.getMenu());
				tokenActionDia.setOnMenuItemClickListener(tokenActionDia);
				tokenActionDia.show();
			}

		}//else if (p1 instanceof TextView)

	}//public void onClick(View p1)
	/**
	 * user asked to remove a token, do it
	 */
	private void deleteToken(String token)
	{
		data.remove(token);
		hidden.remove(token);
		buildTableView();
	}//private void deleteToken(String token)
	/**
	 * fire up the dialog that will ask for a new/redisplay language 
	 */
	public void showAddLangDialog()
	{
		FragmentManager fm = getFragmentManager();
		DialogFragAddLang editNameDialog = new DialogFragAddLang();
		editNameDialog.setAddLangDialogListener(this);
		editNameDialog.setData(data, hidden);
		editNameDialog.show(fm, "fragment_add_lang");
	}//public void showAddLangDialog()
	/**
	 * fire up the dialog that will ask for a new token
	 */
	public void showAddTokenDialog()
	{
		FragmentManager fm = getFragmentManager();
		DialogFragAddToken editNameDialog = new DialogFragAddToken();
		editNameDialog.setAddTokenDialogListener(this);
		editNameDialog.show(fm, "fragment_add_token");
	}
	/**
	 * the add lang dialog finished, add now the language or fail if it cannot be interpeted as ISO code
	 */
	@Override
	public void onFinishAddLangDialog(String inputText)
	{
		//Log.d(TAG, "Add lang: " + inputText);
		if (inputText == null || inputText.length() <= 0) return;
		String sanitized = inputText.trim();
		if (hidden.get(sanitized) != null)
		{
			//Log.d(TAG, "known lang: " + hidden.get(sanitized));
			hidden.put(sanitized, false);
			buildTableView();
			return;
		}//if (hidden.get(sanitized) != null)

		sanitized = normalizer.normalizeLangName(sanitized);
		if (hidden.get(sanitized) != null)
		{
			boolean status = hidden.get(sanitized);
			hidden.put(sanitized, !status);
			buildTableView();
			return;
		}//else  if (hidden.get(sanitized) != null)
		addNewLang(sanitized);
	}//public void onFinishAddLangDialog(String inputText)
	/**
	 * callback to notify that a token should be added
	 */
	@Override
	public void onFinishAddTokenDialog(StringEntry input)
	{
		data.set(input.token, "default", input);
		buildTableView();
	}//public void onFinishAddTokenDialog(StringEntry input)
	/**
	 * coming back from the hovering menu
	 */
	public void onFinishTokenDialog(String token, String action)
	{
		switch (action)
		{
			case "delete": 
				if (token.contains(":"))
				{
					String[] pos = token.split(":");
					token = pos[0];
					try
					{
						int line = Integer.parseInt(pos[1]);
						for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
						{
							String lang = keyVal.getValue();
							StringEntry aE = data.get(token, lang);
							if (aE != null && aE instanceof ArrayEntry)
							{
								ArrayEntry rec = (ArrayEntry) aE;
								rec.remove(line);
							}//if (aE != null && aE instanceof ArrayEntry)
						}//for (String lang: langList)
						//Toast.makeText(context, "removed line  " + line + " of " + token, Toast.LENGTH_SHORT).show();
					}//try
					catch (NumberFormatException e)
					{
						Toast.makeText(context, "couldn't extract linenum out of  " + token, Toast.LENGTH_SHORT).show();
					}//catch
				}//if (token.contains(":"))
				else deleteToken(token);
				break;
			case "rename":
				//Toast.makeText(context, "rename of " + token + " not yet supported  ", Toast.LENGTH_SHORT).show();
				DialogRenameFrag dial = new DialogRenameFrag().setData(data, token).setCaller(this);
				dial.show(context.getFragmentManager(), "rename");
//				for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
//				{
//					String lang = keyVal.getValue();
//					StringEntry aE = data.get(token, lang);
//					if (aE != null)
//					{
//					}//if (aE != null && aE instanceof ArrayEntry)
//				}//for (String lang: langList)
//				
				break;
			case "copy":
				//Toast.makeText(context, "copy  " + token + " not yet supported  ", Toast.LENGTH_SHORT).show();
				if (data.contains(token))
				{
					int n = 1;
					Pattern pat = Pattern.compile("^(.*?)(\\d+)\\s*$");
					Matcher m = pat.matcher(token);
					if(m.find())
					{
						token = m.group(1);
						n = Integer.parseInt(m.group(2));
					}
					String newName = token + n;
					while (data.contains(newName)) 
					{
						n++;
						newName = token + n;
					}//while(data.contains(newName))

					for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
					{
						String lang = keyVal.getValue();
						StringEntry aE = data.get(token, lang);

						if (aE != null)
						{
							data.set(newName, lang, StringEntryFactory.instantiate(aE));
						}//if (aE != null && aE instanceof ArrayEntry)
					}//for (String lang: langList)
				}//	if (data.contains(token))
				break;
		}//    switch (action)
		buildTableView();
	}//public void onFinishTokenDialog(String inputText)
	/**
	 clear
	 * 
	 * clear the data
	 */
	public void clear()
	{
		data.clear();
		hidden.clear();
	}
	/**
	 * coming back from the hovering menu
	 */
	public void onFinishArrayDialog(String token, String action)
	{
		int line;
		if (token.contains(":"))
		{
			String[] pos = token.split(":");
			token = pos[1];
			try
			{
				line = Integer.parseInt(pos[2]);
				//Toast.makeText(context, "removed line  " + line + " of " + token, Toast.LENGTH_SHORT).show();
			}//try
			catch (NumberFormatException e)
			{
				Toast.makeText(context, "couldn't extract linenum out of  " + token, Toast.LENGTH_SHORT).show();
				return;
			}//catch
		}//if (token.contains(":"))
		else
		{
			Toast.makeText(context, "not correct token  " + token, Toast.LENGTH_SHORT).show();
			return;
		}

		switch (action)
		{
			case "delete":
				for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
				{
					String lang = keyVal.getValue();
					StringEntry aE = data.get(token, lang);
					if (aE != null && aE instanceof ArrayEntry)
					{
						ArrayEntry rec = (ArrayEntry) aE;
						rec.remove(line);
					}//if (aE != null && aE instanceof ArrayEntry)
				}//for (String lang: langList)

				//else deleteToken(token);
				break;
			case "up":
				for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
				{
					String lang = keyVal.getValue();
					StringEntry aE = data.get(token, lang);
					if (aE != null && aE instanceof ArrayEntry)
					{
						ArrayEntry rec = (ArrayEntry) aE;
						rec.swap(line, line - 1);
					}//if (aE != null && aE instanceof ArrayEntry)
				}//for (String lang: langList)
				break;
			case "down":
				for (Map.Entry<Integer,String> keyVal: data.header().entrySet())
				{
					String lang = keyVal.getValue();
					StringEntry aE = data.get(token, lang);
					if (aE != null && aE instanceof ArrayEntry)
					{
						ArrayEntry rec = (ArrayEntry) aE;
						rec.swap(line, line + 1);
					}//if (aE != null && aE instanceof ArrayEntry)
				}//for (String lang: langList)
				break;
		}//    switch (action)
		buildTableView();
	}//public void onFinishArrayDialog(String inputText)

}//public class StringXmlTableFrag extends Fragment
