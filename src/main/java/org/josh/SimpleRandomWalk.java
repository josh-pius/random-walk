package org.josh;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class SimpleRandomWalk {
    private int init;
    private List<XYSeries> seriess;
    private ChartPanel chartPanel;
    private static final int SECURITY_COUNT = 5;
    public SimpleRandomWalk(){
        this.seriess = new ArrayList<>();
        IntStream.iterate(1,n->n+1).limit(SECURITY_COUNT).forEach(i->seriess.add(new XYSeries("Random Walk 2d "+ i)));
        XYSeriesCollection dataset = new XYSeriesCollection();
        for(XYSeries series: seriess){
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(
                "2D Random Walk",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);

        // Create the frame
        JFrame frame = new JFrame("2D Random Walk");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        this.chartPanel = new ChartPanel(chart);
        frame.add(chartPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    public class SecurityRandomWalk implements  Runnable{
        private final XYSeries series;

        public SecurityRandomWalk(XYSeries series){
            this.series = series;
        }
       @Override
       public void run(){
           ThreadLocalRandom random = ThreadLocalRandom.current();
           int init = 0;
           for(int i = 0; i<100000; i++){
               try {
                   sleep(1);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
               if(random.nextGaussian(0,1)>0){
                   init+=1;
               }
               else{
                   init-=1;
               }
               series.add(i,init);
           }
       }
    }
    public void walk() throws InterruptedException {
        startAnimationLoop();
        for (XYSeries series : seriess) {
            new Thread(new SecurityRandomWalk(series)).start();
        }
    }

    /**
     * Repainting should happen separately from the generation loop for performance reasons
     */
    private void startAnimationLoop() {
        new Thread(()->{
            // Redraw the chart
            SwingUtilities.invokeLater(() -> {
                chartPanel.repaint();
            });
        }).start();
    }
}
