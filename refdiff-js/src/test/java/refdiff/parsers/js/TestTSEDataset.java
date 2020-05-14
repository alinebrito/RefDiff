package refdiff.parsers.js;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static refdiff.test.util.CstDiffMatchers.contains;
import static refdiff.test.util.CstDiffMatchers.doesntContain;
import static refdiff.test.util.CstDiffMatchers.node;
import static refdiff.test.util.CstDiffMatchers.relationship;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import refdiff.core.RefDiff;
import refdiff.core.diff.CstDiff;
import refdiff.core.diff.CstRootHelper;
import refdiff.core.diff.Relationship;
import refdiff.core.diff.RelationshipType;
import refdiff.core.io.GitHelper;
import refdiff.test.util.JsParserSingleton;

public class TestTSEDataset {

	private JsPlugin jsPlugin;
	
	private File tempFolder;
	
	private RefDiff refDiffJs;
	
    @Before
    public void setUp() throws Exception {
    	this.jsPlugin = JsParserSingleton.get();
    	this.tempFolder = new File("test-data/diff/");
    	this.refDiffJs = new RefDiff(this.jsPlugin);
    	this.refDiffJs.cloneGitRepository(new File(this.tempFolder, "refdiff-study/meteor"), this.getURL("refdiff-study/meteor"));
    	this.refDiffJs.cloneGitRepository(new File(this.tempFolder, "refdiff-study/react-native"), this.getURL("refdiff-study/react-native"));
    	this.refDiffJs.cloneGitRepository(new File(this.tempFolder, "refdiff-study/vue"), this.getURL("refdiff-study/vue"));
    	this.refDiffJs.cloneGitRepository(new File(this.tempFolder, "refdiff-study/webpack"), this.getURL("refdiff-study/webpack"));
    }
    
    private String getURL(final String projectName) {
    	return "https://github.com/" + projectName + ".git";
    }
    
    private static void printRefactorings(CstDiff diff, RelationshipType type) {
    	for (Relationship rel : diff.getRefactoringRelationships()) {
    		if(rel.getType().name().equals(type.name())) {
    	   		System.out.println("\n" + rel.getType().name() + " " + rel.getNodeAfter().getType());
        		System.out.println("From: " + String.join(".", CstRootHelper.getNodePath(rel.getNodeBefore())) + "\nTo " + String.join(".", CstRootHelper.getNodePath(rel.getNodeAfter())));
    		}
    	}
    }
    
