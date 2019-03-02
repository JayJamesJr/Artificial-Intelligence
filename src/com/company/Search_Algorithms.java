package com.company;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * This class will read a file of text
 * containing a map into a 2D array
 * of nodes and will perform the informed
 * search methods:
 * Best First Search, Iterative Deepening Search, and A* Search
 * Each method will record :
 *
 * 1)	The cost of the path found
 * 2)	The number of nodes expanded
 * 3)	The maximum number of nodes held in memory
 * 4)	The runtime of the algorithm in milliseconds
 * 5)	The path as a sequence of coordinates (row, col), (row col), … , (row, col)
 *
 * @author Jay James, Daniel Villa
 * @ Version 11.0.2
 *
 */
public class Search_Algorithms {
    private String file; // The file we will use to read in our data and perform our search algorithms
    private Node[][] search_space; // The map of nodes we will use to perform our algorithms on
    private int start_x; // The x - coordinate of the starting position of our search
    private int start_y; // The y-coordinate of the starting position of our search
    private int goal_x; // The x-coordinate of the goal position of our search
    private int goal_y; // The y-coordinate of the goal position of our search
    private boolean node_found; // A boolean to alert an algorithm that a given node has been found
    private List<Node> closed_set;
    private Queue<Node> open_set;
    private List<Node> path;


    public Search_Algorithms(String file){
        search_space = readFile(file);
        closed_set = new ArrayList();
        open_set = new PriorityQueue<>(new Node_Comparator());
        path = new ArrayList();
    }

    /** This method take a file as input and return a 2D - array of nodes with coordinates
        *that we will use with our map traversal algorithms.
     * @return 2D array of type Node
    */
    public Node[][] readFile(String file){
        /* The lab file specified that the first three lines of our file will
         contain information regarding:
         A) The size of our search space
         B) The index of our starting location
         C) The index of our goal location

         The three arrays below will be used to store the date we will parse
         from the file using a scanner
         */

        String[] space_size; // Line from file is split and each index holds a size for each dimension of the array
        String[] start_location = new String[0]; // Line from file is split and each index holds a coordinate for our starting location
        String[] end_location = new String[0]; // Line from file is split and each index holds a coordinate for our ending location
        Node[][] search_space = new Node[0][0]; // Array to be returned after reading the file. Contains all the nodes in our search space
        int line = 0; // This line will be used to keep track of the index of the first dimension of our 2D array

        try{
            // This scanner will be used to read our file into the 2D array of nodes
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(file)));

