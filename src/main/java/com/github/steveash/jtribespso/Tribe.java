package com.github.steveash.jtribespso;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import com.github.steveash.jtribespso.rand.IRandom;
import com.github.steveash.jtribespso.rand.JdkRandom;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * The Tribe class manages the links between particles.  A particle that does not belong to a tribe has no internal or external links
 */
public class Tribe implements SolutionHolder {

    public static final Function<Tribe,List<Particle>> SelectMembers = new Function<Tribe, List<Particle>>() {
        @Override
        public List<Particle> apply(Tribe input) {
            return input.tribeMembers();
        }
    };

    private final IRandom rng;
    private final List<Particle> tribeMembers;
    private final List<Tribe> informers;

    private double historicalBestError;
    private Particle shaman;
    private boolean isGood;

    /**
     * Constructs a new tribe containing a single particle.
     * @param member
     */
    public Tribe(Particle member) {
        this(member, new JdkRandom());
    }

    /**
     * Constructs a new tribe containing a single particle.
     * @param member
     * @param randomNumberGenerator
     */
    public Tribe(Particle member, IRandom randomNumberGenerator) {
        this(ImmutableList.of(member), randomNumberGenerator);
    }

    public Tribe(Collection<Particle> members) {
        this(members, new JdkRandom());
    }

    /**
     * @param members particles that will belong to this tribe
     * @param randomNumberGenerator It should be considered "owned" by the tribe object
     */
    public Tribe(Collection<? extends Particle> members, IRandom randomNumberGenerator) {
        checkNotNull(members);
        checkNotNull(randomNumberGenerator);
        checkArgument(!members.isEmpty(), "must have a member");

        this.rng = randomNumberGenerator;
        this.tribeMembers = Lists.newArrayList(members);
        this.informers = Lists.newArrayList();
        for (Particle tribeMember : tribeMembers) {
            checkNotNull(tribeMember);
            tribeMember.setParent(this);
        }

        updateShaman();
        this.historicalBestError = this.bestSolution().getError();
        updateIsGood();
    }

    @Override
    public Solution bestSolution() {
        return this.shaman.bestSolution();
    }

    /**
     * Gets a read only collection of the particles that make up this tribe
     */
    public List<Particle> tribeMembers() {
        return this.tribeMembers;
    }

    /**
     * The Shaman is the best particle in the tribe
     */
    public Particle getShaman() {
        return this.shaman;
    }

    /**
     * @return Gets the Shaman particles of other tribes that are informers of this tribe
     */
    public ImmutableList<Particle> externalLinks() {
        Builder<Particle> builder = ImmutableList.builder();
        for (Tribe informer : informers) {
            builder.add(informer.getShaman());
        }
        return builder.build();
    }

    /**
     * Gets the number of internal links in the tribe.  This includes particle's self links and both outgoing and
     * incoming links from a particle even if the endpoints of both links are the same.
     * A tribe of 2 particles will have four internal links.  Two self links and one link from particle A to particle B
     * and another link from particle B to particle A
     */
    public int internalLinkCount() {
        return this.tribeMembers.size() * this.tribeMembers.size();
    }

    /**
     * Gets the number of external links in the tribe  This only counts outgoing external links, not incoming links
     */
    public int externalLinkCount() {
        return this.informers.size();
    }

    /**
     * Indicates whether or not a tribe is "Good".  A tribe that has improved its best performance since the last adaptation
     * has a 50% chance of being good.  A tribe that has not improved its best performance since the last adaptation is
     * not good.  This value is recalculated every time the swarm adapts
     * @return
     */
    public boolean isGood() {
        return this.isGood;
    }

    /**
     * @return Gets the number of particles in the tribe
     */
    public int memberCount() {
        return this.tribeMembers.size();
    }

    public int countGoodParticles() {
        int count = 0;
        for (Particle tribeMember : tribeMembers) {
            if (tribeMember.isGood())
                count += 1;
        }
        return count;
    }

    private boolean anyBetterInformer() {
        for (Tribe informer : informers) {
            if (informer.bestSolution().getError() < this.bestSolution().getError())
                return true;
        }
        return false;
    }

    /**
     * Attempts to remove the worst particle from the tribe.  In the case of a monoparticle tribe, the removal will only occur if one of its informers has a better performance
     * @return
     */
    public boolean tryRemoveWorstParticle() {

        if (this.tribeMembers.size() > 1) {
            //This is a tribe with more than one particle.  We're just going to kill off the worst one
            Particle worst = OrderBySolutionErrorAsc.max(this.tribeMembers);
            this.tribeMembers.remove(worst);
            return true;
        }
        if (this.tribeMembers.size() == 1) {

            // This is a monoparticle tribe.  We need to see if we have any better informers If we don't, then we're
            // saved from extinction because we still hold valuable information
            if (anyBetterInformer()) {

                // We're going to remove the last particle in this tribe. This will cause the tribe to go extinct so
                // we need to redistribe this tribe's informers
                Tribe bestInformerTribe = OrderBySolutionErrorAsc.min(this.informers);
                Tribe.redistributeLinks(this, bestInformerTribe);
                this.tribeMembers.clear();

                return true;
            }
        }
        return false;
    }

    /**
     * Removes any external links from the source tribe and assigns them to the destination tribe
     * @param source
     * @param destination
     */
    private static void redistributeLinks(Tribe source, Tribe destination) {
        for (Tribe informer : source.informers) {
            informer.removeInformer(source);
            informer.addInformer(destination);
            destination.addInformer(informer);
        }
        source.informers.clear();
    }

    /**
     * Adds a tribe to this tribe's list of informers
     * @param informer
     */
    public void addInformer(Tribe informer) {
        checkNotNull(informer);

        if (this.informers.contains(informer) || informer == this) {
            // If we already have this informer, do nothing If the informer is us, do nothing.
            return;
        }
        this.informers.add(informer);
        informer.addInformer(this);
    }

    private void removeInformer(Tribe source) {
        this.informers.remove(source);
    }

    public void notifySwarmAdapted() {
        updateIsGood();
    }

    /**
     * Recalculates whether or not this tribe is "Good".  This is actually just a 50% probability but it remains consistent
     * in between swarm adaptations
     */
    private void updateIsGood() {
        double currentBestError = this.bestSolution().getError();
        if (currentBestError < this.historicalBestError) {
            //At least one of the particles has improved it's best performance since the last adaptation
            this.isGood = this.rng.nextDouble() >= .5;
            this.historicalBestError = currentBestError;
        } else {
            this.isGood = false;
        }
    }

    public void notifySwarmMoved() {
        updateShaman();
    }

    /**
     * Recalculates the shaman of the tribe
     */
    private void updateShaman() {
        this.shaman = OrderBySolutionErrorAsc.min(tribeMembers);
    }

    @Override
    public String toString() {
        return "Tribe{members = " + tribeMembers.size() + ", good members = " + countGoodParticles() + "}";
    }
}