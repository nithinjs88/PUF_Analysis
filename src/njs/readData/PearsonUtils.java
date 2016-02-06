package njs.readData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import njs.model.DeviceInfo;
import njs.model.DiffBtwnCorrelationData;
import njs.model.MaxPearsonCorrInfo;
import njs.model.PUFData;
import njs.model.PearsonInfoDevice;
import njs.model.PearsonInfoShiftMSB;
import njs.model.ShiftAndCorrInfo;
import njs.model.ShiftAndCorrInfoWithDevAndTryInfo;
import njs.model.TwoDeviceIdWrapper;
import njs.utils.FileUtils;
import njs.utils.MyConstants;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
/**
 * All try indices start with 1
 * @author user
 *
 */
public class PearsonUtils {
	
	public static final int MIN_LAST_BITS_DIFF_CORN = 1;
	public static final int MAX_LAST_BITS_DIFF_CORN = EntryPoint.NO_OF_BITS;
	
	public static final PearsonsCorrelation INSTANCE = new PearsonsCorrelation();
	/*public static void main(String[] args) {
		double[]arr1 = {1,2};
		double[]arr2 = {2,1};
		double correlation = new PearsonsCorrelation().correlation(arr1, arr2);
		System.out.println(correlation);
	}*/

	public static void pearsonCoeffBetweenDev(PUFData pufData, String dev1,
			String dev2, String fileName, int noOfLastBits) {
		Integer devId1 = Integer.parseInt(dev1);
		Integer devId2 = Integer.parseInt(dev2);
		Double[][] pearsonCoeff = getPearsonCorrelationMatrix(pufData,
				noOfLastBits, devId1, devId2);
		String csv = WriteDataUtils.getCSVDoubleDim(pearsonCoeff);
		WriteDataUtils.writeToFile(fileName, csv);
	}

	public static Double[][] getPearsonCorrelationMatrix(PUFData pufData,
			int noOfLastBits, Integer devId1, Integer devId2) {
		DeviceInfo deviceInfo1 = pufData.idVsDevInfoMap.get(devId1);
		DeviceInfo deviceInfo2 = pufData.idVsDevInfoMap.get(devId2);
		Map<DeviceInfo, double[][]> map = pufData.getLastBitInfo(noOfLastBits);
		double[][] dev1Arr = map.get(deviceInfo1);
		double[][] dev2Arr = map.get(deviceInfo2);
		Double[][] pearsonCoeff = new Double[EntryPoint.NO_OF_VALID_TRIES][EntryPoint.NO_OF_VALID_TRIES];
		for (int i = 0; i < EntryPoint.NO_OF_VALID_TRIES; i++) {
			for (int j = 0; j < EntryPoint.NO_OF_VALID_TRIES; j++) {
				if(i <= j) {
					pearsonCoeff[i][j] = INSTANCE.correlation(dev1Arr[i], dev2Arr[j]);
				} else {
					pearsonCoeff[i][j] = pearsonCoeff[j][i];
				}
			}
		}
		return pearsonCoeff;
	}
	
	public static double pearsonCoeffDevAndTry(PUFData pufData, Integer dev1, Integer try1,
											Integer dev2, Integer try2, int noOfLastBits) {
		DeviceInfo deviceInfo1 = pufData.idVsDevInfoMap.get(dev1);
		DeviceInfo deviceInfo2 = pufData.idVsDevInfoMap.get(dev2);
		Map<DeviceInfo, double[][]> map = pufData.getLastBitInfo(noOfLastBits);
		double[][] dev1Arr = map.get(deviceInfo1);
		double[][] dev2Arr = map.get(deviceInfo2);
		return INSTANCE.correlation(dev1Arr[try1-1], dev2Arr[try2 - 1]);
	}
	
