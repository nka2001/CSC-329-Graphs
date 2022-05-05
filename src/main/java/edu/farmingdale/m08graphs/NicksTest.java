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
import org.jgrapht.alg.connectivity.ConnectivityInspector;

/**
 *
 * @author gerstl
 */
public class NicksTest implements RunTest {

    public String runTest() {
        // uses org.jgrapht.alg.connectivity.ConnectivityInspector<V,E>
        // large test will create a giant random graph
        // or 
        // example one baeldung.com/cs.graph-connected.components section 3.2
        // note that we use vertices 0..11 not 1..12
        // create 2 graphs
        // undirected, 10 node
        final int NODE_COUNT = 12;
        var myGraph = new Graph(NODE_COUNT, false);

        org.jgrapht.Graph<Integer, DefaultEdge> otherGraph = new SimpleGraph<>(DefaultEdge.class);
        for (int i = 0; i < NODE_COUNT; ++i) {
            otherGraph.addVertex(i);
        }
        // add a bunch of edges
        myGraph.addEdge(0, 3); // E1
        otherGraph.addEdge(0, 3); // E1
        myGraph.addEdge(3, 4); // E2
        otherGraph.addEdge(3, 4); // E2
        myGraph.addEdge(0, 1); // E3
        otherGraph.addEdge(0, 1); // E3
        myGraph.addEdge(0, 2); // E4
        otherGraph.addEdge(0, 2); // E4
        myGraph.addEdge(1, 2); // E5
        otherGraph.addEdge(1, 2); // E5
        myGraph.addEdge(2, 4); // E6
        otherGraph.addEdge(2, 4); // E6
        myGraph.addEdge(2, 5); // E7
        otherGraph.addEdge(2, 5); // E7
        myGraph.addEdge(6, 8); // E8
        otherGraph.addEdge(6, 8); // E8
        myGraph.addEdge(6, 7); // E9
        otherGraph.addEdge(6, 7); // E9
        myGraph.addEdge(10, 9); // E10
        otherGraph.addEdge(10, 9); // E10
        myGraph.addEdge(9, 11); // E11
        otherGraph.addEdge(9, 11); // E11
        if (otherGraph.edgeSet().size() != myGraph.getEdgeCount()) {
            return "E1001";
        }
        // compare the graphs. Very inefficient, but exhaustive
        for (int i = 0; i < NODE_COUNT; ++i) {
            for (int j = i + 1; j < NODE_COUNT; ++j) {
                if (myGraph.edgeExists(i, j)) {
                    // verify that i->j exists in other
                    if (otherGraph.getAllEdges(i, j).isEmpty()) {
                        return "E1002";
                    }
                } else {
                    // verify that i->j does not exist in other
                    if (!otherGraph.getAllEdges(i, j).isEmpty()) {
                        return "E1003";
                    }

                }
            }
        } // check graphs the same
        // check that toString() is implemented. Note that we'll also
        // check it in the large test, but I'll need to manually check the output
       

        // check connected components
        var otherConnectivity = new ConnectivityInspector(otherGraph);
        var otherListOfComponentMembers = otherConnectivity.connectedSets();
        System.out.print("Per jgrapht, there are " + otherListOfComponentMembers.size() + " sets");
        System.out.println(" containing: " + otherListOfComponentMembers);
        int numberOfComponents = myGraph.setConnectedComponents();

        System.out.println(otherListOfComponentMembers.size());
        System.out.println(numberOfComponents);
        if (numberOfComponents != otherListOfComponentMembers.size()) {
            return "E1005";
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
        System.out.print("Per us, there are " + ourListOfComponentMembers.size() + " sets");
        System.out.println(" containing: " + ourListOfComponentMembers);
        // now compare the lists, ignoring the order by making them into sets
        if (!(new HashSet<>(otherListOfComponentMembers).
                equals(new HashSet<>(ourListOfComponentMembers)))) {
            return "E1006";
        }

        return "";
    }

}
