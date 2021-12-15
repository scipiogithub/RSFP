/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsfp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author scipio
 */
public class RSFP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        /*int totree = solution.solution.indexOf(solution.randomDivide());
        System.out.println(solution);
        //solution.mergeSolution(totree, solution.solution.size() - 1);
        for (int i = 0; i < 10; i++) {
            if (solution.randomMerge()) {
                System.out.println(solution);
                System.out.println(i);
                break;
            }
        }*/
        String dataset[] = {"small.csv", "large.csv"};
        Solution.T = 1.0;
        //Solution.cool = 0.9999;
        for (int j = 0; j < 2; j++) {
            ArrayList<double[]> instances = readfile(dataset[j]);
            for (int i = 0; i < instances.size(); i++) {
                double instance[] = instances.get(i);
                Solution.maxcount = (int) Math.pow(instance[0], 3.75);
                Solution.backtothebest = (int) Math.pow(Solution.maxcount, 3.75 * 0.2);
                double result[] = run(instance);
                String line = String.format("%d;%d;%d;%.1f;%.3f;%.4f", (int) instance[0], (int) instance[1],
                        (int) instance[2], result[0], result[1], result[2]);
                System.out.println(line);
            }
        }
        /*double params[] = {2.25};
        int testsize = 20;
        int testdata[][] = {{1, 4, 10, 12, 18}, {0, 7, 10, 16, 26}};
        String dataset[] = {"small.csv", "large.csv"};
        for (double param : params) {
            //double pc[][] = new double[2][5], prev[][] = new double[2][5];
            Solution.T = 2;
            System.out.println(String.format("%.2f", param));
            for (int j = 0; j < testdata.length; j++) {
                ArrayList<double[]> instances = readfile(dataset[j]);
                for (int i = 0; i < testdata[j].length; i++) {
                    double totalres0 = 0, totalres1 = 0, totalres2 = 0;
                    double instance[] = instances.get(testdata[j][i]);
                    Solution.maxcount = (long) Math.pow(instance[0], 3.75);
                    Solution.backtothebest = (long) Math.pow(Solution.maxcount, 3.75 * 0.2);
                    for (int t = 0; t < testsize; t++) {
                        double result[] = run(instance);
                        totalres0 += result[0];
                        totalres1 += result[1];
                        totalres2 += result[2];
                        /*String line = String.format("%d;%d;%d;%.1f;%.3f;%.4f", (int) instance[0], (int) instance[1],
                            (int) instance[2], result[0], result[1], result[2]);
                    System.out.println(line);*//*
                    }
                    String line = String.format("%d;%d;%d;%.1f;%.3f;%.4f", (int) instance[0], (int) instance[1],
                            (int) instance[2], totalres0 / testsize, totalres1 / testsize, totalres2 / testsize);
                    System.out.println(line);
                }

            }
        }*/
    }

    private static double[] run(double instance[]) {
        int V = (int) instance[0], E = (int) instance[1], L = (int) instance[2];
        double LB = Math.ceil(V / (instance[2] + 1));
        double result[] = new double[3];

        //System.out.println(Solution.maxcount + " " + Solution.backtothebest);
        long totaltime = 0;
        double totalfitness = 0;
        int totaltrees = 0;
        for (int i = 0; i < 5; i++) {
            ArrayList data = readfile(V, E, L, i);
            Integer edges[][] = (Integer[][]) data.get(0);
            int connections[] = (int[]) data.get(1), sortarg[] = new int[V];
            long t = System.currentTimeMillis();
            Solution solution = new Solution(V, edges, connections, L);
            solution.run(LB);
            t = System.currentTimeMillis() - t;
            /*System.out.println(i + " ===========");
            System.out.println(solution);*/
            totaltime += t;
            totaltrees += solution.bestsolution.size();
            totalfitness += solution.bestfitness;

        }
        result[0] = totaltrees / 5.0;
        result[1] = totaltime / 5000.0;
        result[2] = totalfitness / 5.0;

        return result;
    }

    public static ArrayList<double[]> readfile(String filename) {
        filename = ".../RSFP/" + filename;
        BufferedReader file;
        ArrayList<double[]> instances = new ArrayList<>();
        try {
            file = new BufferedReader(new FileReader(filename));
            while (true) {
                String line = file.readLine();
                if (line == null) {
                    break;
                }
                String parsed[] = line.split(";");
                double instance[] = new double[4];
                instance[0] = Double.parseDouble(parsed[1]);
                instance[1] = Double.parseDouble(parsed[2]);
                instance[2] = Double.parseDouble(parsed[3]);
                instance[3] = Double.parseDouble(parsed[4]);
                instances.add(instance);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return instances;
    }

    public static ArrayList readfile(int v, int e, int L, int s) {
        String filename = ".../RSFP/instances/" + v
                + "_" + e + "_" + L + "_" + s + ".csv";
        BufferedReader file;
        Integer edges[][] = new Integer[v][v];
        ArrayList<Integer> allcolors = new ArrayList<>();
        int connections[] = new int[v];
        try {
            file = new BufferedReader(new FileReader(filename));

            for (int i = 0; i < v; i++) {
                for (int j = 0; j < v; j++) {
                    edges[i][j] = new Integer(-1);
                }
            }

            while (true) {
                String line = file.readLine();
                if (line == null) {
                    break;
                }
                String parsed[] = line.split(";");
                int v1 = Integer.parseInt(parsed[0]), v2 = Integer.parseInt(parsed[1]), l = Integer.parseInt(parsed[2]);
                Integer val = contains(allcolors, l);
                if (val == null) {
                    val = new Integer(l);
                    allcolors.add(val);
                }
                connections[v1] += 1;
                connections[v2] += 1;
                edges[v1][v2] = edges[v2][v1] = val;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        ArrayList result = new ArrayList();
        result.add(edges);
        result.add(connections);
        return result;
    }

    private static Integer contains(ArrayList<Integer> array, int val) {
        for (Integer a : array) {
            if (a.intValue() == val) {
                return a;
            }
        }
        return null;
    }

}
