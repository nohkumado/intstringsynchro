package com.nohkumado.intstringsynchro;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.github.angads25.filepicker.controller.*;
import java.io.*;
import java.util.*;

public class DialogFragSelectProject extends DialogFragment implements OnItemClickListener
{
  public static final String TAG = "DiaSelProj"; /** needed for logging */
  private DialogSelectionListener callback;
  private TreeMap<String,StringFile> sorted;
  private ArrayList<StringFile> data;

  private ListView projList;

  private ArrayAdapter<String> dataAdapter;


  public void setDialogSelectionListener(DialogSelectionListener callbacks)
  { callback = callbacks;}

  /**
   * set the data to be used, no defaults setted, this is really needed...
   * @param langs the list of languages
   * @param the map of cols to show
   */
  public void setData(ArrayList<StringFile> tmp)
  {
    data = tmp;
  }
  /**
   * creates the view for this thing
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.frag_sel_proj, container);
    projList = (ListView) view.findViewById(R.id.available_projs);
    getDialog().setTitle(R.string.sel_proj);
    /**
     analyze the data and pretty print it 
     */
    sorted = new TreeMap<>();
    for (StringFile aF: data) 
    {
      StringBuilder sb = new StringBuilder();
      String fName = aF.getAbsolutePath();
      //sb.append("before '").append(fName).append("' ");
      fName = fName.replace("/app/src/main/res/values", "");
      //sb.append("after app'").append(fName).append("' ");
      fName = fName.replace("/src/main/res/values", "");
      //sb.append("after src'").append(fName).append("' ");
      fName = fName.replace("/main/res/values", "");
      //sb.append("after main'").append(fName).append("' ");
      fName = fName.replace("/res/values", "");
      //sb.append("after res'").append(fName).append("' ");
      String [] splitted = fName.split("/");
      fName = splitted[splitted.length - 1];
      //sb.append("rest '").append(fName).append("' ");

      sorted.put(fName, aF);
      //sb.append("added ").append(fName).append(" for ").append(aF.getAbsolutePath());
      //Log.d(TAG,sb.toString());
    }




    ArrayList<String> list = new ArrayList<>();
    //for (File aF: data) list.add(aF.getAbsolutePath());
    for (String key: sorted.keySet()) list.add(key);

    dataAdapter = new ArrayAdapter<String>(getActivity(),
                                           android.R.layout.simple_list_item_1, list);
    projList.setOnItemClickListener(this);

    projList.setAdapter(dataAdapter);

    return view;
  }//createView

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    String key = ((TextView) view).getText().toString();
    StringFile obj = sorted.get(key);
    Toast.makeText(getActivity(), "hit "+key+"for "+obj.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    //Log.d(TAG,"clicked on "+obj.getAbsolutePath());
    callback.onSelectedFilePaths(new String[] {obj.getAbsolutePath()});
    dismiss();
  }


}