	@Test
	public void shouldDetectMoveFunction() throws Exception {
		String projectName = "refdiff-study/meteor";
		String commit = "e7ad5d2a422720eeae4a407456a5bfcc64755471";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.MOVE, 
					node("tools/isobuild/builder.js", "symlinkWithOverwrite"), 
					node("tools/fs/files.js", "symlinkWithOverwrite"))
		));
	}

	@Test
	public void shouldDetectMoveFile() throws Exception {
		String projectName = "refdiff-study/meteor";
		String commit = "fd63390bf7f54266981f23de9d0628165917a21f";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.MOVE, 
					node("packages/facts/facts.js"), 
					node("packages/deprecated/facts/facts.js"))
		));
	}

	@Test
	public void shouldDetectInlineFunction() throws Exception {//OK
		
		String projectName = "refdiff-study/react-native";
		String commit = "0125813f213662ec1d9bb4a456f5671adcff9d83";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.INLINE, 
					node("Libraries/Renderer/oss/ReactFabric-dev.js", "ReactFiberClassComponent", "resetInputPointers"), 
					node("Libraries/Renderer/oss/ReactFabric-dev.js","ReactFiberClassComponent", "resumeMountClassInstance")),
			relationship(RelationshipType.INLINE, 
					node("Libraries/Renderer/oss/ReactNativeRenderer-dev.js", "ReactFiberClassComponent", "resetInputPointers"), 
					node("Libraries/Renderer/oss/ReactNativeRenderer-dev.js","ReactFiberClassComponent", "resumeMountClassInstance"))
		));
		
		assertThat(diffForCommit, doesntContain(
				relationship(RelationshipType.INLINE, 
						node("Libraries/Renderer/oss/ReactFabric-dev.js", "ReactFiberClassComponent", "resetInputPointers"), 
						node("Libraries/Renderer/oss/ReactNativeRenderer-dev.js","ReactFiberClassComponent", "resumeMountClassInstance")),
				relationship(RelationshipType.INLINE, 
						node("Libraries/Renderer/oss/ReactNativeRenderer-dev.js", "ReactFiberClassComponent", "resetInputPointers"), 
						node("Libraries/Renderer/oss/ReactFabric-dev.js","ReactFiberClassComponent", "resumeMountClassInstance"))
			));
		
		diffForCommit
		  .getRefactoringRelationships().stream()
		  .filter(rel -> RelationshipType.INLINE.name().equals(rel.getType().name()))
		  .collect(Collectors.toList())
		  .forEach(refactoring -> {
			  assertEquals(refactoring.getNodeBefore().getLocation().getFile().toString(), refactoring.getNodeAfter().getLocation().getFile().toString());
		  });

	}

	@Test
	public void shouldDetectMoveAndRenameFunction() throws Exception {
		String projectName = "refdiff-study/vue";
		String commit = "4e0c48511d49f331fde31fc87b6ca428330f32d1";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.MOVE_RENAME, 
					node("src/core/util/env.js", "nextTickHandler"), 
					node("src/core/util/next-tick.js", "flushCallbacks"))
		));
	}

	@Test
	public void shouldDetectMoveAndRenameFile() throws Exception {
		String projectName = "refdiff-study/vue";
		String commit = "7b8b0e48f7b3fa1dd3063d4a2cd38c0cde7baa99";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		printRefactorings(diffForCommit, null);
		diffForCommit
		  .getRefactoringRelationships().stream()
		  .filter(rel -> RelationshipType.MOVE_RENAME.name().equals(rel.getType().name()))
		  .filter(rel -> JsNodeType.FILE.toString().equals(rel.getNodeAfter().getType()))
		  .collect(Collectors.toList())
		  .forEach(rel -> {
			  assertNotEquals(rel.getNodeBefore().getSimpleName(), rel.getNodeAfter().getSimpleName());
			  assertNotEquals(rel.getNodeBefore().getNamespace(), rel.getNodeAfter().getNamespace());
		  });
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.MOVE_RENAME,
				node("src/entries/web-server-renderer.js"),
				node("src/server/index.js"))
		));
	}

	@Test
	public void shouldDetectExtractFunction() throws Exception {
		String projectName = "refdiff-study/vue";
		String commit = "a08feed8c410b89fa049fdbd6b9459e2d858e912";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.EXTRACT, 
					node("dist/vue.runtime.js", "setAttr"), 
					node("dist/vue.runtime.js", "baseSetAttr"))
		));
	}
	
    @Test
	public void shouldDetectExtractAndMoveFunction() throws Exception {
		String projectName = "refdiff-study/vue";
		String commit = "bc2918f0e596d0e133a25606cbb66075402ce6c3";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.EXTRACT_MOVE, 
					node("packages/weex-template-compiler/build.js", "parse", "start"), 
					node("packages/weex-template-compiler/build.js", "createASTElement")),
			relationship(RelationshipType.EXTRACT_MOVE, 
					node("packages/weex-template-compiler/build.js", "parse", "start"), 
					node("packages/weex-template-compiler/build.js", "processElement"))
				
		));
		diffForCommit
		  .getRefactoringRelationships().stream()
		  .filter(rel -> RelationshipType.EXTRACT_MOVE.name().equals(rel.getType().name()))
		  .collect(Collectors.toList())
		  .forEach(refactoring -> {
			  assertEquals(refactoring.getNodeBefore().getLocation().getFile().toString(), refactoring.getNodeAfter().getLocation().getFile().toString());
		  });
	}
	
	@Test
	public void shouldDetectInternalMoveFunction() throws Exception {
		String projectName = "refdiff-study/webpack";
		String commit = "8b3772d47fc94fe3c3175602bba5eef6605fad86";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.INTERNAL_MOVE, 
					node("lib/EntryOptionPlugin.js", "apply", "itemToPlugin"),//TODO: Class name? EntryOptionPlugin
					node("lib/EntryOptionPlugin.js", "itemToPlugin"))
		));
	}
	
	@Test
	public void shouldDetectInternalMoveAndRenameFunction() throws Exception {
		String projectName = "refdiff-study/meteor";
		String commit = "91a4a46ea1d687de1f929e3b9f0bae9c2db0c83d";
		File repo = new File(this.tempFolder, projectName);
		CstDiff diffForCommit = refDiffJs.computeDiffForCommit(repo, commit);
		assertThat(diffForCommit, contains(
			relationship(RelationshipType.INTERNAL_MOVE_RENAME, 
					node("packages/liveui/liveui.js", "render", "update", "patch"),
					node("packages/liveui/liveui.js", "_patch"))
		));
	}

}
