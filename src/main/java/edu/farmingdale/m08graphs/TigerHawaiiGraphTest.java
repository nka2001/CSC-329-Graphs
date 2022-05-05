/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.farmingdale.m08graphs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author gerstl
 */
public class TigerHawaiiGraphTest implements RunTest {

    public String runTest() {
        // this uses some of the Tiger Hawaii dataset
        // available at http://www.inf.udec.cl/~jfuentess/datasets/graphs.php
        var dataFile = new File("data_files" + File.separator + "tiger_map_hawaii.pg");
        // format is : (1) number of nodes, (2) number of edges, (3-end) from to 
        String parent = dataFile.getAbsoluteFile().getParent();
        System.out.println("Data file is in the directory : " + parent);
        if (!dataFile.exists()) {
            return "E1001";
        }
        Scanner scanner;
        try {
            scanner = new Scanner(dataFile);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("The file tiger_map_hawaii.pg should be in the directory " + parent);
            return "E1002";
        }
        // read the size
        int nodeCount = scanner.nextInt();
        // read the node size (we don't care, but quality control)
        int edgeCount = scanner.nextInt();

        // undirected?
        var myGraph = new Graph(nodeCount, false);

        org.jgrapht.Graph<Integer, DefaultEdge> otherGraph = new SimpleGraph<>(DefaultEdge.class);
        for (int i = 0; i < nodeCount; ++i) {
            otherGraph.addVertex(i);
        }
        // read each pair
        while (scanner.hasNext()) {
            int node1 = scanner.nextInt();
            int node2 = scanner.nextInt();
            // add both
            myGraph.addEdge(node1, node2);
            otherGraph.addEdge(node1, node2);
        }
        if (otherGraph.edgeSet().size() != edgeCount) {
            System.err.println("edge count is " + edgeCount + " but we found " + otherGraph.edgeSet().size() + " nodes");
            return "E1003";
        }
 
        System.out.println("In the end, " + otherGraph.edgeSet().size() + " edges were added ");
        // check connected components
        var otherConnectivity = new ConnectivityInspector(otherGraph);
        var otherListOfComponentMembers = otherConnectivity.connectedSets();
        System.out.println("Per jgrapht, there are " + otherListOfComponentMembers.size() + " sets");
        int numberOfComponents = myGraph.setConnectedComponents();
        System.out.println("Per us, there are " + numberOfComponents + " sets");
        if (numberOfComponents != otherListOfComponentMembers.size()) {
            return "E1004";
        }
        // the jgrapht library returns a List<Set<Integer>> denoting the 
        // connected Components. We now convert ours to the same, then do a bit
        // of set magic
        // convert
        var ourListOfComponentMembers
                = new ArrayList<HashSet<Integer>>();
        for (int i = 0; i < numberOfComponents; ++i) {
            // sigh. Wish Collections.nCopies didn't just link to the same one
            ourListOfComponentMembers.add(new HashSet<>());
        }
        // now for each vertex, add the number to the correct set
        for (int currentVertex = 0; currentVertex < nodeCount; ++currentVertex) {
            ourListOfComponentMembers.get(myGraph.getComponentNumber(currentVertex)).add(currentVertex);
        }

        // now compare the lists, ignoring the order by making them into sets
        if (!(new HashSet<>(otherListOfComponentMembers).
                equals(new HashSet<>(ourListOfComponentMembers)))) {
            return "E1005";
        }
        return "";
    }
    @Override public String getTestName(){
        return "Tiger Hawaii test (slow)";
    }
}
