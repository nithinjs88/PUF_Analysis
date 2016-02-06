package njs.readData;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import njs.model.CorrDiffInfo;
import njs.model.DeviceDataToAnalyze;
import njs.model.DeviceInfo;
import njs.model.DiffBtwnCorrelationData;
import njs.model.PUFData;
import njs.model.ShiftAndCorrInfoWithDevAndTryInfo;
import njs.utils.FileUtils;

public class AnalyzeDevice {
	
	public static final int LAST_BITS_TO_ANALYZE = 12;
	public static final double THRESHOLD = 0.999999;
	
	public static void analyse(File pufDataFile, File diffBtwnCornFile, File devToAnalyseCSVFile) {
		PUFData pufData = ReadDataUtils.readDataFromFile(pufDataFile, PUFData.class);
		DiffBtwnCorrelationData diffBtwnCornData = 
				ReadDataUtils.readDataFromFile(diffBtwnCornFile, DiffBtwnCorrelationData.class);
		DeviceDataToAnalyze deviceDataToAnalyze = new DeviceDataToAnalyze();
		double[][] deviceOutputFromCSV = ReadDataUtils.getDeviceOutputFromCSV(devToAnalyseCSVFile);
		deviceDataToAnalyze.deviceOutput = deviceOutputFromCSV;
		
		analyse(pufData, diffBtwnCornData, deviceDataToAnalyze);
	}
	
	public static void analyse(PUFData pufData, DiffBtwnCorrelationData diffBtwnCorrelationData, File devToAnalyseCSVFile) {
		DeviceDataToAnalyze deviceDataToAnalyze = new DeviceDataToAnalyze();
		double[][] deviceOutputFromCSV = ReadDataUtils.getDeviceOutputFromCSV(devToAnalyseCSVFile);
		deviceDataToAnalyze.deviceOutput = deviceOutputFromCSV;
		
		analyse(pufData, diffBtwnCorrelationData, deviceDataToAnalyze);
	}
	
	public static void analyse(PUFData pufData, DiffBtwnCorrelationData diffBtwnCorrelationData,
			DeviceDataToAnalyze deviceToAnalyse) {
		Map<DeviceInfo, double[][]> lastBitInfo = pufData.getLastBitInfo(LAST_BITS_TO_ANALYZE);
		List<DeviceInfo> deviceInfos = pufData.deviceInfos;
		double[][] deviceInDBTries;
		double[][] devToAnalyzeTries = deviceToAnalyse.getLastBitInfo(LAST_BITS_TO_ANALYZE);
		ShiftAndCorrInfoWithDevAndTryInfo netMaxCornInfo = new ShiftAndCorrInfoWithDevAndTryInfo();
		//our unknown device
		netMaxCornInfo.devId1 = -1;
		//will be populated in further stages
		netMaxCornInfo.devId2 = -1;
		netMaxCornInfo.corr = -1;
		
		ShiftAndCorrInfoWithDevAndTryInfo itrDevCornInfo;
		for (DeviceInfo deviceInfo : deviceInfos) {
			deviceInDBTries = lastBitInfo.get(deviceInfo);
			itrDevCornInfo = new ShiftAndCorrInfoWithDevAndTryInfo();
			//our unknown device
			itrDevCornInfo.devId1 = -1;
			//will be populated in further stages
			itrDevCornInfo.devId2 = deviceInfo.deviceId;
			itrDevCornInfo.corr = -1;
			
			PearsonUtils.populateMaxCornInfoDifferentDevices(itrDevCornInfo, devToAnalyzeTries, deviceInDBTries);
			System.out.println(itrDevCornInfo.toString(QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS));
			if(netMaxCornInfo.compareTo(itrDevCornInfo) < 0) {
				netMaxCornInfo.corr = itrDevCornInfo.corr;
				netMaxCornInfo.shift = itrDevCornInfo.shift;
				netMaxCornInfo.try1 = itrDevCornInfo.try1;
				netMaxCornInfo.try2 = itrDevCornInfo.try2;
				netMaxCornInfo.devId2 = deviceInfo.deviceId;
				System.out.println("Net Max Corn Info Updated");
			}
			System.out.println();
		}
		System.out.println("==================================================================");
		System.out.println("Max correlation is ");
		System.out.println(netMaxCornInfo.toString(QueryConstants.AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS));
		System.out.println();
		System.out.println("Last bits considered is "+LAST_BITS_TO_ANALYZE+", Threshold is "+THRESHOLD);
		if(netMaxCornInfo.corr > THRESHOLD) {
			System.out.println("Device is "+ netMaxCornInfo.devId2+" as Max Corrn Obtained greater than threshold");
			LinkedHashMap<Integer,CorrDiffInfo> linkedHashMap = diffBtwnCorrelationData.lastBitsVsdevIdVsDiffInfo.get(LAST_BITS_TO_ANALYZE);
			CorrDiffInfo corrDiffInfo = linkedHashMap.get(netMaxCornInfo.devId2);
			System.out.println("Max correlation in DB  for Device: "+netMaxCornInfo.devId2+" is "+corrDiffInfo.maxCornSameDev);
		} else {
			System.out.println("Device match not found as Max Corrn Obtained is lesser than threshold");
		}
	}
	
	public static void main(String[] args) {
		File pufDataFile = FileUtils.getFile("PUFData.dat", AnalyzeDevice.class);
		PUFData pufData = ReadDataUtils.readDataFromFile(pufDataFile, PUFData.class);
		
		File diffBtwnCornFile = FileUtils.getFile("DiffBtwnCorr.dat", AnalyzeDevice.class);
		DiffBtwnCorrelationData diffBtwnCornData = 
				ReadDataUtils.readDataFromFile(diffBtwnCornFile, DiffBtwnCorrelationData.class);
		
		File devCSVFile;
		//for (int i = 0; i < EntryPoint.DEVICE_IDS.length; i++) {
			//devCSVFile = FileUtils.getFile(String.valueOf(EntryPoint.DEVICE_IDS[i])+".txt", AnalyzeDevice.class);
		devCSVFile = FileUtils.getFile("209_Unknown.txt", AnalyzeDevice.class);
		//devCSVFile = FileUtils.getFile("242_Unknown.txt", AnalyzeDevice.class);
			analyse(pufData, diffBtwnCornData, devCSVFile);
		//}
	}
}
