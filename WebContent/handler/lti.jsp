<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="blackboard.platform.session.*,
				java.util.*,
				java.net.*,
				blackboard.data.user.*,
				blackboard.persist.*,
				blackboard.data.course.*,
				blackboard.persist.course.*,
				javax.crypto.spec.SecretKeySpec,
				blackboard.data.role.*,
                javax.crypto.SecretKey,
                blackboard.data.course.CourseMembership.*,
                java.util.Base64.Encoder,org.apache.commons.codec.binary.Base64,
                javax.crypto.Mac"
         errorPage="../error.jsp"%>
<%@page import="com.spvsoftwareproducts.blackboard.utils.B2Context" errorPage="../error.jsp"%>
<%@page import="iloslib.*,
				iloslib.models.*,
				iloslib.collections.*,
				iloslib.repositories.*"
        errorPage ="../error.jsp" %>
<%@ page import="java.sql.Array" %>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<%@ page isErrorPage="true" %>

<bbData:context id="ctx">
    <bbNG:learningSystemPage ctxId="ctx2" hideCourseMenu="1" >

        <%
            String SERVER_NAME = "server-name";
            String API_KEY = "api-key";
            String API_SECRET = "secret-key";

            B2Context b2Context = new B2Context(request);
            String jQueryPath = b2Context.getPath() + "js/jquery.min.js";

            String returnUrl = b2Context.getServerUrl() + b2Context.getPath() + "handler/ltiSuccess.jsp";
            String apiKey = b2Context.getSetting(API_KEY);
            String instanceName = b2Context.getServerUrl();
            String server = b2Context.getSetting(SERVER_NAME);

            String endPoint = server + "/lti/embed";
            String familyCode = "BlackboardBB";
            String selectionDirective = "embed_content";
            String intendedUse = "embed";

            User user = b2Context.getContext().getUser();
            String email = user.getEmailAddress();
            String name = user.getGivenName();

            //Weird bug that converts spaces to + so I'm just removing any space from the name
            name = name.replaceAll("\\s+","");

            CourseMembership courseMembership = b2Context.getContext().getCourseMembership();
            Role role = courseMembership.getRole();

            //Just as the VidGrid app, the token is null, so we have to add the extra &
            String secret = b2Context.getSetting(API_SECRET)+"&";

            String basePostString = "POST&" + URLEncoder.encode(endPoint) + "&";
            //Manually create a base string and signature, they have to be in alphabetic order
            String baseStringParameters = "ext_content_intended_use=" + intendedUse +
                    "&launch_presentation_return_url=" + URLEncoder.encode(returnUrl) +
                    "&lis_person_contact_email_primary=" + URLEncoder.encode(email) +
                    "&lis_person_name_given=" + URLEncoder.encode(name) +
                    "&oauth_consumer_key=" + apiKey +
                    "&roles=" + URLEncoder.encode(role.toString()) +
                    "&selection_directive=" + selectionDirective +
                    "&tool_consumer_info_product_family_code=" + familyCode +
                    "&tool_consumer_instance_name=" + URLEncoder.encode(instanceName);

            String baseString = basePostString + URLEncoder.encode(baseStringParameters);

            //As in the app we need to sign the baseString using HmacSHA1 and after that we encodeBase64 the baseString
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] text = baseString.getBytes();

            String signature = new String(Base64.encodeBase64(mac.doFinal(text))).trim();
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

        <<form method="post" id="ltiPost" name="ltiPost" action="<%=server%>/lti/embed">
            <input type="hidden" name="ext_content_intended_use" id="ext_content_intended_use" value="<%=intendedUse%>">
            <input type="hidden" name="launch_presentation_return_url" id="launch_presentation_return_url" value="<%=returnUrl%>">
            <input type="hidden" name="lis_person_contact_email_primary" id="lis_person_contact_email_primary" value="<%=email%>">
            <input type="hidden" name="lis_person_name_given" id="lis_person_name_given" value="<%=name%>">
            <input type="hidden" name="oauth_consumer_key" id="oauth_consumer_key" value="<%=apiKey%>">
            <input type="hidden" name="roles" id="roles" value="<%=role.toString()%>">
            <input type="hidden" name="tool_consumer_instance_name" id="tool_consumer_instance_name" value="<%=instanceName%>">
            <input type="hidden" name="selection_directive" id="selection_directive" value="<%=selectionDirective%>">
            <input type="hidden" name="tool_consumer_info_product_family_code" id="tool_consumer_info_product_family_code" value="<%=familyCode%>">
            <input type="hidden" name="oauth_signature" id="oauth_signature" value="<%=signature%>">
            <input type="hidden" name="baseString" id="baseString" value="<%=baseString%>">
        </form>

    </bbNG:learningSystemPage>
</bbData:context>