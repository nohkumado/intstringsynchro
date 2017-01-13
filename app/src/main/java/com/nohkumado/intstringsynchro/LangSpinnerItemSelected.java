package com.nohkumado.intstringsynchro;

import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class LangSpinnerItemSelected implements OnItemSelectedListener
{

  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
  {
    //Toast.makeText(parent.getContext(),
    //               "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
    //               Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0)
  {
    // TODO Auto-generated method stub
  }

}
