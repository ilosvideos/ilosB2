package iloslib;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import iloslib.models.*;
import iloslib.repositories.*;

public class IlosB2 {

	private String m_serverUrl;
	private String m_apiUrl;
	private String m_apiKey;
	private String m_secretKey;
	private String m_secureApiBaseUrl;
	private String m_simpleApiBaseUrl;
	private VideoRepository m_vr;
	private SimpleHttpClient m_http;
	private String SERVER_NAME = "server-name";
	private String API_SERVER_NAME = "api-server-name";

	public IlosB2(String serverUrl, String apiKey, String secretKey, String apiUrl)
	{
		m_vr = new VideoRepository();
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
	
	public VideoRepository getVideoRepository()
	{
		return m_vr;
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
	
	public String getPluginUrl() {
		return (this.m_serverUrl + "/app/plugin/plugin.aspx");
	}
	
	public String getEmbedPluginUrl() {
		return (this.m_serverUrl );
		
	}
	
	public String getContentHtml(String randtag){
		String plugInUrl=this.getEmbedPluginUrl();

		String embedHtml = "<iframe id=\"ilosEmbeddedContent_"+randtag+"\" width=\"640\" height=\"360\" allowTransparency=\"true\"";
		embedHtml += " mozallowfullscreen webkitallowfullscreen allowfullscreen style=\"background-color:transparent;\" frameBorder=\"0\"";
		embedHtml += " src=\""+plugInUrl+"/embed/"+randtag+"\"></iframe>";
		return embedHtml;
	}

	public String getContentHtmlByUrl(String url){

		String embedHtml = "<iframe id=\"ilosEmbeddedContent_\" width=\"640\" height=\"360\" allowTransparency=\"true\"";
		embedHtml += " mozallowfullscreen webkitallowfullscreen allowfullscreen style=\"background-color:transparent;\" frameBorder=\"0\"";
		embedHtml += " src=\""+url+"\"></iframe>";
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

}
