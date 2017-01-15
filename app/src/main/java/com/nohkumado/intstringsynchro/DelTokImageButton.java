package com.nohkumado.intstringsynchro;
import android.content.*;
import android.util.*;
import android.widget.*;

public class DelTokImageButton extends ImageButton
{
  String token;
  public DelTokImageButton(String name,Context context)
  {
    super(context);
    token = name;
  }//CTOR
  public DelTokImageButton(String name,Context context, AttributeSet attrs)
  {
    super(context,attrs);
    token = name;
  }//CTOR
  public DelTokImageButton(String name,Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context,attrs,defStyleAttr);
    token = name;
  }//CTOR
  public DelTokImageButton(String name,Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
  {
    super(context,attrs,defStyleAttr,defStyleRes);
    token = name;
  }

  public void setToken(String token)
  {
    this.token = token;
  }

  public String getToken()
  {
    return token;
  }//CTOR
}
