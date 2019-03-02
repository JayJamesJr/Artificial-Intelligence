package com.company;
public class Main {

    public static void main(String[] args){
//    	String file = "C://Users//jayjj//IdeaProjects//AI_Lab1//src//com//company//test.txt";
//    	Search_Algorithms sa = new Search_Algorithms(file);
//		sa.display_search_space();
//		System.out.println();
//		sa.BFS();
//		sa.IDDFS(sa.getSearch_space()[sa.getStart_x()][sa.getStart_y()],10);
//		System.out.println();
//		Search_Algorithms sa1 = new Search_Algorithms("C://Users//jayjj//IdeaProjects//AI_Lab1//src//com//company//test2.txt");
//		sa1.display_search_space();
//		System.out.println();
//		sa1.BFS();
//		sa1.IDDFS(sa.getSearch_space()[sa1.getStart_x()][sa1.getStart_y()],10);
		Search_Algorithms sa3 = new Search_Algorithms("C://Users//jayjj//IdeaProjects//AI_Lab1//src//com//company//test3.txt");
		sa3.display_search_space();
		sa3.BFS();
		sa3.IDDFS(sa3.getSearch_space()[sa3.getStart_x()][sa3.getStart_y()],45);
		sa3.a_star_search();




	}

}
