package org.mifosplatform.provisioning.entitlements.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class EntitlementsData {

	private Long id;
	private Long prdetailsId;
	private String provisioingSystem;

	private Long serviceId;
	private String product;
	private String hardwareId;

	private String requestType;
	private String itemCode;
	private String itemDescription;

	private Long clientId;
	private String accountNo;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String city;
	private String address;
	private String zip;

	private String officeUId;
	private String branch;
	private String regionCode;
	private String regionName;

	private String status;
	private StakerData results;
	private String error;
	private Long planId;
	private String orderNo;

	private String deviceId;
	private String ipAddress;
	private String serviceType;
	private Long orderId;
	private LocalDate startDate;
	private LocalDate endDate;
	private StakerData stalkerData;
	
	private Long zebraSubscriberId;
	private BigDecimal itemPrice;
	private Long itemId;
	
	private String displayName;
	private String login;
	private String password;
	
	private String selfcareUsername;
	private String selfcarePassword;

	public EntitlementsData() {

	}
	
	//Beenius
	public EntitlementsData(Long id, Long prdetailsId,String provisioingSystem, Long serviceId, String product,String hardwareId, 
			String requestType, String itemCode,String itemDescription, Long clientId, String accountNo,String firstName, String lastName,
			String officeUId, String branch,String regionCode, String regionName, String deviceId,String ipAddress) {

		this.id = id;
		this.prdetailsId = prdetailsId;
		this.provisioingSystem = provisioingSystem;
		this.serviceId = serviceId;
		this.product = product;
		this.hardwareId = hardwareId;
		this.requestType = requestType;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.clientId = clientId;
		this.accountNo = accountNo;

		this.firstName = firstName;
		this.lastName = lastName;
		this.officeUId = officeUId;
		this.branch = branch;
		this.regionCode = regionCode;
		this.regionName = regionName;
		this.deviceId = deviceId;
		this.ipAddress = ipAddress;

	}

	//ZeebraOTT
	public EntitlementsData(Long id, Long clientId, String accountNo,String firstName, String lastName, String email, String phone,
			String city, String zip, String address, String provisioingSystem,Long serviceId, Long prdetailsId, String product,
			String macId,String requestType, Long zebraSubscriberId, BigDecimal itemPrice,
			Long itemId, String itemCode, String itemDescription) {
		
		this.id= id;
		this.clientId = clientId;
		this.accountNo = accountNo;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.city = city;
		this.zip = zip;
		this.address = address;
		this.provisioingSystem = provisioingSystem;
		this.serviceId = serviceId;
		this.prdetailsId = prdetailsId;
		this.product = product;
		this.hardwareId = macId;
		this.zebraSubscriberId = zebraSubscriberId;
		this.itemPrice = itemPrice;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.itemId = itemId;
		this.requestType = requestType;
		
	}

	//General Purpose
	public EntitlementsData(Long id, Long prdetailsId, String requestType,
			String hardwareId, String provisioingSystem, String product,
			Long clientId, Long planId, String orderNo, Long orderId,
			LocalDate startDate, LocalDate endDate, String servicetype,
			String displayName, String login, String password) {
		
		this.id = id;
		this.prdetailsId = prdetailsId;
		this.product = product;
		this.requestType = requestType;
		this.hardwareId = hardwareId;
		this.provisioingSystem = provisioingSystem;
		this.clientId = clientId;
		this.planId = planId;
		this.orderNo = orderNo;
		this.orderId = orderId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.serviceType = servicetype;
		this.displayName = displayName;
		this.login = login;
		this.password = password;
		
	}

	public Long getId() {
		return id;
	}

	public Long getPrdetailsId() {
		return prdetailsId;
	}

	public String getProvisioingSystem() {
		return provisioingSystem;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getProduct() {
		return product;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getAccountNo() {
		return accountNo;

	}

	public String getFirstName() {
		return firstName;
	}


	public String getBranch() {
		return branch;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public String getRegionName() {
		return regionName;
	}

	public String getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public Long getPlanId() {
		return planId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastName() {
		return lastName;
	}

	public String getOfficeUId() {
		return officeUId;
	}


	public Long getOrderId() {
		return orderId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setResults(StakerData data) {
		this.stalkerData=data;
		
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getZip() {
		return zip;
	}

	public StakerData getResults() {
		return results;
	}

	public String getServiceType() {
		return serviceType;
	}

	public StakerData getStalkerData() {
		return stalkerData;
	}

	public Long getZebraSubscriberId() {
		return zebraSubscriberId;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public Long getItemId() {
		return itemId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
	
	
}
