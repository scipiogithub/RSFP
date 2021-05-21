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
public class Tree {

    int maxL;
    Integer edges[][];
    ArrayList<Integer> L;
    ArrayList<Node> V;
    Node firstnode;

    public Tree() {

    }
    /*
    public boolean allvexists(Node node) {
        for (Node n : V) {
            if (n.owner != this) {
                return false;
            }
        }
        if (!V.contains(node)) {
            return false;
        }
        for (Node nextnode : node.nextnodes) {
            if (!V.contains(nextnode) || !allvexists(nextnode)) {
                return false;
            }
        }
        return true;
    }*/

    @Override
    public String toString() {

        return "["
                + tostr(firstnode, null)
                + ")]"; //To change body of generated methods, choose Tools | Templates.
    }

    private String tostr(Node node, String str) {
        String mystr = (str != null ? str + ", " : "(") + node.id;
        String result = "";//, end = node.nextnodes.size() > 1 ? "\n" : "";
        for (int i = 0; i < node.nextnodes.size(); i++) {
            Node nextode = node.nextnodes.get(i);
            result += tostr(nextode, mystr);
            if (i < node.nextnodes.size() - 1) {
                result += ") ";
            }
        }
        if (result.equals("")) {
            result = mystr;
        }
        return result;
    }

    public Tree copy() {
        Tree mycopy = new Tree();
        mycopy.edges = edges;
        mycopy.maxL = maxL;
        mycopy.L = new ArrayList<>(L);
        mycopy.V = new ArrayList<>();
        for (int i = 0; i < V.size(); i++) {
            Node node = V.get(i);
            Node copiednode = new Node(node.id, mycopy);
            mycopy.V.add(copiednode);
        }
        createNodes(firstnode, mycopy);

        return mycopy;
    }

    private void createNodes(Node node, Tree copiedTree) {
        int index = V.indexOf(node);
        Node newnode = copiedTree.V.get(index);
        if (node.previousnode != null) {
            int prevind = V.indexOf(node.previousnode);
            Node prevnode = copiedTree.V.get(prevind);
            newnode.previousnode = prevnode;
        } else {
            newnode.previousnode = null;
            copiedTree.firstnode = newnode;
        }
        newnode.nextnodes = new ArrayList<>();
        for (Node nextnode : node.nextnodes) {
            index = V.indexOf(nextnode);
            Node copiednextnode = copiedTree.V.get(index);
            newnode.nextnodes.add(copiednextnode);
            createNodes(nextnode, copiedTree);
        }
    }

    public Tree(Integer[][] edges, int maxL, Node firstnode) {
        this.edges = edges;
        this.maxL = maxL;
        L = new ArrayList<>();
        V = new ArrayList<>();
        V.add(firstnode);
        this.firstnode = firstnode;
        firstnode.previousnode = null;
        firstnode.owner = this;
    }

    public boolean nodeCanbeAdded(Node node, Node candidate) {
        if (node.owner != this || candidate.owner == this || L.size() == maxL) {
            return false;
        }
        Integer color = edges[node.id][candidate.id];
        if (color >= 0 && !L.contains(color)) {
            return true;
        }
        return false;
    }

    public boolean treeCanbeMerged(Node tonode, Tree candidate) {
        if (tonode.owner != this || L.size() == maxL) {
            return false;
        }
        Integer connectioncolor = edges[tonode.id][candidate.firstnode.id];
        if (candidate.L.contains(connectioncolor)) {
            return false;
        }
        if (!nodeCanbeAdded(tonode, candidate.firstnode)) {
            return false;
        }
        for (Integer color : candidate.L) {
            if (L.contains(color)) {
                return false;
            }
        }
        return true;

    }

    public void mergetrees(Node mergepoint, Tree newtree) {
        V.addAll(newtree.V);
        L.add(edges[mergepoint.id][newtree.firstnode.id]);
        L.addAll(newtree.L);
        mergepoint.nextnodes.add(newtree.firstnode);
        newtree.firstnode.previousnode = mergepoint;
        for (Node node : newtree.V) {
            node.owner = this;
        }
    }

    public void addNode(Node tonode, Node candidate) {
        tonode.nextnodes.add(candidate);
        candidate.previousnode = tonode;
        candidate.nextnodes.clear();
        candidate.owner = this;
        V.add(candidate);
        int color = edges[candidate.id][tonode.id];
        L.add(color);
    }

    public Tree divedetree(Node dividepoint, Tree newtree) {
        if (newtree == null) {
            Node prevnode = dividepoint.previousnode;
            prevnode.nextnodes.remove(dividepoint);
            L.remove(edges[prevnode.id][dividepoint.id]);
            V.remove(dividepoint);

            newtree = new Tree(edges, maxL, dividepoint);
            dividepoint.owner = newtree;
        }

        for (Node nextnode : dividepoint.nextnodes) {
            L.remove(edges[dividepoint.id][nextnode.id]);
            newtree.L.add(edges[dividepoint.id][nextnode.id]);
            V.remove(nextnode);
            newtree.V.add(nextnode);
            nextnode.owner = newtree;
            newtree = divedetree(nextnode, newtree);
        }

        return newtree;
    }

}
