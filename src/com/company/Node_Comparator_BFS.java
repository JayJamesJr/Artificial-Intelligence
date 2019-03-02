package com.company;

import java.util.Comparator;
/**
 * Node Comparator used for our Best First Search cost
 * comparison
 * to be passed into the priority queue
 * @author Jay James
 * @author Daniel Villa
 */
public class Node_Comparator_BFS implements Comparator<Node> {
    @Override
    public int compare(Node node, Node node1){
        if(node.getCost()>node1.getCost()){
            return 1;
        }
        if(node.getCost() < node1.getCost()){
            return -1;
        }
        return 0;

    }
}
