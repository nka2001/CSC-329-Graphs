/*
 * (c) 2021, David Gerstl, all rights reserved 
 * 
 */
package edu.farmingdale.m08graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author gerstl
 */
public class Graph {

    /**
     * class Edge implements an edge
     */
    class Edge implements Comparable<Edge> {

        // not strictly necessary, list [] we are on determines fromNode
        int fromNode;
        int connectsToNode;
        double weight;
        Edge next;

        /**
         * Default Edge Ctor
         *
         * @param from node we are from
         * @param to node we connect to
         * @param weight edge weight
         */
        Edge(int from, int to, double weight) {
            fromNode = from;
            connectsToNode = to;
            this.weight = weight;
            next = null;
        }

        /**
         * compareTo to compare edges on weight only!
         *
         * @param e2 other node
         * @return 0 if the weight is the same, -1 of e2 is heavier, 1 ow.
         */
        public int compareTo(Edge e2) {
            if (weight == e2.weight) {
                return 0;
            }
            Double dist = weight - e2.weight;
            return (dist < 0) ? -1 : 1;
        }
    }

    /**
     * class for nodes
     */
    class Node {

        // not strictly necessary, position in list = #
        int nodeNumber;
        String nodeName;
        // embedded graph has x, y coordinates
        int xCoordinate;
        int yCoordinate;

        void set(String name, int x, int y) {
            nodeName = name;
            xCoordinate = x;
            yCoordinate = y;
        }
    }
// note that we can represent this as a list of nodes, with edges inside.
// this can also just be a 2 lists, edges and nodes.
    List<Node> nodes;
    List<Edge> edges;
    // edges are a linked structure, so the .size() doesn't work
    int edgeCount = 0;
    List<Integer> componentNumbers;
    // not sure we also need this
    Boolean componentNumbersValid;
    Boolean directed;

    /**
     * ctor
     *
     * @param numberOfNodes size of graph (node count)
     * @param directed directed or not boolean
     */
    public Graph(int numberOfNodes, Boolean directed) {
        nodes = new ArrayList<>(); // List is an abstract type
        edges = new ArrayList<>();
        // -1 indicates that the collections are not set up.
        componentNumbers = new ArrayList<>(Collections.nCopies(numberOfNodes, -1));
        componentNumbersValid = false;
        for (int i = 0; i < numberOfNodes; ++i) {
            nodes.add(i, new Node());
            nodes.get(i).nodeNumber = i;
            // note: No name
            edges.add(i, null);
        }
        this.directed = directed;
    }

    /**
     * Public method to set some details of the node, especially coordinates for
     * an embedded graph
     *
     * @param number node number to set
     * @param name Name for the node (not really used)
     * @param x x location
     * @param y y location
     * @return false if the number is out of range
     */
    public Boolean setNode(int number, String name, int x, int y) {
        // this has no effect on connected components, so no
        // componentNumbersValid = false; // any structure modification messes up
        // the component numbers

        if (number >= nodes.size()) {
            return false;
        }
        nodes.get(number).set(name, x, y);
        return true;
    }

    /**
     * Check if an edge exists between nodes
     *
     * @param fromNode node from
     * @param toNode node to
     * @return true iff the node exists
     */
    public Boolean edgeExists(int fromNode, int toNode) {
        Edge iter = edges.get(fromNode);
        while (null != iter) {
            if (iter.connectsToNode == toNode) {
                return true;
            }
            iter = iter.next;
        } // while
        return false;
    }

    /**
     * Get the number of edges
     *
     * @return the number of edges
     */
    public int getEdgeCount() {
        return edgeCount;
    }

    /**
     * Return the index of the node at (x,y)
     *
     * @param xCoord x coordinate
     * @param yCoord y coordinate
     * @return the number of the node at (x,y), or -1 if not found
     */
    public int getNode(int xCoord, int yCoord) {
        for (int i = 0; i < nodes.size(); ++i) {
            Node theNode = nodes.get(i);
            if (theNode.xCoordinate == xCoord
                    && theNode.yCoordinate == yCoord) {
                return i;
            }
        }
        return -1;
    }

    public Boolean addEdge(int fromX, int fromY, int toX, int toY) {
        int fromNode = getNode(fromX, fromY);
        int toNode = getNode(toX, toY);
        if (-1 == fromNode || -1 == toNode) {
            return false;
        }
        return addEdge(fromNode, toNode);
    }

    /**
     * private function to return euclidean distance between nodes
     *
     * @param fromNodeNumber node number
     * @param toNodeNumber node number
     * @return distance between (x,y) and (x',y') as a double
     */
    double euclideanDistance(int fromNodeNumber, int toNodeNumber) {
        Node fromNode = nodes.get(fromNodeNumber);
        Node toNode = nodes.get(toNodeNumber);
        return Math.sqrt(Math.pow(fromNode.xCoordinate - toNode.xCoordinate, 2)
                + Math.pow(fromNode.yCoordinate - toNode.yCoordinate, 2));
    }

