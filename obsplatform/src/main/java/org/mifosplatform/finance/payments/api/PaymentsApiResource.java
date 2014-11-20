package org.mifosplatform.finance.payments.api;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporary;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporaryRepository;
import org.mifosplatform.billing.selfcare.exception.SelfCareTemporaryAlreadyExistException;
import org.mifosplatform.billing.selfcare.exception.SelfCareTemporaryEmailIdNotFoundException;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.finance.payments.exception.DalpayRequestFailureException;
import org.mifosplatform.finance.payments.exception.KortaRequestFailureException;
import org.mifosplatform.finance.payments.service.PaymentReadPlatformService;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGateway;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.ConfigurationPropertyNotFoundException;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Path("/payments")
@Component
@Scope("singleton")
public class PaymentsApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "clientId", "paymentDate", "paymentCode",
					"amountPaid", "statmentId", "externalId", "Remarks"));
	private final static String RESOURCENAMEFORPERMISSIONS = "PAYMENT";

	private final PlatformSecurityContext context;
	private final PaymentReadPlatformService readPlatformService;
	private final DefaultToApiJsonSerializer<PaymentData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;
	private final SelfCareTemporaryRepository selfCareTemporaryRepository;
	private final ConfigurationRepository configurationRepository;
	private final PaymentGatewayRepository paymentGatewayRepository;

	@Autowired
	public PaymentsApiResource(final PlatformSecurityContext context,final PaymentReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<PaymentData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService writePlatformService, final SelfCareTemporaryRepository selfCareTemporaryRepository,
			final ConfigurationRepository globalConfigurationRepository, final PaymentGatewayRepository paymentGatewayRepository) {
		
		this.context = context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.writePlatformService = writePlatformService;
		this.selfCareTemporaryRepository = selfCareTemporaryRepository;
		this.configurationRepository = globalConfigurationRepository;
		this.paymentGatewayRepository = paymentGatewayRepository;
	}

	/**
	 * This method is using for posting data to create payment
	 */
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createPayment(@PathParam("clientId") final Long clientId,	final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createPayment(clientId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
    
	/**
	 * This method is using for getting template data to create payment
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDetailsForPayments(@QueryParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final Collection<McodeData> data = this.readPlatformService.retrievemCodeDetails("Payment Mode");
		final PaymentData paymentData=new PaymentData(data);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, paymentData,RESPONSE_DATA_PARAMETERS);

	}
	
	@GET
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllDetailsForPayments(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final List<PaymentData> paymentData = readPlatformService.retrivePaymentsData(clientId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, paymentData,RESPONSE_DATA_PARAMETERS);

	}
	
	
	/**
	 * This method is using for posting data to create payment using paypal
	 */
	 @POST
	 @Path("paypal")
	 @Consumes("application/x-www-form-urlencoded")
	 @Produces({ MediaType.APPLICATION_JSON })
	 public String checkout(@FormParam("txn_id") final String txnId,@FormParam("payment_date") final Date paymentDate,@FormParam("mc_gross") final BigDecimal amount,
			 @FormParam("address_name") final String name,@FormParam("payer_email") final String payerEmail,
			 @FormParam("transaction_subject") final String clientStringId){
	   try {
		   
		  final Long clientId= Long.parseLong(clientStringId);
		  final SimpleDateFormat daformat=new SimpleDateFormat("dd MMMM yyyy");
		  final String date=daformat.format(paymentDate);
		  final JsonObject object=new JsonObject();
		  object.addProperty("txn_id", txnId);
		  object.addProperty("dateFormat","dd MMMM yyyy");
		  object.addProperty("locale","en");
		  object.addProperty("paymentDate",date);
		  object.addProperty("amountPaid",amount);
		  object.addProperty("isChequeSelected","no");
		  object.addProperty("receiptNo",txnId);
		  object.addProperty("remarks",payerEmail);
		  object.addProperty("paymentCode",27);
		  
		  final CommandWrapper commandRequest = new CommandWrapperBuilder().createPayment(clientId).withJson(object.toString()).build();
		  final CommandProcessingResult result1 = this.writePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result1); 
	        
	  } 
	   catch(Exception e){
	    return e.getMessage();
	   }
	 }
	 
	 /**
	 * This method is using for cancelling payment with payment id
	 */
	 @PUT
	 @Path("cancelpayment/{paymentId}")
	 @Consumes({ MediaType.APPLICATION_JSON })
	 @Produces({ MediaType.APPLICATION_JSON })
	 public String cancelPayment(@PathParam("paymentId") final Long paymentId,final String apiRequestBodyAsJson) {
		 
			final CommandWrapper commandRequest = new CommandWrapperBuilder().cancelPayment(paymentId).withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}	
	 
	 
	 /**
	 * This method is using for posting data to create payment using dalpay
	 */
	 @POST
	 @Path("dalpay")
	 @Consumes({ MediaType.APPLICATION_JSON })
	 @Produces({ MediaType.TEXT_HTML})
	 public String dalpayCheckout(final String apiRequestBodyAsJson){

		   try {
			   	  final JSONObject json= new JSONObject(apiRequestBodyAsJson);
			   	  final String orderNumber = json.getString("order_num");
			   	  final Long clientId = json.getLong("user1");
			   	  String returnUrl = json.getString("user2");
			   	  final String screenName = json.getString("user3");
			   	  final String EmailId = json.getString("cust_email");
			   	  final String amount = json.getString("total_amount");
			   	  final BigDecimal totalAmount = new BigDecimal(amount);
			   	returnUrl = returnUrl.replace("index.html", "index.html#/"+screenName);
			   	  
				if (clientId !=null && clientId > 0) {
					final String date = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
					final JsonObject object = new JsonObject();
					object.addProperty("txn_id", orderNumber);
					object.addProperty("dateFormat", "dd MMMM yyyy");
					object.addProperty("locale", "en");
					object.addProperty("paymentDate", date);
					object.addProperty("amountPaid", totalAmount);
					object.addProperty("isChequeSelected", "no");
					object.addProperty("receiptNo", orderNumber);
					object.addProperty("remarks", "Updated with Dalpay");
					object.addProperty("paymentCode", 27);

					final CommandWrapper commandRequest = new CommandWrapperBuilder().createPayment(clientId).withJson(object.toString()).build();
					this.writePlatformService.logCommandSource(commandRequest);
					
				}else if(clientId !=null && clientId == 0){
					
					final SelfCareTemporary selfCareTemporary = this.selfCareTemporaryRepository.findOneByEmailId(EmailId);
					if(selfCareTemporary != null && selfCareTemporary.getPaymentStatus().equalsIgnoreCase("INACTIVE")){
						
						selfCareTemporary.setPaymentData(json.toString());
						selfCareTemporary.setPaymentStatus("PENDING");
						this.selfCareTemporaryRepository.save(selfCareTemporary);											
						
					}else if(selfCareTemporary != null){				
						throw new SelfCareTemporaryAlreadyExistException(EmailId);					
					}else{					
						throw new SelfCareTemporaryEmailIdNotFoundException(EmailId);					
					}		
				}else{		
					throw new DalpayRequestFailureException(clientId);				
				}
				
				return "<!-- success--> <span>Order Accepted Successfully</span>"
				+ "<br>"
				+ "<a href='"+ returnUrl +"'>"
				+ "<strong>CLICK HERE</strong> to return to your account</a>";
			   	  
				  
		  } 
		   catch(Exception e){
		    return e.getMessage();
		   }
		 
	 }
	 
	@POST
	@Path("paypalEnquirey/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String paypalEnquireyPayment(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().paypalEnquireyPayment(clientId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * This method is using for posting data to create payment using korta
	 */
	@POST
	@Path("korta")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String kortaPayment(final String apiRequestBodyAsJson) {

		try {
			final JSONObject json = new JSONObject(apiRequestBodyAsJson);
			final Long clientId = json.getLong("clientId");
			final String reference = json.getString("reference");
			final String totalAmount = json.getString("amount");
			
			if (clientId != null && clientId > 0) {
				final String date = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
				final JsonObject object = new JsonObject();
				object.addProperty("txn_id", reference);
				object.addProperty("dateFormat", "dd MMMM yyyy");
				object.addProperty("locale", "en");
				object.addProperty("paymentDate", date);
				object.addProperty("amountPaid", totalAmount);
				object.addProperty("isChequeSelected", "no");
				object.addProperty("receiptNo", reference);
				object.addProperty("remarks","Payment With Korta PaymentGateway");
				object.addProperty("paymentCode", 27);

				final CommandWrapper commandRequest = new CommandWrapperBuilder().createPayment(clientId).withJson(object.toString()).build();
				final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
				return this.toApiJsonSerializer.serialize(result);

			} else if (clientId != null && clientId == 0) {
				final String emailId = json.getString("emailId");
				final SelfCareTemporary selfCareTemporary = this.selfCareTemporaryRepository.findOneByEmailId(emailId);
				if (selfCareTemporary != null && selfCareTemporary.getPaymentStatus().equalsIgnoreCase("INACTIVE")) {
					final JsonObject obj = new JsonObject();
					obj.addProperty("order_num", reference);
					obj.addProperty("total_amount", totalAmount);
					obj.addProperty("cust_email", emailId);

					selfCareTemporary.setPaymentData(obj.toString());
					selfCareTemporary.setPaymentStatus("PENDING");
					this.selfCareTemporaryRepository.save(selfCareTemporary);
					return selfCareTemporary.getId().toString();
				} else if (selfCareTemporary != null) {
					throw new SelfCareTemporaryAlreadyExistException(emailId);
				} else {
					throw new SelfCareTemporaryEmailIdNotFoundException(emailId);
				}
			} else {
				throw new KortaRequestFailureException(clientId);
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	/**
	 * This method is used for Online Payment 
	 * Systems like Paypal,Dalpay,Korta etc...
	 * 
	 * Storing these payment details in 2 tables.
	 * 1) b_paymentgateway and 
	 * 2) b_payment.
	 */
	 @POST
	 @Path("onlinepayment")
	 @Consumes({ MediaType.APPLICATION_JSON })
	 @Produces({ MediaType.TEXT_HTML})
	 public String OnlinePaymentMethod(final String apiRequestBodyAsJson){

		   try {
			   Long value = null;
			   String deviceId = " ";
			   
			   Configuration configuration=configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_ONLINEPAYMODE);
				  
			   if(configuration == null){
				   throw new ConfigurationPropertyNotFoundException(ConfigurationConstants.CONFIG_PROPERTY_ONLINEPAYMODE);
			   }
			   	
			   value = Long.parseLong(configuration.getValue());
			   	
			   final JSONObject json= new JSONObject(apiRequestBodyAsJson); 
			   final Long clientId = json.getLong("clientId");			   	  
			   final String emailId = json.getString("emailId");			   	 
			   final String txnId = json.getString("transactionId");			   	 
			   final String amount = json.getString("total_amount");
			   final String source = json.getString("source");
			   final String data = json.getJSONObject("otherData").toString();
			   deviceId = json.getString("device");	
			   final BigDecimal totalAmount = new BigDecimal(amount);
			   Date date = new Date();
			   
			   PaymentGateway paymentGateway = new PaymentGateway(deviceId, " ", date, totalAmount, txnId, source, data);
			   
			  
			   	if (clientId !=null && clientId > 0 && value != null && value > 0) {
			   		final String formattedDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
					final JsonObject object = new JsonObject();
					object.addProperty("txn_id", txnId);
					object.addProperty("dateFormat", "dd MMMM yyyy");
					object.addProperty("locale", "en");
					object.addProperty("paymentDate", formattedDate);
					object.addProperty("amountPaid", totalAmount);
					object.addProperty("isChequeSelected", "no");
					object.addProperty("receiptNo", txnId);
					object.addProperty("remarks", "Updated with Dalpay");
					object.addProperty("paymentCode", value);

					final CommandWrapper commandRequest = new CommandWrapperBuilder().createPayment(clientId).withJson(object.toString()).build();
					final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
					
					if (result.resourceId() != null) {
						paymentGateway.setObsId(result.resourceId());
						paymentGateway.setPaymentId(result.resourceId().toString());
						paymentGateway.setStatus("Success");
						paymentGateway.setAuto(false);
						this.paymentGatewayRepository.save(paymentGateway);
					}
					
					return this.toApiJsonSerializer.serialize(result);
					
					
				}else if(clientId !=null && clientId == 0 && value != null && value > 0){
					
					final SelfCareTemporary selfCareTemporary = this.selfCareTemporaryRepository.findOneByEmailId(emailId);
					if(selfCareTemporary != null && selfCareTemporary.getPaymentStatus().equalsIgnoreCase("INACTIVE")){
						
						selfCareTemporary.setPaymentData(json.toString());
						selfCareTemporary.setPaymentStatus("PENDING");
						this.selfCareTemporaryRepository.save(selfCareTemporary);
						
						paymentGateway.setObsId(clientId);
						paymentGateway.setStatus("Success");
						paymentGateway.setPaymentId("0");
						this.paymentGatewayRepository.save(paymentGateway);
						
						return "Success";
					}else if(selfCareTemporary != null){				
						throw new SelfCareTemporaryAlreadyExistException(emailId);					
					}else{					
						throw new SelfCareTemporaryEmailIdNotFoundException(emailId);					
					}		
				}else{
					paymentGateway.setStatus("Failure");
					this.paymentGatewayRepository.save(paymentGateway);
					throw new DalpayRequestFailureException(clientId);				
				}
				
				/*return "<!-- success--> <span>Order Accepted Successfully</span>"
				+ "<br>"
				+ "<a href='"+ returnUrl +"'>"
				+ "<strong>CLICK HERE</strong> to return to your account</a>";*/
			   	  
			   	
		  } 
		   catch(Exception e){
		    return e.getMessage();
		   }
		 
	 }
}
