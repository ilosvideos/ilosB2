<%@ page import="blackboard.platform.plugin.PlugInUtil" errorPage="../error.jsp"%>
<%@ page import="com.spvsoftwareproducts.blackboard.utils.B2Context" errorPage="../error.jsp"%>
<%@ page import="iloslib.*"  errorPage ="../error.jsp" %>
<%@ page import="java.util.*,
				java.net.*" 
		errorPage="../error.jsp"%>
<%
	String API_SERVER_NAME = "api-server-name";
	String SERVER_NAME = "server-name";
	String API_KEY = "api-key";
	String SECRET_KEY = "secret-key";

	B2Context b2Context = new B2Context(request);

	EnsembleB2 eb2 = new EnsembleB2(
			b2Context.getSetting(SERVER_NAME),
			b2Context.getSetting(API_KEY),
			b2Context.getSetting(SECRET_KEY),
			b2Context.getSetting(API_SERVER_NAME));

	String WYSIWYG_WEBAPP = "/webapps/wysiwyg";
	String randtag = request.getParameter("randtag");

	String embedHtml = eb2.getContentHtml(randtag);
	
	// Process the vtbe
    request.setAttribute( "embedHtml", embedHtml );
    String embedUrl = PlugInUtil.getInsertToVtbePostUrl().replace( WYSIWYG_WEBAPP, "" );
    RequestDispatcher rd = getServletContext().getContext( WYSIWYG_WEBAPP ).getRequestDispatcher( embedUrl );
    rd.forward( request, response );
%>