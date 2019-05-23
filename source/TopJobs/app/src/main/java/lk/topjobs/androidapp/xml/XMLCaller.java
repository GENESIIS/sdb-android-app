package lk.topjobs.androidapp.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import lk.topjobs.androidapp.data.JobPostData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;

import android.os.AsyncTask;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

/**
 * @author Harsha Kodagoda
 * 20190523 PS SDB-954 Add LC attribute from the adapter to get the location data from the RSS.
 */
public class XMLCaller extends AsyncTask<String, String, XMLCallResult> {
	private XMLCallback xmlCallback = null;
	private JobPostData jobPostData;
	private int currentPostId = 0;
	// private ArrayList<JobPostData> favoriteJobList;
	private SimpleDateFormat shortDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.getDefault());
	private SimpleDateFormat fullDateFormat = new SimpleDateFormat(
			"dd MMM yyyy HH:mm:ss", Locale.getDefault());

	// public XMLCaller(XMLCallback xmlCallback,
	// ArrayList<JobPostData> favoriteJobList) {
	// this.xmlCallback = xmlCallback;
	// this.favoriteJobList = favoriteJobList;
	// }

	public XMLCaller(XMLCallback xmlCallback) {
		this.xmlCallback = xmlCallback;
	}

	@Override
	protected XMLCallResult doInBackground(String... params) {
		XMLCallResult callResult = new XMLCallResult();
		try {
			if (params.length > 0) {
				HttpClient httpClient = new DefaultHttpClient();
				String url = params[0];
				HttpPost httpPost = new HttpPost(url);
				HttpResponse httpResponse;
				httpResponse = httpClient.execute(httpPost);
				HttpEntity httpResponseEntity = httpResponse.getEntity();
				if (httpResponseEntity != null) {
					InputStream inputStream = httpResponseEntity.getContent();
					callResult = parseXML(convertStreamToString(inputStream));
					return callResult;
				} else {
					callResult.jobPostList = null;
					callResult.status = 0;
					return callResult;
				}
			} else {
				callResult.jobPostList = null;
				callResult.status = 0;
				return callResult;
			}
		} catch (Exception ex) {
			callResult.status = -1;
			callResult.exception = ex;
			callResult.jobPostList = null;
			return callResult;
		}

	}

	private String convertStreamToString(InputStream inputStream) {
		BufferedReader bufferedReader = null;
		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stringBuilder.toString();
	}

	private XMLCallResult parseXML(String data) {
		final XMLCallResult xmlCallResult = new XMLCallResult();
		final ArrayList<JobPostData> jobList = new ArrayList<JobPostData>();
		RootElement rootElement = new RootElement("rss");
		Element elementChannel = rootElement.getChild("channel");
		elementChannel.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {

			}
		});
		Element elementLastUpdate = elementChannel.getChild("lastBuildDate");
		elementLastUpdate
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						String tempTime = body.substring(4);
						tempTime = tempTime.substring(0, tempTime.length() - 3)
								.trim();
						try {
							Date date = fullDateFormat.parse(tempTime);
							xmlCallResult.lastUpdate = date;
						} catch (ParseException e) {
							e.printStackTrace();
							xmlCallResult.lastUpdate = null;
						}
					}
				});
		Element elementItem = elementChannel.getChild("item");
		elementItem.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				jobPostData = new JobPostData();
			}
		});

		elementItem.getChild("title").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setTitle(body);
						jobPostData.splitTitle();
					}
				});
		elementItem.getChild("description").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setDescription(body);
					}
				});
		elementItem.getChild("link").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setLink(body);
					}
				});
		elementItem.getChild("pubDate").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						try {
							jobPostData.setPubDate(shortDateFormat.parse(body));
						} catch (ParseException e) {
							jobPostData.setPubDate(null);
						}
					}
				});
		elementItem.getChild("closingDate").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						try {
							jobPostData.setClosingDate(shortDateFormat
									.parse(body));
						} catch (ParseException e) {
							jobPostData.setClosingDate(null);
						}
					}
				});
		elementItem.getChild("js").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setJs(body);
						jobPostData.splitTitle();
					}
				});
		elementItem.getChild("ac").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setAc(body);
					}
				});
		elementItem.getChild("ec").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setEc(body);
					}
				});
		elementItem.getChild("lc").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						jobPostData.setLc(body);
					}
				});
		elementItem.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				jobPostData.setId(currentPostId);
				// jobPostData.setFavorite(checkFavorite(jobPostData.getRefNo()));
				jobList.add(jobPostData);
				currentPostId = currentPostId + 1;
			}
		});
		try {
			currentPostId = 0;
			Xml.parse(data, rootElement.getContentHandler());
			xmlCallResult.status = 1;
			xmlCallResult.jobPostList = jobList;
			return xmlCallResult;
		} catch (Exception e) {
			e.printStackTrace();
			xmlCallResult.status = -1;
			xmlCallResult.exception = e;
			xmlCallResult.jobPostList = null;
			return xmlCallResult;
		}
	}

	// private boolean checkFavorite(String refNo) {
	// try {
	// if (favoriteJobList != null && favoriteJobList.size() > 0) {
	// boolean match = false;
	// for (int i = 0; i < favoriteJobList.size(); i++) {
	// String favoriteRefNo = favoriteJobList.get(i).getRefNo();
	// if (favoriteRefNo.equalsIgnoreCase(refNo)) {
	// match = true;
	// break;
	// }
	// }
	// return match;
	// } else {
	// return false;
	// }
	// } catch (Exception e) {
	// return false;
	// }
	// }

	@Override
	protected void onPostExecute(XMLCallResult result) {
		super.onPostExecute(result);
		if (xmlCallback != null) {
			xmlCallback.onXMLCallComplete(result);
		}
	}
}
