package com.parq.server.dao.model.object;

import java.util.Date;
import java.util.List;

public class UserSelfReporting {
	private long reportId;
	private long userId;
	private List<Long> spaceIds;
	private List<String> parkingSpaceStatus;
	private Date reportDateTime;
	private int score1;
	private int score2;
	private int score3;
	private int score4;
	private int score5;
	private int score6;

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<Long> getSpaceIds() {
		return spaceIds;
	}

	public void setSpaceIds(List<Long> spaceIds) {
		this.spaceIds = spaceIds;
	}

	public List<String> getParkingSpaceStatus() {
		return parkingSpaceStatus;
	}

	public void setParkingSpaceStatus(List<String> parkingSpaceStatus) {
		this.parkingSpaceStatus = parkingSpaceStatus;
	}

	public int getScore1() {
		return score1;
	}

	public void setScore1(int score1) {
		this.score1 = score1;
	}

	public int getScore2() {
		return score2;
	}

	public void setScore2(int score2) {
		this.score2 = score2;
	}

	public int getScore3() {
		return score3;
	}

	public void setScore3(int score3) {
		this.score3 = score3;
	}

	public int getScore4() {
		return score4;
	}

	public void setScore4(int score4) {
		this.score4 = score4;
	}

	public int getScore5() {
		return score5;
	}

	public void setScore5(int score5) {
		this.score5 = score5;
	}

	public int getScore6() {
		return score6;
	}

	public void setScore6(int score6) {
		this.score6 = score6;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}
}
