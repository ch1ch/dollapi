package com.dollapi.domain;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class RechargePackage {

	public RechargePackage() {
		
	}

	/**
	*  
	*/
	private Long id;

	/**
	*  
	*/
	private String packageName;

	/**
	*  
	*/
	private String packageUrl;

	/**
	*  
	*/
	private BigDecimal price;

	/**
	*  
	*/
	private Long gameMoney;

	/**
	*  
	*/
	private Integer status;

	/**
	*  
	*/
	private Date createTime;
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Long getId(){
		return id;
	}
	
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public void setPackageUrl(String packageUrl){
		this.packageUrl = packageUrl;
	}
	
	public String getPackageUrl(){
		return packageUrl;
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
	
	public void setStatus(Integer status){
		this.status = status;
	}
	
	public Integer getStatus(){
		return status;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	
	public Date getCreateTime(){
		return createTime;
	}
}
