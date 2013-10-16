package com.example.androidphotoalbum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * @author Eytan Biala
 */
public class Controller {

	private static Controller instance = null;

	protected HashMap<String, Album> albums;
	public static final String ALBUM_FILE = "albums.dat";
	Context ctx;

	protected Controller(Context context) {
		this.ctx = context;

		try {
			File f = new File(ctx.getFilesDir() + File.separator + ALBUM_FILE);
			if (f.exists()) {
				Log.e("File", "Read existing file.");
				read();
			} else {
				Log.e("File", "Created new file.");
				File dir = ctx.getFilesDir();
				if (dir.isDirectory()) {
					for (File entry : dir.listFiles()) {
						entry.delete();
					}
				}
				albums = new HashMap<String, Album>();
				write();
			}
		} catch (Exception e) {

		}
		return;
	}

	public static Controller getInstance(Context context) {
		if (instance == null) {
			instance = new Controller(context);
		}
		return instance;
	}

	public void read() {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(ctx.openFileInput(ALBUM_FILE));
			albums = (HashMap<String, Album>) ois.readObject();
			ois.close();
			Log.i("File read", "success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write() {
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(ctx.openFileOutput(ALBUM_FILE, Context.MODE_WORLD_WRITEABLE));
			os.writeObject(albums);
			os.close();
			Log.i("File write", "success");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the current user's albums
	 * 
	 * @return Hashmap K,V where K = album name, V = Album object
	 */
	public HashMap<String, Album> getAlbums() {
		return this.albums;
	}

	/**
	 * Get the number of photos in an album.
	 * 
	 * @param album Name of the album to get the size of.
	 * @return Number of photos in an album.
	 */
	public int getAlbumSize(String album) {
		Album a = albums.get(album);
		if (a != null) {
			return a.getPhotos().values().size();
		}
		return 0;
	}

	/**
	 * Get the number of photos in an album.
	 * 
	 * @param album Album object to get the size of.
	 * @return Number of photos in an album.
	 */
	public int getAlbumSize(Album album) {
		if (album != null) {
			return album.getPhotos().values().size();
		}
		return 0;
	}

	/**
	 * Get photos in an album (utility method)
	 * 
	 * @param album The album for which to get photos for.
	 * @return Photos in the given album.
	 */
	public HashMap<String, Photo> listPhotosInAlbum(String album) {
		Album a = albums.get(album);
		if (a != null) {
			return a.getPhotos();
		}
		return null;
	}

	/**
	 * Check if an album exists
	 * 
	 * @param album Name of the album to check.
	 * @return True if album exists, false if album does not exist.
	 */
	public boolean albumExists(String album) {
		if (albums.containsKey(album)) {
			return true;
		}
		return false;
	}

	/**
	 * Create an album for the user.
	 * 
	 * @param album Name of the album to create.
	 * @return True if album was created, false if album exists.
	 */
	public boolean createAlbum(String album) {
		if (!albums.containsKey(album)) {
			albums.put(album, new Album(album));
			write();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Delete an album for a given user.
	 * 
	 * @param album Name of the album to delete.
	 * @return True if the album is deleted, false if the album does not exist.
	 */
	public boolean deleteAlbum(String album) {
		if (albums.containsKey(album)) {
			// To delete files on delete album
			// HashMap<String, Photo> photos = albums.get(album).getPhotos();
			// for (Map.Entry<String, Photo> entry : photos.entrySet()) {
			// new File(ctx.getFilesDir(),
			// entry.getValue().getFilename()).delete();
			// }
			albums.remove(album);
			write();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create an album for a given user.
	 * 
	 * @param album Name of the album to rename.
	 * @param newName Name for the album to be renamed to.
	 * @return True if the album is renamed, false if the album does not exist.
	 */
	public boolean renameAlbum(String album, String newName) {
		Album oldAlbum = albums.get(album);
		if (oldAlbum != null) {
			Album newAlbum = new Album(newName);
			HashMap<String, Photo> oldPhotos = oldAlbum.getPhotos();
			HashMap<String, Photo> newPhotos = oldPhotos;
			for (Photo p : oldPhotos.values()) {
				p.setParentAlbum(album);
			}
			newAlbum.setPhotos(newPhotos);
			albums.put(newName, newAlbum);
			albums.remove(album);
			write();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add a photo to an album for a given user.
	 * 
	 * @param filepath Filename of the photo.
	 * @param albumName Album that the photo should be added to.
	 * @return True if the photo is added, false if the photo is already in the
	 *         album.
	 */
	public boolean addPhotoToAlbum(String filepath, String albumName) throws FileNotFoundException,
			IOException {
		if (!new File(filepath).exists()) {
			throw new FileNotFoundException();
		}

		// store file

		String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
		File file = new File(ctx.getFilesDir(), filename);
		if (!file.exists()) {
			InputStream in = new FileInputStream(filepath);
			OutputStream out = new FileOutputStream(file);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}

		if (!albums.containsKey(albumName)) {
			return false;
		}
		Photo p = new Photo(filename);
		if (!albums.get(albumName).getPhotos().containsKey(filepath)) {
			p.setTags(new ArrayList<PhotoTag>());
			p.setParentAlbum(albumName);
			albums.get(albumName).getPhotos().put(filename, p);
			write();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Move a photo from on album to another album.
	 * 
	 * @param filename Filename of the photo.
	 * @param oldAlbumName The album where the photo is currently located.
	 * @param newAlbumName Album that the photo should be added to.
	 * @return True if the photo is moved, false if the oldAlbum or newAlbum is
	 *         not found or the .
	 */
	public boolean movePhoto(String filename, String oldAlbumName, String newAlbumName) {
		if (!albums.containsKey(oldAlbumName)) {
			return false;
		}
		if (!albums.containsKey(newAlbumName)) {
			return false;
		}
		if (albums.get(oldAlbumName).getPhotos().containsKey(filename)) {
			// if photo already exists in the new album, return silently without
			// doing anything
			if (!albums.get(newAlbumName).getPhotos().containsKey(filename)) {
				albums.get(newAlbumName).getPhotos()
						.put(filename, albums.get(oldAlbumName).getPhotos().get(filename));
			} else {
				write();
				return true;
			}
			albums.get(oldAlbumName).getPhotos().remove(filename);
			write();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Remove a photo from an album.
	 * 
	 * @param filename Filename of the photo.
	 * @param album The album where the photo is currently located.
	 * @return True if the photo is removed, false if the photo is not found in
	 *         the album. If the photo does not exist in any other albums, the file will be deleted.
	 */
	public boolean removePhotoFromAlbum(String filename, String album) {
		if (albums.get(album).getPhotos().containsKey(filename)) {
			HashMap<String, Photo> allPhotos = getAllPhotos();
			if (!allPhotos.containsKey(filename)) { // only delete photo file if no other albums contain the same photo
				 new File(ctx.getFilesDir(), filename).delete();
			}
			albums.get(album).getPhotos().remove(filename);
			write();
			return true;
		} else {
			return false;
		}
	}

	private HashMap<String, Photo> getAllPhotos() {
		HashMap<String, Photo> all = new HashMap<String, Photo>();
		for (Album a : this.albums.values()) {
			all.putAll(a.getPhotos());
		}
		return all;
	}

	/**
	 * Add a tag to a photo.
	 * 
	 * @param filename Filename of the photo.
	 * @param tagType Tag type to add.
	 * @param tagValue Tag value to add.
	 * @return True if the tag is added, false if the tag already exists.
	 */
	public boolean addTagToPhoto(String album, String filename, String tagType, String tagValue) {
		HashMap<String, Photo> photos = this.albums.get(album).getPhotos();
		if (photos.containsKey(filename)) { // if photo is in album

			PhotoTag t = new PhotoTag(tagType, tagValue);
			List<PhotoTag> tags = photos.get(filename).getTags();
			if (!tags.contains(t)) {
				photos.get(filename).addTag(t);
				write();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Delete a tag from a photo.
	 * 
	 * @param filename Filename of the photo.
	 * @param tagType Tag type to delete.
	 * @param tagValue Tag value to delete.
	 * @return True if the tag is deleted, false if the tag does not exist.
	 */
	public boolean deleteTagFromPhoto(String album, String filename, String tagType, String tagValue) {
		Photo photo = this.albums.get(album).getPhotos().get(filename);
		List<PhotoTag> tags = photo.getTags();
		PhotoTag t = new PhotoTag(tagType, tagValue);

		if (tags.isEmpty()) {
			return false;
		} else if (tags.contains(t)) {
			tags.remove(t);
			System.out.println("Deleted tag.");
			photo.setTags(tags);
			Album a = this.albums.get(album);
			this.albums.get(album).deletePhotoFromAlbum(photo, a);
			this.albums.get(album).addPhotoToAlbum(photo, a, photo.getFilename());
			write();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * List info for a photo.
	 * 
	 * @param filename Filename of the photo to get.
	 * @return Photo object for the given filename, null if photo does not
	 *         exist.
	 */
	public Photo listPhotoInfo(String filename) {
		for (Album album : albums.values()) { // albums
			if (album.getPhotos().containsKey(filename)) {
				return album.getPhotos().get(filename);
			}
		}
		return null;
	}

	/**
	 * Get photos by tag.
	 * 
	 * @param tags Hashmap of tags to get photos for, where K = tagType, V =
	 *            tagValue
	 * @return List of photos with matching tags.
	 */
	public List<Photo> getPhotosByTag(List<PhotoTag> tags) {
		List<Photo> matching = new ArrayList<Photo>();
		for (Album album : albums.values()) { // albums
			for (Photo photo : album.getPhotos().values()) { // photos
				List<PhotoTag> photoTags = photo.getTags();
				for (PhotoTag tag : tags) {
					if (tag.getTagType().isEmpty()) { // match any tag type
						for (PhotoTag t : photoTags) { // iterate through all of
														// the photo's tags and
														// compare tagValue
							if (t.getTagValue().equals(tag.getTagValue())) {
								matching.add(photo);
							}
						}
					} else {
						if (photoTags.contains(tag)) {
							matching.add(photo);
						}
					}
				}
			}
		}
		return matching;
	}

	/**
	 * Get a list of albums that contain a given photo.
	 * 
	 * @param filename Filename of the photo to search for.
	 * @return List of album names that the photo is in.
	 */
	public List<String> findParentAlbumsOfPhoto(String filename) {

		List<String> matching = new ArrayList<String>();
		for (Album album : this.albums.values()) { // albums
			for (Photo photo : album.getPhotos().values()) { // photos
				if (photo.getFilename().equalsIgnoreCase(filename)) {
					matching.add(album.getAlbumName());
				}
			}
		}
		matching.removeAll(Arrays.asList("", null));
		Collections.sort(matching);
		return matching;
	}

	public List<PhotoTag> getTags() {

		List<PhotoTag> tags = new ArrayList<PhotoTag>();
		for (Album album : this.albums.values()) { // albums
			for (Photo photo : album.getPhotos().values()) { // photos
				tags.addAll(photo.getTags());
			}
		}
		return tags;
	}

	public List<PhotoTag> getTagsForPhoto(String album, String filename) {
		return this.albums.get(album).getPhotos().get(filename).getTags();
	}

}