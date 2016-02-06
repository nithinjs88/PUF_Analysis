package njs.model;

import java.io.Serializable;

import njs.readData.QueryConstants;
import njs.utils.MyConstants;

public class ShiftAndCorrInfoWithDevAndTryInfo extends ShiftAndCorrInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8706155003349586213L;
	public int devId1;
	public int devId2;
	public int try1;
	public int try2;
	
	public String toString(String queryType) {
		StringBuilder builder = new StringBuilder();
		if(queryType.equals(QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS) || 
			queryType.equals(QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS)) {
			builder.append("Max Pearson Corr ");
			builder.append(MyConstants.NEW_LINE);
			if(queryType.equals(QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS)) {
				builder.append("Device: ");
				builder.append(devId1);
			} else {
				builder.append("Device 1: ");
				builder.append(devId1);
				builder.append(", Device 2: ");
				builder.append(devId2);
			}
			builder.append(MyConstants.NEW_LINE);
			builder.append("Max Pearson Corr: ");
			builder.append(corr);
			builder.append(",Try 1: ");
			builder.append(try1);
			builder.append(",Try 2:");
			builder.append(try2);
			builder.append(",Shift:");
			builder.append(shift);
		}
		return builder.toString();
	}
}
