/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsfp;

import java.util.ArrayList;

/**
 *
 * @author scipio
 */
public class Node {
    int id;
    Tree owner;
    Node previousnode;
    ArrayList<Node> nextnodes;
    
    public Node() {
        
    }
    
    public Node(int id, Tree owner) {
        this.id = id;
        this.owner = owner;
        nextnodes = new ArrayList<>();
    }
    
    
}
