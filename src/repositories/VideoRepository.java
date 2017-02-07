package iloslib.repositories;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.*;

import iloslib.ObjectSerializer;
import iloslib.collections.VideoCollection;
import iloslib.models.Video;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


//
// Repository for Video(s) supports Xml persistence and CRUD
//
public class VideoRepository {
	
	private List<Video> m_vidList;
	
	// Constructor
	public VideoRepository() {
		m_vidList = new ArrayList<Video>();
	}

	// Returns a collection of Videos
	public List<Video> getVideos()
	{
		return m_vidList;
	}
	
	// Sets the collection of Videos
	public void setVideos(List<Video> vList)
	{
		m_vidList = vList;
	}
	
	// Adds a Video, provided the wdId does not already exist.
	public boolean addVideo(Video vid)
	{	
		boolean result = false;
		if  (!existsVideoById(vid.videoID))
		{
			result = m_vidList.add(vid);
		}
		return result;
	}
	
	// Adds a list of video to this repository returns the number added
	public int addVideos( List<Video> vl)
	{
		int count = 0;
		for (Video v : vl)
		{
			if (this.addVideo(v)) count++;
		}
		return count;
	}
	
	// Deletes the Video with wdIdKey from the collection
	public boolean deleteVideoById(String videoId)
	{
		boolean result = false;
		for (Video vid : m_vidList)
		{
			if (vid.videoID == videoId) {
				result = m_vidList.remove(vid);
				break;
			}
		}
		return result;
	}
	
	// Verifies whether the wdIdKey exists in the collection
	public boolean existsVideoById(String videoId)
	{
		boolean result = false;
		for (Video vid : m_vidList)
		{
			if (vid.videoID == videoId) {
				result = true;
				break;
			}
		}
		return result;	
	}

	// Converts the Repository to XML
	public String toSerializedXmlString() throws Exception
	{
		VideoCollection vc = new VideoCollection();
		vc.setCollection(m_vidList);
		ObjectSerializer os = new ObjectSerializer();
		String xmlString = os.toXmlString(vc);
		return xmlString;
	}
	
	// builds the repository from an XML string.
	public void  fromSerializedXmlString(String xmlString) throws Exception
	{
		ObjectSerializer os = new ObjectSerializer();
		VideoCollection vc = new VideoCollection();
		vc = (VideoCollection)os.fromXmlString(VideoCollection.class, xmlString);
		m_vidList = vc.getCollection();
	}
	
	// builds the repository from an Raw XML string (as received from Ensemble APIs).
	public void  fromRawXmlString(String xmlString) throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db =dbf.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(xmlString.getBytes("UTF8"));		
		Document doc=db.parse(is);
		m_vidList = new ArrayList<Video>(); // start with empty
		//Boolean contentSchema = true;
		    
