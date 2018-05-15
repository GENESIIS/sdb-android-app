package lk.topjobs.androidapp.activities;

import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.utils.ShowToast;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;

/**
 * @author Harsha Kodagoda
 * 
 */
public class AdvertViewerActivity extends SherlockActivity {
	private WebView webView;
	private ProgressBar progressBar;
	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advert_viewer);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar_gap);
		webView = (WebView) findViewById(R.id.webView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		try {
			url = getIntent().getExtras().getString("advert_link");
			Log.i(url);
		} catch (Exception e) {
			url = "";
		}
		launchUrl();
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
		getSupportMenuInflater().inflate(R.menu.advert_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_reload) {
			launchUrl();
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void launchUrl() {
		if (url.length() > 0) {
			if (progressBar != null) {
				progressBar.setVisibility(View.VISIBLE);
			}
			webView.getSettings().setLoadWithOverviewMode(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setLoadsImagesAutomatically(true);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
			webView.setWebViewClient(new WebClient());
		}
	}

	private void hideProgressBar() {
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}

	private class WebClient extends WebViewClient {

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			hideProgressBar();
			super.onPageFinished(view, url);
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
