package com.nohkumado.intstringsynchro;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nohkumado.nohutils.collection.*;

public class StringEntryAdapter extends BaseAdapter
{
  private static final String TAG="Adapter";
  
  private TreeMapTable<String,String> mData = new TreeMapTable<>();
  private LayoutInflater mInflater;

  private Context myContext;

  public StringEntryAdapter(MainActivity c,TreeMapTable<String,String> d)
  {
    myContext = c;
    mData = d;
  }

  /*
  public void addItem(final StringEntry item)
  {
    mData.add(item);
    notifyDataSetChanged();
  }*/

  @Override
  public int getCount()
  {
    Log.d(TAG,"returnong size "+mData.size());
    return mData.size();
  }

  @Override
  public String getItem(int position)
  {
    return mData.get(position);
  }

  @Override
  public long getItemId(int position)
  {
    return position;
  }

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
    }
    else
    {
      holder = (ViewHolder)convertView.getTag();
    }
    
    String key = mData.get(position);
    holder.tagView.setText(key);
    if(key != null) holder.defView.setText(mData.get(key,"default"));
    Log.d(TAG,"displaying "+position+" k:"+key+" v:"+mData.get(key,"default")+"\n"+mData);
    return convertView;
  }

  public static class ViewHolder
  {
    public TextView tagView;
    public EditText defView;
  }
}
