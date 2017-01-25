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
 * floating menu over token
 */
public class DialogTokenMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener
{
  /** the interface to be able to pass back stuff to the callback
   */
  public interface TokenDialogListener
  {
    /** the method the callback shoud implement */
    void onFinishTokenDialog(String token, String action);
  }//public interface TokenDialogListener

  public static final String TAG = "DiaTok"; /** needed for Log.d */
  TokenDialogListener listener; /** the callback */
  String token = "";
  /**
   * CTOR
   */
  public DialogTokenMenu(Context context, View anchor, String token)
  {
    super(context, anchor);
    this.token = token;
  }//public DialogTokenMenu(Context context, View anchor, String token)
  /**
   * CTOR
   */
  public DialogTokenMenu(Context context, View anchor, int gravity, String token)
  {
    super(context, anchor, gravity);
    this.token = token;
  }//public DialogTokenMenu(Context context, View anchor, int gravity, String token)
  /**
   * CTOR
   */
  public DialogTokenMenu(Context context, View anchor, int gravity, int popupStyleAttr, int popupStyleRes, String token) 
  {
    super(context, anchor, gravity, popupStyleAttr, popupStyleRes);
    this.token = token;
  }//public DialogTokenMenu(Context context, View anchor, int gravity, int popupStyleAttr, int popupStyleRes, String token) 
  /**
   * catch click on menu item
   */
  @Override
  public boolean onMenuItemClick(MenuItem p1)
  {
    switch (p1.getItemId())
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
    }//switch (p1.getItemId())
    return true;
  }//public boolean onMenuItemClick(MenuItem p1)
  /**
   * set the callback
   */
  public void setTokenDialogListener(TokenDialogListener p0)
  {
    listener = p0;
  }//public void setTokenDialogListener(TokenDialogListener p0)
}//public class DialogTokenMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener
