package refdiff.examples;

public class Main {

	public static void main(String[] args) {
		
		String nameProject = args[0];
		String commit = args[1];

		System.out.println(nameProject + " " + commit);
		
		RefDiffRunJs refDiffRunJs = new RefDiffRunJs();
		refDiffRunJs.run(nameProject, commit);
		System.out.println("End!");

	}

}
