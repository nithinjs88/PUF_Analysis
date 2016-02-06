package njs.model;

import java.util.HashMap;
import java.util.Map;

import njs.readData.EntryPoint;

public class DeviceDataToAnalyze {
	public double[][] deviceOutput;
	public Map<Integer, double[][]> lastBitsVsOutput = new HashMap<Integer, double[][]>();
	
	public double[][] getLastBitInfo() {
		return getLastBitInfo(EntryPoint.NO_OF_BITS);
	}

	public double[][] getLastBitInfo(int nolastBits) {
		if(nolastBits == EntryPoint.NO_OF_BITS)
			return deviceOutput;
		double[][] lastBitOutput = lastBitsVsOutput.get(nolastBits);
		if(lastBitOutput == null) {
			return populateLastBitInfo(nolastBits);
		}
		return lastBitOutput;
	}
	
	public double[][] populateLastBitInfo(int noOfLastBits) {
		int mod = (int) Math.pow(2, noOfLastBits);
		double[][] lastBitArray =  new double[EntryPoint.NO_OF_VALID_TRIES][EntryPoint.NO_OF_VALID_VALS];
		for (int i = 0; i < EntryPoint.NO_OF_VALID_TRIES; i++) {
			for (int j = 0; j < EntryPoint.NO_OF_VALID_VALS; j++) {
				lastBitArray[i][j] = deviceOutput[i][j]%mod;
			}
		}
		lastBitsVsOutput.put(noOfLastBits, lastBitArray);
		return lastBitArray;
	}
}
