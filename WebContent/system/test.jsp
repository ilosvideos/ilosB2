<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="iloslib.IlosB2" errorPage="../error.jsp"%>
<%
	String apiUrl = request.getParameter("url");
	String apiKey = request.getParameter("apiKey");
	String secretKey = "";
	String serverUrl = "";

	IlosBe eb2 = new  IlosB2( serverUrl,  apiKey,  secretKey, apiUrl);

	try	
	{
		String result = eb2.TestApiWithResult();
		out.println(result);
	}
	catch (Exception e)
	{
		out.println(e.getMessage());
	}

%>