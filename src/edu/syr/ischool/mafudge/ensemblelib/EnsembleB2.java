package edu.syr.ischool.mafudge.ensemblelib;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import edu.syr.ischool.mafudge.ensemblelib.models.*;
import edu.syr.ischool.mafudge.ensemblelib.repositories.*;


// Main Class Which Brokers All Activity with Ensemble
public class EnsembleB2 {

	private String m_serverUrl;
	private String m_apiUrl;
	private String m_apiKey;
	private String m_secretKey;
	private String m_domain;
	private String m_secureApiBaseUrl;
	private String m_simpleApiBaseUrl;
	private VideoRepository m_vr;
	private WebDestinationRepository m_wd;
	private InstContentRepository m_ic;
	private SimpleHttpClient m_http;

	// This is the proper constructor going forward
	public EnsembleB2(String serverUrl, String apiKey, String secretKey, String domain)
	{
		m_vr = new VideoRepository();
		m_ic = new InstContentRepository();
		m_wd = new WebDestinationRepository();
		m_http= new SimpleHttpClient();
		m_apiKey = apiKey;
		m_secretKey = secretKey;
		m_domain = domain;
		m_serverUrl =  serverUrl.replaceAll("/$", "");
		//todo send api server too as parameter
		m_serverUrl = "https://cloud.ilosvideos.com/";
		m_apiUrl = "https://cloudapi.ilosvideos.com/";

		m_simpleApiBaseUrl = m_apiUrl + "/opensearch/?api_key=" + m_apiKey;
		m_secureApiBaseUrl = m_apiUrl + "/opensearch/?api_key=" + m_apiKey;
	}
	
	
	public String TestApiWithResult() throws Exception {
		String result;
		DateTime nowUtc = (new DateTime()).withZone(DateTimeZone.UTC);
		String timeStamp = nowUtc.toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
		String testUrl = this.m_secureApiBaseUrl + "/Test?ts=" + timeStamp + "&hmac=";
		String hmac = HMacMD5Encoder.Encode(this.m_secretKey, testUrl.toLowerCase());
		result = m_http.webGet(testUrl + hmac).replaceAll("\\<.*?\\>", "");
		return result;
	}
	
	public Boolean TestApi() throws Exception {
		String Expected = "Success";
		String result = this.TestApiWithResult();
		return (result.indexOf(Expected) > 0);
	}

	public VideoRepository getVideoRepository()
	{
		return m_vr;
	}
	
	public InstContentRepository getInstContentRepository()
	{
		return m_ic;
	}
	
	public String getServerUrl() { return m_serverUrl; }
	public String getApiKey() { return m_apiKey; }
	public String getSecretKey() { return m_secretKey; }
	public String getDomain() { return m_domain; }
	
	
	public List<Video> getVideosByUrl(String stringUrl) throws Exception
	{
		String result = m_http.get(stringUrl);
		if (result.length()>0) {
			m_vr.fromRawXmlString(result);
		}
		return m_vr.getVideos();
	}
	
	public List<Video> getVideosByWebDestination(String webDestinationId) throws Exception	{
		return getVideosByUrl(getWebDestinationHref(webDestinationId));
	}
	
	public String getWebDestinationHref(String webDestinationId) throws Exception {
		return (this.m_simpleApiBaseUrl + "/video/list.xml/" + webDestinationId);
	}
	
	// Pre-Ensemble 3.4
	public String getPluginUrl() {
		return (this.m_serverUrl + "/app/plugin/plugin.aspx");
	}
	
	// Ensemble 3.4 and up
	public String getEmbedPluginUrl() {
		//return (this.m_serverUrl + "/app/plugin/embed.aspx");
		return (this.m_serverUrl );
		
	}
	
	public String getContentUrl(String contentId) throws Exception {
		String requestUrl = this.buildPreviewUrl(contentId);
		String hmac = HMacMD5Encoder.Encode(this.m_secretKey, requestUrl.toLowerCase());
		return (requestUrl + hmac);
	}
		
	public String getWebDestinationHtml(String webDestinationID) {
		// Ensemble 3.4 and up
		return getWebDestinationHtmlAsIFrame(webDestinationID);
		// return getWebDestinationHtmlAsJavascript(webDestinationID);
	}

