package com.nohkumado.intstringsynchro;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 
 * floating menu over token
 */

public class DialogArray extends PopupMenu implements PopupMenu.OnMenuItemClickListener
  {
    /** the interface to be able to pass back stuff to the callback
     */
    public interface ArrayDialogListener
    {
      /** the method the callback shoud implement */
      void onFinishArrayDialog(String token, String action);
    }//public interface TokenDialogListener

    public static final String TAG = "DiaArr"; /** needed for Log.d */
    ArrayDialogListener listener; /** the callback */
    String token = "";
    /**
     * CTOR
     */
    public DialogArray(Context context, View anchor, String token)
    {
      super(context, anchor);
      this.token = token;
    }//public DialogTokenMenu(Context context, View anchor, String token)
    /**
     * CTOR
     */
    public DialogArray(Context context, View anchor, int gravity, String token)
    {
      super(context, anchor, gravity);
      this.token = token;
    }//public DialogTokenMenu(Context context, View anchor, int gravity, String token)
    /**
     * CTOR
     */
    public DialogArray(Context context, View anchor, int gravity, int popupStyleAttr, int popupStyleRes, String token) 
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
        case R.id.array_up:
          listener.onFinishArrayDialog(token, "up");
          break;
        case R.id.array_del:
          listener.onFinishArrayDialog(token, "delete");
          break;
        case R.id.array_down:
          listener.onFinishArrayDialog(token, "down");
          break;
      }//switch (p1.getItemId())
      return true;
    }//public boolean onMenuItemClick(MenuItem p1)
    /**
     * set the callback
     */
    public void setTokenDialogListener(ArrayDialogListener p0)
    {
      listener = p0;
    }//public void setTokenDialogListener(TokenDialogListener p0)
  }//public class DialogTokenMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener
