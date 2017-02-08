
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

        <%
            B2Context b2Context = new B2Context(request);
            String jQueryPath = b2Context.getPath() + "js/jquery.min.js";
            pageContext.setAttribute("bundle", b2Context.getResourceStrings());

        %>
        <bbNG:jsFile href="<%=jQueryPath %>"/>

        <bbNG:jsBlock>
            <script type="text/javascript">
                jQuery.noConflict();
            </script>
        </bbNG:jsBlock>

        <iframe src="lti.jsp" width="100%" height="600px" id="ilosIframe">

        </iframe>

        <script type="text/javascript">
            jQuery('#ilosIframe').load(function(){

                var url = jQuery("#ilosIframe").contents().find(".url").text();

                if (url.indexOf("ilosvideos") >= 0)
                {
                    document.location.href= "vtbe-search-vid-process.jsp?url="+encodeURIComponent(url);
                }

            });
        </script>
    </bbNG:learningSystemPage>
</bbData:context>