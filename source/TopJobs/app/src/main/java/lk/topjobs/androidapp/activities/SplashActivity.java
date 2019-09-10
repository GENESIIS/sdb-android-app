/**
 * 20180823 PS SDB-921-4669 Remove EasyTracker activity start from onStart() and onStop() methods
 * 20190517 PS SDB-954-4701 Removed the LocationListener from this class.
 */

package lk.topjobs.androidapp.activities;
import lk.topjobs.androidapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Harsha Kodagoda
 *
 */
public class SplashActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		try {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startCategoryActivity();
				}
			});
			thread.start();
		} catch (Exception ex) {
			startCategoryActivity();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void startCategoryActivity() {
		Intent intent = new Intent(this, JobCategoryListActivity.class);
		startActivity(intent);
		finish();
	}

}
