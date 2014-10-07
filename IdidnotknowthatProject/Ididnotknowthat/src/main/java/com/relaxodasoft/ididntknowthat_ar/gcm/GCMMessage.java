package com.relaxodasoft.ididntknowthat_ar.gcm;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.Date;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.relaxodasoft.ididntknowthat_ar.image_handler.ImageUrl;

import android.util.Log;

public class GCMMessage implements Serializable {
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String LINK = "link";
	private static final String IMAGE = "image";
	private static final String THUMB = "thumb";

	private int id;
	private String title;
	private String description;
	private Date time;

	ArrayList<ImageUrl> imageLinks;

	public GCMMessage() {
		imageLinks = new ArrayList<ImageUrl>();

	}

	public GCMMessage(String title, String desc, ArrayList<ImageUrl> links) {

		this.title = title;
		this.description = desc;
		this.imageLinks = links;
	}

	public static GCMMessage ToGCMMessage(String msg)
			throws ParserConfigurationException, SAXException, IOException {

		GCMMessage gcmMsg = new GCMMessage();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(msg)));

		// get title
		NodeList n1 = doc.getElementsByTagName(TITLE);
		gcmMsg.setTitle(n1.item(0).getTextContent());

		// get description
		n1 = doc.getElementsByTagName(DESCRIPTION);
		gcmMsg.setDescription(n1.item(0).getTextContent());

		// get imagelinks
		n1 = doc.getElementsByTagName(LINK);
		
		ArrayList<ImageUrl> urls = new ArrayList<ImageUrl>();
		ImageUrl url ;
		Log.i("GCMMESSAGE", n1.getLength()+"");
		
		for (int i = 0; i < n1.getLength(); i++) {
			url = new ImageUrl();
			
			NodeList chilNodes = n1.item(i).getChildNodes();
			//String[] cont = n1.item(i).getTextContent().split("/n");
			//n1.item(0).get
			//Log.i("GCMMESSAGE", " ##"+  n1.item(i).getTextContent() + "##");
			int cont = 0;
			String[] values= new String[2];
			
			for (int j = 0; j < chilNodes.getLength(); j++) {
				if(chilNodes.item(j) != null && chilNodes.item(j).getTextContent().length() > 4){
					values[cont++] =  chilNodes.item(j).getTextContent();
				}
			}
			url.setImage_url(values[0]);
			url.setThumb_url(values[1]);
			urls.add(url);
		}
		
		gcmMsg.setImageLinks(urls);
		
		return gcmMsg;
	}

	// getters an setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<ImageUrl> getImageLinks() {
		return imageLinks;
	}

	/*
	 * public String getImageLinks(String spliter) { String links = ""; for
	 * (ImageUrl element : imageLinks) { links += element + spliter; }
	 * 
	 * return links; }
	 */

	public void setTitle(String tit) {
		this.title = tit;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public void setImageLinks(ArrayList<ImageUrl> links) {
		this.imageLinks = links;
	}

	public void addImageLink(ImageUrl link) {
		imageLinks.add(link);
	}

}
