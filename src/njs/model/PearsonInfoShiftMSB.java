package njs.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import njs.readData.QueryConstants;
import njs.utils.MyConstants;
/**
 * Try indices start with 1
 * @author user
 *
 */
public class PearsonInfoShiftMSB implements IPearsonInfo,Comparable<PearsonInfoShiftMSB>,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1312986483216365650L;
	
	public int deviceId1;
	public int deviceId2;
	public int try1;
	public int try2;
	public Double pearsonCorr;
	public Double maxPearsonCorrShift;
	public int shift;
	public Map<Integer, Double> lastBitsPearsonShifts = new LinkedHashMap<Integer, Double>();
	
	@Override
	public String toString(String queryType) {
		StringBuilder builder = new StringBuilder();
		if(QueryConstants.ANALYSE_PEARSON_DEV_TRY.equals(queryType)) {
			builder.append("Dev 1: ");
			builder.append(deviceId1);
			builder.append(MyConstants.NEW_LINE);
			builder.append("Dev 2: ");
			builder.append(deviceId2);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Try For Dev 1: ");
			builder.append(try1);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Try For Dev 2: ");
			builder.append(try2);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Pearson Corr: ");
			builder.append(pearsonCorr);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Max Pearson Corr: ");
			builder.append(maxPearsonCorrShift);
			builder.append(", Shift: ");
			builder.append(shift);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Last Bit Pearson Correlation Corr to Max Shift");
			builder.append(lastBitsPearsonShifts.toString());
		}
		return builder.toString();
	}

	@Override
	public int compareTo(PearsonInfoShiftMSB o) {
		int compareTo = maxPearsonCorrShift.compareTo(o.maxPearsonCorrShift);
		if(compareTo == 0) {
			if(o.deviceId1 == o.deviceId2) {
				return 1;
			}
			if(deviceId1 == deviceId2) {
				return -1;
			}
			return 0;
		}
		return compareTo;
	}
}
