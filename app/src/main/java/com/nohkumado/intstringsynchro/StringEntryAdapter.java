package com.nohkumado.intstringsynchro;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nohkumado.nohutils.collection.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * @deprecated until we eventually put back again the listView 
 *
 */
public class StringEntryAdapter extends BaseAdapter
{
  /** my try at the holder design pattern... failed miserably :D but since i switched to tableview..*/
  public static class ViewHolder
  {
    public TextView tagView;
    public EditText defView;
  }//public static class ViewHolder
  
  private static final String TAG="Adapter";/** needed for Log.d*/

  private TreeMapTable<String,String> mData = new TreeMapTable<>(); /** the tabulare data */
  private LayoutInflater mInflater; /** the inflater sevice */

  private Context myContext; /** the context */
  /** CTOR */
  public StringEntryAdapter(MainActivity c, TreeMapTable<String,String> d)
  {
    myContext = c;
    mData = d;
  }//CTOR
  /**
   * delegation for the object count
   */
  @Override
  public int getCount()
  {
    return mData.size();
  }//public int getCount()
  /** 
   * delegation for tthe getter
   */
  @Override
  public String getItem(int position)
  {
    return mData.get(position);
  }//public String getItem(int position)
  /**
  * the actual selected position
  */
  @Override
  public long getItemId(int position)
  {
    return position;
  }//public long getItemId(int position)
  /**
  * getView
  * inflate the different listview lines
  */
  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ViewHolder holder = null;
    if (mInflater == null)   mInflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (convertView == null)
    {
      holder = new ViewHolder();
      convertView = mInflater.inflate(R.layout.str_listview_line, null);
      holder.tagView = (TextView)convertView.findViewById(R.id.lw_label);
      holder.defView = (EditText)convertView.findViewById(R.id.lw_edit);
      convertView.setTag(holder);
    }//if (convertView == null)
    else holder = (ViewHolder)convertView.getTag();

    String key = mData.get(position);
    holder.tagView.setText(key);
    if (key != null) holder.defView.setText(mData.get(key, "default"));
    //Log.d(TAG, "displaying " + position + " k:" + key + " v:" + mData.get(key, "default") + "\n" + mData);
    return convertView;
  }//public View getView(int position, View convertView, ViewGroup parent)
}//public class StringEntryAdapter extends BaseAdapter
