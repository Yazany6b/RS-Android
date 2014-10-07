package com.relaxodasoft.ididntknowthat_ar.image_handler;

import java.io.Serializable;

public class ImageUrl implements Serializable{
	
	private int id;
	private int url_id;
	private String image_url;
	private String thumb_url;
	private String image_disk;
	private String thumb_disk;
	
	public ImageUrl(int id, int url_id, String image_url, String thumb_url,
			String image_disk, String thumb_disk) {
		this.id = id;
		this.url_id = url_id;
		this.image_url = image_url;
		this.thumb_url = thumb_url;
		this.image_disk = image_disk;
		this.thumb_disk = thumb_disk;
	}
	
	public ImageUrl(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUrl_id() {
		return url_id;
	}

	public void setUrl_id(int url_id) {
		this.url_id = url_id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	public String getImage_disk() {
		return image_disk;
	}

	public void setImage_disk(String image_disk) {
		this.image_disk = image_disk;
	}

	public String getThumb_disk() {
		return thumb_disk;
	}

	public void setThumb_disk(String thumb_disk) {
		this.thumb_disk = thumb_disk;
	}
	
	public boolean isImageExistOnDisk(){
		return (image_disk == null)?false:true;
	}
	
	public boolean isThumbExistOnDisk(){
		return (thumb_disk == null)?false:true;
	}

}
