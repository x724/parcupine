package parkservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ParkRequest {
	long uid;
	long spotId;
	int durationMinutes;	
	int chargeAmount;
	int paymentType;
	AuthRequest userInfo;
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	
	public long getSpotId() {
		return spotId;
	}
	public void setSpotId(long spotId) {
		this.spotId = spotId;
	}
	public int getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public int getChargeAmount() {
		return chargeAmount;
	}
	public void setChargeAmount(int chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	public int getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
	public AuthRequest getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(AuthRequest userInfo) {
		this.userInfo = userInfo;
	}
	
	
	
}
