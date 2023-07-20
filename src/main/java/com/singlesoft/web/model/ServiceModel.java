package com.singlesoft.web.model;

import java.math.BigDecimal;


import java.sql.Timestamp;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

@Entity
@Table(name="Services")
public class ServiceModel {
	

	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;
	
	@Column(name = "Model")
	private String model;
	
	@Column(name = "Tag")
	private String tag;

	@Column(name = "description")
    private String description;
    
    @Column(name = "part_Cost")
    private BigDecimal partCost;
    
    @Column(name = "labor_Tax")
    private BigDecimal laborTax;

    @Column(name = "Final_price")
    private BigDecimal finalPrice;
    
    @Column(name = "Pay_value")
    private BigDecimal payed;

    @Column(name = "Discount", precision = 5, scale = 3)
    private BigDecimal discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custome_id", nullable = false)
    private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

	@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'Budget'")
	private String status;

	@Column(name = "Prevision_Time")
	private Timestamp PrevisionTime;

	@Column(name = "Creation_Time")//, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp CreationTime;

	@Column(name = "payway", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'Money'")
	private String payway;
	
	@Column(name = "img", nullable = true)
	@ElementCollection
	private List<String> imgName = new ArrayList<>();
	

	//Id getter and setter
	public String getId() {
		return id;

	}
	public void setId() {
		String randomId = RandomStringUtils.randomAlphabetic(16);
		this.id = randomId;
	}
	// Model getter and setter
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	// Tag getter and setter
    public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	// Description Getter and Setter
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	// Part cost getter and setter
	public BigDecimal getPartCost() {
		return partCost;
	}
	public void setPartCost(BigDecimal partCost) {
		this.partCost = partCost;
	}

	// LaborTax getter and setter
	public BigDecimal getLaborTax() {
		return laborTax;
	}
	public void setLaborTax(BigDecimal laborTax) {
		this.laborTax = laborTax;
	}
	
	// Final Cost getter and setter
	public BigDecimal getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice() {
	    BigDecimal basePrice = laborTax.add(partCost);
	    BigDecimal dis = this.discount;
	    this.finalPrice = basePrice.subtract(basePrice.multiply(dis.divide(BigDecimal.valueOf(100))));
	     
	}

	// Discount getter and setter
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	// Payed getter and setter
	public BigDecimal getPayed() {
		return payed;
	}
	public void setPayed(BigDecimal payed) {
		this.payed = payed;
	}
	
	// Customer getter and setter
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	// User getter and setter
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	// Status getter and setter
	public int getStatus() {
		//return this.status;
		String[] statusOp = {"Budget","Authorized","NoFix", "Bench","Finished"}; 
		for (int i = 0; i < statusOp.length; i++) {
		        if (this.status.equals(statusOp[i])) {
		            return i;
		        }
		    }
		return -1;
	}
	public void setStatus(int statusNum) {
		//this.status = statusNum; 
		if(statusNum<=5) {
			String[] statusOp = {"Budget","Authorized","NoFix", "Bench","Finished"}; 
			this.status = statusOp[statusNum];
		}
	}

	// forecast Timestamp getter and setter
	public Timestamp getPrevisionTime() {
		return PrevisionTime;
	}
	public void setPrevisionTime(Timestamp previsionTime) {
		PrevisionTime = previsionTime;
	}

	// Creation Timestamp getter and setter
	public Timestamp getCreationTime() {
		return CreationTime;
	}
	public void setTimestamp() {
		// Set the current Timestramp
		this.CreationTime = new Timestamp(System.currentTimeMillis());
	}
	public String getCustomerName() {
		return this.customer.getName();
		
	}

	// Status getter and setter
	public int getPayway() {
		String[] payOp = {"Money", "Pix", "Credit", "Debit"}; 
		for (int i = 0; i < payOp.length; i++) {
		        if (this.payway.equals(payOp[i])) {
		            return i;
		        }
		    }
		return -1;
	}
	public void setPayway(int statusNum) {
		String[] payOp = {"Money", "Pix", "Credit", "Debit"}; 
		if(statusNum<4) {
			this.payway = payOp[statusNum];
		}
	}

	// ImgName Getter and Setter
	public List<String> getImgName() {
	    return imgName;
	}
	public void setImgName(List<String> imgName) {
		this.imgName = imgName;
	}

	public void addImgName(String imgName) {
	    this.imgName.add(imgName);
	}
}
