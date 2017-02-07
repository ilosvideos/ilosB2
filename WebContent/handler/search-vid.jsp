
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
<%
	boolean isVtbe = request.getParameter("vtbe") != null ? true : false;
%>
<bbNG:learningSystemPage ctxId="ctx2" hideCourseMenu="<%=isVtbe %>" >

<bbNG:cssFile href="../css/IlosB2.css"/>
<bbNG:cssFile href="../css/jquery.fancybox.css" /> 

<%
	String API_SERVER_NAME = "api-server-name";
	String SERVER_NAME = "server-name";
	String API_KEY = "api-key";
	String SECRET_KEY = "secret-key";
	B2Context b2Context = new B2Context(request);
	String jQueryPath = b2Context.getPath() + "js/jquery.min.js";
	String fancyBoxPath = b2Context.getPath() + "js/jquery.fancybox.pack.js";

	IlosB2 eb2 = new IlosB2(
			b2Context.getSetting(SERVER_NAME),
			b2Context.getSetting(API_KEY),
			b2Context.getSetting(SECRET_KEY),
			b2Context.getSetting(API_SERVER_NAME)
	);

	pageContext.setAttribute("bundle", b2Context.getResourceStrings());
	String courseId = ctx.getCourseId().toString();  
	String userName = ctx.getUser().getUserName();
	String searchText = request.getParameter("searchText"); 
	String ref  = request.getMethod().equalsIgnoreCase("POST") ? 
			request.getParameter("http_ref")!= null ? request.getParameter("http_ref") : ""   : 
			request.getHeader("referer")!= null ? URLEncoder.encode(request.getHeader("referer"),"UTF-8") : "";

	// Sanitize the search text input replaceAll("[^A-Za-z0-9 ]", "") and replace " " with "+"
	String encodedSearchText = searchText != null ? URLEncoder.encode(searchText,"UTF-8"): "";
	// need to encode again to pass the "%" symbol on the query string.
	encodedSearchText = encodedSearchText.contains("%") ? URLEncoder.encode(encodedSearchText,"UTF-8") : encodedSearchText;
	String processUrl = "vtbe-search-vid-process.jsp";

%>
	<bbNG:jsFile href="<%=jQueryPath %>"/>
	<bbNG:jsFile href="<%=fancyBoxPath %>"/>

	<bbNG:pageHeader instructions="Search Ilos Video">
		<bbNG:pageTitleBar iconUrl="https://s3.amazonaws.com/ilos-public-assets/ilos_icon_16x_16.png"
						   showTitleBar="true"
						   title="Search for a video to add through this interface."/>
	</bbNG:pageHeader>

	<bbNG:jsBlock>
   	<script type="text/javascript">
    	 jQuery.noConflict();
    	     	      
     	// Use jQuery via jQuery(...)
     	jQuery(document).ready(function(){
     		jQuery("#Searching").hide();
			//jQuery(".ilos-searchResult-itemContainer").hide();
     		
       		jQuery("#submitButton").click(function() {
       			jQuery("#noResults").hide();
         		jQuery("#Searching").show();
				//jQuery(".ilos-searchResult-itemContainer").show();
    			return true;
	     	});
       		
       		jQuery(".various").fancybox({
                type: 'iframe',
                scrolling: 'no',
                maxWidth: 520,
                maxHeight: 350,
                fitToView: true,
                width: '100%',
                height: '100%',
                autoSize: false,
                autoResize: true,
                autoCenter: true,
                closeClick: false,
                openEffect: 'none',
                closeEffect: 'none',
                helpers : {
                    overlay : {
                        css : { 'overflow' : 'hidden' }
                    }
                }
            });
     	});
   	</script>
	</bbNG:jsBlock>

	<bbNG:actionControlBar>
		<bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
	  		<bbNG:form action="" method="POST" id="id_searchForm" name="searchForm">
		 		<input type="hidden" value="<%=ref %>" name="http_ref" />
		 		<%--<label for="searchText">Search:</label>--%>
		 		&nbsp;
		 		<bbNG:textElement class="searchInput" name="searchText" id="searchText" placeholder="Keywords..." autocomplete="off" isRequired="true" value="<%=searchText %>" size="30" maxLength="255" />
		 		&nbsp;
		 		&nbsp;
			 	<input type="submit" class="button genericButton" value="Search" id="submitButton"/>
	  		</bbNG:form>
	   	</bbNG:actionPanelButton>
	</bbNG:actionControlBar>

	<div style="text-align: center">
		<div id="Searching" style="display: block"><img src="../images/searching.gif" alt="searching" /></div>
	</div>

	<div id="grid" class="grid-vertical" style="display: block; position: relative;">
	<% if (request.getMethod().equalsIgnoreCase("POST")) { %>

	<!-- Search Results go Here -->
	<%
		ref = request.getParameter("http_ref");
		List<Video> vl = new ArrayList<Video>();
		vl = eb2.getSharedLibraryVideo(encodedSearchText, userName);

		// If you've returned results...
		if (vl.size() > 0)
		{
			for (Video v : vl)
			{
		%>
				<div class="grid-item">
					<div class="g-info">
						<img style="background-image: url(<%=v.thumbnailUrl %>)">
						<p class="i-text">
							<span class="g-title i-text-md"><%=v.videoTitle %></span>
							<span class="g-icons i-text-xs pull-right"><img src="../images/play.png" width="24" alt="Plays" /> <%=v.plays %> &nbsp;<img src="../images/duration.png" width="24" alt="Duration" /> <%=v.duration %></span>
						</p>
						<p class="i-text i-text-xs">
							<span class="g-author"><%=v.author %></span>
							<span class="g-date i-text-light"><%=v.videoDate %></span>

							<span class="g-buttons i-text-light pull-right">
								<a href="<%=processUrl %>?randtag=<%=v.randtag %>&amp;title=<%=URLEncoder.encode(v.videoTitle,"UTF-8") %>&amp;course_id=<%=courseId %>&amp;http_ref=<%=ref %>">
									<input type="submit" class="button successButton" value="Embed" id=""/>
								</a>
								<a class="various fancybox.iframe"  href="<%=v.embedLink %>" title="<%=v.videoTitle %>">
									<input type="submit" class="button infoButton" value="Preview" id=""/>
								</a>
							</span>

						</p>
					</div>
				</div>
				</a>
			<% }  // for each %>
		<% } else { // no search results %>

				<div id="noResults"><strong>Your search did not return any content.</strong></div>

		<% } // if (vl.size()>0) %>

	<% } // if (postback) %>
	</div>
</bbNG:learningSystemPage>
</bbData:context>