		NodeList nl = doc.getElementsByTagName("item");
		if (nl.getLength() == 0) {
			nl = doc.getElementsByTagName("item");
			//contentSchema = false;
		}
		for(int i=0;i<nl.getLength();i++)
		{
			//TODO check video class
			Video v = new Video();
			NodeList videoID;

			videoID=doc.getElementsByTagName("video_id");
			v.videoID=videoID.item(i).getChildNodes().item(0).getNodeValue();
			v.videoDate = getElementValue(doc,"pubDate",i);
			v.videoDescription = getElementValue(doc,"description",i);
			v.videoTitle = getElementValue(doc,"title",i);
			v.thumbnailUrl = getElementValue(doc,"media:thumbnail",i);
			v.link = getElementValue(doc,"link",i);
			v.embedLink = getElementValue(doc,"embedLink",i);
			v.randtag = getElementValue(doc,"randtag",i);

			this.addVideo(v);
		} //end for
	}

	private String getElementValue(Document xmlDoc, String tagName, int index) 
	{
		NodeList nodes = xmlDoc.getDocumentElement().getElementsByTagName(tagName);
		String result = "";
		try {
			if(tagName == "pubDate" || tagName == "embedLink" || tagName == "randtag")
			{
				Element element = (Element) nodes.item(index);
				result = element.getChildNodes().item(0).getNodeValue();
			}
			else if(tagName == "media:thumbnail")
			{
				Element element = (Element) nodes.item(index);
				result = element.getAttribute("url");
			}
			else
			{
				Element element = (Element) nodes.item(++index);
				result = element.getChildNodes().item(0).getNodeValue();
			}
		} catch (NullPointerException e) {
			result = "";
		}
		return result;
	}
		
} //end class
		/* XML that comes from ilos
		 <item>
    		<video_id>206073</video_id>
    		<title>open search 2</title>
    		<link>https://cloud.ilosvideos.com/view/GqF8CpMD3Huk</link>
    		<description>
        		<![CDATA[<img src="https://cdn.cloud.ilosvideos.com/content/G/q/F/GqF8CpMD3Huk/GqF8CpMD3Huk_NvLddqlWyPwq.jpg?Expires=1486397544&amp;Signature=Vp9i2O1UfxxFFzaKNDr5bU8LG5jL474d7LcFquKgDfIJNMWO4-rxUGEKtmQJvDhcCNxsft9lqDzpP8bCZbP~BSmcA~wyoKkS7q8cK2oRWTH8DB~PCZMaxZH2eoHfOUn6ZCqDrf98U38KZpHDMXle6rS-03XYbHqK11ovhw0RFNNbqJivdevwgTat~ZSjukjNQ56fsBT-wEiJbzqlkvWZYXO77~dJC6TeJolK~9Nbp2eNmj4utOcjh-wbKHxn3Bx7yZ37x5ynjCi~K8xUYiwtBu9bJQo-GQCl1EO0o9Y7jPiiWGkSUUmCNMmTSihlANp4AkBSUDb2rE0uglcHHuvS~g__&amp;Key-Pair-Id=APKAJD2F63UVTWAR63DQ"><br>]]>
            </description>
            <pubDate>Mon, 06 Feb 2017 04:10:15 Z</pubDate>
        	<media:thumbnail height="102" width="180" url="https://cdn.cloud.ilosvideos.com/content/G/q/F/GqF8CpMD3Huk/GqF8CpMD3Huk_NvLddqlWyPwq.jpg?Expires=1486397544&amp;Signature=Vp9i2O1UfxxFFzaKNDr5bU8LG5jL474d7LcFquKgDfIJNMWO4-rxUGEKtmQJvDhcCNxsft9lqDzpP8bCZbP~BSmcA~wyoKkS7q8cK2oRWTH8DB~PCZMaxZH2eoHfOUn6ZCqDrf98U38KZpHDMXle6rS-03XYbHqK11ovhw0RFNNbqJivdevwgTat~ZSjukjNQ56fsBT-wEiJbzqlkvWZYXO77~dJC6TeJolK~9Nbp2eNmj4utOcjh-wbKHxn3Bx7yZ37x5ynjCi~K8xUYiwtBu9bJQo-GQCl1EO0o9Y7jPiiWGkSUUmCNMmTSihlANp4AkBSUDb2rE0uglcHHuvS~g__&amp;Key-Pair-Id=APKAJD2F63UVTWAR63DQ"/>
    		<media:content height="102" width="180" medium="image" type="image/jpeg" url="https://cdn.cloud.ilosvideos.com/content/G/q/F/GqF8CpMD3Huk/GqF8CpMD3Huk_NvLddqlWyPwq.jpg?Expires=1486397544&amp;Signature=Vp9i2O1UfxxFFzaKNDr5bU8LG5jL474d7LcFquKgDfIJNMWO4-rxUGEKtmQJvDhcCNxsft9lqDzpP8bCZbP~BSmcA~wyoKkS7q8cK2oRWTH8DB~PCZMaxZH2eoHfOUn6ZCqDrf98U38KZpHDMXle6rS-03XYbHqK11ovhw0RFNNbqJivdevwgTat~ZSjukjNQ56fsBT-wEiJbzqlkvWZYXO77~dJC6TeJolK~9Nbp2eNmj4utOcjh-wbKHxn3Bx7yZ37x5ynjCi~K8xUYiwtBu9bJQo-GQCl1EO0o9Y7jPiiWGkSUUmCNMmTSihlANp4AkBSUDb2rE0uglcHHuvS~g__&amp;Key-Pair-Id=APKAJD2F63UVTWAR63DQ"/>
		</item>
		 */



