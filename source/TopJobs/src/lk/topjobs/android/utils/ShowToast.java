package lk.topjobs.android.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Harsha Kodagoda
 * 
 */
public class ShowToast {
	public ShowToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 120);
		toast.show();
	}

	public ShowToast(Context context, Exception e) {
		Toast toast = Toast.makeText(context, e.toString(), Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 120);
		toast.show();
	}
}
