package lk.topjobs.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import lk.topjobs.android.data.JobCategoryData;
import lk.topjobs.android.data.JobPostData;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.res.AssetManager;

/**
 * @author Harsha Kodagoda
 * 
 */
public class MainApplication extends Application {
	private ArrayList<JobCategoryData> jobCategoryList;
	public ArrayList<JobPostData> originalJobList;
	public ArrayList<JobPostData> filteredJobList;
	public ArrayList<JobPostData> favoriteJobList;

	public ArrayList<JobCategoryData> getJobCategoryList() {
		if (jobCategoryList == null) {
			jobCategoryList = loadRSSFeedList();
		}
		return jobCategoryList;
	}

	private ArrayList<JobCategoryData> loadRSSFeedList() {
		AssetManager assetManager = getApplicationContext().getResources()
				.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("categories.json");
			if (inputStream != null) {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				ArrayList<JobCategoryData> categoryList = new ArrayList<JobCategoryData>();
				JSONArray jsonArray = new JSONArray(stringBuilder.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					JobCategoryData jobCategoryData = new JobCategoryData();
					jobCategoryData.setId(jsonObject.getInt("id"));
					jobCategoryData.setName(jsonObject.getString("name"));
					jobCategoryData.setUrl(jsonObject.getString("url"));
					categoryList.add(jobCategoryData);
				}
				return categoryList;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
