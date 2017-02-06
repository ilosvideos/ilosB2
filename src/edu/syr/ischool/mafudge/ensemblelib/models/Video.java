package edu.syr.ischool.mafudge.ensemblelib.models;

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
	public String thumbnailUrl;

}