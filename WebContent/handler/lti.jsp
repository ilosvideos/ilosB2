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
    <bbNG:learningSystemPage ctxId="ctx2" hideCourseMenu="1" >

        <%
            String SERVER_NAME = "server-name";
            String API_KEY = "api-key";

            B2Context b2Context = new B2Context(request);
            String jQueryPath = b2Context.getPath() + "js/jquery.min.js";

            String returnUrl = b2Context.getServerUrl() + b2Context.getPath() + "handler/ltiSuccess.jsp";
            String apiKey = b2Context.getSetting(API_KEY);
            String instanceName = b2Context.getServerUrl();
            String server = b2Context.getSetting(SERVER_NAME);
        %>

        <bbNG:jsFile href="<%=jQueryPath %>"/>
        <bbNG:jsBlock>
            <script type="text/javascript">
                jQuery.noConflict();

                // Use jQuery via jQuery(...)
                jQuery(document).ready(function(){
                    jQuery("#ltiPost").submit();
                });

            </script>
        </bbNG:jsBlock>

        <bbNG:cssFile href="../css/IlosB2.css"/>
        <bbNG:cssFile href="../css/jquery.fancybox.css" />

        <form method="post" id="ltiPost" name="ltiPost" action="<%=server%>/lti/embed">
            <input type="hidden" name="oauth_consumer_key" id="oauth_consumer_key" value="<%=apiKey%>">
            <input type="hidden" name="launch_presentation_return_url" id="launch_presentation_return_url" value="<%=returnUrl%>">
            <input type="hidden" name="tool_consumer_instance_name" id="tool_consumer_instance_name" value="<%=instanceName%>">
            <input type="hidden" name="selection_directive" id="selection_directive" value="embed_content">
            <input type="hidden" name="ext_content_intended_use" id="ext_content_intended_use" value="embed">
            <input type="hidden" name="tool_consumer_info_product_family_code" id="tool_consumer_info_product_family_code" value="blackboard">
        </form>

    </bbNG:learningSystemPage>
</bbData:context>