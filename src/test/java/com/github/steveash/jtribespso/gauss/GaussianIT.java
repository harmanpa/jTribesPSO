package com.github.steveash.jtribespso.gauss;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.SearchSpace;
import com.github.steveash.jtribespso.impl.MultithreadedGaussianSearchSpace;
import com.github.steveash.jtribespso.impl.SingleThreadedGaussianSearchSpace;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * This is a toy program that parses a data file (Data.txt) and attempt to do a curve fit.  The data is the superposition of
 * several gaussian curves.  This program attempts to extract the original component curves from the data.
 */
public class GaussianIT {
    private static final Logger log = LoggerFactory.getLogger(GaussianIT.class);

    @Test
    public void shouldFitCurves() throws Exception {

        List<double[]> fitData = parseFitData("gauss-data.txt");

        log.info("********** Single Threaded Indepedent Gaussian **********");
        String gaussianResults = runTests(10, fitData, new SingleThreadedFactory());
        log.info(gaussianResults);
        log.info("");

        log.info("********** Multi Threaded Independent Gaussian **********");
        MultithreadedFactory factory = new MultithreadedFactory();
        String multiThreadedGaussianResults = runTests(10, fitData, factory);
        log.info(multiThreadedGaussianResults);
        log.info("");
        factory.close();
    }

    //Parse a CSV file to use its data
    private static List<double[]> parseFitData(String filename) throws URISyntaxException, IOException {
        File csv = new File(Thread.currentThread().getContextClassLoader().getResource(filename).toURI());

        List<String> lines = Files.readLines(csv, Charsets.UTF_8);
        ArrayList<double[]> data = Lists.newArrayListWithCapacity(lines.size());
        for (int i = 0; i < lines.size(); i++) {

            double[] row = new double[2];
            row[0] = i;
            String raw = lines.get(i);
            row[1] = Double.parseDouble(raw.trim());
            data.add(row);
        }
        return data;
    }

    private interface SpaceFactory {
        SearchSpace<?> create(IObjectiveFunction func);
    }

    private static class MultithreadedFactory implements SpaceFactory {

        private ThreadPoolExecutor innerPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        private final ListeningExecutorService pool = makeNewPool();

        private ListeningExecutorService makeNewPool() {
            return MoreExecutors.listeningDecorator(
                    MoreExecutors.getExitingExecutorService(
                            innerPool
                    )
            );
        }

        @Override
        public SearchSpace<?> create(IObjectiveFunction func) {
            Preconditions.checkState(innerPool.getActiveCount() == 0);
            return new MultithreadedGaussianSearchSpace(pool, func, 4);
        }

        public void close() throws InterruptedException {
            pool.shutdownNow();
            pool.awaitTermination(1, TimeUnit.DAYS);
        }
    }

    private static class SingleThreadedFactory implements SpaceFactory {
        @Override
        public SearchSpace<?> create(IObjectiveFunction func) {
            return new SingleThreadedGaussianSearchSpace(
                    func, new HyperspaceRandom());
        }
    }

    static String runTests(int loops, List<double[]> fitData, SpaceFactory factory) {
        int successCount = 0;
        int evalCount = 0;
        long totalMillis = 0;

        for (int n = 0; n < loops; n++) {
            GaussianFitFunction objectiveFunction = new GaussianFitFunction(2, fitData);
            SearchSpace<?> space = factory.create(objectiveFunction);

            Stopwatch watch = Stopwatch.createStarted();
            for (int moves = 0; moves < 5000; moves++) {
                space.moveThenAdapt();
                if (space.bestSolution().getError() < 0.5) {
                    successCount += 1;
                    break;
                }
            }
            watch.stop();

            evalCount += objectiveFunction.evaluations;
            totalMillis += watch.elapsed(TimeUnit.MILLISECONDS);
        }

        double avgEvals = ((double) evalCount) / loops;
        double avgRoundMillis = ((double) totalMillis) / loops;

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("\n********** Average Evaluations **********\n");
        resultBuilder.append(String.format("%.4f\n", avgEvals));
        resultBuilder.append("********** % Success **********\n");
        resultBuilder.append(String.format("%.4f\n", ((double) successCount / (double) loops)));
        resultBuilder.append("********** Time **********\n");
        resultBuilder.append("Average Time: " + String.format("%.4f ms\n", avgRoundMillis));
        resultBuilder.append("Total Time: " + totalMillis + " ms\n");

        return resultBuilder.toString();
    }
}
