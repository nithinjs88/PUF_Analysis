package njs.model;

import java.io.Serializable;

public class ShiftAndCorrInfo implements Comparable<ShiftAndCorrInfo>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7132079385479024527L;
	public int shift;
	public double corr;
	@Override
	public int compareTo(ShiftAndCorrInfo o) {
		int cmpDbl =  Double.compare(corr, o.corr);
		if(cmpDbl == 0) {
			//if corrs are equal give high preference to one with lower abs value of shift
			//System.out.println("Corr equal");
			return Integer.compare(Math.abs(o.shift),Math.abs(shift));
		}
		return cmpDbl;
		
	}
}
