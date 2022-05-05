/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.farmingdale.m08graphs;


/**
 * This interface denotes classes that can run a test
 *
 * @author gerstl
 */
public interface RunTest {

    /**
     * Runs a test.
     *
     * @return the empty string for a successful test. A descriptive error
     * otherwise
     */
    String runTest();

    /**
     * Returns the display name of the test
     *
     * @return String representation of the test name, simple class name by
     * default.
     */
    default String getTestName() {
        // if they do not implement, use the class name 
        return this.getClass().getSimpleName();
    }
}
