package com.github.steveash.jtribespso;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import com.github.steveash.jtribespso.event.ParticleMovedEvent;
import com.github.steveash.jtribespso.exception.DimensionMismatchException;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

/**
 * Particle base class
 */
public abstract class Particle implements SolutionHolder {
    /**
     * This is the number of past positions that are "remembered" by the particle.  This value should be
     * greater or equal to 2.  Values above 2 are only useful for diagnostic purposes because they aren't used
     * to determine "goodness".
     */
    private static final int HistoryLength = 2;

    // Maintains a history of whether or not the particle has improved its best position
    private final List<Boolean> solutionHistory;
    private final IHyperspaceRandom rng;
    private final IObjectiveFunction goodnessFunction;
    private final EventBus bus = new EventBus();

    private Tribe parent = null;
    private volatile Solution bestSolution;
    private volatile EuclidianVector position;
    private volatile double currentError;

    @Override
    public Solution bestSolution() {
        return bestSolution;
    }

    public EuclidianVector getPosition() {
        return position;
    }

    public double getCurrentError() {
        return currentError;
    }

    public IHyperspaceRandom getRandomNumberGenerator() {
        return rng;
    }

    /**
     * Gets a value indicating whether or not this particle is "good"
     * A particle is considered "good" if its best performance at
     * time t (now) is better than its best performance at time t-1
     * @return
     */
    public boolean isGood() {
        if (solutionHistory.isEmpty()) return false;
        return solutionHistory.get(0);
    }

    /**
     * Gets a value indicating whether or not this particle is "Excellent"
     * A particle is considered "excellent" if its best performance
     * at time t (now) is better than its best performance at time t-1 AND its best performance at time t-1 is better
     * than its best performance at time t-2
     * @return
     */
    public boolean isExcellent() {
        if (this.solutionHistory.size() < 2) return false;
        return this.solutionHistory.get(0) && this.solutionHistory.get(1);
    }

    /**
     * Gets the external informers of this particle.  Note that if this particle isn't the best in it's tribe
     * it won't have any external informers.
     * Note that the collection of ExternalInformers does not contain a self reference.
     * @return
     */
    public ImmutableList<Particle> externalInformers() {
        //If we aren't in a tribe, we have no external informers so we return an empty set
        //Alternatively, if we're not the shaman of our tribe, we don't have any external informers either
        if (parent == null || parent.getShaman() != this)
            return ImmutableList.of();

        //Ok, we're in a tribe AND we're the shaman.  We have external links
        return this.parent.externalLinks();
    }

    public Tribe getParent() {
        return this.parent;
    }

    /**
     * Gets or sets the tribe of the particle.  Note that this property can only be set once.  Subsequent attemps to set the parent
     * tribe results in an InvalidOperationException
     * @param value
     */
    public void setParent(Tribe value) {
        checkNotNull(value);
        if (parent == null) {
            //Check and make sure the particle really is in the tribe.
            if (!value.tribeMembers().contains(this)) {
                throw new IllegalStateException("The particle is not actually a member of this tribe");
            }
            parent = value;
        }
        // We already have a parent.  Check if the value that the caller is trying to set is the parent we already have
        // If it is, we'll let it slide.  If someone is trying to set our parent to a different tribe, then a programming
        // error has occured and it's time to throw.
        else if (parent != value) {
            throw new IllegalStateException("The parent tribe of a particle cannot be changed once it has been assigned");
        }
    }

    /**
     * Gets the internal informers of this particle.  Note that if the particle doesn't belong to a tribe,
     * it will only have itself as an informer
     * Note that the collection of InternalInformers always contains a self-reference
     * @return
     */
    public List<Particle> internalInformers() {
        if (parent == null) {
            return ImmutableList.of(this);
        } else {
            return this.parent.tribeMembers();
        }
    }

    /**
     * Creates a new Particle with the specified objective function and location.
     * The default HyperspaceRandom random number generator will be used to move the particle
     * @param objectiveFunction
     * @param initialPosition
     */
    protected Particle(IObjectiveFunction objectiveFunction, EuclidianVector initialPosition) {
        this(objectiveFunction, initialPosition, new HyperspaceRandom());
    }

