package com.example.androidphotoalbum;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * @author Eytan Biala
 */
public class StringDisplayUtil {

	public static String getPhotoFilenameWithoutExtension(String s) {
		String name = new File(s).getName();
		String[] extensions = {"jpg", "png", "gif", "bmp", "tiff", "exif"};
		for (int i = 0; i < extensions.length; i++) {
			name = name.replace("." + extensions[i], "");
			name = name.replace("." + extensions[i].toUpperCase(Locale.getDefault()), "");
		}
		return name;
	}

	public static String getAlbumTitle(Album a) {

		String albumName = a.getAlbumName();
		StringBuilder b = new StringBuilder(albumName);
		int numPhotos = a.getPhotos().size();

		b.append(" - ");
		if (numPhotos == 0) {
			b.append("No photos");
		} else if (numPhotos > 1) {
			b.append(numPhotos);
			b.append(" photos");
		} else {
			b.append(numPhotos);
			b.append(" photo");
		}

		return b.toString();
	}

	public static String getPhotoFrameTitle(Photo p, Album album) {
		StringBuilder b = new StringBuilder(album.getAlbumName());
		b.append(" - ");
		b.append(p.getPhotoFilenameWithoutExtension());

		return b.toString();
	}

	public static String formatTagList(List<PhotoTag> tags) {
		StringBuilder b = new StringBuilder();

		int size = tags.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				PhotoTag tag = tags.get(i);
				b.append(tag.getTagType());
				b.append(": ");
				b.append(tag.getTagValue());
				if (i != size - 1) {
					b.append(", ");
				}
			}
		} else {
			b.append("None.");
		}

		return b.toString();
	}
}
