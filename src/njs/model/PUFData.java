package njs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import njs.readData.EntryPoint;

public class PUFData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2544582530230378066L;
	
	public int noOfLastBits;
	public int noOfDevices;
	public double[] input;
	public List<DeviceInfo> deviceInfos = new ArrayList<DeviceInfo>();
	public Map<Integer, DeviceInfo> idVsDevInfoMap = new LinkedHashMap<Integer, DeviceInfo>();
	public Map<DeviceInfo, double[][]> deviceOutputs = new LinkedHashMap<DeviceInfo, double[][]>();
	private Map<Integer,Map<DeviceInfo, double[][]>> lastBitsMap 
				= new LinkedHashMap<Integer,Map<DeviceInfo, double[][]>>();
	
	public void addDeviceOuptut(DeviceInfo deviceInfo, double[][] output) {
		deviceOutputs.put(deviceInfo, output);
		
	}
	
	public void populateLastBitInfo() {
		getLastBitInfo(noOfLastBits);
	}

	public Map<DeviceInfo, double[][]> getLastBitInfo(int nolastBits) {
		if(nolastBits == EntryPoint.NO_OF_BITS)
			return deviceOutputs;
		Map<DeviceInfo, double[][]> map = lastBitsMap.get(nolastBits);
		if(map == null) {
			return populateLastBitInfo(nolastBits);
		}
		return map;
	}
	
	public Map<DeviceInfo, double[][]> populateLastBitInfo(int noOfLastBits) {
		int mod = (int) Math.pow(2, noOfLastBits);
		Map<DeviceInfo, double[][]> lastBitMap = new LinkedHashMap<DeviceInfo, double[][]>();
		Set<Entry<DeviceInfo,double[][]>> entrySet = deviceOutputs.entrySet();
		for (Entry<DeviceInfo, double[][]> entry : entrySet) {
			double[][] lastBitArray =  new double[EntryPoint.NO_OF_VALID_TRIES][EntryPoint.NO_OF_VALID_VALS];
			double[][] deviceOp = entry.getValue();
			for (int i = 0; i < EntryPoint.NO_OF_VALID_TRIES; i++) {
				for (int j = 0; j < EntryPoint.NO_OF_VALID_VALS; j++) {
					lastBitArray[i][j] = deviceOp[i][j]%mod;
				}
			}
			lastBitMap.put(entry.getKey(), lastBitArray);
		}
		lastBitsMap.put(noOfLastBits, lastBitMap);
		return lastBitMap;
	}
}
