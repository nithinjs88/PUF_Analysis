package njs.model;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4419983757237184023L;
	//less than or equal to number of devices
	public int deviceNo = 0;
	public int deviceId = 0;
	
	public DeviceInfo() {
		
	}
	
	public DeviceInfo(int deviceId) {
		this.deviceId = deviceId;
	}
	public DeviceInfo(int deviceNo, int deviceId) {
		this.deviceId = deviceId;
		this.deviceNo = deviceNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceId;
		result = prime * result + deviceNo;
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
		DeviceInfo other = (DeviceInfo) obj;
		if (deviceId != other.deviceId)
			return false;
		if (deviceNo != other.deviceNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[no=" + deviceNo + ",id=" + deviceId
				+ "]";
	}
	
	
	
	
}