	// Ensemble pre 3.4
	public String getWebDestinationHtmlAsJavascript(String webDestinationID) {
		String plugInUrl = this.getPluginUrl();  		
		String embedHtml = "<div id=\"ensembleContentContainer_" + webDestinationID + "\" class=\"ensembleContentContainer\" style=\"width: 99%; height: 1000px;\">";
		embedHtml += "<script type=\"text/javascript\" src=\"" + plugInUrl + "?destinationID=" + webDestinationID + "&useIFrame=true\"></script></div>";
		return embedHtml;
	}

	// Ensemble 3.4 and higher
	public String getWebDestinationHtmlAsIFrame(String webDestinationID) {
		String plugInUrl = this.getEmbedPluginUrl();  		
		String embedHtml = "<iframe id=\"ensembleFrame_" + webDestinationID + "\" src=\"" + plugInUrl + "?DestinationID=" + webDestinationID + "\" ";
		embedHtml += "frameborder=\"0\" style=\"width: 99%; height : 1000px;\" allowfullscreen></iframe>"; 
		return embedHtml;
	}

	public String getContentHtml(String contentID, String thumbnail){
		return getContentHtmlAsIFrame(contentID,thumbnail);
	}
	
	// Ensemble pre 3.4
/*
	public String getContentHtmlAsJavascript(String contentID, String thumbnail) {
		String plugInUrl = this.getPluginUrl();
		String embedHtml = "<div id=\"ensembleEmbeddedContent_" + contentID + "\" class=\"ensembleEmbeddedContent\" style=\"width: 480px; height: 326px;\">";
		embedHtml += thumbnail + "<script type=\"text/javascript\" src=\"" + plugInUrl  + "?contentID=" + contentID;
		embedHtml += "&useIFrame=true&embed=true&displayTitle=false&startTime=0&autoPlay=false&hideControls=false&showCaptions=false&width=480&height=270\">";
		embedHtml +="</script></div>";
		return embedHtml;
	}
*/

	// Ensemble 3.4 and higher
	public String getContentHtmlAsIFrame(String randtag, String thumbnail) {
		String plugInUrl=this.getEmbedPluginUrl();

		String embedHtml = "<iframe id=\"ilosEmbeddedContent_"+randtag+"\" width=\"640\" height=\"360\" allowTransparency=\"true\"";
		embedHtml += " mozallowfullscreen webkitallowfullscreen allowfullscreen style=\"background-color:transparent;\" frameBorder=\"0\"";
		embedHtml += " src=\""+plugInUrl+"embed/"+randtag+"\"></iframe>";
		return embedHtml;
	}

	public List<WebDestination> getWebDestinations(String userName) throws Exception {
		String result;
		String requestUrl = this.buildRequestUrl("/webDestinations/user/", getUserWithDomain(userName));
		String hmac = HMacMD5Encoder.Encode(this.m_secretKey, requestUrl.toLowerCase());
		result = m_http.webGet(requestUrl + hmac);
		if (result.length()>0) {
			m_wd.fromRawXmlString(result);
		}
		return m_wd.getWebDestinations();
	}

	public List<Video> getSharedLibraryVideo(String searchText, String userName) throws Exception {
		String result = "";
		String requestUrl = this.buildRequestSearchUrl("", getUserWithDomain(userName), searchText);
		result = m_http.webGet(requestUrl);
		if (result.length() > 0) {
			m_vr.fromRawXmlString(result);
		}
		return m_vr.getVideos();
	}
	
	private String getUserWithDomain(String userName){
		return this.m_domain.length() == 0 ? userName : userName + "@" + this.m_domain;
	}

	private String buildRequestSearchUrl(String command, String userNameWithDomain, String searchText) {
		searchText = searchText.length() == 0 ? "!" : searchText;
		String requestUrl = this.m_secureApiBaseUrl + "&q=" + searchText;
		return requestUrl;
	}

	private String buildRequestUrl(String command, String userNameWithDomain) {
		DateTime nowUtc = (new DateTime()).withZone(DateTimeZone.UTC);
		String timeStamp = nowUtc.toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
		String requestUrl = this.m_secureApiBaseUrl + command + userNameWithDomain + "?ts=" + timeStamp + "&hmac=";
		return requestUrl;
	}
	
	private String buildPreviewUrl(String contentId) {
		DateTime nowUtc = (new DateTime()).withZone(DateTimeZone.UTC);
		String timeStamp = nowUtc.toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
		String requestUrl = this.m_secureApiBaseUrl + "/content/" +contentId + "?ts=" + timeStamp + "&hmac=";
		return requestUrl;
		
	}

}
