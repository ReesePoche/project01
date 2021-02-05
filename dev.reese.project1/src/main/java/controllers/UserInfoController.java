package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import dev.reese.project1.entities.Employee;
import dev.reese.project1.entities.ReimbursementMessage;
import dev.reese.project1.entities.ReimbursementPayout;
import dev.reese.project1.entities.TuitionReimbursementRequest;
import serviceexceptions.EmployeeNotFoundException;
import services.LoginService;
import services.PayoutServices;
import services.TRFormSubmitionService;
import services.TRMSMessagingService;

public class UserInfoController {
	
	private static LoginService ls = new LoginService();
	public static Gson gson = new Gson();
	private static TRFormSubmitionService TRFSS = new TRFormSubmitionService();
	private static PayoutServices ps = new PayoutServices();
	private static TRMSMessagingService ms = new TRMSMessagingService();
	
	public static void GetThisEmployeeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("in get this employee info");
		HttpSession sess = request.getSession();
		String id = sess.getAttribute("loggedInEmployeeID").toString();
		int employeeId = Integer.parseInt(id);
		Employee e = ls.getEmployee(employeeId);
		response.getWriter().append((e != null) ? gson.toJson(e) : "{}");
	}
	
	public static void GetThisEmployeeRequests(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("in get this employee requests");
		HttpSession sess = request.getSession();
		String id = sess.getAttribute("loggedInEmployeeID").toString();
		int employeeId = Integer.parseInt(id);
		List<TuitionReimbursementRequest> trrs = TRFSS.getAllRequestsForEmployee(employeeId);
		System.out.println(trrs.size());
		response.getWriter().append((trrs != null) ? gson.toJson(trrs) : "{}");
	}
	
	public static void GetThisEmployeePayouts(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("in get this employee requests");
		HttpSession sess = request.getSession();
		String id = sess.getAttribute("loggedInEmployeeID").toString();
		int employeeId = Integer.parseInt(id);
		List<ReimbursementPayout> payouts = ps.getEmployeePayouts(employeeId);
		response.getWriter().append((payouts != null) ? gson.toJson(payouts) : "{}");
	}
	
	public static void GetThisEmployeeMessages(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("in get this employee requests");
		HttpSession sess = request.getSession();
		String id = sess.getAttribute("loggedInEmployeeID").toString();
		int employeeId = Integer.parseInt(id);
		List<ReimbursementMessage> messages = null;
		try {
			messages = ms.getAllMessagesForEmployee(employeeId);
		} catch (EmployeeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.getWriter().append((messages != null) ? gson.toJson(messages) : "{}");
	}
	
	public static void getTheRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("in get this employee requests");
		HttpSession sess = request.getSession();
		String id = sess.getAttribute("loggedInEmployeeID").toString();
		int employeeId = Integer.parseInt(id);
		
		TuitionReimbursementRequest trr = null;
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
		System.out.println(sb.toString());
		sess.setAttribute("trrId", sb.toString());
		response.getWriter().append("SR");
		//int requestId = Integer.parseInt(sb.toString());
//		trr = TRFSS.getRequest(requestId);
//		Employee e = ls.getEmployee(trr.getForEmployee());
//		response.getWriter().append((e != null) ? gson.toJson(e) : "{}");
//		response.getWriter().append((trr != null) ? gson.toJson(trr) : "{}");
		
	    
	    
	    
	}
	
	
	public static void ShowTheRequestToEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException{
		HttpSession sess = request.getSession();
		String stringid = sess.getAttribute("trrId").toString();
		int requestId = Integer.parseInt(stringid);
		TuitionReimbursementRequest trr = TRFSS.getRequest(requestId);
		Employee e = ls.getEmployee(trr.getForEmployee());
		response.getWriter().append((e != null) ? gson.toJson(e) : "{}");
		response.getWriter().append((trr != null) ? gson.toJson(trr) : "{}");
	}
	
	
	

}
