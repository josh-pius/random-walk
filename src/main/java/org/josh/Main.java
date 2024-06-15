package org.josh;


import org.josh.walks.GaussianRandomWalk;
import org.josh.walks.SimpleRandomWalk;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create the chart
        SimpleRandomWalk simpleRandomWalk = new SimpleRandomWalk();
        simpleRandomWalk.walk();
        GaussianRandomWalk gaussianRandomWalk = new GaussianRandomWalk();
        gaussianRandomWalk.walk();
    }
}