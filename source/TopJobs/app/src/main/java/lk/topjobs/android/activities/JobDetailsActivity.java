/**
 * 20180417 PS SDB-868-4603 Opening the vacancy url by default browser
 *
 */

package lk.topjobs.android.activities;

import java.text.SimpleDateFormat;
import java.util.Locale;

import lk.topjobs.android.MainApplication;
import lk.topjobs.android.R;
import lk.topjobs.android.data.JobPostData;
import lk.topjobs.android.database.DatabaseHelper;
import lk.topjobs.android.utils.ShowToast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Harsha Kodagoda
 *
 *
 */
public class JobDetailsActivity extends SherlockActivity {
	private MainApplication application;
	private int mode = 1;
	private int currentIndex = 0;
	private JobPostData currentJobPost;
	private TextView textViewPosition;
	private TextView textViewEmployer;
	private TextView textViewJobRef;
	private TextView textViewClosingDate;
	private TextView textViewJobDescription;
	private DatabaseHelper databaseHelper;
	private ImageView imageViewNextButton;
	private ImageView imageViewNextInactiveButton;
	private ImageView imageViewPreviousButton;
	private ImageView imageViewPreviousInactiveButton;
	private ImageView imageViewFavoriteButton;
	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_details);
		setResult(RESULT_CANCELED);
		application = (MainApplication) getApplication();
		databaseHelper = new DatabaseHelper(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar_gap);
		textViewPosition = (TextView) findViewById(R.id.textViewPosition);
		textViewEmployer = (TextView) findViewById(R.id.textViewEmployer);
		textViewJobRef = (TextView) findViewById(R.id.textViewJobRef);
		textViewClosingDate = (TextView) findViewById(R.id.textViewClosingDate);
		textViewJobDescription = (TextView) findViewById(R.id.textViewJobDescription);
		imageViewNextButton = (ImageView) findViewById(R.id.imageViewNextButton);
		imageViewNextInactiveButton = (ImageView) findViewById(R.id.imageViewNextButtonInactive);
		imageViewPreviousButton = (ImageView) findViewById(R.id.imageViewPreviousButton);
		imageViewPreviousInactiveButton = (ImageView) findViewById(R.id.imageViewPreviousButtonInactive);
		imageViewFavoriteButton = (ImageView) findViewById(R.id.imageViewFavoriteButton);
		try {
			mode = getIntent().getExtras().getInt("mode");
		} catch (Exception e) {
			mode = 1;
		}
		try {
			currentIndex = getIntent().getExtras().getInt("list_index");
		} catch (Exception e) {
			currentIndex = 0;
		}
		if (showJobDetails() == false) {
			finish();
		}
	}

	private boolean showJobDetails() {
		try {
			int count = 0;
			if (mode == 2) {
				count = application.favoriteJobList.size();
				currentJobPost = application.favoriteJobList.get(currentIndex);
			} else {
				count = application.filteredJobList.size();
				currentJobPost = application.filteredJobList.get(currentIndex);
			}
			if (currentIndex == 0) {
				imageViewPreviousButton.setVisibility(View.GONE);
				imageViewPreviousInactiveButton.setVisibility(View.VISIBLE);
			} else {
				imageViewPreviousButton.setVisibility(View.VISIBLE);
				imageViewPreviousInactiveButton.setVisibility(View.GONE);
			}
			if (currentIndex == count - 1) {
				imageViewNextButton.setVisibility(View.GONE);
				imageViewNextInactiveButton.setVisibility(View.VISIBLE);
			} else {
				imageViewNextButton.setVisibility(View.VISIBLE);
				imageViewNextInactiveButton.setVisibility(View.GONE);
			}
			textViewPosition.setText(currentJobPost.getPosition());
			textViewEmployer.setText(currentJobPost.getEmployer());
			try {
				int refNo = Integer.parseInt(currentJobPost.getRefNo());
				textViewJobRef.setText(String.valueOf(refNo));
			} catch (Exception e) {
				textViewJobRef.setText(currentJobPost.getRefNo());
			}
			textViewJobDescription.setText(currentJobPost.getDescription());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"EEEE, MMMM dd, yyyy", Locale.getDefault());
			textViewClosingDate.setText(dateFormat.format(currentJobPost
					.getClosingDate()));
			if (databaseHelper.isFavorite(currentJobPost.getRefNo())) {
				imageViewFavoriteButton
						.setImageResource(R.drawable.btn_favorite_on);
			} else {
				imageViewFavoriteButton
						.setImageResource(R.drawable.btn_favorite_off);
			}
			return true;
		} catch (Exception ex) {
			Log.i("PASINDU",ex.getMessage());
			new ShowToast(this, getString(R.string.unable_to_open_this_job));
			return false;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.job_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.action_share) {
			shareJob();
		} else if (item.getItemId() == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_favorites) {
			Intent intent = new Intent(this, FavoriteActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void shareJob() {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			String extraText = getString(R.string.check_out_this_job) + "\n\n"
					+ currentJobPost.getAdvertisementLink();
			intent.putExtra(Intent.EXTRA_TEXT, extraText);
			intent.putExtra(
					android.content.Intent.EXTRA_SUBJECT,
					currentJobPost.getPosition() + " - "
							+ currentJobPost.getEmployer());
			startActivity(Intent.createChooser(intent,
					getString(R.string.share_via)));
		} catch (Exception ex) {
			new ShowToast(this, getString(R.string.unable_to_share));
		}

	}

	private void viewNextJob() {
		try {
			int count = 0;
			if (mode == 2) {
				count = application.favoriteJobList.size();
			} else {
				count = application.filteredJobList.size();
			}
			currentIndex = currentIndex + 1;
			if (currentIndex >= count) {
				currentIndex = count - 1;
			}
			showJobDetails();
		} catch (Exception e) {
		}
	}

	private void viewPreviousJob() {
		try {
			currentIndex = currentIndex - 1;
			if (currentIndex < 0) {
				currentIndex = 0;
			}
			showJobDetails();
		} catch (Exception e) {
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
		} else if (view.getId() == R.id.imageViewPreviousButton) {
			viewPreviousJob();
		} else if (view.getId() == R.id.imageViewNextButton) {
			viewNextJob();
		} else if (view.getId() == R.id.imageViewFavoriteButton) {
			if (mode == 2) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(false);
				builder.setMessage(getString(R.string.favorites_delete_confirmation));
				builder.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteFavoritesJob();
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
			} else {
				try {
					int result = databaseHelper.toggleFavorite(currentJobPost);
					if (result == 1) {
						imageViewFavoriteButton
								.setImageResource(R.drawable.btn_favorite_on);
					} else {
						imageViewFavoriteButton
								.setImageResource(R.drawable.btn_favorite_off);
					}
					setResult(RESULT_OK);
				} catch (Exception e) {
				}
			}
		} else if (view.getId() == R.id.buttonOpenLink) {
			// SDB-868-4603 Opening the vacancy url bu default browser
			try {
				url = currentJobPost.getAdvertisementLink();
			} catch (Exception e) {
				e.printStackTrace();
				url = "";
			}
			if (url.length()>0){
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}else {
				Toast.makeText(getApplicationContext(),"Advertisement URL not found", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void deleteFavoritesJob() {
		try {
			if (databaseHelper.removeFromFavorite(currentJobPost.getRefNo())) {
				new ShowToast(this, getString(R.string.successfully_removed));
			}
			setResult(RESULT_OK);
			finish();
		} catch (Exception e) {
			new ShowToast(this, getString(R.string.unable_to_remove_this_job));
		}
	}

}