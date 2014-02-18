package com.orange.ucp.dto;

public class Activity {
	private static final long serialVersionUID = 1L;

	private String id;
	private String device;
	private String value;
	private String time;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int _hash = 0;
		_hash += (getId() != null ? getId().hashCode() : 0);
		return _hash;
	}

	@Override
	public boolean equals(Object _object) {
		if (!(_object instanceof Device)) {
			return false;
		}
		Device _other = (Device) _object;
		if ((this.getId() == null && _other.getId() != null)
				|| (this.getId() != null && !this.getId()
						.equals(_other.getId()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Activity[id=" + getId() + "]" + serialVersionUID;
	}

}