    /**
     * wrapper for getEdge(#,#) and getNode(x,y). Returns the weight of the edge
     * between (x,y) and (x',y')
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return the edge distance (not euclidean)between the nodes, if they exist
     * and there is an edge between them, ow null
     */
    // returns null for edges not found (so we can do negative 
    public Double getEdge(int fromX, int fromY, int toX, int toY) {
        int fromEdge = getNode(fromX, fromY);
        int toEdge = getNode(toX, toY);
        if (-1 == fromEdge || -1 == toEdge) {
            return null;
        }
        return getEdge(fromEdge, toEdge);
    }

    /**
     * returns the single edge distance between nodes
     *
     * @param fromNode
     * @param toNode
     * @return If an edge exists between the nodes, returns the edge weight
     */
    public Double getEdge(int fromNode, int toNode) {
        Edge current = edges.get(fromNode);
        while (null != current) {
            if (toNode == current.connectsToNode) {
                return current.weight;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Add an edge between nodes with specified weight
     *
     * @param fromNode from node index
     * @param toNode to node index
     * @param weight edge weight
     * @return true if added (no parallel edges)
     */
    public Boolean addEdge(int fromNode, int toNode, double weight) {
        // validate this is a new edge
        if (edgeExists(fromNode, toNode)) {
            return false;
        }
        componentNumbersValid = false; // any structure modification messes up
        // the component numbers
        Edge newEdge = new Edge(fromNode, toNode, weight);
        newEdge.next = edges.get(fromNode);
        // set, not add. These are indexed by the slot
        edges.set(fromNode, newEdge); // add not set
        if (!directed) {
            newEdge = new Edge(toNode, fromNode, weight);
            newEdge.next = edges.get(toNode);
            // set, not add since we want the list to remain correctly anchored
            edges.set(toNode, newEdge);
        }
        ++edgeCount;
        return true;
    }

    /**
     * Adds an edge, with edge length as euclidean distance between the node's
     * (x,y) coordinates.
     *
     * @param fromNode from node index
     * @param toNode to node index
     * @return true iff the node is added
     */
    public Boolean addEdge(int fromNode, int toNode) {
        Double weight = euclideanDistance(fromNode, toNode);
        return addEdge(fromNode, toNode, weight);
    }

    /**
     * This is a Module 12 method: Computer the shortest path from specified
     * nodes to all others.
     *
     * @param fromNode node to computer shortest path from
     * @return shortest path graph
     */
    public Graph computeShortestPath(int fromNode) {
        Graph rv = new Graph(nodes.size(), directed);
        return rv;
    }

    // returns the A* result of the graph from node to node
    /**
     * This is a Module 12 method. Compute A* route between fromNode and toNode
     *
     * @param fromNode source node
     * @param toNode destination node
     * @return the graph of the shortest path
     */
    public Graph computeAStar(int fromNode, int toNode) {
        Graph rv = new Graph(nodes.size(), directed);
        return rv;
    }

    // returns the number of connected components found
    /**
     * Set up the connected component sets, only in an undirected graph
     *
     * @return the number of separate connected components found
     */
    public int setConnectedComponents() {
        return 0;
    }

    /**
     * Check if two nodes are in the same component (the component numbers are
     * somewhat arbitrary, so this is a good compromise.
     *
     * @param node1
     * @param node2
     * @return true iff the nodes are in the same connected component
     */
    public boolean sameComponentAs(int node1, int node2) {

        return false;
    }

    /**
     * Public function to return component number
     *
     * @param node
     * @return
     */
    public int getComponentNumber(int node) {
        if (!componentNumbersValid) {
            // maybe throw exception otherwise
            setConnectedComponents();
        }
        return componentNumbers.get(node);
    }

    /**
     * convert the graph to strings.
     *
     * @return See the assignment sheet for details
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // first the list of strings
        sb.append("([");
        var nIter = nodes.iterator();
        while (nIter.hasNext()) {
            sb.append(nIter.next().nodeNumber);
            if (nIter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("], [");
        var eIter = edges.iterator();
        boolean prev = false;
        while (eIter.hasNext()) {
            var thisEdge = eIter.next();
            while (null != thisEdge) {
                if (directed || thisEdge.fromNode < thisEdge.connectsToNode) {
                    if (prev) {
                        sb.append(", ");
                    }
                    sb.append("[" + thisEdge.fromNode + "," + thisEdge.connectsToNode + "]");
                    prev = true;
                }
                thisEdge = thisEdge.next;
            }
        }
        sb.append("])");
        return sb.toString();
    }
}
