package refdiff.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import refdiff.core.RefDiff;
import refdiff.core.cst.CstNode;
import refdiff.core.cst.Parameter;
import refdiff.core.diff.CstDiff;
import refdiff.core.diff.CstRootHelper;
import refdiff.core.diff.Relationship;
import refdiff.parsers.js.JsNodeType;
import refdiff.parsers.js.JsPlugin;

public class RefDiffRunJs extends RefDiffRun{
	
	private String getClassName(final CstNode cst) {
		List<String> listNodes = CstRootHelper.getNodePath(cst);//[path, Class..., method ]
		String innerClassName =  listNodes.size() > 2 ? "." + String.join(".", listNodes.subList(1, listNodes.size() - 1)): "";
		return innerClassName;
	}
	
	@Override
	protected String getPathEntity(CstNode cstNode) {
		String path = cstNode.getLocation().getFile();
		path = path.replaceAll("/", ".");
		path = path.replaceAll(".js", "");
		path = path + this.getClassName(cstNode);
		return path;
	}
	
	@Override
	protected String getFullNameEntity(CstNode cstNode) {
		String refactoringLevel = cstNode.getType();
		String path = this.getPathEntity(cstNode);
		if(JsNodeType.FUNCTION.equals(refactoringLevel)) {
			return path + "#" + cstNode.getLocalName().replaceAll(" ", "");
		}
		else if(JsNodeType.CLASS.equals(refactoringLevel)){
			return path + "#" + cstNode.getLocalName();
		}
		else {
			return path;
		}
	}
	
	private Boolean isRefactoringInClass(CstNode cstNode) {
		String className = this.getClassName(cstNode);
		return JsNodeType.CLASS.equals(cstNode.getType()) || className != "" ;
	}
	
	private String getParameters(CstNode cstNode) {
		List<Parameter> parameters = cstNode.getParameters();
		List<String> parametersName = new ArrayList<String>();
		for (Parameter parameter : parameters) {
			parametersName.add(parameter.getName());
		}
		return String.join(",", parametersName);
	}
	
	//	Head:
	//	name_project;url;sha1;entity_before_full_name;entity_before_simple_name;
	//	entity_before_location;entity_after_full_name;entity_after_simple_name;
	//	entity_after_location;refactoring_level;refactoring_name;entity_before_is_inner_class;
	//	entity_after_is_inner_class";
	@Override
	protected void writeCstDiffToCSV(final String pathOutput, final String projectName, final CstDiff cstDiff, String sha1) {
			
			if(cstDiff == null) {
				System.err.println("Cst is null");
				return;
			}
			
			List<String> lines = new ArrayList<String>();
			System.out.println("Refactorings: " + cstDiff.getRefactoringRelationships().size());
			for (Relationship rel : cstDiff.getRefactoringRelationships()) {
				
				//Entity before
				String entity_before_contains_class = this.isRefactoringInClass(rel.getNodeBefore()).toString();
				String entity_before_full_name = this.getFullNameEntity(rel.getNodeBefore());
				String entity_before_simple_name = rel.getNodeBefore().getLocalName().replaceAll(" ", "");
				String entity_before_location = rel.getNodeBefore().getLocation().toString();
				String entity_before_parameters = this.getParameters(rel.getNodeBefore());
				
				//Entity after
				String entity_after_contains_class = this.isRefactoringInClass(rel.getNodeAfter()).toString();
				String entity_after_full_name = this.getFullNameEntity(rel.getNodeAfter());
				String entity_after_simple_name = rel.getNodeAfter().getLocalName().replaceAll(" ", "");
				String entity_after_location = rel.getNodeAfter().getLocation().toString();
				String entity_after_parameters = this.getParameters(rel.getNodeAfter());
				
				//Refactoring info
				String refactoring_level = rel.getNodeAfter().getType();
				String refactoring_name = rel.getType().name();
				String separator = ";";
			
				String line = 
						projectName + separator +
						this.getURL(projectName) + separator + 
						sha1 + separator +
						entity_before_full_name + separator +
						entity_before_simple_name + separator +
						entity_before_location + separator +
						entity_after_full_name + separator +
						entity_after_simple_name + separator +
						entity_after_location + separator +
						refactoring_level + separator +
						refactoring_name  + separator +
						entity_before_contains_class + separator +
						entity_after_contains_class + separator +
						entity_before_parameters + separator +
						entity_after_parameters;
				
//				System.out.println("\n\n");
//				System.out.println(refactoring_name);
//				System.out.println(refactoring_level);
//				System.out.println(entity_before_full_name);
//				System.out.println(entity_before_contains_class);
//				System.out.println(entity_before_simple_name);
//				System.out.println(entity_before_parameters);
//				System.out.println("--");
//				System.out.println(entity_after_full_name);
//				System.out.println(entity_after_contains_class);
//				System.out.println(entity_after_simple_name);
//				System.out.println(entity_after_parameters);
//				
				lines.add(line);
//				System.out.println(line);
			}
			
			UtilFile.writeFile(pathOutput, this.getNameFileCSV(projectName), lines);
			
		}
	
	public void run(final String projectName, final String commit) {
		File tempFolder = new File("/home/aline/aline/projetos/refdiff-run-dataset/journal-2020-js/bare_projects");
		String pathOutput = "/home/aline/aline/projetos/refdiff-run-dataset/journal-2020-js/output";
		try {
			JsPlugin jsPlugin = new JsPlugin();
			RefDiffCustom refDiffJs = new RefDiffCustom(jsPlugin);
			File repo = refDiffJs.cloneGitRepository(new File(tempFolder, projectName), this.getURL(projectName));
			CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
			writeCstDiffToCSV(pathOutput, projectName, diffForCommit, commit);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}

}
