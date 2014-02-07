package com.github.steveash.jtribespso;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.ListIterator;

import com.github.steveash.jtribespso.event.SwarmAdaptedEvent;
import com.github.steveash.jtribespso.event.SwarmAdaptingEvent;
import com.github.steveash.jtribespso.event.SwarmMovedEvent;
import com.github.steveash.jtribespso.event.SwarmMovingEvent;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

/**
 * The base class for all Tribes PSO search spaces
 * There is a lot of default behavior included in the search space object.  For example, by default new particles are
 * placed in the search space based on uniform distributions.  Most of the default behaviors can be changed
 * by child classes.
 * @param <TParticle>
 */
public abstract class SearchSpace<TParticle extends Particle> {
    private final List<Tribe> tribeList;
    private final IHyperspaceRandom rng;
    private final IObjectiveFunction goodnessFunction;
    private final EventBus bus = new EventBus();
    private int movesSinceAdaptation;

    public EventBus eventBus() {
        return bus;
    }

    /**
     * Initializes a new SearchSpace object to optimize the specified objective function.  This search space
     * will use the TribesPSO.HyperspaceRandom random number generator to move the particles
     * @param objectiveFunction
     */
    protected SearchSpace(IObjectiveFunction objectiveFunction) {
        this(objectiveFunction, new HyperspaceRandom());
    }

    /**
     * Initializes a new SearchSpace object to optimize the specified objective function.  This search space
     * will use the specified random number generator to move the particles
     * @param objectiveFunction
     * @param randomNumberGenerator
     */
    protected SearchSpace(IObjectiveFunction objectiveFunction, IHyperspaceRandom randomNumberGenerator) {
        this.goodnessFunction = checkNotNull(objectiveFunction);
        this.rng = checkNotNull(randomNumberGenerator);
        this.movesSinceAdaptation = 0;
        this.tribeList = Lists.newArrayList();
    }

    protected List<Tribe> tribes() {
        return tribeList;
    }

    public Iterable<Particle> tribeMembers() {
        return FluentIterable
                .from(this.tribeList)
                .transformAndConcat(Tribe.SelectMembers);
    }

    protected IHyperspaceRandom getRandomNumberGenerator() {
        return rng;
    }

    public IObjectiveFunction goodnessFunction() {
        return goodnessFunction;
    }

    /**
     * Gets the best solution that's been found by the swarm so far.  Will return null if the swarm was just
     * constructed but MoveThenAdapt() hasn't been called it
     */
    public Solution bestSolution() {

        if (tribes().isEmpty())
            return null;

        return SolutionHolder.OrderBySolutionErrorAsc.min(this.tribeList).bestSolution();
    }

    /**
     * Gets the number of tribes in the search space
     * @return
     */
    public int tribeCount() {
        return this.tribeList.size();
    }

    public Iterable<EuclidianVector> particlePositions() {
        List<EuclidianVector> positions = Lists.newArrayList();
        for (Tribe tribe : tribeList) {
            for (Particle particle : tribe.tribeMembers()) {
                positions.add(particle.getPosition());
            }
        }
        return positions;
    }

    /**
     * Gets the number of particles in the search space
     * @return
     */
    public int swarmSize() {
        int count = 0;
        for (Tribe tribe : tribeList) {
            count += tribe.memberCount();
        }
        return count;
    }

    private int swarmLinkCount() {
        int count = 0;
        for (Tribe tribe : tribeList) {
            count += tribe.externalLinkCount() + tribe.internalLinkCount();
        }
        return count;
    }

    /**
     * Called by MoveThanAdapt().  Implementers should provide logic to move some, or all of the particles
     * in the swarm.
     */
    protected abstract void move();

    /**
     * Called when the swarm needs to add a new particle.  This method provides the new particle at a specified
     * location
     * <p/>
     * The implementation of this method determines the type of particles that make up the swarm.  You can have
     * this method always generate the same type of particle, or you can pick different particles based on
     * how well the swarm is doing.
     * @param position
     * @return
     */
    protected abstract TParticle generateParticleAtPosition(EuclidianVector position);

    /**
     * If the swarm is empty, this method will seed the search space based on the behavior of
     * SeedSearchSpace();
     * <p/>
     * If the swarm is not empty, this method Moves the particles in the swarm then
     * checks if it's time for an adaptation after the move.  If it's time, this method calls
     * Adapt() to adapt the swarm size.
     */
    public void moveThenAdapt() {
        /* We initialize the swarm in this method instead of the constructor to avoid calling virtual
         * methods from within the constructor.  Doing it this way is a little bit more confusing
         * but I think it's worth it because it allows people to inherit from SearchSpace and change how
         * the particles are initially distributed more easily
         */
        if (this.swarmSize() == 0) {
            seedSearchSpace();
            return;
        }

        bus.post(new SwarmMovingEvent());
        move();
        this.movesSinceAdaptation += 1;

        //First let all of the tribes know that the swarm has moved
        for (Tribe tribe : tribeList) {
            tribe.notifySwarmMoved();
        }
        //Then notify anybody else who wants to know
        bus.post(new SwarmMovedEvent());

        if (needsToAdapt(movesSinceAdaptation)) {

            bus.post(new SwarmAdaptingEvent());
            adapt();
            this.movesSinceAdaptation = 0;

            //Again, we're going to tell all of the tribes that we've adapted the swarm
            for (Tribe tribe : tribeList) {
                tribe.notifySwarmAdapted();
            }
            bus.post(new SwarmAdaptedEvent());
        }
    }

