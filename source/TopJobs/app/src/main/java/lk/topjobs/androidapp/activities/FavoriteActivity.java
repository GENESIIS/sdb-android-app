package lk.topjobs.androidapp.activities;

import lk.topjobs.androidapp.MainApplication;
import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.adapters.FavoriteListAdapter;
import lk.topjobs.androidapp.data.JobPostData;
import lk.topjobs.androidapp.database.DatabaseHelper;
import lk.topjobs.androidapp.utils.ShowToast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
public class FavoriteActivity extends SherlockActivity implements
		OnClickListener, OnItemClickListener {
	private DatabaseHelper databaseHelper;
	private MainApplication application;
	private FavoriteListAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_list);
		setResult(RESULT_CANCELED);
		application = (MainApplication) getApplication();
		databaseHelper = new DatabaseHelper(this);
		listView = (ListView) findViewById(R.id.listView);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar_gap);
		updateFavoriteList();
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

	private void updateFavoriteList() {
		try {
			application.favoriteJobList = databaseHelper.getAllFavorites();
			adapter = new FavoriteListAdapter(this,
					application.favoriteJobList, this);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
		} catch (Exception e) {
			new ShowToast(this,
					getString(R.string.unable_to_open_favorite_jobs));
			new ShowToast(this, e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.favorites, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.action_remove_all) {
			if (application.favoriteJobList != null
					&& application.favoriteJobList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(false);
				builder.setMessage(getString(R.string.favorites_delete_all_confirmation));
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteAllFavoritesJob();
							}
						});
				builder.setNegativeButton(getString(R.string.no),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						});
				builder.show();
			}
		} else if (item.getItemId() == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imageViewFooterLogo) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.topjobs_url)));
				startActivity(intent);
			} catch (Exception ex) {
				new ShowToast(this, getString(R.string.unable_to_open_link));
			}
		} else {
			final JobPostData jobPostData = (JobPostData) view.getTag();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setMessage(getString(R.string.favorites_delete_confirmation));
			builder.setPositiveButton(getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							deleteFavoritesJob(jobPostData);
						}
					});
			builder.setNegativeButton(getString(R.string.no),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					});
			builder.show();
		}
	}

	private void deleteFavoritesJob(JobPostData jobPostData) {
		try {
			if (databaseHelper.removeFromFavorite(jobPostData.getRefNo())) {
				new ShowToast(this, getString(R.string.successfully_removed));
				updateFavoriteList();
			}
			setResult(RESULT_OK);
		} catch (Exception e) {
			new ShowToast(this, getString(R.string.unable_to_remove_this_job));
		}
	}

	private void deleteAllFavoritesJob() {
		try {
			if (databaseHelper.removeAllFavorite()) {
				new ShowToast(this,
						getString(R.string.all_jobs_successfully_removed));
				updateFavoriteList();
			}
			setResult(RESULT_OK);
		} catch (Exception e) {
			new ShowToast(this, getString(R.string.unable_to_remove_all_jobs));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int index,
			long arg3) {
		Intent intent = new Intent(this, JobDetailsActivity.class);
		intent.putExtra("list_index", index);
		intent.putExtra("mode", 2);
		startActivityForResult(intent, 2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2 && resultCode == RESULT_OK) {
			updateFavoriteList();
		}
	}
}
