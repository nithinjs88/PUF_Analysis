package njs.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import njs.readData.QueryConstants;
import njs.utils.MyConstants;
@XmlRootElement
public class DiffBtwnCorrelationData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6413316736338561103L;

	public LinkedHashMap<Integer, LinkedHashMap<Integer,CorrDiffInfo>> lastBitsVsdevIdVsDiffInfo = new LinkedHashMap<Integer, LinkedHashMap<Integer,CorrDiffInfo>>();
	
	public LinkedHashMap<Integer, LinkedHashMap<Integer, ShiftAndCorrInfoWithDevAndTryInfo>> lastBitsVsSameDeviceMaxInfo
	 = new LinkedHashMap<Integer, LinkedHashMap<Integer,ShiftAndCorrInfoWithDevAndTryInfo>>();
	
	public LinkedHashMap<Integer, LinkedHashMap<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo>> lastBitsVsDiffDeviceMaxInfo
	 = new LinkedHashMap<Integer, LinkedHashMap<TwoDeviceIdWrapper,ShiftAndCorrInfoWithDevAndTryInfo>>();
	
	public void populateCorrDiffInfoForAllDev(int noOfLastBits) {
		LinkedHashMap<Integer,CorrDiffInfo> mapForCorrDiffInfo = new LinkedHashMap<Integer,CorrDiffInfo>();
		lastBitsVsdevIdVsDiffInfo.put(noOfLastBits, mapForCorrDiffInfo);
		
		Map<Integer, ShiftAndCorrInfoWithDevAndTryInfo> map = lastBitsVsSameDeviceMaxInfo.get(noOfLastBits);
		Map<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo> map2 = lastBitsVsDiffDeviceMaxInfo.get(noOfLastBits);
		Set<Entry<Integer,ShiftAndCorrInfoWithDevAndTryInfo>> entrySet = map.entrySet();
		int noOfDevs = entrySet.size();
		for (Iterator<Entry<Integer,ShiftAndCorrInfoWithDevAndTryInfo>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<Integer, ShiftAndCorrInfoWithDevAndTryInfo> entry = (Entry<Integer, ShiftAndCorrInfoWithDevAndTryInfo>) iterator
					.next();
			Integer devId = entry.getKey();
			CorrDiffInfo corrDiffInfo = new CorrDiffInfo();
			corrDiffInfo.maxCornSameDev = entry.getValue().corr;
			
			double sumOfDiffCorInfo = 0.0;
			Set<Entry<TwoDeviceIdWrapper,ShiftAndCorrInfoWithDevAndTryInfo>> entrySet2 = map2.entrySet();
			for (Iterator<Entry<TwoDeviceIdWrapper,ShiftAndCorrInfoWithDevAndTryInfo>> iterator2 = entrySet2.iterator(); iterator2.hasNext();) {
				Entry<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo> entry2 = (Entry<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo>) iterator2
						.next();
				TwoDeviceIdWrapper key = entry2.getKey();
				if(key.devId1 == devId || key.devId2 == devId) {
					sumOfDiffCorInfo = sumOfDiffCorInfo + entry2.getValue().corr;
				}
				
			}
			corrDiffInfo.avgMaxDiffDev = sumOfDiffCorInfo/(noOfDevs - 1);
			corrDiffInfo.diffInCorn = corrDiffInfo.maxCornSameDev - corrDiffInfo.avgMaxDiffDev;
			mapForCorrDiffInfo.put(devId, corrDiffInfo);
		}
		
	}
	
	public String getMaxInfoForDevices(int noOfLastBits) {
		StringBuilder builder = new StringBuilder();
		builder.append("Last Bits: ");
		builder.append(noOfLastBits);
		builder.append(MyConstants.NEW_LINE);
		Map<Integer, CorrDiffInfo> map = lastBitsVsdevIdVsDiffInfo.get(noOfLastBits);
		if(map != null) {
			Set<Entry<Integer,CorrDiffInfo>> entrySet = map.entrySet();
			for (Entry<Integer, CorrDiffInfo> entry : entrySet) {
				builder.append("Device ID : ");
				builder.append(entry.getKey());
				builder.append(MyConstants.NEW_LINE);
				builder.append(entry.getValue().toString());
				builder.append(MyConstants.NEW_LINE);
			}
		}
		builder.append(MyConstants.NEW_LINE);
		builder.append("====================================================");
		return builder.toString();
	}
	public String getFullDetailedInfo(int noOfLastBits) {
		StringBuilder builder = new StringBuilder();
		builder.append(getMaxInfoForDevices(noOfLastBits));
		builder.append("====================================================");
		builder.append(getPairWiseMaxCornStr(noOfLastBits));
		return builder.toString();
	}
	
	
	public String getPairWiseMaxCornStr(int noOfLastBits) {
		StringBuilder builder = new StringBuilder();
		builder.append(MyConstants.NEW_LINE);
		builder.append("----------Same Device----------------");
		builder.append(MyConstants.NEW_LINE);
		
		Map<Integer, ShiftAndCorrInfoWithDevAndTryInfo> map = lastBitsVsSameDeviceMaxInfo.get(noOfLastBits);
		if(map != null) {
			Collection<ShiftAndCorrInfoWithDevAndTryInfo> values = map.values();
			for (ShiftAndCorrInfoWithDevAndTryInfo shiftAndCorrInfoWithDevAndTryInfo : values) {
				builder.append(shiftAndCorrInfoWithDevAndTryInfo.toString(QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS));
				builder.append(MyConstants.NEW_LINE);
			}
		}
		builder.append(MyConstants.NEW_LINE);
		builder.append("----------Diff Devices----------------");
		builder.append(MyConstants.NEW_LINE);
		Map<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo> map2 = lastBitsVsDiffDeviceMaxInfo.get(noOfLastBits);
		if(map2 != null) {
			Collection<ShiftAndCorrInfoWithDevAndTryInfo> values = map2.values();
			for (ShiftAndCorrInfoWithDevAndTryInfo shiftAndCorrInfoWithDevAndTryInfo : values) {
				builder.append(shiftAndCorrInfoWithDevAndTryInfo.toString(QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS));
				builder.append(MyConstants.NEW_LINE);
			}
		}
		return builder.toString();
	}
}
