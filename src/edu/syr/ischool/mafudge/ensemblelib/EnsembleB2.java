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
	private String m_secureApiBaseUrl;
	private String m_simpleApiBaseUrl;
	private VideoRepository m_vr;
	private WebDestinationRepository m_wd;
	private InstContentRepository m_ic;
	private SimpleHttpClient m_http;
	private String SERVER_NAME = "server-name";
	private String API_SERVER_NAME = "api-server-name";

	// This is the proper constructor going forward
	public EnsembleB2(String serverUrl, String apiKey, String secretKey, String apiUrl)
	{
		m_vr = new VideoRepository();
		m_ic = new InstContentRepository();
		m_wd = new WebDestinationRepository();
		m_http= new SimpleHttpClient();
		m_apiKey = apiKey;
		m_secretKey = secretKey;
		m_serverUrl =  serverUrl.replaceAll("/$", "");

		m_apiUrl = apiUrl;

		m_simpleApiBaseUrl = m_apiUrl + "/opensearch/?api_key=" + m_apiKey;
		m_secureApiBaseUrl = m_apiUrl + "/opensearch/?api_key=" + m_apiKey;
	}
	
	
	public String TestApiWithResult() throws Exception {
		String result;
		String testUrl = this.m_secureApiBaseUrl;
		result = m_http.webGet(testUrl).replaceAll("\\<.*?\\>", "");
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
	
	// Ensemble 3.4 and higher
	public String getContentHtmlAsIFrame(String randtag, String thumbnail) {
		String plugInUrl=this.getEmbedPluginUrl();

		String embedHtml = "<iframe id=\"ilosEmbeddedContent_"+randtag+"\" width=\"640\" height=\"360\" allowTransparency=\"true\"";
		embedHtml += " mozallowfullscreen webkitallowfullscreen allowfullscreen style=\"background-color:transparent;\" frameBorder=\"0\"";
		embedHtml += " src=\""+plugInUrl+"embed/"+randtag+"\"></iframe>";
		return embedHtml;
	}

	public List<Video> getSharedLibraryVideo(String searchText, String userName) throws Exception {
		String result = "";
		String requestUrl = this.buildRequestSearchUrl(searchText);
		result = m_http.webGet(requestUrl);
		if (result.length() > 0) {
			m_vr.fromRawXmlString(result);
		}
		return m_vr.getVideos();
	}
	
	private String buildRequestSearchUrl(String searchText) {
		searchText = searchText.length() == 0 ? "!" : searchText;
		String requestUrl = this.m_secureApiBaseUrl + "&q=" + searchText;
		return requestUrl;
	}

	private String buildPreviewUrl(String contentId) {
		DateTime nowUtc = (new DateTime()).withZone(DateTimeZone.UTC);
		String timeStamp = nowUtc.toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
		String requestUrl = this.m_secureApiBaseUrl + "/content/" +contentId + "?ts=" + timeStamp + "&hmac=";
		return requestUrl;
		
	}

}
