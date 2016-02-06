package njs.readData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import njs.model.DeviceInfo;
import njs.model.PUFData;
import njs.model.PearsonInfoShiftMSB;
import njs.utils.FileUtils;
import njs.utils.MyUtilities;
import njs.utils.PatternUtils;

public class EntryPoint {
	
	private static final String QUERIES_TXT = "queries.txt";
	private static final String INPUT_TXT = "inputToDeviceForTry.txt";
	public static final String FOLDER_LOCN = "F:/Study/Work/Eclipse Workspace/RCP/PufAnalysis/src/njs/readData";
	public static final File FOLDER_FILE = new File(FOLDER_LOCN);
	
	private static final String COMMENT = "#";
	public static final int[] DEVICE_IDS = {207,217,242,243,246,254};
	//public static final int[] DEVICE_IDS = {207,217};
	//no of lines in file. No of rows in data
	public static final int NO_OF_VALID_TRIES = 100;
	//no of values in line. No of cols in data
	public static final int NO_OF_VALID_VALS = 90;
	/**
	 * No of bits in o/p
	 */
	public static final int NO_OF_BITS = 12;
	public static final int[] BITS_TO_ANALYSE = {3,4,5};
	
	/**
	 * In shift. Device 1 output remains same. Device 2 is shifted relative to Device 1.
	 * If shift is negative means device 2 outputs occurs left and data analysed is the data
	 * common to all regions.
	 * 			=======   Device 1
	 * 		  -------     Device 2
	 */
	public static int MIN_SHIFT = -20;
	/**
	 * In shift. Device 1 output remains same. Device 2 is shifted relative to Device 1.
	 * If shift is positive means device 2 outputs occurs right and data analysed is the data
	 * common to all regions.
	 * 			=======     Device 1
	 * 		      -------   Device 2
	 */
	public static int MAX_SHIFT = 20;
	
	public static final String MSG_FORMAT_INTERHD_LAST_BITS= "interHD_{0}_{1}bits.txt";
	public static final String MSG_FORMAT_PEARSON_DEV_LAST_BITS= "{0}vs{1}_PearsonCoeff_{2}bits.txt";
	
	
	public static void main(String[] args) throws Exception {
		PUFData pufData = readDeviceInfo();
		parseQueries(pufData);
		System.out.println("Done");
	}


	private static PUFData readDeviceInfo() {
		PUFData pufData = new PUFData();
		int noOfDevices = DEVICE_IDS.length;
		pufData.noOfDevices = noOfDevices;
		List<DeviceInfo> deviceInfos = new ArrayList<DeviceInfo>();
		pufData.deviceInfos = deviceInfos;
		Map<Integer, DeviceInfo> idVsDevInfoMap = pufData.idVsDevInfoMap;
 		for (int i = 0; i < noOfDevices; i++) {
			DeviceInfo deviceInfo = new DeviceInfo();
			deviceInfo.deviceId = DEVICE_IDS[i];
			deviceInfo.deviceNo = (i+1);
			deviceInfos.add(deviceInfo);
			idVsDevInfoMap.put(DEVICE_IDS[i], deviceInfo);
			
			File devOpFile = FileUtils.getFile(DEVICE_IDS[i] + ".txt", EntryPoint.class);
			double[][] deviceOutputFromCSV = ReadDataUtils.getDeviceOutputFromCSV(devOpFile);
			pufData.addDeviceOuptut(deviceInfo, deviceOutputFromCSV);
		}
		File file = FileUtils.getFile(INPUT_TXT, EntryPoint.class);
		double[] input = ReadDataUtils.getDeviceInputFromFile(file);
		pufData.input = input;
		return pufData;
	}


	private static void parseQueries(PUFData pufData) throws Exception {
		File file = FileUtils.getFile(QUERIES_TXT, EntryPoint.class);
		String fileContents = FileUtils.getFileContents(file);
		String[] split = PatternUtils.NEWLINE_PATTERN.split(fileContents);
		String line;
		int noOfLastBits = -1;
		for (int i = 0; i < split.length; i++) {
			line = split[i];
			while (line.startsWith(COMMENT)) {
				i++;
				line = split[i];
			}
			line = split[i];
			if(noOfLastBits == -1) {
				//populateMSB
				try {
					noOfLastBits = Integer.parseInt(line.trim());
					pufData.noOfLastBits = noOfLastBits;
					pufData.populateLastBitInfo();
				} catch (Exception e) {
					for (int j = PearsonUtils.MIN_LAST_BITS_DIFF_CORN;
							j <= PearsonUtils.MAX_LAST_BITS_DIFF_CORN; j++) {
						pufData.populateLastBitInfo(j);
					}
					//put to 0 st that queries aare parsed.
					noOfLastBits = 0;
					parseQuery(pufData,line);
					//noOfLastBits not mentioned
					//So populate for all bits
					//e.printStackTrace();
				}
			} else {
				//parseQuery
				parseQuery(pufData,line);
			}
			
		}
	}


