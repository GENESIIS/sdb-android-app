/**
 * 20180823 PS SDB-921-4669 Remove EasyTracker activity start from onStart() and onStop() methods
 * 20190516 PS SDB-954-4701 Add the LocationManager class and get the GPS location by LocationAddress and a pop up.
 */

package lk.topjobs.androidapp.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import lk.topjobs.androidapp.MainApplication;
import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.adapters.JobListAdapter;
import lk.topjobs.androidapp.data.JobCategoryData;
import lk.topjobs.androidapp.data.JobPostData;
import lk.topjobs.androidapp.data.LocationAddress;
import lk.topjobs.androidapp.utils.ShowToast;
import lk.topjobs.androidapp.xml.XMLCallResult;
import lk.topjobs.androidapp.xml.XMLCallback;
import lk.topjobs.androidapp.xml.XMLCaller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Harsha Kodagoda
 *
 */
public class JobListActivity extends SherlockActivity implements XMLCallback,
		TextWatcher, OnItemClickListener, ActionBar.OnNavigationListener, LocationListener {
	private MainApplication application;
	private PopupWindow progressPopup;
	private ListView listView;
	private EditText editTextSearch;
	private TextView textViewTotalPosts;
	private TextView textViewLastUpdate;
	private JobListAdapter jobListAdapter;
	private JobCategoryData currentJobCategory;
	private ArrayAdapter<JobCategoryData> categorySpinnerAdapter;
	private boolean isDownloading = false;
	private RelativeLayout layoutProgress;
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_list);
		application = (MainApplication) getApplication();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar);
		getSupportActionBar().hide();
		layoutProgress = (RelativeLayout) findViewById(R.id.layoutProgress);
		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		editTextSearch.addTextChangedListener(this);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		textViewTotalPosts = (TextView) findViewById(R.id.textViewTotalPosts);
		textViewLastUpdate = (TextView) findViewById(R.id.textViewLastUpdate);
		ArrayList<JobCategoryData> jobCategoryList = application
				.getJobCategoryList();
		if (jobCategoryList != null && jobCategoryList.size() > 0) {
			int categoryIndex = 0;
			try {
				categoryIndex = getIntent().getExtras().getInt("category_index");
				currentJobCategory = jobCategoryList.get(categoryIndex);
			} catch (Exception e) {
				currentJobCategory = jobCategoryList.get(0);
			}
			categorySpinnerAdapter = new ArrayAdapter<JobCategoryData>(this,
					R.layout.actionbar_spinner_dropdown_item, jobCategoryList);
			getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_LIST);
			getSupportActionBar().setListNavigationCallbacks(
					categorySpinnerAdapter, this);
			getSupportActionBar().setSelectedNavigationItem(categoryIndex);
			initialDownload();
		} else {

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

	private void initialDownload() {
		layoutProgress.post(new Runnable() {
			public void run() {
				startInitialDownload();
			}
		});
	}

	private void startInitialDownload() {
		downloadRSSFeed();
		getSupportActionBar().show();
		layoutProgress.setVisibility(View.GONE);
	}

	private void showProgressPopup() {
		View popUpView = getLayoutInflater().inflate(
				R.layout.popup_window_loading_screen, null);
		progressPopup = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		progressPopup.setAnimationStyle(android.R.style.Animation_Dialog);
		progressPopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0);

	}

	private void clear() {
		try {
			editTextSearch.setText("");
			textViewLastUpdate.setText("");
			textViewTotalPosts.setText("");
			application.filteredJobList.clear();
			application.originalJobList.clear();
			refreshList();
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.job_list, menu);
		return true;
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	@SuppressLint("MissingPermission")
	private void openPopUp(){
		//Checking the GPS location CC4701
		if (!isNetworkConnected()){
			LocationAddress.showSettingsAlert(JobListActivity.this);
		}else{
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10,0,this);
			LocationAddress.getAddressFromLocation(getApplicationContext(),locationManager);
			Log.i("GPS", LocationAddress.getInstance().locationStr);
		}

		String[] colors = {LocationAddress.getInstance().locationStr, "ALL"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Search according to current Location");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadRSSFeed();
			}
		});
		builder.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.action_refresh) {
			downloadRSSFeed();
		}else if (item.getItemId() == R.id.action_gps) {
			openPopUp();
		} else if (item.getItemId() == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_favorites) {
			Intent intent = new Intent(this, FavoriteActivity.class);
			startActivityForResult(intent, 1);
		}
		return super.onOptionsItemSelected(item);
	}

	private void downloadRSSFeed() {
		clear();
		isDownloading = true;
		if (currentJobCategory != null) {
			showProgressPopup();
			XMLCaller xmlCaller = new XMLCaller(this);
			xmlCaller.execute(currentJobCategory.getUrl());
		}
	}

	private void refreshList() {
		try {
			jobListAdapter.notifyDataSetChanged();
		} catch (Exception e) {
		}
	}
 
	@Override
	public void onXMLCallComplete(XMLCallResult result) {
		try {
			if (result.jobPostList != null) {
				application.originalJobList = result.jobPostList;
				application.filteredJobList = result.jobPostList;
				try {
					Collections.sort(application.originalJobList,
							new JobListIdComparator());
					Collections.sort(application.originalJobList,
							new JobListDateComparator());
					jobListAdapter = new JobListAdapter(this, application);
					listView.setAdapter(jobListAdapter);
					textViewTotalPosts.setText(application.originalJobList
							.size() + " " + getString(R.string.job_postings));
					try {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
						textViewLastUpdate
								.setText(getString(R.string.last_update) + " "
										+ dateFormat.format(result.lastUpdate));
					} catch (Exception ex) {
						textViewLastUpdate.setText("");
					}
				} catch (Exception e) {
					if (progressPopup != null)
						progressPopup.dismiss();
					isDownloading = false;
					new ShowToast(this, getString(R.string.download_error_msg));
				}
				if (progressPopup != null)
					progressPopup.dismiss();
				isDownloading = false;
			} else {
				if (progressPopup != null)
					progressPopup.dismiss();
				isDownloading = false;
				new ShowToast(this, getString(R.string.download_error_msg));
			}
		} catch (Exception ex) {
			new ShowToast(this, getString(R.string.download_error_msg));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		Intent intent = new Intent(this, JobDetailsActivity.class);
		intent.putExtra("list_index", index);
		startActivityForResult(intent, 1);
	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
		if (jobListAdapter != null) {
			jobListAdapter.getFilter().filter(text.toString());
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

	private class JobListIdComparator implements Comparator<JobPostData> {

		@Override
		public int compare(JobPostData lhs, JobPostData rhs) {
			return String.valueOf(rhs.getId()).compareTo(
					String.valueOf(lhs.getPubDate()));
		}

	}

	private class JobListDateComparator implements Comparator<JobPostData> {

		@Override
		public int compare(JobPostData lhs, JobPostData rhs) {
			return rhs.getPubDate().compareTo(lhs.getPubDate());
		}

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (isDownloading == false) {
			try {
				if (currentJobCategory == null) {
					currentJobCategory = categorySpinnerAdapter
							.getItem(itemPosition);
					downloadRSSFeed();
				} else {
					if (currentJobCategory.getId() == categorySpinnerAdapter
							.getItem(itemPosition).getId()) {
					} else {
						currentJobCategory = categorySpinnerAdapter
								.getItem(itemPosition);
						downloadRSSFeed();
					}
				}
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			refreshList();
		}
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
}
