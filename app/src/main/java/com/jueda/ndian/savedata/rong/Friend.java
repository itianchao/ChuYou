package com.jueda.ndian.savedata.rong;
/**
 * 
 * @author AM 模拟你的好友信息 bean
 *
 */
public class Friend {
	
	
	public String userId;
	
	public String userName;
	
	public String portraitUri;

	public Friend(String userId, String userName, String portraitUri){
		this.userId = userId;
		this.userName = userName;
		this.portraitUri = portraitUri;
	}
	public Friend(){

	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPortraitUri() {
		return portraitUri;
	}

	public void setPortraitUri(String portraitUri) {
		this.portraitUri = portraitUri;
	}
	
	
}