	public static PearsonInfoShiftMSB pearsonShiftDevTry(PUFData pufData, int deviceId1, int try1 , int
			deviceId2, int try2, int noOfLastBits) {
		PearsonInfoShiftMSB infoShift = new PearsonInfoShiftMSB();
		infoShift.deviceId1 = deviceId1;
		infoShift.try1 = try1;
		infoShift.deviceId2 = deviceId2;
		infoShift.try2 = try2;
		double pearsonCoeff = pearsonCoeffDevAndTry(pufData, deviceId1, try1, deviceId2, try2, noOfLastBits);
		infoShift.pearsonCorr = pearsonCoeff;
		
		DeviceInfo deviceInfo1 = pufData.idVsDevInfoMap.get(deviceId1);
		DeviceInfo deviceInfo2 = pufData.idVsDevInfoMap.get(deviceId2);
		double[] dev1OpTry = pufData.deviceOutputs.get(deviceInfo1)[try1 - 1];
		double[] dev2OpTry = pufData.deviceOutputs.get(deviceInfo2)[try2 - 1];
		boolean ignoreZeroShift = ((deviceId1 == deviceId2) && (try1 == try2));
		
		ShiftAndCorrInfo maxCorrelationByShifting = getMaxCorrelationByShifting(dev1OpTry, dev2OpTry, ignoreZeroShift );
		infoShift.maxPearsonCorrShift = maxCorrelationByShifting.corr;
		infoShift.shift = maxCorrelationByShifting.shift;
		
		double[] dev1OpTryLastBits, dev2OpTryLastBits;
		for (int lastBits: EntryPoint.BITS_TO_ANALYSE) {
			Map<DeviceInfo, double[][]> lastBitInfo = pufData.getLastBitInfo(lastBits);
			dev1OpTryLastBits = lastBitInfo.get(deviceInfo1)[try1 - 1];
			dev2OpTryLastBits = lastBitInfo.get(deviceInfo2)[try2 - 1];
			List<double[]> shiftedOuptutsToCompare = getShiftedOuptutsToCompare(dev1OpTryLastBits, dev2OpTryLastBits, infoShift.shift);
			pearsonCoeff = PearsonUtils.INSTANCE.correlation(shiftedOuptutsToCompare.get(0),
					shiftedOuptutsToCompare.get(1));
			infoShift.lastBitsPearsonShifts.put(lastBits, pearsonCoeff);
		}
		
		return infoShift;
	}
	
	public static ShiftAndCorrInfo getMaxCorrelationByShifting(double[] device1Op, double[] device2Op,
			boolean ignoreZeroShift) {
		ShiftAndCorrInfo info = new ShiftAndCorrInfo();
		info.corr = Double.MIN_VALUE;
		List<double[]> shiftedOuptutsToCompare;
		double pearsonCoeff;
		for (int i = EntryPoint.MIN_SHIFT; i <= EntryPoint.MAX_SHIFT; i++) {
			if(i != 0 || !ignoreZeroShift) {
				shiftedOuptutsToCompare = getShiftedOuptutsToCompare(device1Op, device2Op, i);
				pearsonCoeff = PearsonUtils.INSTANCE.correlation(shiftedOuptutsToCompare.get(0),
						shiftedOuptutsToCompare.get(1));
				if(pearsonCoeff > info.corr) {
					info.corr = pearsonCoeff;
					info.shift = i;
				}
			}
		}
		return info;
	}
	
	public static List<double []> getShiftedOuptutsToCompare(double[] device1Op, double[] device2Op,int shift) {
		List<double []> list = new ArrayList<double[]>();
		int length = Math.min(device1Op.length, device2Op.length) - Math.abs(shift);
		if(length > 0) {
			double[] cmp1 = new double[length];
			double[] cmp2 = new double[length];
			list.add(cmp1);
			list.add(cmp2);
			int startItr1 = 0;
			int startItr2 = 0;
			if(shift < 0) {
				startItr2 = (-shift); 
			} else if(shift > 0) {
				startItr1 = shift; 
			}
			for (int i = 0; i < length; i++,startItr1++,startItr2++) {
				cmp1[i] = device1Op[startItr1];
				cmp2[i] = device2Op[startItr2];
			}
		}
		return list;
	}
	
