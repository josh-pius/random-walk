package org.josh.walks;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.josh.interfaces.BiFunction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class GaussianRandomWalk {
    private static final int SECURITY_COUNT = 5;
    private final List<XYSeries> seriesList;
    private final ChartPanel chartPanel;
    BiFunction<Double, Random,Double> gaussianUpdateFunction = (i, rand)->{ return i + rand.nextGaussian();};

    public GaussianRandomWalk() {
        this.seriesList = new ArrayList<>();
        IntStream.iterate(1, n -> n + 1).limit(SECURITY_COUNT).forEach(i -> seriesList.add(new XYSeries("Gaussian Random Walk 2d " + i)));
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries series : seriesList) {
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(
                "2D Gaussian Random Walk",
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

    public void walk() throws InterruptedException {
        startAnimationLoop();
        for (XYSeries series : seriesList) {
            new Thread(new SecurityRandomWalk(series, gaussianUpdateFunction)).start();
        }
    }


    /**
     * Repainting should happen separately from the generation loop for performance reasons
     */
    private void startAnimationLoop() {
        new Thread(() -> {
            // Redraw the chart
            SwingUtilities.invokeLater(() -> {
                chartPanel.repaint();
            });
        }).start();
    }

    public class SecurityRandomWalk implements Runnable {
        private final XYSeries series;
        private final BiFunction<Double, Random, Double> updateFunction;

        public SecurityRandomWalk(XYSeries series, BiFunction<Double, Random, Double> updateFunction) {
            this.series = series;
            this.updateFunction = updateFunction;
        }

        @Override
        public void run() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            double init = 0;
            for (int i = 0; i < 100000; i++) {
                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                init = (double)updateFunction.apply(init,random);
                series.add(i, init);
            }
        }
    }
}
