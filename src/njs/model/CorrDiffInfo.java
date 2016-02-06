package njs.model;

import java.io.Serializable;

public class CorrDiffInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4633533328324073672L;
	public double maxCornSameDev;
	public double avgMaxDiffDev;
	public double diffInCorn;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Max Corn Same Dev: ");
		builder.append(maxCornSameDev);
		builder.append(",");
		builder.append("Avg Of Max Corr Different Dev: ");
		builder.append(avgMaxDiffDev);
		builder.append(",");
		builder.append("Diff In Corn: ");
		builder.append(diffInCorn);
		return builder.toString();
	}
}