    /**
     * Signals that the swarm is due for an adaptation.  This implementation returns true when the
     * moves since the last adaptation is greater than or equal to the total number of links in the swarm
     * divided by four
     * Override this method if you want to change how frequently the swarm adapts its size
     * @param movesSinceLastAdaptation
     * @return
     */
    protected boolean needsToAdapt(int movesSinceLastAdaptation) {
        int adaptationInterval = swarmLinkCount() / 4;
        return movesSinceLastAdaptation >= adaptationInterval;
    }

    /**
     * Adapts the swarm size.  Good tribes have a chance of killing their best particle.  If there are bad tribes
     * in the swarm, a new tribe will be created with one particle per bad tribe.  Each bad tribe will
     * have the new tribe added as an informer
     * <p/>
     * Note to inheritors: Adapt is responsible for removing any empty tribes from the swarm
     */
    protected void adapt() {
        //Kill bad particles in good tribes
        for (Tribe tribe : tribeList) {
            if (tribe.isGood()) {
                tribe.tryRemoveWorstParticle();
            }
        }
        removeAllEmptyTribes();

        List<Tribe> badTribes = collectBadTribes();
        if (!badTribes.isEmpty()) {

            Tribe newTribe = new Tribe(generateNewParticle(badTribes.size()), this.getRandomNumberGenerator());
            for (Tribe badTribe : badTribes) {
                badTribe.addInformer(newTribe);
            }

            this.tribeList.add(newTribe);
        }
    }

    private List<Tribe> collectBadTribes() {
        List<Tribe> badTribes = Lists.newArrayList();
        for (Tribe tribe : tribeList) {
            if (!tribe.isGood()) {
                badTribes.add(tribe);
            }
        }
        return badTribes;
    }

    private void removeAllEmptyTribes() {
        ListIterator<Tribe> iter = tribeList.listIterator();
        while (iter.hasNext()) {
            Tribe t = iter.next();
            if (t.tribeMembers().isEmpty()) {
                iter.remove();
            }
        }
    }

    /**
     * Creates a new position inside the search space
     * In the default implementation,  all positions in the search space are equally likely.
     * The min and max dimension bounds are determined by the IObjectiveFunction that was passed into the
     * search space's constructor.
     * @return
     */
    protected EuclidianVector generatePosition() {
        EuclidianVectorBuilder position = new EuclidianVectorBuilder();
        for (int n = 0; n < this.goodnessFunction().getDimensions(); n++) {
            double randomPosition = getRandomNumberGenerator().nextDouble(
                    goodnessFunction().getMinBounds().get(n),
                    goodnessFunction().getMaxBounds().get(n));
            position.add(randomPosition);
        }
        return position.build();
    }

    /**
     * Adds a single new particle to the search space.  If the search space's objective function
     * has an initial guess, the new particle will be placed there.
     */
    protected void seedSearchSpace() {
        TParticle initialParticle = null;
        EuclidianVector guess = this.goodnessFunction().getInitialGuess();
        if (guess == null) {
            initialParticle = generateNewParticle();
        } else {
            initialParticle = generateParticleAtPosition(guess);
        }
        Tribe initialTribe = new Tribe(initialParticle, this.getRandomNumberGenerator());

        //Generate the first tribe
        this.tribeList.add(initialTribe);
    }

    protected List<TParticle> generateNewParticle(int numberToGenerate) {
        List<TParticle> particles = Lists.newArrayListWithCapacity(numberToGenerate);
        for (int n = 0; n < numberToGenerate; n++) {
            particles.add(generateNewParticle());
        }
        return particles;
    }

    /**
     * Creates a new particle.  This function calls GenerateParticlePosition() to get the position of the new
     * <p/>
     * particle and GenerateParticleAtPosition(EuclidianVector) to actually create the particle
     * This method is called by the default impelmentation of Adapt() and SeedSearchSpace()
     * <p/>
     * The new particle with a position determined by GeneratePosition() and other details determined
     * by GenerateParticleAtPosition(EuclidianVector)
     * @return
     */
    protected TParticle generateNewParticle() {
        EuclidianVector position = generatePosition();
        return generateParticleAtPosition(position);
    }
}
