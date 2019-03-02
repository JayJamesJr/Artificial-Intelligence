package com.company;

import java.util.ArrayList;

/**
 * Node class used to store data that our
 * search algorithms will use to perform calculations
 * on
 * @author Jay James
 * @author Daniel Villa
 */

public class Node  {
    private int cost; // The cost each node will contain
    private int x; // The x coordinate in the array
    private int y; // The y coordinate in the array
    private int g; // The distance from the start to the node
    private int h; // The distance from the node to the destination
    Node predecessor;// The node that precedes current node in the path
    private int depth = 0; // Depth level of a given node
    private boolean isImpasse; // Verifies whether a node is passable
    private ArrayList<Node> neighbors; // List of neighboring nodes


    /**
     * Constructor for a given node
     *
     * @param cost cost of each node
     * @param x x coordinate
     * @param y y coordinate
     */
    public Node(int cost, int x, int y){
        this.cost = cost;
        this.x = x;
        this.y = y;
        neighbors = new ArrayList<>();


    }

    //******************* Setters and Getters ***************************
    public Node getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }


    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    public int getF(){
        return getG() + getH();
    }

    public boolean isImpasse() {
        return this.cost == 0;
    }

    public void setImpasse(boolean impasse) {
        isImpasse = impasse;
    }
    public void add(Node n){
        this.neighbors.add(n);
    }



    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getX() {
        return x;
    }

    //********************************************************************************

    public void displayLocation(){
        System.out.print("("+this.getY()+","+this.getY()+")" + " ");
    }











}
