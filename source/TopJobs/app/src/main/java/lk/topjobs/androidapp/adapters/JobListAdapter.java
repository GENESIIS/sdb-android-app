package lk.topjobs.androidapp.adapters;

import java.util.ArrayList;
import java.util.Locale;

import lk.topjobs.androidapp.MainApplication;
import lk.topjobs.androidapp.R;
import lk.topjobs.androidapp.data.JobPostData;
import lk.topjobs.androidapp.database.DatabaseHelper;
import lk.topjobs.androidapp.utils.DateTimeUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Harsha Kodagoda
 * 
 */
public class JobListAdapter extends BaseAdapter implements Filterable,
		OnClickListener {
	private LayoutInflater layoutInflater;
	private DatabaseHelper databaseHelper;
	private MainApplication application;

	public JobListAdapter(Context context, MainApplication application) {
		databaseHelper = new DatabaseHelper(context);
		this.application = application;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		try {
			return application.filteredJobList.size();
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public JobPostData getItem(int index) {
		try {
			return application.filteredJobList.get(index);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.listrow_job_list, viewGroup,
					false);
			viewHolder = new ViewHolder();
			viewHolder.textViewEmployer = (TextView) view
					.findViewById(R.id.textViewEmployer);
			viewHolder.textViewPosition = (TextView) view
					.findViewById(R.id.textViewPosition);
			viewHolder.textViewDate = (TextView) view
					.findViewById(R.id.textViewDate);
			viewHolder.imageViewFavorite = (ImageView) view
					.findViewById(R.id.imageViewFavorite);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		try {
			JobPostData jobPostData = getItem(index);
			if (databaseHelper.isFavorite(jobPostData.getRefNo())) {
				viewHolder.imageViewFavorite
						.setImageResource(R.drawable.btn_favorite_on_list);
			} else {
				viewHolder.imageViewFavorite
						.setImageResource(R.drawable.btn_favorite_off_list);
			}
			viewHolder.imageViewFavorite.setOnClickListener(this);
			viewHolder.imageViewFavorite.setTag(jobPostData);
			viewHolder.textViewEmployer.setText(jobPostData.getEmployer());
			viewHolder.textViewPosition.setText(jobPostData.getPosition());
			String relativeTime = DateTimeUtils
					.getRelativeDayString(jobPostData.getPubDate());
			viewHolder.textViewDate.setText(relativeTime);
		} catch (Exception e) {
			viewHolder.textViewEmployer.setText(e.toString());
			viewHolder.textViewPosition.setText("N/A");
			viewHolder.textViewDate.setText("N/A");
		}
		return view;
	}

	static class ViewHolder {
		TextView textViewEmployer;
		TextView textViewPosition;
		TextView textViewDate;
		ImageView imageViewFavorite;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				final FilterResults filterResults = new FilterResults();
				final ArrayList<JobPostData> resultList = new ArrayList<JobPostData>();
				if (constraint != null) {
					if (application.originalJobList != null
							&& application.originalJobList.size() > 0) {
						for (final JobPostData jobPostData : application.originalJobList) {
							if (jobPostData
									.getPosition()
									.toLowerCase(Locale.getDefault())
									.contains(
											constraint.toString().toLowerCase(
													Locale.getDefault()))
									|| jobPostData
											.getEmployer()
											.toLowerCase(Locale.getDefault())
											.contains(
													constraint
															.toString()
															.toLowerCase(
																	Locale.getDefault())))
								resultList.add(jobPostData);
						}
					}
					filterResults.values = resultList;
				}
				return filterResults;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				application.filteredJobList = (ArrayList<JobPostData>) results.values;
				notifyDataSetChanged();
			}
		};
	}

	@Override
	public void onClick(View v) {
		JobPostData jobPostData = (JobPostData) v.getTag();
		int result = databaseHelper.toggleFavorite(jobPostData);
		ImageView imageView = (ImageView) v;
		if (result == 1) {
			imageView.setImageResource(R.drawable.btn_favorite_on_list);
			// jobPostData.setFavorite(true);
		} else {
			imageView.setImageResource(R.drawable.btn_favorite_off_list);
			// jobPostData.setFavorite(false);
		}
	}
}
