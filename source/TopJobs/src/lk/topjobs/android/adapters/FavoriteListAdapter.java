package lk.topjobs.android.adapters;

import java.util.ArrayList;

import lk.topjobs.android.R;
import lk.topjobs.android.data.JobPostData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Harsha Kodagoda
 * 
 */
public class FavoriteListAdapter extends BaseAdapter {

	private ArrayList<JobPostData> favoriteJobList;
	private LayoutInflater layoutInflater;
	private OnClickListener onClickListener;

	public FavoriteListAdapter(Context context,
			ArrayList<JobPostData> favoriteJobList,
			OnClickListener onClickListener) {
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.favoriteJobList = favoriteJobList;
		this.onClickListener = onClickListener;
	}

	@Override
	public int getCount() {
		return favoriteJobList.size();
	}

	@Override
	public JobPostData getItem(int index) {
		return favoriteJobList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.listrow_favorite_list,
					viewGroup, false);
			viewHolder = new ViewHolder();
			viewHolder.textViewEmployer = (TextView) view
					.findViewById(R.id.textViewEmployer);
			viewHolder.textViewPosition = (TextView) view
					.findViewById(R.id.textViewPosition);
			viewHolder.imageViewDelete = (ImageView) view
					.findViewById(R.id.imageViewDeleteButton);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		try {
			JobPostData jobPostData = getItem(index);
			if (onClickListener != null) {
				viewHolder.imageViewDelete.setOnClickListener(onClickListener);
				viewHolder.imageViewDelete.setTag(jobPostData);
			}
			viewHolder.textViewEmployer.setText(jobPostData.getEmployer());
			viewHolder.textViewPosition.setText(jobPostData.getPosition());
		} catch (Exception e) {
			viewHolder.textViewEmployer.setText(e.toString());
			viewHolder.textViewPosition.setText("N/A");
		}
		return view;
	}

	static class ViewHolder {
		TextView textViewEmployer;
		TextView textViewPosition;
		ImageView imageViewDelete;
	}
}
