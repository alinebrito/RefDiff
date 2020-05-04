package refdiff.examples;

import refdiff.core.cst.CstNode;
import refdiff.core.diff.CstDiff;

public abstract class RefDiffRun {

	protected String getNameFileCSV(final String projectName) {
		return "refactorings_refdiff_2.0_project_"+ projectName.replaceAll("/", "_").replaceAll("-", "_") + ".csv";
	}
	
	protected String getURL(final String projectName) {
		return "https://github.com/" + projectName + ".git";
	}
	
	protected abstract String getPathEntity(CstNode cstNode);
	
	protected abstract String getFullNameEntity(CstNode cstNode);
	
	protected abstract void writeCstDiffToCSV(final String pathOutput, final String projectName, final CstDiff cstDiff, String sha1);

}
