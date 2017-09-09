package com.dollapi.domain;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderCommodity {

	public OrderCommodity() {
		
	}

	/**
	*  
	*/
	private Long id;

	/**
	*  
	*/
	private String commodityName;

	/**
	*  
	*/
	private Integer commodityType;

	/**
	*  
	*/
	private String commodityUrl;

	/**
	*  
	*/
	private Long gameMoney;

	/**
	*  
	*/
	private BigDecimal cost;

	/**
	*  
	*/
	private Long commodityCount;

	/**
	*  
	*/
	private String introduce;

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
	
	public void setCommodityName(String commodityName){
		this.commodityName = commodityName;
	}
	
	public String getCommodityName(){
		return commodityName;
	}
	
	public void setCommodityType(Integer commodityType){
		this.commodityType = commodityType;
	}
	
	public Integer getCommodityType(){
		return commodityType;
	}
	
	public void setCommodityUrl(String commodityUrl){
		this.commodityUrl = commodityUrl;
	}
	
	public String getCommodityUrl(){
		return commodityUrl;
	}
	
	public void setGameMoney(Long gameMoney){
		this.gameMoney = gameMoney;
	}
	
	public Long getGameMoney(){
		return gameMoney;
	}
	
	public void setCost(BigDecimal cost){
		this.cost = cost;
	}
	
	public BigDecimal getCost(){
		return cost;
	}
	
	public void setCommodityCount(Long commodityCount){
		this.commodityCount = commodityCount;
	}
	
	public Long getCommodityCount(){
		return commodityCount;
	}
	
	public void setIntroduce(String introduce){
		this.introduce = introduce;
	}
	
	public String getIntroduce(){
		return introduce;
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
