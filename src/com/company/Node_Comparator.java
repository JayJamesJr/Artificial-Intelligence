package com.company;

import java.util.Comparator;

/**
 * Node Comparator used for our Manhattan Distance heuristic
 * to be passed into the priority queue
 * @author Jay James
 * @author Daniel Villa
 */
public class Node_Comparator implements Comparator<Node> {
    @Override
    public int compare(Node node, Node node1){
        if(node.getF() > node1.getF()){
            return 1;
        }
        if(node.getF() < node1.getF()){
            return -1;
        }
        return 0;

    }
}
