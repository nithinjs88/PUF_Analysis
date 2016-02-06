package njs.model;

import java.io.Serializable;

public class TwoDeviceIdWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3782744502503970706L;
	public int devId1;
	public int devId2;
	@Override
	public int hashCode() {
		return devId1*31 + devId2*13;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TwoDeviceIdWrapper) {
			TwoDeviceIdWrapper other = (TwoDeviceIdWrapper) obj;
			return (devId1 == other.devId1 && devId2 == other.devId2)
					||(devId1 == other.devId2 && devId2 == other.devId1);
		}
		return false;
	}
	
	
}
