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
	public List<Video> getVideos() {
		return m_vidList;
	}

	// Sets the collection of Videos
	public void setVideos(List<Video> vList) {
		m_vidList = vList;
	}

	// Adds a Video, provided the wdId does not already exist.
	public boolean addVideo(Video vid) {
		boolean result = false;
		if (!existsVideoById(vid.videoID)) {
			result = m_vidList.add(vid);
		}
		return result;
	}

	// Adds a list of video to this repository returns the number added
	public int addVideos(List<Video> vl) {
		int count = 0;
		for (Video v : vl) {
			if (this.addVideo(v)) count++;
		}
		return count;
	}

	// Deletes the Video with wdIdKey from the collection
	public boolean deleteVideoById(String videoId) {
		boolean result = false;
		for (Video vid : m_vidList) {
			if (vid.videoID == videoId) {
				result = m_vidList.remove(vid);
				break;
			}
		}
		return result;
	}

	// Verifies whether the wdIdKey exists in the collection
	public boolean existsVideoById(String videoId) {
		boolean result = false;
		for (Video vid : m_vidList) {
			if (vid.videoID == videoId) {
				result = true;
				break;
			}
		}
		return result;
	}

	// Converts the Repository to XML
	public String toSerializedXmlString() throws Exception {
		VideoCollection vc = new VideoCollection();
		vc.setCollection(m_vidList);
		ObjectSerializer os = new ObjectSerializer();
		String xmlString = os.toXmlString(vc);
		return xmlString;
	}

	// builds the repository from an XML string.
	public void fromSerializedXmlString(String xmlString) throws Exception {
		ObjectSerializer os = new ObjectSerializer();
		VideoCollection vc = new VideoCollection();
		vc = (VideoCollection) os.fromXmlString(VideoCollection.class, xmlString);
		m_vidList = vc.getCollection();
	}

	// builds the repository from an Raw XML string (as received from Ensemble APIs).
	public void fromRawXmlString(String xmlString) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(xmlString.getBytes("UTF8"));
		Document doc = db.parse(is);
		m_vidList = new ArrayList<Video>(); // start with empty

		NodeList nl = doc.getElementsByTagName("item");
		if (nl.getLength() == 0) {
			nl = doc.getElementsByTagName("item");
		}

		for (int i = 0; i < nl.getLength(); i++) {

			Video v = new Video();
			NodeList videoID;

			v.thumbnailUrl = getElementValue(doc, "media:thumbnail", i);

			v.videoDate = getElementValue(doc, "pubDateDiffForHuman", i);
			v.embedLink = getElementValue(doc, "embedLink", i);
			v.randtag = getElementValue(doc, "randtag", i);

			videoID = doc.getElementsByTagName("video_id");
			v.videoID = videoID.item(i).getChildNodes().item(0).getNodeValue();
			v.videoDescription = getElementValue(doc, "description", i);
			v.videoTitle = getElementValue(doc, "title", i);

			v.link = getElementValue(doc, "link", i);
			v.plays = getElementValue(doc, "plays", i);
			v.duration = getElementValue(doc, "duration", i);
			v.author = getElementValue(doc, "author", i);

			this.addVideo(v);
		}
	}

	private String getElementValue(Document xmlDoc, String tagName, int index) {
		NodeList nodes = xmlDoc.getDocumentElement().getElementsByTagName(tagName);
		Element element;
		String result = "";
		try {
			switch (tagName) {
				case "pubDateDiffForHuman":
				case "embedLink":
				case "randtag":
				case "link":
				case "plays":
				case "duration":
				case "author":
					element = (Element) nodes.item(index);
					result = element.getChildNodes().item(0).getNodeValue();
					break;
				case "media:thumbnail":
					element = (Element) nodes.item(index);
					result = element.getAttribute("url");
					break;
				default:
					element = (Element) nodes.item(++index);
					result = element.getChildNodes().item(0).getNodeValue();
			}
		} catch (NullPointerException e) {
			result = "";
		}
		return result;
	}
}