package lk.topjobs.androidapp.activities;

import lk.topjobs.androidapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Harsha Kodagoda
 * 
 */
public class SplashActivity extends Activity {

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
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	private void startCategoryActivity() {
		Intent intent = new Intent(this, JobCategoryListActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
	}
}
