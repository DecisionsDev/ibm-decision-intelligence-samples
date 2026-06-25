/*
 * Licensed Materials - Property of IBM
 * 5737-I23 5900-AUD
 * Copyright IBM Corp. 2018 - 2026. All Rights Reserved.
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
*/

package com.ibm.di.samples;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Validate
 */
@WebServlet("/validate")
public class Validate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String duration = "numberOfMonthlyPayments";
	private static final String amount = "amount";
	private static final String loanToValue = "loanToValue";
	private static final String startDate = "startDate";
	private static final String firstName = "firstName";
	private static final String lastName = "lastName";
	private static final String areaNumber = "areaNumber";
	private static final String groupCode = "groupCode";
	private static final String serialNumber = "serialNumber";
	private static final String bDate = "date";
	private static final String chapter = "chapter";
	private static final String reason = "reason";
	private static final String yearlyIncome = "yearlyIncome";
	private static final String zipCode = "zipCode";
	private static final String creditScore = "creditScore";
	private static final String birthDate = "birthDate";
	private static final String latestBankruptcy ="latestBankruptcy";
	private static final String inputDateFormatTemplate = "dd/MM/yyyy";
	private static final String outputDateFormatTemplate = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private int countApproved;
	private int countRejected;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Validate() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ")
				.append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JsonReader jsonReader = Json.createReader(request.getInputStream());
		JsonObject json = jsonReader.readObject();
		jsonReader.close();
		log(json.toString());
		String result = validate(json);
		response.setContentType("application/json;charset=utf-8");
		PrintWriter pw = response.getWriter();
		pw.write(result);
		pw.flush();
		pw.close();
	}

	/**
	 * Validate button action: calls the decision service with the input provided in the form.
	 * @param jsonInput
	 * @return response to the decision service execution.
	 *
	 */
	public String validate(JsonObject jsonInput) {
		DIResponse response = null;
		boolean showTrace  = (boolean) jsonInput.getBoolean("showTrace");
		JsonObject payload = null;
						System.out.println("**Before try " );

		try {
			InputData data = new InputData(jsonInput);
			int count = jsonInput.getInt("executionCount");
			if (count < 1) count = 1;
			if (count > 20) count = 20;
			String apikey = jsonInput.getString("apikey");
			String server = jsonInput.getString("server");
			String decisionServiceId = jsonInput.getString("decisionServiceId");
			String operation = jsonInput.getString("operation");

			RestJavaClient restClient = new RestJavaClient();
			String messagesToDisplay = "<ul>";
			countApproved = 0;
			countRejected = 0;
			for (int i=0; i < count; i++) {
				data.generateData(i, count);
				payload = getJSONPayload(jsonInput, data, showTrace);
				System.out.println("**Payload " + payload);
				response = restClient.executeDecision(server, decisionServiceId, operation, apikey, payload.toString());
				System.out.println("**Response status" + response.status + " payload " + response.payload);
				messagesToDisplay = messagesToDisplay + "<li>" + getSummaryMessage(response, jsonInput, data) + "</li>";
			}
			// return the latest answer with all the messages
			return decodeResponse(count, response, showTrace, payload, messagesToDisplay +"</ul>");
		} catch (ParseException e) {
			return parseErrorToJSON(e);
		} catch (ClassCastException e) {
			return castErrorToJSON(e);
		} catch (URISyntaxException e) {
			return hostErrorToJSON(e);
		} catch (RuntimeException e) {
			return runtimeErrorToJSON(e);
		} catch (UnknownHostException e) {
			return hostErrorToJSON(e);
	    } catch (FileNotFoundException e) {
			return fileErrorToJSON(e);
		} catch (IOException e) {
			return keyErrorToJSON(e);
		} catch (Exception e) {
			return errorToJSON(e);
		}
	}

	/****      B U I L D  T H E  I N P U T  P A Y L O A D      ****/

	/**
	 * Build the request payload from the form input
	 * @param jsonInput
	 * @param data 
	 * @param showTrace when the trace is enabled add the required parts to the payload
	 * @return
	 */
	private JsonObject getJSONPayload(JsonObject jsonInput,
									  InputData data, boolean showTrace) throws Exception{
		SSN ssnObject = new SSN(jsonInput.getString("SSNCode"));
		JsonObject jsonSSN = Json.createObjectBuilder()
				.add(areaNumber, ssnObject.getAreaNumber())
				.add(groupCode,  ssnObject.getGroupCode())
				.add(serialNumber, ssnObject.getSerialNumber()).build();
		JsonObject jsonBorrower;
		String bankruptcyString = jsonInput.getString("bankruptcyDate");
		if ((bankruptcyString != null) && !bankruptcyString.isEmpty()) {
			JsonObject bankruptcy = Json.createObjectBuilder()
					.add(bDate, prepareDate(bankruptcyString))
					.add(chapter,  jsonInput.getInt("bankruptcyChapter"))
					.add(reason, jsonInput.getString("bankruptcyReason")).build();
			jsonBorrower = Json.createObjectBuilder()
					.add(firstName, jsonInput.getString("firstName"))
					.add(lastName, jsonInput.getString("lastName"))
					.add(birthDate, prepareDate(jsonInput.getString("birthDate")))
					.add("SSN", jsonSSN)
					.add(zipCode, data.getZipCode())
					.add("spouse", JsonValue.NULL)
					.add(latestBankruptcy, bankruptcy)
					.add("SSN", jsonSSN)
					.add(creditScore, data.getCreditScore())
					.add(yearlyIncome, data.getYearlyIncome()).build();
		} else {
			jsonBorrower = Json.createObjectBuilder()
					.add(firstName, jsonInput.getString("firstName"))
					.add(lastName, jsonInput.getString("lastName"))
					.add(birthDate, prepareDate(jsonInput.getString("birthDate")))
					.add("SSN", jsonSSN)
					.add(zipCode, data.getZipCode())
					.add("spouse", JsonValue.NULL)
					.add(latestBankruptcy, JsonValue.NULL)
					.add("SSN", jsonSSN)
					.add(creditScore, data.getCreditScore())
					.add(yearlyIncome, data.getYearlyIncome()).build();

		}
		JsonObject loanObject = Json.createObjectBuilder()
				.add(amount, data.getAmount())
				.add(duration, data.getDuration())
				.add(startDate,  prepareDate(jsonInput.getString("startDate")))
				.add(loanToValue,
						jsonInput.getJsonNumber("yearly-interest-rate").doubleValue() /100).build();
		JsonObject inputObject = Json
				.createObjectBuilder()
				.add("borrower", jsonBorrower)
				.add("loan", loanObject)
				.add("currentTime", prepareCurrentDate())
				.build();
		if (showTrace) {
			JsonObject payload = Json
					.createObjectBuilder()
					.add("input", inputObject)
					.add("executionTraceFilters", createTraceObject(jsonInput))
					.build();
			return payload;
		}
		JsonObject payload = Json
				.createObjectBuilder()
				.add("input", inputObject)
				.build();
		return payload;
	}


	private JsonValue createTraceObject(JsonObject jsonInput) {
		JsonObject traceObject = Json.createObjectBuilder()
				.add("executionDuration", true)
				.add("printedMessages", true)
				.add("decisionModel",  Json.createObjectBuilder()
						.add("inputParameters","Object")
						.add("outputParameters","Object")
						.add("inputNode","Object")
						.add("outputNode","Object").build()
				)
				.add("rules",  Json.createObjectBuilder()
						.add("boundObjectsAtStart","Object")
						.add("boundObjectsAtEnd","Object")
						.add("allRules",true)
						.add("executedRules",true)
						.add("nonExecutedRules",false).build()
				)
				.build();
		return traceObject;
	}

	private String prepareDate(String dateString) throws Exception{
		// change the date format to the appropriate format
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputDateFormatTemplate);
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputDateFormatTemplate);
		Date date = inputDateFormat.parse(dateString);
		return outputDateFormat.format(date);
	}
	private String prepareCurrentDate() {
		// change the date format to the appropriate format
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputDateFormatTemplate);
		Date date = new Date();
		return outputDateFormat.format(date);
	}

	/****       D E C O D E  T H E  R E S P O N S E         
	 * @param messagesToDisplay *****/
	private String decodeResponse(int count, DIResponse response, boolean showTrace, JsonObject jsonInput, String messagesToDisplay) {
		JsonObject decodedResponse;
		if (response.status == DIResponse.SUCCESS) {
			JsonReader jsonReader = Json.createReader(new StringReader(response.payload));
			JsonObject jsonResponse = jsonReader.readObject();
			JsonObject output = jsonResponse.getJsonObject("output");
			JsonObject approval = output.getJsonObject("approval");
			String trace = "";
			if (showTrace)
				trace = buildTraceResponse(messagesToDisplay, jsonInput, jsonResponse);
			decodedResponse = Json.createObjectBuilder()
					.add("success", true)
					.add("approved",approval.getBoolean("approved"))
					.add("header", buildHeader())
					.add("message",messagesToDisplay)
					.add("showTrace",showTrace)
					.add("jsonOutputContent", jsonResponse)
					.add("trace", trace)
				.build();
		} else if (response.status == DIResponse.NOTFOUND)  {
			JsonReader jsonReader = Json.createReader(new StringReader(response.payload));
			try {
				JsonObject jsonResponse = jsonReader.readObject();
				decodedResponse = Json.createObjectBuilder()
						.add("success", false)
						.add("notFound", true)
						.add("message", buildIncidentResponse(jsonResponse))
						.add("jsonOutputContent", jsonResponse)
						.build();
			} catch (Exception e) {
				// Unexpected response: no server
				decodedResponse = Json.createObjectBuilder()
						.add("success", false)
						.add("message", "Status " + response.status +": check the runtime server is up and running on host " + jsonInput.getString("server")
								+".")
						.build();
			}
		} else  {
			decodedResponse = Json.createObjectBuilder()
					.add("success", false)
					.add("message", "Status " + response.status +": check the runtime server is up and running on host " + jsonInput.getString("server")
							+". \n Check the provided apikey for the runtime authentication.")
				.build();
		}
		return decodedResponse.toString();
	}



	private String buildHeader() {
		if (countApproved ==1 && countRejected ==0) return "Approved loan request";
		if (countApproved ==0 && countRejected ==1) return "Rejected loan request";
		return countApproved + " Approved loan request(s) and " + countRejected + " Rejected loan request(s)";
	}

	private String getSummaryMessage(DIResponse response, JsonObject jsonInput, InputData data) {
		if (response.status == DIResponse.SUCCESS) {
			JsonReader jsonReader = Json.createReader(new StringReader(response.payload));
			JsonObject jsonResponse = jsonReader.readObject();
			JsonObject output = jsonResponse.getJsonObject("output");
			JsonObject approval = output.getJsonObject("approval");
			if (approval.getBoolean("approved")) 
				countApproved++;
			else 
				countRejected++;
			return buildSummaryMessage(jsonInput, data, output);
		}
		return "";
	}

	private String buildIncidentResponse(JsonObject jsonResponse) {
		JsonObject incident = jsonResponse.getJsonObject("incident");
		return incident.getString("incidentCategory") + " " + incident.getString("stackTrace");
	}

	private String buildTraceResponse(String message, JsonObject jsonInput, JsonObject jsonResponse) {
		JsonObject rootProperties = jsonResponse.getJsonObject("executionTrace").getJsonObject("rootRecord").getJsonObject("properties");
		JsonArray nestedRecords = rootProperties.getJsonArray("nestedRecords");
		String result =  "<b>Latest execution fired rules:</b><ul>";
		Iterator<JsonValue> iterator = nestedRecords.iterator();
		while (iterator.hasNext()) {
			JsonObject record = (JsonObject)iterator.next();
			JsonObject properties = record.getJsonObject("properties");
			if (properties.getString("kind").equals("Decision") && properties.getJsonArray("nestedRecords") != null) {
				if (hasFiredRules(properties)) {
				result = result + "<li> in node " + properties.getString("name") + "<ul>" + getFiredRulesFromResponse(properties) + "</ul></li>";
				}
			}
		}
		return result + "</ul>" + "<b>Decision(s):</b> " + message;
	}

	private boolean hasFiredRules(JsonObject object) {
		JsonArray nestedRecordsEngine = object.getJsonArray("nestedRecords");
		// For a decision model there is only one record for the Sequential engine
		JsonObject engineRecord = (JsonObject)nestedRecordsEngine.getJsonObject(0);
		JsonObject engineProperties = engineRecord.getJsonObject("properties");
		return (engineProperties.getJsonArray("nestedRecords") != null);
		}

	private String getFiredRulesFromResponse(JsonObject object) {
		JsonArray nestedRecordsEngine = object.getJsonArray("nestedRecords");
		// For a decision model there is only one record for the Sequential engine
		JsonObject engineRecord = (JsonObject)nestedRecordsEngine.getJsonObject(0);
		JsonObject engineProperties = engineRecord.getJsonObject("properties");
		JsonArray nestedRecords = engineProperties.getJsonArray("nestedRecords");
		Iterator<JsonValue> iterator = nestedRecords.iterator();
		String result = "";
		while (iterator.hasNext()) {
			JsonObject record = (JsonObject)iterator.next();
			if (record.getString("recordType").equals("Rule")) {
				JsonObject properties = record.getJsonObject("properties");
				result = result + "<li>" + properties.getString("name") + "</li>";
			}
		}
		return result;
	}

	private String buildSummaryMessage(JsonObject jsonInput, InputData data, JsonObject output) {
		String reason = output.getJsonObject("approval").getString("message");
		String message = jsonInput.getString("firstName") + " " + jsonInput.getString("lastName") 
		        + " with $" + data.getYearlyIncome() + " yearly income and $"+ data.getCreditScore()
		        + " credit score loan request of $"
				+ data.getAmount()+ " on " + data.getDuration() + " months is ";
											System.out.println("**Message " + message);

		if (!output.getJsonObject("approval").getBoolean("approved")) return message + "rejected: " + reason +".";
		String insurance = " No insurance is required.";
		JsonObject insuranceObject = output.getJsonObject("insurance");
		if (insuranceObject.getBoolean("required"))
			insurance = " An insurance is required with a rate of " + insuranceObject.getJsonNumber("rate").doubleValue() *100+ "%.";
		return message + "approved." + insurance;
	}


	private String parseErrorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
				.add("validData", false)
				.add("message", "Check you provide valid dates.")
				.build();
		return value.toString();
	}
	private String errorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
		.add("error",true)
		.add("text", e.toString())
		.build();
		return value.toString();
	}
	private String runtimeErrorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
		.add("error",true)
		.add("text", "Check the execution details and check that Loan Validation Service is deployed on the expected server.")
		.build();
		return value.toString();
	}
	private String hostErrorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
		.add("error",true)
		.add("text", "Check in the decision and runtime details that the host name is correct.")
		.build();
		return value.toString();
	}
