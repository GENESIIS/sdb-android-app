package lk.topjobs.android.data;

import java.util.Date;

/**
 * @author Harsha Kodagoda
 * 
 */
public class JobPostData {
	private static String advertisementUrl = "http://topjobs.lk/employer/advertismentpreview.jsp";
	private int id;
	private String title;
	private String position;
	private String employer;
	private String link;
	private String description;
	private Date pubDate;
	private Date closingDate;
	private String ac;
	private String js;
	private String ec;

	// private boolean favorite = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title.replace("�", "");
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPosition() {
		return position.replace("�", "");
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEmployer() {
		return employer.replace("�", "");
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description.replace("�", "");
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public Date getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	public String getAc() {
		return ac;
	}

	public void setAc(String ac) {
		this.ac = ac;
	}

	public String getJs() {
		return js;
	}

	public void setJs(String js) {
		this.js = js;
	}

	public String getEc() {
		return ec;
	}

	public void setEc(String ec) {
		this.ec = ec;
	}

	// public boolean isFavorite() {
	// return favorite;
	// }
	//
	// public void setFavorite(boolean favorite) {
	// this.favorite = favorite;
	// }

	public void splitTitle() {
		try {
			if (getJs().length() > 0 && getTitle().length() > 0) {
				String[] titleParts = getTitle().split(getJs());
				if (titleParts.length >= 2) {
					String position = titleParts[0].trim();
					if (position.endsWith(" -")) {
						position = position.substring(0, position.length() - 2);
					}
					setPosition(position.replace("�", ""));
					String employer = titleParts[1].trim();
					if (employer.startsWith("- ")) {
						employer = employer.substring(2);
					}
					setEmployer(employer.replace("�", ""));
				}
			}
		} catch (Exception ex) {
		}
	}

	public String getRefNo() {
		return getJs();
	}

	public String getAdvertisementLink() {
		try {
			if (getAc().length() > 0 && getJs().length() > 0
					&& getEc().length() > 0) {
				String params = "?jc=" + getJs() + "&ac=" + getAc() + "&ec="
						+ getEc();
				return advertisementUrl + params;
			} else {
				return "";
			}
		} catch (Exception ex) {
			return "";
		}
	}
}
