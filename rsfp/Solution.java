/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsfp;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author scipio
 */
public class Solution {

    ArrayList<Tree> solution, bestsolution;
    static long maxcount, backtothebest;
    static double T, cool;// localprob = 0.05;
    double fitness, bestfitness;
    int V, connections[], L;
    Integer edges[][];
    Random random;

    @Override
    public String toString() {
        String result = "";
        for (Tree tree : solution) {
            result += tree.toString() + "\t";
        }
        result += "Fitness:" + fitness + "\n";
        for (Tree tree : bestsolution) {
            result += tree.toString() + "\t";
        }
        result += "Best Fitness:" + bestfitness + "\n";
        return result;
    }

    /*private boolean treeWithMoreThan2Vexist() {
        for (Tree tree : solution) {
            if (tree.V.size() > 1) {
                return true;
            }
        }
        return false;
    }*/

    public Solution(int V, Integer[][] edges, int[] connections, int L) {
        this.V = V;
        this.edges = edges;
        this.connections = connections;
        this.L = L;
        int sortarg[] = new int[V];
        random = new Random();

        for (int i = 0; i < V; i++) {
            sortarg[i] = i;
        }
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < V - 1; i++) {
                for (int j = i + 1; j < V; j++) {
                    if (connections[sortarg[i]] > connections[sortarg[j]]) {
                        int temp = sortarg[i];
                        sortarg[i] = sortarg[j];
                        sortarg[j] = temp;
                        flag = true;
                    }
                }
            }
        }
        solution = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            Node newnode = new Node(i, null), node = null;
            Tree tree = null;
            for (int j = 0; j < solution.size() && tree == null; j++) {
                for (int n = 0; n < solution.get(j).V.size(); n++) {
                    node = solution.get(j).V.get(n);
                    if (solution.get(j).nodeCanbeAdded(node, newnode)) {
                        tree = solution.get(j);
                        break;
                    }
                }
            }
            if (tree == null) {
                tree = new Tree(edges, L, newnode);
                solution.add(tree);
            } else {
                tree.addNode(node, newnode);
            }
        }
        /*for (Tree tree : solution) {
            if (!tree.allvexists(tree.firstnode)) {
                System.out.println("bura");
            }
        }*/

        bestfitness = fitness = fitness();
        bestsolution = copySolution();
        /*
        for (Tree tree : bestsolution) {
            if (!tree.allvexists(tree.firstnode)) {
                System.out.println("bura");
            }
        }*/

    }

    public void run(double LB) {
        long counter = 0, limit = 0;
        while (counter++ < maxcount) {
            int toTree = random.nextInt(solution.size()), tree = random.nextInt(solution.size());
            while (toTree == tree) {
                tree = random.nextInt(solution.size());
            }
            /*if (!divideandmerge(toTree, tree) && random.nextDouble() < localprob) {
                localsearch();
            }*/
            if (divideandmerge(toTree, tree) && fitness > bestfitness) {
                counter = 0;
                bestfitness = fitness;
                bestsolution = copySolution();
                if (bestsolution.size() <= LB) {
                    break;
                }
                limit = 0;
                //System.out.println(solution.size() + " " + fitness);
            }

            if (limit++ > backtothebest) {
                limit = 0;
                if (Math.abs(fitness - bestfitness) > 0.00001) {
                    solution = bestsolution;
                    fitness = bestfitness;
                    bestsolution = copySolution();
                }

                /*
                for (Tree tree : solution) {
                    if (!tree.allvexists(tree.firstnode)) {
                        System.out.println("bura");
                    }
                }*/
            }
            //T *= cool;
        }
    }

    private boolean accept(int v1, int v2, int tr) {
        /*if (v1 == 1 && v2 - tr > 1) {
            return true;
        }
        if (v1 == 1 && v2 - tr == 1 && 2 * v2 - tr < 2 * v1 + tr) {
            return true;
        }
        if (v2 - tr > 1 && 2 * v2 - tr < 2 * v1 + tr) {
            return true;
        }*/
        double d = v1 - v2 + tr;
        if (d > 0)
            return true;
        if (/*(v2 - tr > 1 || (v1 == 1 && v2 - tr == 1)) &&*/ random.nextDouble() <= Math.exp(d / T)) {
            return true;

        }

        return false;
    }

    /*private void localsearch() {
        //int alltrees = solution.size();
        for (int tree1 = 0; tree1 < solution.size(); tree1++) {
            for (int tree2 = 0; tree1 < solution.size() && tree2 < solution.size(); tree2++) {
                if (tree1 == tree2) {
                    continue;
                }
                if (mergeSolution(tree1, tree2) || mergeSolution(tree2, tree1)) {
                    fitness = fitness();
                    continue;
                }
                Tree treetodivide = solution.get(tree2);
                int v2 = treetodivide.V.size(), v1 = solution.get(tree1).V.size();
                if (v2 == 1) {
                    continue;
                }
                //int allv = treetodivide.V.size();
                for (int v = 1; v < treetodivide.V.size(); v++) {
                    Node dividepoint = treetodivide.V.get(v);
                    Tree newtree = divideSolution(treetodivide, dividepoint);
                    int tr = newtree.V.size();
                    if (accept(v1, v2, tr, 0)
                            && (mergeSolution(tree1, solution.size() - 1) || mergeSolution(solution.size() - 1, tree1))) {
                        fitness = fitness();
                        break;
                    }
                    if (!mergeSolution(tree2, solution.size() - 1)) {
                        System.out.println("HATA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }

            }
        }
    }*/
    private boolean divideandmerge(int tree1, int tree2) {
        if (mergeSolution(tree1, tree2) || mergeSolution(tree2, tree1)) {
            fitness = fitness();
            return true;
        }
        Tree treetodivide = solution.get(tree2);
        int v2 = treetodivide.V.size(), v1 = solution.get(tree1).V.size();
        if (v2 == 1) {
            return false;
        }
        //Tree beforediv = treetodivide.copy();
        Node dividepoint = treetodivide.V.get(random.nextInt(v2 - 1) + 1);
        Tree newtree = divideSolution(treetodivide, dividepoint);
        int tr = newtree.V.size();
        if (accept(v1, v2, tr)
                && (mergeSolution(tree1, solution.size() - 1) || mergeSolution(solution.size() - 1, tree1))) {
            fitness = fitness();
            return true;
        }
        if (!mergeSolution(tree2, solution.size() - 1)) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return false;

    }

    /*public void randomDivide(int itercount) {
        Tree tree = solution.get(random.nextInt(solution.size()));
        while (itercount <= tabu + tree.divided || tree.V.size() < 2) {
            tree = solution.get(random.nextInt(solution.size()));
        }
        Node node = tree.V.get(random.nextInt(tree.V.size() - 1) + 1);
        divideSolution(tree, node, itercount);
    }*/
    public Tree divideSolution(Tree tree, Node dividepoint) {
        Tree newtree = tree.divedetree(dividepoint, null);
        solution.add(newtree);
        //fitness = fitness();
        return newtree;
        /*if (!newtree.allvexists(newtree.firstnode)) {
            System.out.println("bura");
        }
        if (!tree.allvexists(tree.firstnode)) {
            System.out.println("bura");
        }*/
    }

    public Node canmergeSolution(Tree toTree, Tree tree) {
        if (toTree.L.size() + tree.L.size() > toTree.maxL) {
            return null;
        }
        int posmod = random.nextInt(toTree.V.size());
        for (int i = 0; i < toTree.V.size(); i++) {
            int pos = (posmod + i) % toTree.V.size();
            Node tonode = toTree.V.get(pos);
            if (toTree.treeCanbeMerged(tonode, tree)) {
                return tonode;
            }
        }
        return null;
    }

    /*public boolean randomMerge(int iteration) {
        int toTree = random.nextInt(solution.size()), tree = random.nextInt(solution.size());
        while (toTree == tree) {
            tree = random.nextInt(solution.size());
        }
        if (mergeSolution(toTree, tree, iteration)) {
            fitness = fitness();
            return true;
        }
        return false;
    }*/
    public boolean mergeSolution(int toTreeno, int treeno) {
        Tree toTree = solution.get(toTreeno), tree = solution.get(treeno);
        Node toNode = canmergeSolution(toTree, tree);
        if (toNode == null) {
            return false;
        }
        toTree.mergetrees(toNode, tree);
        /*
        if (!toTree.allvexists(toTree.firstnode)) {
            System.out.println("bura");
        }*/
        solution.remove(tree);
        return true;
    }

    public ArrayList<Tree> copySolution() {
        ArrayList<Tree> newsolution = new ArrayList<>();
        for (int i = 0; i < solution.size(); i++) {
            Tree newtree = solution.get(i).copy();
            newsolution.add(newtree);
        }
        return newsolution;
    }

    public double fitness() {
        double total = 0;
        int beta = 0;
        for (int i = 0; i < solution.size(); i++) {
            int _T_ = solution.get(i).V.size();
            total += ((double) _T_ / V) * ((double) _T_ / V);
            if (_T_ == 1) {
                beta += 2;
            } else if (_T_ > 1) {
                beta += 1;
            }
        }
        return total / beta;
    }

}
