package ir.fanap.samplesdk;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by R.Sharifi on 27 Jan 2021
 */
public class  Toasty {

 public static Toast success(Context context, String message,long duration,boolean flag) {
    return Toast.makeText(context, message, Toast.LENGTH_LONG);
  }

  public static Toast success(Context context, String message) {
    return Toast.makeText(context, message, Toast.LENGTH_LONG);
  }

  public static Toast error(Context context, String message) {
    return Toast.makeText(context, message, Toast.LENGTH_LONG);
  }

  public static Toast error(Context context, String message,long duration,boolean flag) {
    return Toast.makeText(context, message, Toast.LENGTH_LONG);
  }

}
