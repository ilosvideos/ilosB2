
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
<%@page import="edu.syr.ischool.mafudge.ensemblelib.*,
				edu.syr.ischool.mafudge.ensemblelib.models.*,  
				edu.syr.ischool.mafudge.ensemblelib.collections.*,  
				edu.syr.ischool.mafudge.ensemblelib.repositories.*"  
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

<bbNG:cssFile href="../css/EnsembleB2.css"/>
<bbNG:cssFile href="../css/jquery.fancybox.css" /> 

<%
	String SHARED_WEB_DEST = "shared-web-destinations";
	String API_SERVER_NAME = "api-server-name";
	String SERVER_NAME = "server-name";
	String API_KEY = "api-key";
	String SECRET_KEY = "secret-key";
	String MY_MEDIA = "myMedia";
	String SHARED_MEDIA= "sharedMedia";
	String INST_CONTENT= "instContent";
	B2Context b2Context = new B2Context(request);
	String jQueryPath = b2Context.getPath() + "js/jquery.min.js";
	String fancyBoxPath = b2Context.getPath() + "js/jquery.fancybox.pack.js";
	String xmlInstContent = b2Context.getSetting(SHARED_WEB_DEST);

	EnsembleB2 eb2 = new EnsembleB2(
			b2Context.getSetting(SERVER_NAME),
			b2Context.getSetting(API_KEY),
			b2Context.getSetting(SECRET_KEY),
			b2Context.getSetting(API_SERVER_NAME)
	);

	pageContext.setAttribute("bundle", b2Context.getResourceStrings());
	String courseId = ctx.getCourseId().toString();  
	String contentId = ctx.getContentId().toString();
	String userName = ctx.getUser().getUserName();
	String searchText = request.getParameter("searchText"); 
	String ref  = request.getMethod().equalsIgnoreCase("POST") ? 
			request.getParameter("http_ref")!= null ? request.getParameter("http_ref") : ""   : 
			request.getHeader("referer")!= null ? URLEncoder.encode(request.getHeader("referer"),"UTF-8") : "";
	String searchSource = (request.getParameterValues("searchSource")!= null ? request.getParameterValues("searchSource")[0] : MY_MEDIA );
	Boolean isMedia = (searchSource.equalsIgnoreCase(MY_MEDIA));
	Boolean isShared = (searchSource.equalsIgnoreCase(SHARED_MEDIA));
	Boolean isInstContent = (searchSource.equalsIgnoreCase(INST_CONTENT));
	
	String DEBUG = ""; //isVtbe? "U Used VBTE" : "U Used Content Handler";
	// Sanitize the search text input replaceAll("[^A-Za-z0-9 ]", "") and replace " " with "+"
	//searchText = searchText!=null ? searchText.replaceAll("[^A-Za-z0-9 ]","") : "";	
	//String encodedSearchText = searchText!=null ? searchText.replaceAll(" ","+") : "";
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
     		
       		jQuery("#submitButton").click(function() {
       			jQuery("#noResults").hide();
         		jQuery("#Searching").show();
       			//alert('wow you clicked me!');
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
<!--<h3>DEBUG: searchSource==<%=searchSource %> searchText==<%=searchText %></h3>-->
<bbNG:actionControlBar>
 <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
  <bbNG:form action="" method="POST" id="id_searchForm" name="searchForm">
     <input type="hidden" value="<%=ref %>" name="http_ref" />
     <label for="searchText">Search:</label>
     &nbsp;
     <bbNG:textElement name="searchText" id="searchText" isRequired="true" value="<%=searchText %>" size="30" maxLength="255" />
     &nbsp;
<%--
     <label for="searchSource" class="hideoff">Source:</label>
     <bbNG:selectElement name="searchSource" id="searchSource">
      <bbNG:selectOptionElement optionLabel="Media Library" value="<%=MY_MEDIA %>" isSelected="<%=isMedia %>"/>
      <bbNG:selectOptionElement optionLabel="Shared Library" value="<%=SHARED_MEDIA %>" isSelected="<%=isShared %>"/>
      <bbNG:selectOptionElement optionLabel="Institutional Content" value="<%=INST_CONTENT %>" isSelected="<%=isInstContent %>"/>
     </bbNG:selectElement>
--%>
     &nbsp;
     <input type="submit" class="genericButton" value="Go" id="submitButton"/>
  </bbNG:form>
   </bbNG:actionPanelButton>
</bbNG:actionControlBar>
<div id="Searching">Searching <img src="../images/searching.gif" alt="searching" /></div>
<% if (request.getMethod().equalsIgnoreCase("POST")) { %>
<!-- Search Results go Here -->
<% 
	ref = request.getParameter("http_ref");
	List<Video> vl = new ArrayList<Video>();
	vl = eb2.getSharedLibraryVideo(encodedSearchText, userName);

	// If you've returned results...
	if (vl.size() > 0) {
		for (Video v : vl) {
	%>
<div class="ensemble-searchResult-itemContainer">
    <div class="ensemble-searchResult-dataContainer">
     <h3><%=v.videoTitle %></h3>    
     <div class="ensemble-searchResult-metadata">
      <strong>Date Added:</strong>&nbsp;<%=v.videoDate %><br>
      <strong>Video ID:</strong>&nbsp;<%=v.videoID %><br>
    </div>
    <div class="ensemble-searchResult-imageContainer">
     <div class="ensemble-searchResult-imageContainer-imgDiv">
       <img width="145" src="<%=v.thumbnailUrl %>" alt="<%=v.videoTitle %>">
     </div>
     <div>
      <table class="ensemble-searchResult-imageOptions" cellpadding="0" cellspacing="0">
       <tbody><tr>
        <td class="ensemble-searchResult-button">
        <a class="various fancybox.iframe"  href="<%=v.embedLink %>" title="<%=v.videoTitle %>">Preview</a>
        </td>
       </tr>
       <tr>
        <td class="ensemble-searchResult-button"> 
         <a href="<%=processUrl %>?randtag=<%=v.randtag %>&amp;title=<%=URLEncoder.encode(v.videoTitle,"UTF-8") %>&amp;course_id=<%=courseId %>&amp;http_ref=<%=ref %>">Select</a>
        </td>
       </tr>
      </tbody></table>
     </div>
    </div>         
    </div>
</div>   
		<% }  // for each %>
	<% } else { // no search results %>
	<div id="noResults"><strong>Your search did not return any content.</strong></div>
	<% } // if (vl.size()>0) %>
	<%=DEBUG %>
<% } // if (postback) %>
</bbNG:learningSystemPage>
</bbData:context>