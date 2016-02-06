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
public class PearsonInfoDevice implements IPearsonInfo, Comparable<PearsonInfoDevice>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4100905450523346203L;
	
	public int deviceId1;
	public int deviceId2;
	public int try1ForMax;
	public int try2ForMax;
	public Double maxPearsonCorr;
	public Double avgAbsoluteCorr;
	public Map<Integer, Double> lastBitsPearsonCorrToMax = new LinkedHashMap<Integer, Double>();
	
	@Override
	public String toString(String queryType) {
		StringBuilder builder = new StringBuilder();
		if(QueryConstants.ANALYSE_PEARSON_DEV.equals(queryType) ||
				QueryConstants.ANALYSE_PEARSON_ALL_DEV.equals(queryType)) {
			builder.append("Dev 1: ");
			builder.append(deviceId1);
			builder.append(MyConstants.NEW_LINE);
			builder.append("Dev 2: ");
			builder.append(deviceId2);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Try For Dev 1: ");
			builder.append(try1ForMax);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Try For Dev 2: ");
			builder.append(try2ForMax);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Max Pearson Corr: ");
			builder.append(maxPearsonCorr);
			builder.append(MyConstants.NEW_LINE);
			
			if(QueryConstants.ANALYSE_PEARSON_DEV.equals(queryType)) {
				builder.append("Avg Absolute Pearson Corr: ");
				builder.append(avgAbsoluteCorr);
				builder.append(MyConstants.NEW_LINE);
			}
			
			builder.append("Last Bit Pearson Correlation Corr to Max");
			builder.append(lastBitsPearsonCorrToMax.toString());
		}
		return builder.toString();
	}

	@Override
	public int compareTo(PearsonInfoDevice o) {
		int compareTo = maxPearsonCorr.compareTo(o.maxPearsonCorr);
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
