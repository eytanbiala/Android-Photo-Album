package com.example.androidphotoalbum;

import java.io.Serializable;

/**
 * @author Eytan Biala
 */
public class PhotoTag implements Serializable {

	private static final long serialVersionUID = 1L;
	String tagType;
	String tagValue;

	public PhotoTag(String tagType, String tagValue) {
		super();
		this.tagType = tagType;
		this.tagValue = tagValue;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tagType == null) ? 0 : tagType.hashCode());
		result = prime * result + ((tagValue == null) ? 0 : tagValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PhotoTag))
			return false;
		PhotoTag other = (PhotoTag) obj;
		if (tagType == null) {
			if (other.tagType != null)
				return false;
		} else if (!tagType.equals(other.tagType))
			return false;
		if (tagValue == null) {
			if (other.tagValue != null)
				return false;
		} else if (!tagValue.equals(other.tagValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return tagType + ": " + tagValue;
	}
}
