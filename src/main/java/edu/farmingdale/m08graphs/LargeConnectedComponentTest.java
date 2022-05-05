/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.farmingdale.m08graphs;

import java.util.ArrayList;
import java.util.HashSet;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.util.Random;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

/**
 *
 * @author gerstl
 */
public class LargeConnectedComponentTest implements RunTest {

    @Override
    public String runTest() {
        final int NODE_COUNT = 10_000;
        var myGraph = new Graph(NODE_COUNT, false);

        org.jgrapht.Graph<Integer, DefaultEdge> otherGraph = new SimpleGraph<>(DefaultEdge.class);
        for (int i = 0; i < NODE_COUNT; ++i) {
            otherGraph.addVertex(i);
        }
        var random = new Random();
        for (int i = 0; i < NODE_COUNT ; ++i) {
            int randomFrom = random.nextInt(NODE_COUNT - 1);
            int randomTo = random.nextInt(NODE_COUNT - 1);
            if (randomFrom == randomTo) {
                continue;
            }
            if (otherGraph.containsEdge(randomFrom, randomTo)
                    != myGraph.edgeExists(randomFrom, randomTo)) {
                System.out.println("Hmm, it thinks an edge " + randomFrom + " to " + randomTo
                        + ((otherGraph.containsEdge(randomFrom, randomTo)) ? " Exists" : " Doesn't exist"));
                System.out.println("we thing that the edge "
                        + ((myGraph.edgeExists(randomFrom, randomTo)) ? " Exists" : " Doesn't exist"));
                System.out.println("and the opposite edge  "
                        + ((myGraph.edgeExists(randomTo, randomFrom)) ? " Exists" : " Doesn't exist"));
                return "E1001";
            }
            if (otherGraph.containsEdge(randomFrom, randomTo)) {
                continue;
            }
            myGraph.addEdge(randomFrom, randomTo);
            otherGraph.addEdge(randomFrom, randomTo);
        }
        System.out.println("In the end, "+otherGraph.edgeSet().size() + " edges were added ");
        // check toString
        if (otherGraph.toString().length() != myGraph.toString().length()) {
            return "E1002";
        }
        // check connected components
        var otherConnectivity = new ConnectivityInspector(otherGraph);
        var otherListOfComponentMembers = otherConnectivity.connectedSets();
        System.out.println("Per jgrapht, there are " + otherListOfComponentMembers.size() + " components");
        int numberOfComponents = myGraph.setConnectedComponents();
        System.out.println("Per us, there are " + numberOfComponents + " components");
        if (numberOfComponents != otherListOfComponentMembers.size()) {
            return "E1003";
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
        for (int currentVertex = 0; currentVertex < NODE_COUNT; ++currentVertex) {
            ourListOfComponentMembers.get(myGraph.getComponentNumber(currentVertex)).add(currentVertex);
        }
        // now compare the lists, ignoring the order by making them into sets
        if (!(new HashSet<>(otherListOfComponentMembers).
                equals(new HashSet<>(ourListOfComponentMembers)))) {
            return "E1004";
        }

        return "";
    } // runTest
}
