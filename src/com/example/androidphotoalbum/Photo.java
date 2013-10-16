package com.example.androidphotoalbum;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eytan Biala
 */
public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;

	String parentAlbum;
	String filename;
	List<PhotoTag> tags;

	public Photo(String filename, List<PhotoTag> tags) {
		super();
		this.filename = filename;
		this.tags = tags;
	}

	public Photo(String filename, List<PhotoTag> tags, String parentAlbum) {
		super();
		this.parentAlbum = parentAlbum;
		this.filename = filename;
		this.tags = tags;
	}

	public Photo(String filename) {
		super();
		this.filename = filename;
		this.tags = new ArrayList<PhotoTag>();
	}

	public String getParentAlbum() {
		return parentAlbum;
	}

	public void setParentAlbum(String parentAlbum) {
		this.parentAlbum = parentAlbum;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<PhotoTag> getTags() {
		return tags;
	}

	public void setTags(List<PhotoTag> tags) {
		this.tags = tags;
	}

	/**
	 * Add a tag to the photo.
	 * 
	 * @param tagType The type of the tag to add.
	 * @param tagValue The value of the tag to add.
	 * @return True if the tag is added, false if the tag already exists.
	 */
	public boolean addTag(String tagType, String tagValue) {
		PhotoTag tag = new PhotoTag(tagType, tagValue);
		for (PhotoTag t : this.tags) {
			if (!tag.equals(t)) {
				return false;
			}
		}
		tags.add(tag);
		return true;
	}

	/**
	 * Add a tag to the photo.
	 * 
	 * @param tagType The type of the tag to add.
	 * @param tagValue The value of the tag to add.
	 * @return True if the tag is added, false if the tag already exists.
	 */
	public boolean addTag(PhotoTag t) {
		if (!this.tags.contains(t)) {
			tags.add(t);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Delete a tag from the photo.
	 * 
	 * @param tagType The type of the tag to delete.
	 * @param tagValue The value of the tag to delete.
	 * @return True if the tag is deleted, false if the tag does not exist.
	 */
	public boolean deleteTag(String tagType, String tagValue) {
		System.out.println("Tags: " + this.tags);
		PhotoTag t = new PhotoTag(tagType, tagValue);
		for (PhotoTag tag : this.tags) {
			if (t.equals(tag)) {
				System.out.println("Match: " + t + ", " + tag);
			}
		}
		if (this.tags.contains(t)) {
			this.tags.remove(t);
			System.out.println("Removed " + t);
			return true;
		}
		return false;
	}

	public String getPhotoFilename() {
		File f = new File(this.filename);
		return f.getName();
	}

	public String getPhotoFilenameWithoutExtension() {
		File f = new File(this.filename);
		String name = f.getName();
		String[] extensions = {"jpg", "png", "gif", "bmp", "tiff", "exif"};
		for (int i = 0; i < extensions.length; i++) {
			name = name.replace("." + extensions[i], "");
			name = name.replace("." + extensions[i].toUpperCase(), "");
		}
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((parentAlbum == null) ? 0 : parentAlbum.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (parentAlbum == null) {
			if (other.parentAlbum != null)
				return false;
		} else if (!parentAlbum.equals(other.parentAlbum))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return filename + "\nAlbum: " + parentAlbum + "\nTags: " + StringDisplayUtil.formatTagList(tags);
	}

}
