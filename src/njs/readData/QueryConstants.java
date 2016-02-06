package njs.readData;


/**
 * All notations follow MATLAB array notation where index starts from 1
 * @author user
 *
 */
public class QueryConstants {
	/**
	 * #Calculate inter ham distance mean and standard deviation for all devices corresponding to each try.
#So ith value in a file is mean/SD of inter hamming distance for ith try

HAM_ITR_ALL_DEV meanFileName sdFileName or
HAM_ITR_ALL_DEV
	 */
	public static final String HAM_ITR_ALL_DEV = "HAM_ITR_ALL_DEV";
	/**
	 * Calculate pearson coeff values between 2 devices for all possible pairwise values of tries
	 * for pufData.noOfLastBits.
	 * PEARSON_COEFF devId1 devId2 $fileName or
	 * PEARSON_COEFF devId1 devId2
	 */
	public static final String PEARSON_COEFF = "PEARSON_COEFF";
	/**
	 * Calculate pearson coeff values between all devices pairwise for all possible pairwise values of tries
	 * for pufData.noOfLastBits.
	 * format: PEARSON_COEFF_ALL_DEV
	 */
	public static final String PEARSON_COEFF_ALL_DEV = "PEARSON_COEFF_ALL_DEV";
	/**
	 * Calculate pearson coeff between a try of a device and another try of another device for pufData.noOfLastBits and
	 * prints to console
	 * PEARSON_COEFF_DEV_TRY $dev1 $try1 $dev2 $try2
	 */
	public static final String PEARSON_COEFF_DEV_TRY = "PEARSON_COEFF_DEV_TRY";
	/**
	 * format: HAM_DEV_TRY $dev1 $try1 $dev2 $try2 $dev3 $try3....
	 */
	public static final String HAM_DEV_TRY = "HAM_DEV_TRY";
	
	/**
	 * Analyse pearson coefficients between output(not pufData.noOfLastBits) of a try of a device and another try of another device
	 * Prints or  outputs to a file correlation value and shift when pearson coeff is maximum and output the shift,
	 * pearson coeff corr to shift and pearson coeff corresponding to shift for last bits considered in {@link EntryPoint#BITS_TO_ANALYSE}
	 * ANALYSE_PEARSON_DEV_TRY $dev1 $try1 $dev2 $try2
	 */
	public static final String ANALYSE_PEARSON_DEV_TRY = "ANALYSE_PEARSON_DEV_TRY";
	/**
	 * Analyse pearson coefficients between output(not pufData.noOfLastBits) of two devices. Find max correlation for which of the tries(discard 1 if two devices are same).
	 * Find avg(|correlation|). For that corresponding tries where correlation is maximum, find correlation for 
	 * output of last bits considered in {@link EntryPoint#BITS_TO_ANALYSE}
	 */
	public static final String ANALYSE_PEARSON_DEV = "ANALYSE_PEARSON_DEV";
	/**
	 * Do ANALYSE_PEARSON_DEV for all pariwise deviceId's
	 */
	public static final String ANALYSE_PEARSON_ALL_DEV = "ANALYSE_PEARSON_ALL_DEV";
	
	public static final String ANALYSE_PEARSON_DEV_SHIFT = "ANALYSE_PEARSON_DEV_SHIFT";
	/**
	 * For each device find max correlation for last bits specified with shifts for all two different tries
	 * considered.
	 * Now find average of the max values
	 */
	public static final String AVG_MAX_SAME_DEV_SHIFT_LAST_BITS = "AVG_MAX_SAME_DEV_SHIFT_LAST_BITS";
	/**
	 * For each pairwise combination of device find max correlation for last bits specified with shifts for all two tries
	 * considered.
	 * Now find average of the max values from nC2 max values
	 */
	public static final String AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS = "AVG_MAX_DIFF_DEV_SHIFT_LAST_BITS";
	
	public static final String DIFF_BTWN_CORR = "DIFF_BTWN_CORR";
	
	public static final String WRITE_PUF_DATA_FILE = "WRITE_PUF_DATA_FILE";
}
