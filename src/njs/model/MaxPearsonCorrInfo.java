package njs.model;

import java.io.Serializable;

import njs.readData.QueryConstants;
import njs.utils.MyConstants;
/**
 * Try indices start with 1
 * @author user
 *
 */
public class MaxPearsonCorrInfo implements IPearsonInfo,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1644375678464081903L;

	/**
	 * here devices can be same
	 */
	public PearsonInfoDevice maxPearsonInfo = new PearsonInfoDevice();
	
	/**
	 * here devices can be different
	 */
	public PearsonInfoDevice maxPearsonInfoDiffDev = new PearsonInfoDevice();
	
	public MaxPearsonCorrInfo() {
		maxPearsonInfo.maxPearsonCorr = Double.MIN_VALUE;
		maxPearsonInfoDiffDev.maxPearsonCorr = Double.MIN_VALUE;
	}
	/*
	public int deviceId1ForMax;
	public int deviceId2ForMax;
	public int try1ForMax;
	public int try2ForMax;
	public Double maxPearsonCorr;
	
	public int deviceId1ForMaxDiffDev;
	public int deviceId2ForMaxDiffDev;
	public int try1ForMaxDiffDev;
	public int try2ForMaxDiffDev;
	public Double maxPearsonCorrDiffDev;
	*/
	/*
	@Override
	public String toString(String queryType) {
		StringBuilder builder = new StringBuilder();
		if(queryType.equals("ANALYSE_PEARSON_ALL_DEV")) {
			StringBuilder buf = new StringBuilder();
			buf.append("======= When devices can be same================");
			builder.append("Dev 1: ");
			builder.append(deviceId1ForMax);
			builder.append(MyConstants.NEW_LINE);
			builder.append("Dev 2: ");
			builder.append(deviceId2ForMax);
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
			
			builder.append(MyConstants.NEW_LINE);
			
			buf.append("======= When devices are different ================");
			builder.append("Dev 1: ");
			builder.append(deviceId1ForMaxDiffDev);
			builder.append(MyConstants.NEW_LINE);
			builder.append("Dev 2: ");
			builder.append(deviceId2ForMaxDiffDev);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Try For Dev 1: ");
			builder.append(try1ForMaxDiffDev);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Try For Dev 2: ");
			builder.append(try2ForMaxDiffDev);
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("Max Pearson Corr: ");
			builder.append(maxPearsonCorrDiffDev);
			builder.append(MyConstants.NEW_LINE);
		}
		return builder.toString();
	}
	*/

	@Override
	public String toString(String queryType) {
		StringBuilder builder = new StringBuilder();
		if(QueryConstants.ANALYSE_PEARSON_ALL_DEV.equals(queryType)) {
			builder.append("======= When devices can be same================");
			builder.append(MyConstants.NEW_LINE);
			builder.append(maxPearsonInfo.toString(queryType));
			builder.append(MyConstants.NEW_LINE);
			
			builder.append("======= When devices are different ================");
			builder.append(MyConstants.NEW_LINE);
			builder.append(maxPearsonInfoDiffDev.toString(queryType));
			builder.append(MyConstants.NEW_LINE);
		}
		return builder.toString();
	}
	
}
