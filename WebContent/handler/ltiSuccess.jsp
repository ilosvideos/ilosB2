<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="blackboard.platform.session.*,
				java.util.*,
				java.net.*,
				blackboard.data.user.*,
				blackboard.persist.*,
				blackboard.data.course.*,
				blackboard.persist.course.*"
         errorPage="../error.jsp"%>
<%@page import="com.spvsoftwareproducts.blackboard.utils.B2Context" errorPage="../error.jsp"%>
<%@page import="iloslib.*,
				iloslib.models.*,
				iloslib.collections.*,
				iloslib.repositories.*"
        errorPage ="../error.jsp" %>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<%@ page isErrorPage="true" %>

<bbData:context id="ctx">
    <bbNG:learningSystemPage ctxId="ctx2" hideCourseMenu="true" >

        <bbNG:cssFile href="../css/IlosB2.css"/>
        <bbNG:cssFile href="../css/jquery.fancybox.css" />

        <span class="url" style="display: none"><% out.write(request.getParameter("url")); %></span>
        <div style="width: 100%">
            <div style="text-align: center">
                <div id="Searching" style="display: block"><img src="../images/searching.gif" alt="searching" /></div>
            </div>
        </div>

    </bbNG:learningSystemPage>
</bbData:context>