    /**
     * Creates a new Particle with the specified objective function, location and random number generator
     * @param objectiveFunction
     * @param initialPosition
     * @param randomNumberGenerator
     */
    protected Particle(IObjectiveFunction objectiveFunction, EuclidianVector initialPosition,
            IHyperspaceRandom randomNumberGenerator) {

        checkNotNull(objectiveFunction);
        checkNotNull(initialPosition);
        checkNotNull(randomNumberGenerator);

        if (objectiveFunction.getDimensions() != initialPosition.getDimensions()) {
            throw new DimensionMismatchException("Goodness function dimensions don't match position dimensions");
        }

        this.solutionHistory = Collections.synchronizedList(
                Lists.<Boolean>newArrayListWithCapacity(HistoryLength + 1)
        );
        this.goodnessFunction = objectiveFunction;
        this.rng = randomNumberGenerator;
        this.position = initialPosition;

        this.bestSolution = new Solution(initialPosition, objectiveFunction.evaluate(initialPosition));
        this.currentError = this.bestSolution.getError();

        // prepopulate with two history values
        this.solutionHistory.add(false);
        this.solutionHistory.add(false);
    }

    /**
     * Calculates the new position for a particle based on the solution of its best informer
     * @param bestInformerSolution
     * @return
     */
    protected abstract EuclidianVector calculateNewPosition(Solution bestInformerSolution);

    /**
     * Attempts to move the particle.  The particle's new position is calculated based on it's best history and the history of it's best informer.
     * If the particle has no informers that are better, it's not going to move
     */
    public void move() {
        /* The original paper doesn't cover some details like "what happens when a particle has no better external imformers.
         * Luckily, I got an answer straight from Maurice Clerc.  His opinion is that a particle that is the best in the
         * swarm (or neighborhood, by extension) should stay put.  If you are going to add random noise to its position, you
         * should normalize the dimensions of the search space first.  I am going to try the first strategy because it
         * is the easiest to code
         */
        ImmutableList<Particle> externalInformers = this.externalInformers();

        Particle bestInformer = OrderBySolutionErrorAsc.min(this.internalInformers());
        if (!externalInformers.isEmpty()) {
            Particle bestExternal = OrderBySolutionErrorAsc.min(this.externalInformers());
            bestInformer = OrderBySolutionErrorAsc.min(bestInformer, bestExternal);
        }

        if (bestInformer == this) {
            //If there's a better informer, we're going to move.  Otherwise we're staying put.
            return;
        }
        //Capture the best informer's best solution so it doesn't change out from under us in a multithreaded environment
        Solution bestInformerSolution = bestInformer.bestSolution();
        EuclidianVector newPosition = calculateNewPosition(bestInformerSolution);
        newPosition = correctBounds(newPosition);

        //Now that we've got our new location, check if it's better and do the necessary book keeping if it is
        double newError = this.goodnessFunction.evaluate(newPosition);

        boolean improvedBestSolution = newError < bestSolution.getError();

        if (improvedBestSolution)
            bestSolution = new Solution(newPosition, newError);

        memorizePerformance(improvedBestSolution);

        EuclidianVector oldPosition = position;
        position = newPosition;
        currentError = newError;

        bus.post(new ParticleMovedEvent(oldPosition, newPosition));
    }

    private EuclidianVector correctBounds(EuclidianVector maybeCorrect) {
        if (!isOutOfBounds(maybeCorrect)) {
            return maybeCorrect;
        }
        EuclidianVector maxs = goodnessFunction.getMaxBounds();
        EuclidianVector mins = goodnessFunction.getMinBounds();

        EuclidianVectorBuilder builder = new EuclidianVectorBuilder();
        for (int i = 0; i < goodnessFunction.getDimensions(); i++) {
            double v = maybeCorrect.get(i);
            v = Math.min(v, maxs.get(i));
            v = Math.max(v, mins.get(i));
            builder.add(v);
        }
        return builder.build();
    }

    private boolean isOutOfBounds(EuclidianVector maybe) {
        EuclidianVector maxs = goodnessFunction.getMaxBounds();
        EuclidianVector mins = goodnessFunction.getMinBounds();

        for (int i = 0; i < goodnessFunction.getDimensions(); i++) {
            double v = maybe.get(i);
            if (v < mins.get(i)) return true;
            if (v > maxs.get(i)) return true;
        }
        return false;
    }

    public EventBus eventBus() {
        return this.bus;
    }

    /**
     * Memorizes the particle's current performance so that it can be used to determine a particle's quality / (IE Good, Exellent, or Neutral)
     * @param improvedBestPerformance
     */
    @VisibleForTesting
    void memorizePerformance(boolean improvedBestPerformance) {
        this.solutionHistory.add(0, improvedBestPerformance);
        //This appears sub-optimal, but in reality the while loop will only get hit once.
        while (this.solutionHistory.size() > HistoryLength)
            solutionHistory.remove(HistoryLength);
    }
}
