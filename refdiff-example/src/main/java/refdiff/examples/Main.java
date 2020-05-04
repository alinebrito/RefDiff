package refdiff.examples;

public class Main {

	public static void main(String[] args) {
		
		String nameProject = args[0];
		String commit = args[1];
		
		//FP due to bug (left name) dist.axios.exports#ontimeout
//		String nameProject = "axios/axios";
//		String commit = "960e1c879892ac6e1c83a798c06b9907e35ad2df";
		
		//Incomplete name due to bug (left name) dist.axios.exports#ontimeout
		//lib.adapters.xhr.exports#onreadystatechange to lib.adapters.xhr.xhrAdapter#onreadystatechange
//		String nameProject = "axios/axios";
//		String commit = "4bbde9ae6c81a47234c50120efa84d29ff39f771";
		
		//Confirmed internal_move (Danilo Study)
//		String nameProject = "angular/angular.js";
//		String commit = "560951e9881b5f772262804384b4da9f673b925e";
		
		//anonymous function utils.doclint.preprocessor.index#exports
//		String nameProject = "puppeteer/puppeteer";
//		String commit = "1be7545b7098ef6c286c3a4992d7bd372995db6d";
		
		//anonymous module scripts.error-codes.extract-errors.exports#addToErrorMap
//		String nameProject = "facebook/react";
//		String commit = "42c3c967d1e4ca4731b47866f2090bc34caa086c";
		
		//two parents
//		String nameProject = "facebook/react";
//		String commit = "5586b3022c667bec0d797a36691365b787437ce2";//2 parents
		
		System.out.println(nameProject + " " + commit);
		
		RefDiffRunJs refDiffRunJs = new RefDiffRunJs();
		refDiffRunJs.run(nameProject, commit);
		System.out.println("End!");

	}

}
