package lk.topjobs.androidapp.xml;

import java.util.ArrayList;
import java.util.Date;

import lk.topjobs.androidapp.data.JobPostData;

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
