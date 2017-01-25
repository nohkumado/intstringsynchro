package com.nohkumado.intstringsynchro;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
*/
public class DialogTokenMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener
{

  public interface TokenDialogListener
  {
    void onFinishTokenDialog(String token, String action);
  }

  public static final String TAG = "DiaTok";
  TokenDialogListener listener;
  Button delete, copy, rename,cancel;
  String token = "";

  public DialogTokenMenu(Context context, View anchor, String token)
  {
    super(context,anchor);
    this.token = token;
  }

  public DialogTokenMenu(Context context, View anchor, int gravity, String token)
  {
    super(context,anchor,gravity);
    this.token = token;
  }

  public DialogTokenMenu(Context context, View anchor, int gravity, int popupStyleAttr, int popupStyleRes,String token) 
  {
    super(context,anchor,gravity,popupStyleAttr,popupStyleRes);
    this.token = token;
  }


  @Override
  public boolean onMenuItemClick(MenuItem p1)
  {
    switch(p1.getItemId())
    {
      case R.id.token_copy:
        listener.onFinishTokenDialog(token, "copy");
        break;
      case R.id.token_delete:
        listener.onFinishTokenDialog(token, "delete");
        break;
      case R.id.token_rename:
        listener.onFinishTokenDialog(token, "rename");
        break;
    }
    
    return true;
  }
  
  public void setTokenDialogListener(TokenDialogListener p0)
  {
    listener = p0;
  }
  
  
}
