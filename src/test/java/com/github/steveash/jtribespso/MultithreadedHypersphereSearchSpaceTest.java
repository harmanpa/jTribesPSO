package com.github.steveash.jtribespso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import com.github.steveash.jtribespso.impl.HypersphereParticle;
import com.github.steveash.jtribespso.impl.MultithreadedHypersphereSearchSpace;
import com.github.steveash.jtribespso.test.Rosenbrock;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/** 
This is a test class for MultithreadedHypersphereSearchSpaceTest and is intended
to contain all MultithreadedHypersphereSearchSpaceTest Unit Tests
*/
public class MultithreadedHypersphereSearchSpaceTest extends SearchSpaceTest<HypersphereParticle> {

    private ListeningExecutorService pool;

        @Before
        public void setUp() throws Exception {
            ExecutorService inner = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newCachedThreadPool());
            pool = MoreExecutors.listeningDecorator(inner);
        }

        @After
        public void tearDown() throws Exception {
            pool.shutdownNow();
            pool.awaitTermination(1, TimeUnit.DAYS);
            pool = null;
        }

	@Override
	protected SearchSpace<HypersphereParticle> createSearchSpace() {
		return new MultithreadedHypersphereSearchSpace(pool, new Rosenbrock(), 4);
	}
}
