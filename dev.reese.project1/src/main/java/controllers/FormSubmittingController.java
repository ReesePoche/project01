package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.reese.project1.entities.Employee;
import dev.reese.project1.entities.TuitionReimbursementRequest;
import serviceexceptions.RequestNotFoundException;
import services.LoginService;
import services.TRFormSubmitionService;
import services.TRMSMessagingService;

public class FormSubmittingController {
	
	private static LoginService ls = new LoginService();
	private static TRMSMessagingService ms = new TRMSMessagingService();
	private static TRFormSubmitionService trfss = new TRFormSubmitionService();
	public static Gson gson = new Gson();
	
	public static void SubmitTRRForm(HttpServletRequest request, HttpServletResponse response) throws IOException{
		HttpSession sess = request.getSession();
		String id = sess.getAttribute("loggedInEmployeeID").toString();
		int employeeId = Integer.parseInt(id);
		StringBuilder sb = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    try {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }
	    } 
	    finally {
	        reader.close();
	    }
	    JsonObject jsonFormObject = new Gson().fromJson(sb.toString(), JsonObject.class);
	    System.out.println(sb.toString());
	    
	    
	    
	    
	    
	    
	    
	   
	    
	    
	    
	    
	    
	    
	    TuitionReimbursementRequest trr = new TuitionReimbursementRequest();
	    trr.setForEmployee(employeeId);
	    trr.setEventDescription(jsonFormObject.get("description").getAsString());
	    trr.setWorkRelatedJustification(jsonFormObject.get("workJustification").getAsString());
	    trr.setEventType(jsonFormObject.get("typeOfEvent").getAsString());
	    trr.setEventLocation(jsonFormObject.get("location").getAsString());
	    
	    String costString = jsonFormObject.get("cost").getAsString();
	    trr.setTotalCost(Double.parseDouble(costString));
	    String formatString = jsonFormObject.get("gradeFormat").getAsString();
	    trr.setGradeFormat(Integer.parseInt(formatString));
	    if(trr.getGradeFormat() == 1 || trr.getGradeFormat() == 2)
	    	trr.setScoreRequired(100);
	    else {
	    	String scoreString =  jsonFormObject.get("scoreNeeded").getAsString();
	    	trr.setScoreRequired(Integer.parseInt(scoreString));
	    }
	    String hourOfWorkString = jsonFormObject.get("HoursOfWork").getAsString();
	    double hoursOfWork = Double.parseDouble(hourOfWorkString);
	    if(hoursOfWork <= 0.00)
	    	trr.setHoursOfWorkMissed(0.00);
	    else
	    	trr.setHoursOfWorkMissed(hoursOfWork);
	    String orginalTimeStamp = jsonFormObject.get("dateTimeOfEvent").getAsString();
	    StringBuilder sbtest = new StringBuilder(orginalTimeStamp);
	    sbtest.setCharAt(10, ' ');
	    sbtest.append(":00");
	    System.out.println(sbtest.toString());
	    trr.setStartDateTimeOfEvent(Timestamp.valueOf(sbtest.toString()));
	    trr.setstatusOfRequest("PROS");
	    trr = trfss.submitRequest(trr);
	    System.out.println(trr.getId());
	    sess.setAttribute("SucessfulFormSubmit", "T");
	    processAndSendInitalApprovalMessage(trr);
	    response.getWriter().append("SS");
		
		
		
		
		
		
		
		
	}
	
	private static void processAndSendInitalApprovalMessage(TuitionReimbursementRequest trr) {
		try {
		trr = trfss.markApprovalsNotNeededAsGotten(trr.getId());
		String whoToSendMessage = trfss.whoShouldBeSentApprovalNext(trr.getId());
		if(whoToSendMessage.equals("SV")) {
			trr = trfss.MarkRequestPendingSVApproval(trr.getId());
			ms.sendSVApprovalRequest(trr.getId());
		}
		else if(whoToSendMessage.equals("DH")) {
			trr = trfss.MarkRequestPendingDHApproval(trr.getId());
			ms.sendDHApprovalRequest(trr.getId());
		}
		else {
			trr = trfss.MarkRequestPendingBenCoApproval(trr.getId());
			ms.sendBenCoApprovalRequest(trr.getId());
		}
		}
		catch(RequestNotFoundException e) {
			System.out.println(e);
		}
			//DH BC
		
	}

}
