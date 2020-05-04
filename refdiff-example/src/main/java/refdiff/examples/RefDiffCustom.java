package refdiff.examples;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import refdiff.core.RefDiff;
import refdiff.core.diff.CstDiff;
import refdiff.core.io.FilePathFilter;
import refdiff.core.io.GitHelper;
import refdiff.core.io.SourceFileSet;
import refdiff.core.util.PairBeforeAfter;
import refdiff.parsers.LanguagePlugin;

public class RefDiffCustom extends RefDiff{

	public RefDiffCustom(LanguagePlugin parser) {
		super(parser);
	}
	
	/*
	 * Override GitHelper.getSourcesBeforeAndAfterCommit to analyze first parent commit.
	 */
	private PairBeforeAfter<SourceFileSet> getSourcesBeforeAndAfterCommit(Repository repository, String commitId, FilePathFilter fileExtensions) {
		try (RevWalk rw = new RevWalk(repository)) {
			RevCommit commitAfter = rw.parseCommit(repository.resolve(commitId));
			
			if (commitAfter.getParentCount() == 0) {
				throw new RuntimeException("There is no parent to commit: " + commitAfter.getName());
			}
			if (commitAfter.getParentCount() > 1) {
				System.out.println("Analizing commit with more than one parent: " + commitAfter.getName());
			}
			
			RevCommit commitBefore = rw.parseCommit(commitAfter.getParent(0));
			return GitHelper.getSourcesBeforeAndAfterCommit(repository, commitBefore, commitAfter, fileExtensions);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public CstDiff computeDiffForCommit(File gitRepository, String commitSha1) {
		try (Repository repo = GitHelper.openRepository(gitRepository)) {
			PairBeforeAfter<SourceFileSet> beforeAndAfter = this.getSourcesBeforeAndAfterCommit(repo, commitSha1, this.getFileFilter());
			return this.getComparator().compare(beforeAndAfter);
		}
	}

}
