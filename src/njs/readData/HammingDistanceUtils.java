package njs.readData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import njs.model.DeviceInfo;
import njs.model.PUFData;
import njs.utils.FileUtils;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class HammingDistanceUtils {
	private static final StandardDeviation SD_INSTANCE = new StandardDeviation();
	private static final Mean MEAN_INSTANCE = new Mean();

	public static void hamDistanceItrAllDev(PUFData pufData, String meanFileStr, String sdFileStr) {
		Double[] meanArray = new Double[EntryPoint.NO_OF_VALID_TRIES];
		Double[] sdArray = new Double[EntryPoint.NO_OF_VALID_TRIES];
		
		for (int i = 0; i < EntryPoint.NO_OF_VALID_TRIES; i++) {
			Double[] meanAndSDForATryForAllDev = getMeanAndSDForATryForAllDev(pufData, i);
			meanArray[i] = meanAndSDForATryForAllDev[0];
			sdArray[i] = meanAndSDForATryForAllDev[1];
		}
		File meanFile = new File(EntryPoint.FOLDER_FILE,meanFileStr);
		if(!meanFile.exists()) {
			try {
				meanFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String csv = WriteDataUtils.getCSVSingleDim(meanArray);
		try {
			FileUtils.writeToFile(meanFile, csv);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		File sdFile = new File(EntryPoint.FOLDER_FILE,sdFileStr);
		if(!sdFile.exists()) {
			try {
				sdFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		csv = WriteDataUtils.getCSVSingleDim(sdArray);
		try {
			FileUtils.writeToFile(sdFile, csv);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param pufData
	 * @param deviceIDs
	 * @param tries in MATLAB Array noattaion. So reduce idx by 1
	 * @return
	 */
	public static Double[] getMeanAndSDForDiffDevDifferentTry(PUFData pufData,List<Integer> deviceIDs, List<Integer> tries) {
		int noOfLastBits = pufData.noOfLastBits;
		int devs = deviceIDs.size();
		double[][] outputs = new double[devs][EntryPoint.NO_OF_VALID_VALS];
		Map<DeviceInfo, double[][]> map = pufData.getLastBitInfo(noOfLastBits);
		Map<Integer, DeviceInfo> idVsDevInfoMap = pufData.idVsDevInfoMap;
		for (int i = 0; i < devs; i++) {
			outputs[i] = map.get(idVsDevInfoMap.get(deviceIDs.get(i)))[tries.get(i) - 1];
		}
		return getMeanAndSD(devs, noOfLastBits, outputs);
	}
	/**
	 * 
	 * @param pufData
	 * @param tryNo - starts from 0
	 * @return
	 */
	public static Double[] getMeanAndSDForATryForAllDev(PUFData pufData, int tryNo) {
		
		int devs = pufData.noOfDevices;
		int noOfLastBits = pufData.noOfLastBits;
		double[][] outputs = new double[devs][EntryPoint.NO_OF_VALID_VALS];
		
		Map<DeviceInfo, double[][]> map = pufData.getLastBitInfo(noOfLastBits);
		List<DeviceInfo> deviceInfos = pufData.deviceInfos;
		for (int i = 0; i < outputs.length; i++) {
			outputs[i] = map.get(deviceInfos.get(i))[tryNo];
		}
		
		
		
		return getMeanAndSD(devs, noOfLastBits, outputs);
	}

	public static Double[] getMeanAndSD(int noOfDev, int noOfLastBits,
			double[][] outputs) {
		double []hamDistPercentage = new double[EntryPoint.NO_OF_VALID_VALS];
		double multFactorPerc = ((double)(2*100))/((double)((noOfDev*(noOfDev-1)*noOfLastBits)));
		double val1,val2;
		int val1AsInt, val2AsInt;
		for (int j = 0; j < EntryPoint.NO_OF_VALID_VALS; j++) {
			long sumForDist = 0;
			for (int deviceItr1 = 0; deviceItr1 < noOfDev; deviceItr1++) {
				for (int deviceItr2 = 0; deviceItr2 < noOfDev; deviceItr2++) {
					if(deviceItr1 < deviceItr2) {
						val1 = outputs[deviceItr1][j];
						val2 = outputs[deviceItr2][j];
						val1AsInt = (int)val1;
						val2AsInt = (int)val2;
						sumForDist = sumForDist + getHamDistance(val1AsInt, val2AsInt, noOfLastBits);
					}
				}
			}
			hamDistPercentage[j] = sumForDist*multFactorPerc;
		}
		Double mean = MEAN_INSTANCE.evaluate(hamDistPercentage);
		Double sd = SD_INSTANCE.evaluate(hamDistPercentage, mean);
		return new Double[]{mean,sd};
	}
	
	public static int getHamDistance(int val1, int val2, int noOfLastBits) {
		int hamDist = 0,mask = 1,pos1,pos2;
		for (int i = 1; i <= noOfLastBits; i++) {
			pos1 = mask & val1;
			pos2 = mask & val2;
			if(pos1 != pos2)
	            hamDist = hamDist + 1;
			mask = mask << 1;
		}
		return hamDist;
	}
}
