package lk.topjobs.androidapp.adapters;

import java.util.ArrayList;

import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.data.JobCategoryData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Harsha Kodagoda
 * 
 */
public class JobCategoryListAdapter extends BaseAdapter {

	private ArrayList<JobCategoryData> jobCategoryList;
	private LayoutInflater layoutInflater;

	public JobCategoryListAdapter(Context context,
			ArrayList<JobCategoryData> jobCategoryList) {
		this.jobCategoryList = jobCategoryList;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return jobCategoryList.size();
	}

	@Override
	public JobCategoryData getItem(int index) {
		return jobCategoryList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.listrow_job_category_list,
					viewGroup, false);
			viewHolder = new ViewHolder();
			viewHolder.textViewName = (TextView) view
					.findViewById(R.id.textViewName);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		JobCategoryData jobCategoryData = getItem(index);
		viewHolder.textViewName.setText(jobCategoryData.getName().replace("/",
				", "));
		return view;
	}

	static class ViewHolder {
		TextView textViewName;
	}
}
