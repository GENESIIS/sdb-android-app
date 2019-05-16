/**
 * 20180823 PS SDB-921-4669 Remove EasyTracker activity start from onStart() and onStop() methods
 * 20190516 PS SDB-954-4701 Add the LocationManager class and get the GPS location by LocationAddress.
 */

package lk.topjobs.androidapp.activities;

import java.util.ArrayList;

import lk.topjobs.androidapp.MainApplication;
import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.adapters.JobCategoryListAdapter;
import lk.topjobs.androidapp.data.JobCategoryData;
import lk.topjobs.androidapp.data.LocationAddress;
import lk.topjobs.androidapp.utils.ShowToast;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Harsha Kodagoda
 *
 */
public class JobCategoryListActivity extends SherlockActivity implements
		OnItemClickListener, LocationListener {
	private MainApplication application;
	private ListView listView;
	private JobCategoryListAdapter adapter;
	private LocationManager locationManager;

	@SuppressWarnings("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_category_list);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar_gap);
		application = (MainApplication) getApplication();
		ArrayList<JobCategoryData> jobCategoryList = application
				.getJobCategoryList();
		listView = (ListView) findViewById(R.id.listView);
		adapter = new JobCategoryListAdapter(this, jobCategoryList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);


		//Checking the GPS location CC4701
		if (!isNetworkConnected()){
			LocationAddress.showSettingsAlert(JobCategoryListActivity.this);
		}else{
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10,0,this);
			LocationAddress.getAddressFromLocation(getApplicationContext(),locationManager);
			Log.i("GPS", LocationAddress.getInstance().locationStr);
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.categories, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.action_favorites) {
			Intent intent = new Intent(this, FavoriteActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		Intent intent = new Intent(this, JobListActivity.class);
		intent.putExtra("category_index", index);
		intent.putExtra("location", LocationAddress.getInstance().locationStr);
		startActivity(intent);
	}

	public void onClick(View view) {
		if (view.getId() == R.id.imageViewFooterLogo) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.topjobs_url)));
				startActivity(intent);
			} catch (Exception ex) {
				new ShowToast(this, getString(R.string.unable_to_open_link));
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
}