private String fileErrorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
		.add("error",true)
		.add("text", "Check in the decision and runtime details that the decision ID and the operation name are correct.")
		.build();
		return value.toString();
	}
private String keyErrorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
		.add("error",true)
		.add("text", "Check in the decision and runtime details that the Api Key is correct.")
		.build();
		return value.toString();
	}

	private String castErrorToJSON(Exception e) {
		JsonObject value = Json.createObjectBuilder()
				.add("validData", false)
				.add("message", "Check you provide numbers when required.")
				.build();
		return value.toString();
	}

	/****       SSN  UTILITY CLASS         *****/

	public static class SSN implements java.io.Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		/**
		 *
		 */
		private String areaNumber;
		private String groupCode;
		private String serialNumber;


		private void parseSSN(String number) {
			int firstDash = number.indexOf('-');
			if (firstDash >= 1) {
				areaNumber = number.substring(0, firstDash);
				int secondDash = number.indexOf('-', firstDash+1);
				if (secondDash >= firstDash+2) {
					groupCode = number.substring(firstDash+1, secondDash);
					serialNumber = number.substring(secondDash+1);
				}
				else {
					groupCode = number.substring(firstDash+1, Math.min(number.length(), firstDash+3));
					serialNumber = number.substring(Math.min(number.length(), firstDash+3), number.length());
				}
			}
			else {
				areaNumber = number.substring(0, Math.min(number.length(), 3));
				groupCode = number.substring(Math.min(number.length(), 3), Math.min(number.length(), 5));
				serialNumber = number.substring(Math.min(number.length(), 5), number.length());
			}
		}


		public void setAreaNumber(String areaNumber) {
			this.areaNumber = areaNumber;
		}

		public void setGroupCode(String groupCode) {
			this.groupCode = groupCode;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public SSN(String number) {
			parseSSN(number);
		}

		public SSN(String areaNumber, String groupCode, String serialNumber) {
			this.areaNumber = areaNumber;
			this.groupCode = groupCode;
			this.serialNumber = serialNumber;
		}

		public int getDigits() {
			return areaNumber.length() + groupCode.length() + serialNumber.length();
		}

		public String getAreaNumber() {
			return areaNumber;
		}

		public String getGroupCode() {
			return groupCode;
		}

		public String getSerialNumber() {
			return serialNumber;
		}


		public String getFullNumber() {
			return getAreaNumber() + "-" + getGroupCode() + "-" + getSerialNumber();
		}

		public String toString() {
			return this.getFullNumber();
		}
	}

}
