package com.dollapi.domain;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RechargeOrder  {

	public RechargeOrder() {
		
	}

	/**
	*  
	*/
	private String id;

	/**
	*  
	*/
	private Long userId;

	/**
	*  
	*/
	private String userName;

	/**
	*  
	*/
	private Long packageId;

	/**
	*  
	*/
	private String packageName;

	/**
	*  
	*/
	private BigDecimal price;

	/**
	*  
	*/
	private Long gameMoney;

	private String outPayOrder;

	/**
	*  
	*/
	private Integer payType;

	private Date createTime;

	/**
	*  1下单  2成功
	*/
	private Integer status;

	public String getOutPayOrder() {
		return outPayOrder;
	}

	public void setOutPayOrder(String outPayOrder) {
		this.outPayOrder = outPayOrder;
	}

	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setUserId(Long userId){
		this.userId = userId;
	}
	
	public Long getUserId(){
		return userId;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setPackageId(Long packageId){
		this.packageId = packageId;
	}
	
	public Long getPackageId(){
		return packageId;
	}
	
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public void setPrice(BigDecimal price){
		this.price = price;
	}
	
	public BigDecimal getPrice(){
		return price;
	}
	
	public void setGameMoney(Long gameMoney){
		this.gameMoney = gameMoney;
	}
	
	public Long getGameMoney(){
		return gameMoney;
	}
	
	public void setPayType(Integer payType){
		this.payType = payType;
	}
	
	public Integer getPayType(){
		return payType;
	}
	
	public void setStatus(Integer status){
		this.status = status;
	}
	
	public Integer getStatus(){
		return status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
