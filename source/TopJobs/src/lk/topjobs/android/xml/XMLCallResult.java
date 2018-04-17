package lk.topjobs.android.xml;

import java.util.ArrayList;
import java.util.Date;

import lk.topjobs.android.data.JobPostData;

/**
 * @author Harsha Kodagoda
 * 
 */
public class XMLCallResult {
	public int status;
	public Exception exception;
	public Date lastUpdate;
	public ArrayList<JobPostData> jobPostList;
}