	public static PearsonInfoDevice getPearsonInfoForDevices(PUFData pufData, int devId1, int devId2) {
		PearsonInfoDevice pearsonInfo = new PearsonInfoDevice();
		pearsonInfo.deviceId1 = devId1;
		pearsonInfo.deviceId2 = devId2;
		Double[][] pearsonCorrelationMatrix = getPearsonCorrelationMatrix(pufData, EntryPoint.NO_OF_BITS, devId1, devId2);
		boolean ignoreSameTry = (devId1 == devId2);
		int noOfValsProcessed = 0;
		double sumOfAbsValsProcessed = 0.0, maxPearsonCorr = Double.MIN_VALUE, pearsonCorrItr;
		int tryDev1Max = -1, tryDev2Max = -1;
		for (int i = 0; i < EntryPoint.NO_OF_VALID_TRIES; i++) {
			for (int j = 0; j < EntryPoint.NO_OF_VALID_TRIES; j++) {
				if(i <j || ((i == j) && !ignoreSameTry)) {
					pearsonCorrItr = pearsonCorrelationMatrix[i][j];
					double abs = Math.abs(pearsonCorrItr);
					sumOfAbsValsProcessed = sumOfAbsValsProcessed + abs;
					if(pearsonCorrItr > maxPearsonCorr) {
						maxPearsonCorr = abs;
						tryDev1Max = i;
						tryDev2Max = j;
					}
					noOfValsProcessed++;
				}
			}
		}
		pearsonInfo.avgAbsoluteCorr = sumOfAbsValsProcessed/noOfValsProcessed;
		pearsonInfo.maxPearsonCorr = maxPearsonCorr;
		pearsonInfo.try1ForMax = tryDev1Max + 1;
		pearsonInfo.try2ForMax = tryDev2Max + 1;
		//get pearson corr for lastbits
		for (int noOfLastBits: EntryPoint.BITS_TO_ANALYSE) {
			double pearsonCoeffDevAndTry = pearsonCoeffDevAndTry(pufData, devId1,
					tryDev1Max+1, devId2, tryDev2Max+1, noOfLastBits);
			pearsonInfo.lastBitsPearsonCorrToMax.put(noOfLastBits, pearsonCoeffDevAndTry);
		}
		return pearsonInfo;
	}

