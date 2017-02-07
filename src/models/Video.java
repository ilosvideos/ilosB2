package iloslib.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

//
// A Video
//
@Root
public class Video {

	public Video() { }
	
	@Element
	public String videoID;

	@Element (required=false)
	public String videoDate;

	@Element (required=false)
	public String videoDescription;

	@Element (required=false)
	public String videoTitle;

	@Element (required=false)
	public String link;

	@Element (required=false)
	public String embedLink;

	@Element (required=false)
	public String randtag;

	@Element (required=false)
	public String thumbnailUrl;

	@Element (required=false)
	public String plays;

	@Element (required=false)
	public String duration;

	@Element (required=false)
	public String author;

}