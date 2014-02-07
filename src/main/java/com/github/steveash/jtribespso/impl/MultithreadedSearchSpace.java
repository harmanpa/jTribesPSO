package com.github.steveash.jtribespso.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.Particle;
import com.github.steveash.jtribespso.SearchSpace;
import com.github.steveash.jtribespso.Tribe;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * A multi threaded (partial) implementation of SearchSpace
 * <p/>
 * The performance of the multithreaded search space is actually worse than the default search space with regards to
 * the number of evaluations that it takes to optimize a problem.  For the Rosenbrock problem, the difference is
 * about 10%.  On the upside, the execution time is roughly the same on a quad core system running 4 threads.
 *
 * Where the multithreaded search space IS useful is for optimizations of objective functions that are computationally
 * intensive.  For example, a least squares fit of discrete data points.  In the case where the objective function is in
 * the slow path of execution, I've seen a factor 2 or 3 speed-up on a quad core system running 4 threads.  Again, the
 * number of evaluations will be higher, but the execution time will be lower
 *
 * Note to inheritors.  When implementing the GenerateParticleAtPosition method, make sure each created particle gets
 * its own random number generator.  Also if you're not using one of the default library particle implementations, make sure
 * the implementation  you're using can have multiple particles in a neighborhood moving at the same time.
 * @param <TParticle>
 */
public abstract class MultithreadedSearchSpace<TParticle extends Particle> extends SearchSpace<TParticle> {

    private final int threadCount;
    private final ListeningExecutorService pool;

    protected MultithreadedSearchSpace(ListeningExecutorService pool, IObjectiveFunction objectiveFunction, int workerCount) {
        super(objectiveFunction);

        this.pool = checkNotNull(pool);
        this.threadCount = workerCount;
    }

    /**
     * Moves all of the particles in the search space, but uses multiple threads to take advantage of systems with
     * multiple processors.  The particles are split up into N groups where N is the number of threads
     * we'll be spooling up.  The particles will run serially within their groups, but the groups will be processed
     * in parallel.
     * </summary>
     * <remarks>
     * Race conditions will make this a bit non deterministic, but since each particle has its own RNG
     * in a multi threaded search space, we already lost the ability to run the optimization deterministically
     * by specifying the RNG seed to the particles.  The base particle's move implementation will work fine
     * if particles are moved in parallel.  It's not ok to have two threads call move on the same particle at the same
     * time.
     */
    @Override
    protected void move() {
        Collection<ListenableFuture<?>> tasks = Lists.newArrayListWithCapacity(threadCount);
        int perThread = (this.swarmSize() / threadCount) + 1;

        ArrayList<Tribe> randomOrder = randomOrderOfTribes();
        FluentIterable<Particle> particlestoMove = FluentIterable
                .from(randomOrder)
                .transformAndConcat(Tribe.SelectMembers);

        for (List<Particle> particlesPerThread : Iterables.partition(particlestoMove, perThread)) {
            tasks.add(pool.submit(makeMoveTask(particlesPerThread)));
        }

        try {
            Futures.allAsList(tasks).get();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private ArrayList<Tribe> randomOrderOfTribes() {
        ArrayList<Tribe> randomOrder = Lists.newArrayList(this.tribes());
        Collections.shuffle(randomOrder);
        return randomOrder;
    }

    private Runnable makeMoveTask(List<Particle> particles) {
        final List<Particle> particlesToMove = ImmutableList.copyOf(particles);
        return new Runnable() {
            @Override
            public void run() {
                for (Particle particle : particlesToMove) {
                    particle.move();
                }
            }
        };
    }

    public int getThreadCount() {
        return threadCount;
    }
}

