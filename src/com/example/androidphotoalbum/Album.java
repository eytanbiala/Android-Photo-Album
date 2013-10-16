package com.example.androidphotoalbum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eytan Biala
 */
public class Album implements Serializable {

	private static final long serialVersionUID = 1L;

	String albumName;
	HashMap<String, Photo> photos; // K = filename, V = Photo

	public Album(String albumName) {
		super();
		this.albumName = albumName;
		this.photos = new HashMap<String, Photo>();
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public HashMap<String, Photo> getPhotos() {
		return photos;
	}

	public String firstPhoto() {
		HashMap<String, Photo> map = getPhotos();
		for (Map.Entry<String, Photo> entries : map.entrySet()) {

			return entries.getValue().filename;
		}
		return null;
	}

	public void setPhotos(HashMap<String, Photo> photos) {
		this.photos = photos;
	}

	/**
	 * Add photo to the Album
	 * 
	 * @param p
	 * @param a
	 * @param filename
	 * @return true if added else returns false if album already have the photo
	 */
	public boolean addPhotoToAlbum(Photo p, Album a, String filename) {
		if (!photos.containsValue(p)) {
			p.setParentAlbum(filename);
			a.photos.put(filename, p);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes a photo p from the Album
	 * 
	 * @param p
	 * @param a
	 * @return true if removed else returns false if photo is not found in the
	 *         album.
	 */
	public boolean deletePhotoFromAlbum(Photo p, Album a) {

		if (a.photos.containsValue(p)) {
			a.photos.remove(p);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((albumName == null) ? 0 : albumName.hashCode());
		result = prime * result + ((photos == null) ? 0 : photos.hashCode());
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
		Album other = (Album) obj;
		if (albumName == null) {
			if (other.albumName != null)
				return false;
		} else if (!albumName.equals(other.albumName))
			return false;
		if (photos == null) {
			if (other.photos != null)
				return false;
		} else if (!photos.equals(other.photos))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Album [albumName=" + albumName + ", photos=" + photos + "]";
	}
}
