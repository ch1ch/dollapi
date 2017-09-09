package com.dollapi.domain;

public class UserAdress {

	public UserAdress() {
		
	}

	/**
	*  
	*/
	private Long id;

	/**
	*  
	*/
	private Long userId;

	/**
	*  
	*/
	private String person;

	/**
	*  
	*/
	private String mobile;

	/**
	*  
	*/
	private String address;
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Long getId(){
		return id;
	}
	
	public void setUserId(Long userId){
		this.userId = userId;
	}
	
	public Long getUserId(){
		return userId;
	}
	
	public void setPerson(String person){
		this.person = person;
	}
	
	public String getPerson(){
		return person;
	}
	
	public void setMobile(String mobile){
		this.mobile = mobile;
	}
	
	public String getMobile(){
		return mobile;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public String getAddress(){
		return address;
	}
}