	private static void parseQuery(PUFData pufData, String query) {
		String[] split = PatternUtils.WHITESPACE_PATTERN.split(query);
		String queryType = split[0];
		if(QueryConstants.HAM_ITR_ALL_DEV.equals(queryType)) {
			String meanFile,sdFile;
			if(split.length == 1) {
				meanFile = WriteDataUtils.getMeanDestFileName(pufData);
				sdFile = WriteDataUtils.getSDDestFileName(pufData);
			} else {
				meanFile = split[1];
				sdFile = split[2];
			}
			HammingDistanceUtils.hamDistanceItrAllDev(pufData,meanFile,sdFile);
			System.out.println(meanFile);
			System.out.println(sdFile);
		} else if(QueryConstants.PEARSON_COEFF.equals(queryType)) {
			String fileName;
			if(split.length == 3) {
				fileName = WriteDataUtils.getPearsonCoeffDestFileName(split[1],split[2], pufData);
			} else {
				fileName = split[3];
			}
			PearsonUtils.pearsonCoeffBetweenDev(pufData,split[1],split[2],fileName,pufData.noOfLastBits);
			System.out.println(fileName);
		} else if(QueryConstants.PEARSON_COEFF_ALL_DEV.equals(queryType)) {
			String dev1,dev2,fileName;
			for (int i = 0; i < DEVICE_IDS.length; i++) {
				for (int j = 0; j < DEVICE_IDS.length; j++) {
					if(i <=j) {
						dev1 = DEVICE_IDS[i] + "";
						dev2 = DEVICE_IDS[j] + "";
						fileName = WriteDataUtils.getPearsonCoeffDestFileName(dev1,dev2, pufData);
						PearsonUtils.pearsonCoeffBetweenDev(pufData,dev1,dev2,fileName,pufData.noOfLastBits);
					}
				}
			}
		} else if(QueryConstants.PEARSON_COEFF_DEV_TRY.equals(queryType)) {
			Integer dev1,dev2,try1,try2;
			dev1 = Integer.parseInt(split[1]);
			try1 = Integer.parseInt(split[2]);
			dev2 = Integer.parseInt(split[3]);
			try2 = Integer.parseInt(split[4]);
			double pearsonCoeffDevAndTry = PearsonUtils.pearsonCoeffDevAndTry(pufData, dev1, try1, dev2, try2,pufData.noOfLastBits);
			StringBuilder builder = new StringBuilder();
			builder.append("Pearson Coeff btw ");
			builder.append("Dev: ");
			builder.append(dev1);
			builder.append(",Try: ");
			builder.append(try1);
			builder.append(",Dev: ");
			builder.append(dev2);
			builder.append(",Try: ");
			builder.append(try2);
			builder.append(" = ");
			builder.append(pearsonCoeffDevAndTry);
			System.out.println(builder);
		} else if(QueryConstants.HAM_DEV_TRY.equals(queryType)) {
			int noOfDevicesOrTries = (split.length - 1)/2;
			List<Integer> devIds = new ArrayList<Integer>();
			List<Integer> tryForDevices = new  ArrayList<Integer>();
			int devId, tryNo;
			for (int i = 0; i < noOfDevicesOrTries; i++) {
				devId = Integer.parseInt(split[2*i + 1]);
				tryNo = Integer.parseInt(split[2*i + 2]);
				devIds.add(devId);
				tryForDevices.add(tryNo);
				
			}
			Double[] meanSD = HammingDistanceUtils.getMeanAndSDForDiffDevDifferentTry(pufData, devIds, tryForDevices);
			StringBuilder builder = new StringBuilder();
			builder.append("Mean: ");
			builder.append(meanSD[0]);
			builder.append(",SD: ");
			builder.append(meanSD[1]);
			builder.append(" for ");
			for (int i = 0; i < noOfDevicesOrTries; i++) {
				builder.append("{ Dev ");
				builder.append(devIds.get(i));
				builder.append(",Try ");
				builder.append(tryForDevices.get(i));
				builder.append(" }");
			}
			System.out.println(builder);
		} else if(QueryConstants.ANALYSE_PEARSON_DEV_TRY.equals(queryType)) {
			int devId1 = Integer.parseInt(split[1]);
			int try1 = Integer.parseInt(split[2]);
			int devId2 = Integer.parseInt(split[3]);
			int try2 = Integer.parseInt(split[4]);
			PearsonInfoShiftMSB pearsonShift = PearsonUtils.pearsonShiftDevTry(pufData, devId1, try1, devId2, try2,EntryPoint.NO_OF_BITS);
			String string = pearsonShift.toString(queryType);
			System.out.println(string);
			StringBuilder fileBuf = new StringBuilder();
			for (String string2 : split) {
				fileBuf.append(string2);
				fileBuf.append("-");
			}
			MyUtilities.removeCharacterAtEndOfBuilder(fileBuf);
			fileBuf.append(".txt");
			String fileName = fileBuf.toString();
			File file = new File(FOLDER_FILE, fileName);
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
		} else if(QueryConstants.ANALYSE_PEARSON_DEV.equals(queryType)) {
			int devId1 = Integer.parseInt(split[1]);
			int devId2 = Integer.parseInt(split[2]);
			PearsonUtils.writeToFilePearsonInfoDevice(pufData, queryType, devId1, devId2);
		} else if(QueryConstants.ANALYSE_PEARSON_ALL_DEV.equals(queryType)) {
			PearsonUtils.analysePearsonCoeffAllDevicesNoShifts(pufData);
		} else if(QueryConstants.AVG_MAX_SAME_DEV_SHIFT_LAST_BITS.equals(queryType)) {
			PearsonUtils.computeAvgMaxPearsonCoeffSameDevice(pufData, pufData.noOfLastBits);
		} else if(QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS.equals(queryType)) {
			PearsonUtils.computeAvgMaxPearsonCoeffDiffDevice(pufData, pufData.noOfLastBits);
		} else if(QueryConstants.DIFF_BTWN_CORR.equals(queryType)) {
			PearsonUtils.computeDiffBtwnCorrelation(pufData);
		} else if(QueryConstants.WRITE_PUF_DATA_FILE.equals(queryType)) {
			for (int j = PearsonUtils.MIN_LAST_BITS_DIFF_CORN;
					j <= PearsonUtils.MAX_LAST_BITS_DIFF_CORN; j++) {
				pufData.populateLastBitInfo(j);
			}
			WriteDataUtils.writePUFDataToDATFile(pufData);
		}
	}
}