            space_size = fileReader.nextLine().split(" ");
            start_location = fileReader.nextLine().split(" ");
            end_location = fileReader.nextLine().split(" ");
            search_space = new Node[Integer.parseInt(space_size[0])][Integer.parseInt(space_size[1])];
            // By the end of the file the value of index should be equal to the number of arrays in our 2D array
            while(fileReader.hasNextLine()){
                // Here we are going to parse the data from each line into
                // a string array containing the costs of each node
                String[] values = fileReader.nextLine().split(" ");
                // Afterwards we create an array of nodes that will store a node containing
                // a coordinate and a cost for each value on that line
                Node[] row = new Node[values.length];
                // Populate the node array with the integer values from the string array
                for(int i = 0; i < values.length; i++){
                    row[i] = new Node(Integer.parseInt(values[i]),line,i);
                    if(row[i].getCost() == 0){
                        // The lab specifies that a position with the value '0' is not passable
                        // Here we check if a node has a cost value of 0 and if it does we mark
                        // it so our algorithms know not to traverse it
                        row[i].setImpasse(true);
                    }
                }
                // Here we set the array at the current line equal to the array we created above
                search_space[line] = row;
                // Afterwards we move to the position of the next array that will be stored in our
                // 2D array
                line++;
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        // Our search_algorithms object contains these fields containing the locations of our starting and ending coordinates
        // We will use these arrays to store the data in those respective fields
        start_x = Integer.parseInt(start_location[0]);
        start_y = Integer.parseInt(start_location[1]);
        goal_x = Integer.parseInt(end_location[0]);
        goal_y = Integer.parseInt(end_location[1]);
        //search_space[start_x][start_x].setCost(0);
        return search_space;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Node[][] getSearch_space() {
        return search_space;
    }

    public void setSearch_space(Node[][] search_space) {
        this.search_space = search_space;
    }

    /**
     * Helper method used to display information about
     * the current search space to the user
     */
    public void display_search_space(){

        System.out.println("The starting location is: " + "("+this.start_x + "," + this.start_y+")");
        System.out.println("The goal location is: " +"("+this.goal_x + "," + this.goal_y+")");
        for(Node[] i: this.getSearch_space()){
            for(Node j : i){
                System.out.print(("("+j.getX() + "," + j.getY())+")" + " ");
            }
            System.out.println();
        }
        System.out.println("Current Traversal Costs:");
        for(Node[] i: this.getSearch_space()){
            for(Node j : i){
                System.out.print((+j.getCost()+ " "));
            }
            System.out.println();
        }
        System.out.println("Current Impasse Locations:" +"");
        for(Node[] i: this.getSearch_space()){
            for(Node j : i){
                if(j.isImpasse()){
                    System.out.print(("("+j.getX() + "," + j.getY())+")" + " ");
                }
            }

        }
        System.out.println();
    }

    /**
     *
     * @param current
     * @return List of nodes containing the generated successor nodes
     * of the current node
     */
    public List<Node> generate_successors(Node current){

        List<Node> successors = new ArrayList<>(); // The list containing successor nodes
        int x = current.getX(); // The x coordinate of the node we are testing
        int y = current.getY(); // The y coordinate of the node we are testing


        /* Since we are using a 2D array/Map there are four positions that
         *we want to check for neighbors. North,South,East, and West
         *We need to make sure that the coordinates we want to add are within
         *the bounds of the search space and that the space is also observable
        */

        // Checks the northern position of the current node
        if(isWithinBounds(x-1) && !search_space[x-1][y].isImpasse()){
            search_space[x-1][y].setH(manhattan_distance(search_space[x-1][y],search_space[goal_x][goal_y]));
            search_space[x-1][y].setG(current.getCost() +search_space[x-1][y].getCost());
            successors.add(search_space[x-1][y]);
        }
        // Checks the southern position of the current node
        if(isWithinBounds(x+1) && !search_space[x+1][y].isImpasse()){
            search_space[x+1][y].setH(manhattan_distance(search_space[x+1][y],search_space[goal_x][goal_y]));
            search_space[x+1][y].setG(current.getCost() +search_space[x+1][y].getCost());
            successors.add(search_space[x+1][y]);
        }
        // Checks the position to the left of the current node
        if(isWithinBounds(y-1) && !search_space[x][y-1].isImpasse()){
            search_space[x][y-1].setH(manhattan_distance(search_space[x][y-1],search_space[goal_x][goal_y]));
            search_space[x][y-1].setG(current.getCost() +search_space[x][y-1].getCost());
            successors.add(search_space[x][y-1]);
        }
        // Checks the position to the right of the current node
        if(isWithinBounds(y+1) && !search_space[x][y+1].isImpasse()){
            search_space[x][y+1].setH(manhattan_distance(search_space[x][y+1],search_space[goal_x][goal_y]));
            search_space[x][y+1].setG(current.getCost() +search_space[x][y+1].getCost());
            successors.add(search_space[x][y+1]);
        }

        // Finally returns the list of successors
        return successors;
    }

    /**
     *
     * @param position
     * @return boolean indicating whether a given position is
     * within array bounds
     */
    public boolean isWithinBounds(int position){
        return ((position  >= 0) && (position < search_space.length));
    }

    public Node findMin(List<Node> nodes){
        Node min = nodes.get(0);
        int cost = Integer.MAX_VALUE;
        for(Node node: nodes){
            if(cost > node.getCost()){
                cost = node.getCost();
                min = node;
            }
        }
        return min;
    }
    /** The first traversal method specified in our lab file
     *This method uses a queue as the fringe that will operate on each node
     *It will span the breadth or each level of the tree until the node is found
     */

    public void BFS() {


                long startTime = System.currentTimeMillis();
                long endTime = startTime + 180000;
                int num_nodes_expanded = 1;
                int total_cost = 0;
                System.out.println("Implementing Best-First-Search from start location:");

                // As stated above, the fringe operating on each node is a queue
                open_set = new PriorityQueue<Node>(new Node_Comparator_BFS());
                closed_set = new ArrayList();
                // Create a node containing the starting location of our search algorithm
                Node start = search_space[start_x][start_y];
                Node destination = search_space[start_x][start_y];
                // List containing expanded nodes
                List<Node> visited_states = new ArrayList<>();


                // Push start location into the stack
                open_set.add(start);
                while (!open_set.isEmpty()) {
                    if(System.currentTimeMillis() > endTime){
                        System.out.println("Time limit exceeded! Runtime -> 3 minutes");
                        return;
                    }
                    // While there are nodes that can be expanded
                    // Remove a node from the queue
                    Node current = open_set.remove();
                    System.out.print("("+current.getX()+","+current.getY()+")");
                    total_cost += current.getCost();
                    // Mark the node as visited
                    closed_set.add(current);
                    // Check to see if the node we have just visited is our goal node
                    if (goal_state(current)) {
                        System.out.println("Found goal node at" + "(" + goal_x + "," + goal_y + ")");
                        break;

                    }
                    // Here we expand each unvisited neighbor and push it into the queue
                    for (Node search : generate_successors(current)) {
                        if (!closed_set.contains(search)) {
                            num_nodes_expanded++;
                            closed_set.add(search);
                            open_set.add(search);
                            search.setPredecessor(current);
                        }
                    }


                }
                System.out.println("Best First Search Runtime -> " + (double)((System.currentTimeMillis()-startTime)) + " milliseconds");
                System.out.println("Number of nodes expanded: " + num_nodes_expanded);
                open_set.clear();
                closed_set.clear();

            }








    /**
     * Takes a node and a depth level and performs
     * iterative deepening up to that depth
     * @param start the start location in our map
     * @param depth the specified depth level
     */

    public void IDDFS(Node start, int depth){

        System.out.println("Iterative Deepening Depth First Search:");
        for(int i = 0; i < depth && ! node_found; i++){
            int nodeNum = depth_limited_search(start,i);
            if(nodeNum == -1) {
                System.out.println("\n3 minutes exceeded");
                return;
            }
            System.out.println("Current Depth: " + i);
            System.out.println("Expanded Nodes: " + nodeNum);

        }

    }



    /**
     * This method performs depth first search up to a given depth
     * uses a stack a the fringe and returns the number of expanded nodes
     * for each depth level
     * @param problem The node we are starting from to reach the solution or goal node
     * @param limit The depth limit set by the user
     * @return number of expanded nodes
     */

    public int depth_limited_search(Node problem, int limit){
        int num_nodes_expanded = 1;
        int path_cost = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime+180000; //3 minutes
        // Here like a normal depth first search a stack is used as the
        // fringe

        Stack<Node> fringe = new Stack<>();
        // Here we set the depth of the starting node that will be
        // passed into our fringe
        problem.setDepth(0);
        // The list defined below will be used to store the nodes
        Set<Node> visited = new HashSet<>();
        // Insert our start location into the fringe
        fringe.push(problem);

        while(!fringe.isEmpty()){
            // Pop a node from the fringe
            if(System.currentTimeMillis() > endTime){
                System.out.println("Time limit Exceeded. Runtime -> 3 minutes");
                return -1;
            }
            Node current = fringe.pop();
            path_cost += current.getCost();
            System.out.print("("+current.getX()+","+current.getY()+")");
            // Check to see if the current node is equal to the destination
            if(goal_state(current)){
                System.out.println("Node has been found!" + "("+current.getX()+","+current.getY()+")");

                // This global variable will stop the wrapper once we find the
                // destination node
                node_found = true;
                System.out.println("Total path cost: " + path_cost);
                return num_nodes_expanded;
            }
            // If the depth exceeds the limit then we bre
            if(current.getDepth() >= limit){
                break;

            }
            for(Node successor: generate_successors(current)){

                successor.setDepth(current.getDepth() + 1);
                if(!visited.contains(successor)){
                    num_nodes_expanded++;
                    visited.add(successor);
                    fringe.push(successor);
                }

            }

        }
        return num_nodes_expanded;

    }

    /**
     *
     * @param current The current node that was removed from of fringe
     * @return a boolean indicating whether of not our goal state has been reached.
     */
    public boolean goal_state(Node current){
        return (current.equals( search_space[goal_x][goal_y]));
    }


    /** This function takes the difference between the x and y values and adds them together
     *to find the distance from the current node to the destination
     * @param node1 current node
     * @param node2 destination
     * @return the manhattan distance from the current node to the destination
     */

    public int manhattan_distance(Node node1, Node node2){

        return((Math.abs(node1.getX()-node2.getX())) + (Math.abs(node1.getY()-node2.getY())));
    }

    /**
     *
     * @param destination the goal state or destination we're trying to reach
     * @return An array list of nodes containing the most efficient path to the
     * destination
     */
    public ArrayList<Node> path(Node destination){
        ArrayList<Node> path = new ArrayList<>();
        int path_cost = 0;
        while(destination.getPredecessor()!= null){
            path.add(destination);
            destination.displayLocation();
            destination = destination.getPredecessor();
        }
        Collections.reverse(path);
        return path;

    }


    /** This implementation of A* search uses a priority queue which
    * takes the cheapest path to a goal node using the manhattan distance
    * or manhattan heuristic. It constantly checks to see if there is a
    * better path from a given position to the destination
     *
     */

    public void a_star_search(){
        long startTime = System.currentTimeMillis();
        long endTime = startTime+180000; //3 minutes
        int total_cost = 0;
        int num_nodes_expanded = 1;
        open_set = new PriorityQueue<>(new Node_Comparator());
        System.out.println("Implementing A-Star Search:");
        Node start = search_space[start_x][start_y];
        Node destination = search_space[goal_x][goal_y];
        start.setH(manhattan_distance(start,destination));

        open_set.add(start);
        System.out.println("Path to Goal:");
        while(!open_set.isEmpty()){
            if(System.currentTimeMillis() > endTime){
                System.out.println("Time limit exceeded! Runtime -> 3 minutes");
            }
            // The priority queue or heap will give us the best node
            // by usage of its comparator
            Node current = open_set.poll();
            /* We keep track of the predecessor nodes in this case to later display
               the path taken to reach the goal state
            */
            if(current.getPredecessor() != null){
                path.add(current.getPredecessor());
                System.out.print("("+current.getPredecessor().getX()+","+ current.getPredecessor().getY()+")");
                total_cost += current.getPredecessor().getCost();


            }
            // Checks for the goal state here
            if(goal_state(current)){
                path.add(current);
                System.out.print("Goal State Found:" + "("+current.getX()+","+current.getY()+")" + "\n");
                System.out.println("Path Cost from: " + "("+start.getX()+","+start.getY()+")"+ "to -->" + "("+current.getX()+","+current.getY()+")" + ":" + total_cost);
                System.out.println("A* Search Runtime -> " + ((System.currentTimeMillis()-startTime)) + " milliseconds");
                System.out.print("Number of nodes expanded: " + num_nodes_expanded);
                open_set.clear();
                closed_set.clear();
                return;
            }
            /* After visiting a node, we add it to the closed set and remove it from the open set
                so we don't visit or expand a given node twice
            */
            open_set.remove(current);
            closed_set.add(current);

            /* Here we iterate through the generated successors
                checking if a given node has been visited or not
            */
            for(Node neighbor: generate_successors(current)){

                if(closed_set.contains(neighbor)){
                    continue;
                }
                if(!open_set.contains(neighbor)){
                    num_nodes_expanded++;
                    open_set.add(neighbor);
                }
                // We set the predecessor to the node that was just expanded
                neighbor.setPredecessor(current);
            }
        }
        open_set.clear();
        closed_set.clear();

    }

    public int getStart_x() {
        return start_x;
    }

    public void setStart_x(int start_x) {
        this.start_x = start_x;
    }

    public int getStart_y() {
        return start_y;
    }

    public void setStart_y(int start_y) {
        this.start_y = start_y;
    }

    public int getGoal_x() {
        return goal_x;
    }

    public void setGoal_x(int goal_x) {
        this.goal_x = goal_x;
    }

    public int getGoal_y() {
        return goal_y;
    }

    public void setGoal_y(int goal_y) {
        this.goal_y = goal_y;
    }

    public boolean isNode_found() {
        return node_found;
    }

    public void setNode_found(boolean node_found) {
        this.node_found = node_found;
    }
    public List<Node> getClosed_set() {
        return closed_set;
    }

    public void setClosed_set(List<Node> closed_set) {
        this.closed_set = closed_set;
    }

    public Queue<Node> getOpen_set() {
        return open_set;
    }

    public void setOpen_set(Queue<Node> open_set) {
        this.open_set = open_set;
    }

    public List<Node> getPath() {
        return path;
    }

    public void setPath(List<Node> path) {
        this.path = path;
    }





}


