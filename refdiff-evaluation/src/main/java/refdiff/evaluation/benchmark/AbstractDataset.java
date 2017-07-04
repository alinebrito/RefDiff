package refdiff.evaluation.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import refdiff.core.api.RefactoringType;
import refdiff.evaluation.utils.RefactoringSet;

public class AbstractDataset {

    protected final List<CommitEntry> commits;

    public AbstractDataset() {
        commits = new ArrayList<>();
    }

    public void add(RefactoringSet rs) {
        CommitEntry entry = new CommitEntry(rs.getProject(), rs.getRevision());
        commits.add(entry);
        entry.expected.add(rs.getRefactorings());
    }

    public List<RefactoringSet> getExpected() {
        return commits.stream().map(e -> e.expected).collect(Collectors.toList());
    }

    public List<RefactoringSet> getNotExpected() {
        return commits.stream().map(e -> e.notExpected).collect(Collectors.toList());
    }

    public RefactoringSet remove(String repo, String sha1) {
        for (int i = 0; i < commits.size(); i++) {
            CommitEntry c = commits.get(i);
            if (c.expected.getProject().equals(repo) && c.expected.getRevision().equals(sha1)) {
                return commits.remove(i).expected;
            }
        }
        throw new RuntimeException(String.format("Not found: %s %s", repo, sha1));
    }

    public CommitEntry commit(String repo, String sha1) {
        for (int i = 0; i < commits.size(); i++) {
            CommitEntry c = commits.get(i);
            if (c.expected.getProject().equals(repo) && c.expected.getRevision().equals(sha1)) {
                return commits.get(i);
            }
        }
        throw new RuntimeException(String.format("Not found: %s %s", repo, sha1));
    }

    public static class CommitEntry {
        private final RefactoringSet expected;
        private final RefactoringSet notExpected;

        public CommitEntry(String repo, String sha1) {
            this.expected = new RefactoringSet(repo, sha1);
            this.notExpected = new RefactoringSet(repo, sha1);
        }

        public CommitEntry addTP(String refType, String entityBefore, String entityAfter) {
            this.expected.add(RefactoringType.fromName(refType), entityBefore, entityAfter);
            return this;
        }

        public CommitEntry addFP(String refType, String entityBefore, String entityAfter) {
            this.notExpected.add(RefactoringType.fromName(refType), entityBefore, entityAfter);
            return this;
        }

        public RefactoringSet getExpected() {
            return expected;
        }

        public RefactoringSet getNotExpected() {
            return notExpected;
        }
    }
}
