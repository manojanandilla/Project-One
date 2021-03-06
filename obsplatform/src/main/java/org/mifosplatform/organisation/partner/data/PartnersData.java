package org.mifosplatform.organisation.partner.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.partneragreement.data.AgreementData;

public class PartnersData {
    
	private Long id;
	private Long officeId;
	private Long additionalinfoId;
	private String partnerName; 
	private BigDecimal creditLimit;
	private String currency;
	private Long parentId;
	private String parentName;
	private String officeType;
	private LocalDate openingDate;
	private String loginName;
	private String city; 
	private String state; 
	private String country; 
	private String email; 
	private String phoneNumber;
	private boolean isCollective;
	private BigDecimal balanceAmount;
	private List<String> countryData;
	private List<String> statesData;
	private List<String> citiesData;
	private Collection<CodeValueData> officeTypes;
	private ApplicationCurrencyConfigurationData currencyData;
	private Collection<OfficeData> allowedParents;
	private List<AgreementData> agreementData;
	private String officeNumber;
	private String contactName;
	private Long userId;
	
	
	public PartnersData(List<String> countryData, List<String> statesData,
			List<String> citiesData, Collection<CodeValueData> officeTypes,
			ApplicationCurrencyConfigurationData currencyData, Collection<OfficeData> allowedParents) {
        
		this.citiesData = citiesData;
		this.currencyData = currencyData;
		this.countryData = countryData;
		this.officeTypes = officeTypes;
		this.allowedParents = allowedParents;
		this.statesData = statesData;
		
		
	}

	public PartnersData(final Long officeId, final Long additionalinfoId,final String partnerName, final BigDecimal creditLimit, 
			final String currency,final Long parentId, final String parentName, final String officeType,final LocalDate openingDate, 
			final String loginName,final String city, final String state,final String country, final String email, final String phoneNumber,
			final String isCollective,final BigDecimal balanceAmount,final String officeNumber,final String contactName,final Long userId) {
		
	this.officeId = officeId;
	this.id = additionalinfoId;
	this.partnerName = partnerName;
	this.creditLimit = creditLimit;
	this.currency = currency;
	this.parentId = parentId;
	this.parentName =parentName;
	this.officeType =officeType;
	this.openingDate = openingDate;
	this.loginName = loginName;
	this.city = city;
	this.state = state;
	this.country =country;
	this.email = email;
	this.phoneNumber =phoneNumber; 
	this.isCollective = isCollective.contains("Y");
	this.balanceAmount = balanceAmount;
	this.officeNumber = officeNumber;
	this.contactName = contactName;
	this.userId = userId;
	
	}
	
	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public Long getAdditionalinfoId() {
		return additionalinfoId;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public BigDecimal getPartnerType() {
		return creditLimit;
	}

	public String getCurrency() {
		return currency;
	}

	public Long getParentId() {
		return parentId;
	}

	
	public String getParentName() {
		return parentName;
	}

	public String getOfficeType() {
		return officeType;
	}

	public LocalDate getOpeningDate() {
		return openingDate;
	}
	
	public String getLoginName() {
		return loginName;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public  boolean getIsCollective() {
		return isCollective;
	}
	
	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	
	public String getOfficeNumber() {
		return officeNumber;
	}

	public String getContactName() {
		return contactName;
	}

	public Long getUserId() {
		return userId;
	}

	public List<String> getCountryData() {
		return countryData;
	}

	public List<String> getStatesData() {
		return statesData;
	}

	public List<String> getCitiesData() {
		return citiesData;
	}


	public ApplicationCurrencyConfigurationData getCurrencyData() {
		return currencyData;
	}

	public Collection<OfficeData> getAllowedParents() {
		return allowedParents;
	}

	public Collection<CodeValueData> getOfficeTypes() {
		return officeTypes;
	}

	public List<AgreementData> getAgreementData() {
		return agreementData;
	}

	public void setAgreementData(List<AgreementData> agreementData) {
		this.agreementData = agreementData;
	}

	

}
