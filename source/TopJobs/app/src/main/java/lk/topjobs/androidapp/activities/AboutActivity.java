/**
 * 20180823 PS SDB-921-4669 Remove EasyTracker activity start from onStart() and onStop() methods
 *
 */

package lk.topjobs.androidapp.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.utils.ShowToast;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Harsha Kodagoda
 * 
 */
public class AboutActivity extends SherlockActivity {

	private TextView textViewAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar_gap);
		textViewAbout = (TextView) findViewById(R.id.textViewAbout);
		PackageInfo packageInfo = null;
		String version = "";
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
			if (packageInfo != null) {
				version = String.valueOf(packageInfo.versionName) + " ("
						+ String.valueOf(packageInfo.versionCode) + ")";
			}
		} catch (Exception e) {
			version = "N/A";
		}
		AssetManager assetManager = getApplicationContext().getResources()
				.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("about_text.txt");
			if (inputStream != null) {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				String aboutText = String.format(stringBuilder.toString(),
						version);
				textViewAbout.setText(Html.fromHtml(aboutText));
				Linkify.addLinks(textViewAbout, Linkify.WEB_URLS);
			}
		} catch (Exception e) {
			finish();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
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