	public static PearsonInfoDevice writeToFilePearsonInfoDevice(PUFData pufData,
			String queryType, int devId1, int devId2) {
		PearsonInfoDevice pearsonInfoForDevices = getPearsonInfoForDevices(pufData, devId1, devId2);
		String string = pearsonInfoForDevices.toString(queryType);
		String fileName = queryType + "_" + devId1 + "_" + devId2 + ".txt";
		File file = new File(EntryPoint.FOLDER_FILE, fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils.writeToFile(file, string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(string);
		return pearsonInfoForDevices;
	}

	public static void analysePearsonCoeffAllDevicesNoShifts(PUFData pufData) {
		MaxPearsonCorrInfo maxInfo = new MaxPearsonCorrInfo();
		for (int i = 0; i < EntryPoint.DEVICE_IDS.length; i++) {
			for (int j = 0; j < EntryPoint.DEVICE_IDS.length; j++) {
				if(i <= j) {
					PearsonInfoDevice pearsonInfoDevice = writeToFilePearsonInfoDevice(pufData, QueryConstants.ANALYSE_PEARSON_DEV,
							EntryPoint.DEVICE_IDS[i], EntryPoint.DEVICE_IDS[j]);
					if(maxInfo.maxPearsonInfo.compareTo(pearsonInfoDevice) < 0) {
						maxInfo.maxPearsonInfo = pearsonInfoDevice;
					}
					if(i != j) {
						if(maxInfo.maxPearsonInfoDiffDev.compareTo(pearsonInfoDevice) < 0) {
							maxInfo.maxPearsonInfoDiffDev = pearsonInfoDevice;
						}
					}
					System.out.println("=======================================================");
				}
			}
		}
		String string = maxInfo.toString(QueryConstants.ANALYSE_PEARSON_ALL_DEV);
		String fileName = QueryConstants.ANALYSE_PEARSON_ALL_DEV + ".txt";
		WriteDataUtils.writeToFile(fileName, string);
		/*File file = new File(EntryPoint.FOLDER_FILE, fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils.writeToFile(file, string);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		System.out.println(string);
	}
	
	public static void computeAvgMaxPearsonCoeffSameDevice(PUFData pufData, int noOfLastBits) {
		int sameDevId;
		StringBuilder builder = new StringBuilder();
		double sumOfMaxCorr = 0.0;
		for (int i = 0; i < EntryPoint.DEVICE_IDS.length; i++) {
			sameDevId = EntryPoint.DEVICE_IDS[i];
			DeviceInfo deviceInfo = pufData.idVsDevInfoMap.get(sameDevId);
			double[] devTry1;
			double[] devTry2;
			ShiftAndCorrInfo info;
			ShiftAndCorrInfoWithDevAndTryInfo maxCorInfo = new ShiftAndCorrInfoWithDevAndTryInfo();
			maxCorInfo.devId1 = sameDevId;
			maxCorInfo.devId2 = sameDevId;
			maxCorInfo.corr = -1;
			maxCorInfo.shift = Integer.MAX_VALUE;
			double[][] deviceTries = pufData.getLastBitInfo(noOfLastBits).get(deviceInfo);
			for (int j = 0; j < EntryPoint.NO_OF_VALID_TRIES; j++) {
				for (int k = 0; k < EntryPoint.NO_OF_VALID_TRIES; k++) {
					if(j<k) {
						devTry1 = deviceTries[j];
						devTry2 =  deviceTries[k];
						info = getMaxCorrelationByShifting(devTry1, devTry2, false);
						if(maxCorInfo.compareTo(info) < 0) {
							maxCorInfo.corr = info.corr;
							maxCorInfo.shift = info.shift;
							maxCorInfo.try1 = j+1;
							maxCorInfo.try2 = k+1;
						}
					}
				}
			}
			sumOfMaxCorr = sumOfMaxCorr + maxCorInfo.corr;
			builder.append(MyConstants.NEW_LINE);
			builder.append(MyConstants.NEW_LINE);
			builder.append(maxCorInfo.toString(QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS));
			
		}
		String str = "Avg Max Pearson Corr: "+(sumOfMaxCorr/EntryPoint.DEVICE_IDS.length) + MyConstants.NEW_LINE;
		StringBuilder startBuilder = new StringBuilder();
		startBuilder.append(str);
		startBuilder.append("===============================================================");
		builder.insert(0, startBuilder.toString());
		String fileName = QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS +"_" + noOfLastBits + ".txt";
		WriteDataUtils.writeToFile(fileName, builder.toString());
		System.out.println(builder.toString());
	}
	
	public static LinkedHashMap<Integer, ShiftAndCorrInfoWithDevAndTryInfo> getMaxPearsonCoeffSameDevice(PUFData pufData, int noOfLastBits) {
		LinkedHashMap<Integer, ShiftAndCorrInfoWithDevAndTryInfo> sameDevIdInfo = 
				new LinkedHashMap<Integer, ShiftAndCorrInfoWithDevAndTryInfo>();
		int sameDevId;
		//StringBuilder builder = new StringBuilder();
		//double sumOfMaxCorr = 0.0;
		for (int i = 0; i < EntryPoint.DEVICE_IDS.length; i++) {
			sameDevId = EntryPoint.DEVICE_IDS[i];
			DeviceInfo deviceInfo = pufData.idVsDevInfoMap.get(sameDevId);
			double[] devTry1;
			double[] devTry2;
			ShiftAndCorrInfo info;
			ShiftAndCorrInfoWithDevAndTryInfo maxCorInfo = new ShiftAndCorrInfoWithDevAndTryInfo();
			maxCorInfo.devId1 = sameDevId;
			maxCorInfo.devId2 = sameDevId;
			maxCorInfo.corr = -1;
			maxCorInfo.shift = Integer.MAX_VALUE;
			double[][] deviceTries = pufData.getLastBitInfo(noOfLastBits).get(deviceInfo);
			for (int j = 0; j < EntryPoint.NO_OF_VALID_TRIES; j++) {
				for (int k = 0; k < EntryPoint.NO_OF_VALID_TRIES; k++) {
					if(j<k) {
						devTry1 = deviceTries[j];
						devTry2 =  deviceTries[k];
						info = getMaxCorrelationByShifting(devTry1, devTry2, false);
						if(maxCorInfo.compareTo(info) < 0) {
							maxCorInfo.corr = info.corr;
							maxCorInfo.shift = info.shift;
							maxCorInfo.try1 = j+1;
							maxCorInfo.try2 = k+1;
						}
					}
				}
			}
			sameDevIdInfo.put(EntryPoint.DEVICE_IDS[i], maxCorInfo);
			//sumOfMaxCorr = sumOfMaxCorr + maxCorInfo.corr;
			//builder.append(MyConstants.NEW_LINE);
			//builder.append(MyConstants.NEW_LINE);
			//builder.append(maxCorInfo.toString(QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS));
			
		}
		return sameDevIdInfo;
		/*String str = "Avg Max Pearson Corr: "+(sumOfMaxCorr/EntryPoint.DEVICE_IDS.length) + MyConstants.NEW_LINE;
		StringBuilder startBuilder = new StringBuilder();
		startBuilder.append(str);
		startBuilder.append("===============================================================");
		builder.insert(0, startBuilder.toString());
		String fileName = QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS +"_" + noOfLastBits + ".txt";
		WriteDataUtils.writeToFile(fileName, builder.toString());
		System.out.println(builder.toString());*/
	}
	public static LinkedHashMap<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo> 
		getMaxPearsonCoeffForDiffDevicePairs(PUFData pufData, int noOfLastBits) {
		LinkedHashMap<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo> map
			= new LinkedHashMap<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo>();
		for (int h = 0; h < EntryPoint.DEVICE_IDS.length; h++) {
			for (int i = 0; i < EntryPoint.DEVICE_IDS.length; i++) {
				if(!(h<i)) {
					continue;
				}
				//different devices
				DeviceInfo deviceInfo1 = pufData.idVsDevInfoMap.get( EntryPoint.DEVICE_IDS[h]);
				DeviceInfo deviceInfo2 = pufData.idVsDevInfoMap.get(EntryPoint.DEVICE_IDS[i]);
				double[] dev1TryItr;
				double[] dev2TryItr;
				double[][] dev1Tries = pufData.getLastBitInfo(noOfLastBits).get(deviceInfo1);
				double[][] dev2Tries = pufData.getLastBitInfo(noOfLastBits).get(deviceInfo2);
				
				ShiftAndCorrInfoWithDevAndTryInfo maxCorInfo = new ShiftAndCorrInfoWithDevAndTryInfo();
				maxCorInfo.devId1 = EntryPoint.DEVICE_IDS[h];
				maxCorInfo.devId2 = EntryPoint.DEVICE_IDS[i];
				maxCorInfo.corr = -1;
				maxCorInfo.shift = Integer.MAX_VALUE;
				
				ShiftAndCorrInfo info;
				for (int j = 0; j < EntryPoint.NO_OF_VALID_TRIES; j++) {
					for (int k = 0; k < EntryPoint.NO_OF_VALID_TRIES; k++) {
						//if(j<=k) {
							dev1TryItr = dev1Tries[j];
							dev2TryItr =  dev2Tries[k];
							info = getMaxCorrelationByShifting(dev1TryItr, dev2TryItr, false);
							if(maxCorInfo.compareTo(info) < 0) {
								maxCorInfo.corr = info.corr;
								maxCorInfo.shift = info.shift;
								maxCorInfo.try1 = j+1;
								maxCorInfo.try2 = k+1;
							}
						//}
					}
				}
				TwoDeviceIdWrapper twoDeviceIdWrapper = new TwoDeviceIdWrapper();
				twoDeviceIdWrapper.devId1 = EntryPoint.DEVICE_IDS[h];
				twoDeviceIdWrapper.devId2 = EntryPoint.DEVICE_IDS[i];
				map.put(twoDeviceIdWrapper, maxCorInfo);
			}
		}
		return map;
	}
	public static void computeAvgMaxPearsonCoeffDiffDevice(PUFData pufData, int noOfLastBits) {
		StringBuilder builder = new StringBuilder();
		double sumOfMaxCorr = 0.0;
		for (int h = 0; h < EntryPoint.DEVICE_IDS.length; h++) {
			for (int i = 0; i < EntryPoint.DEVICE_IDS.length; i++) {
				if(!(h<i)) {
					continue;
				}
				//different devices
				DeviceInfo deviceInfo1 = pufData.idVsDevInfoMap.get( EntryPoint.DEVICE_IDS[h]);
				DeviceInfo deviceInfo2 = pufData.idVsDevInfoMap.get(EntryPoint.DEVICE_IDS[i]);
				
				
				ShiftAndCorrInfoWithDevAndTryInfo maxCorInfo = new ShiftAndCorrInfoWithDevAndTryInfo();
				maxCorInfo.devId1 = EntryPoint.DEVICE_IDS[h];
				maxCorInfo.devId2 = EntryPoint.DEVICE_IDS[i];
				maxCorInfo.corr = -1;
				maxCorInfo.shift = Integer.MAX_VALUE;
				
				double[][] dev1Tries = pufData.getLastBitInfo(noOfLastBits).get(deviceInfo1);
				double[][] dev2Tries = pufData.getLastBitInfo(noOfLastBits).get(deviceInfo2);
				
				populateMaxCornInfoDifferentDevices(maxCorInfo, dev1Tries, dev2Tries);
				sumOfMaxCorr = sumOfMaxCorr + maxCorInfo.corr;
				builder.append(MyConstants.NEW_LINE);
				builder.append(MyConstants.NEW_LINE);
				builder.append(maxCorInfo.toString(QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS));
			}
		}
		int noOfChecks = (EntryPoint.DEVICE_IDS.length*(EntryPoint.DEVICE_IDS.length-1))/2;
		String str = "Avg Max Pearson Corr: "+(sumOfMaxCorr/noOfChecks) + MyConstants.NEW_LINE;
		StringBuilder startBuilder = new StringBuilder();
		startBuilder.append(str);
		startBuilder.append("===============================================================");
		builder.insert(0, startBuilder.toString());
		String fileName = QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS +"_" + noOfLastBits + ".txt";
		WriteDataUtils.writeToFile(fileName, builder.toString());
		System.out.println(builder.toString());
	}

	public static void populateMaxCornInfoDifferentDevices(
			ShiftAndCorrInfoWithDevAndTryInfo maxCorInfo, double[][] dev1Tries,
			double[][] dev2Tries) {
		double[] dev1TryItr;
		double[] dev2TryItr;
		ShiftAndCorrInfo info;
		for (int j = 0; j < EntryPoint.NO_OF_VALID_TRIES; j++) {
			for (int k = 0; k < EntryPoint.NO_OF_VALID_TRIES; k++) {
				//if(j<=k) {
					dev1TryItr = dev1Tries[j];
					dev2TryItr =  dev2Tries[k];
					info = getMaxCorrelationByShifting(dev1TryItr, dev2TryItr, false);
					if(maxCorInfo.compareTo(info) < 0) {
						maxCorInfo.corr = info.corr;
						maxCorInfo.shift = info.shift;
						maxCorInfo.try1 = j+1;
						maxCorInfo.try2 = k+1;
					}
				//}
			}
		}
	}
	
	public static void computeDiffBtwnCorrelation(PUFData pufData) {
		
		DiffBtwnCorrelationData diffBtwnCorn = new DiffBtwnCorrelationData();
		for (int i = MIN_LAST_BITS_DIFF_CORN; i <=
				MAX_LAST_BITS_DIFF_CORN; i++) {
			populateDiffBtwnCorrelation(pufData, diffBtwnCorn, i);
		}
		WriteDataUtils.writeToFile(diffBtwnCorn);
		WriteDataUtils.writeDiffBtwnCornToDATFile(diffBtwnCorn);
	}

	public static void populateDiffBtwnCorrelation(PUFData pufData,DiffBtwnCorrelationData
			diffBtwnCorn, int noOfLastBits) {
		LinkedHashMap<TwoDeviceIdWrapper, ShiftAndCorrInfoWithDevAndTryInfo> maxPearsonCoeffForDiffDevicePairs
		= getMaxPearsonCoeffForDiffDevicePairs(pufData, noOfLastBits);
		LinkedHashMap<Integer, ShiftAndCorrInfoWithDevAndTryInfo> maxPearsonCoeffSameDevice = getMaxPearsonCoeffSameDevice(pufData, noOfLastBits);
		
		diffBtwnCorn.lastBitsVsDiffDeviceMaxInfo.put(noOfLastBits, maxPearsonCoeffForDiffDevicePairs);
		diffBtwnCorn.lastBitsVsSameDeviceMaxInfo.put(noOfLastBits, maxPearsonCoeffSameDevice);
		diffBtwnCorn.populateCorrDiffInfoForAllDev(noOfLastBits);
		
		//diffBtwnCorn.lastBitsVsDiffDeviceMaxInfo.put(noOfLastBits, value)
	}
}
