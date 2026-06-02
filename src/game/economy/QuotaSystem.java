package game.economy;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the quota system for the supercomputer.
 * Tracks company rank, quota progress, time limit, and handles quota cycles.
 */
public class QuotaSystem {
    private int currentRank;
    private int baseQuota;
    private int currentQuotaProgress;
    private int timeLimit;
    private int currentTurn;
    private Location supercomputerLocation;
    private boolean quotaMetThisCycle;

    private static final int STARTING_RANK = 1;
    private static final int STARTING_BASE_QUOTA = 100;
    private static final int STARTING_TIME_LIMIT = 200;
    private static final double QUOTA_INCREASE_PERCENTAGE = 0.05;
    private static final double TIME_LIMIT_INCREASE_PERCENTAGE = 0.10;

    /**
     * Constructor for QuotaSystem.
     *
     * @param supercomputerLocation the location of the supercomputer
     */
    public QuotaSystem(Location supercomputerLocation) {
        this.supercomputerLocation = supercomputerLocation;
        this.currentRank = STARTING_RANK;
        this.baseQuota = STARTING_BASE_QUOTA;
        this.currentQuotaProgress = 0;
        this.timeLimit = STARTING_TIME_LIMIT;
        this.currentTurn = 0;
        this.quotaMetThisCycle = false;
    }

    /**
     * Update the quota system each turn.
     *
     * @param gameMap the current game map
     * @return result message of quota system update
     */
    public String tick(GameMap gameMap) {
        currentTurn++;
        String result = "";

        if (isDeadlineReached()) {
            if (quotaMetThisCycle) {
                result = progressRank();
                currentQuotaProgress = 0;
                quotaMetThisCycle = false;
            } else {
                result = fireAdjacentWorkers(gameMap);
            }
        }

        return result;
    }

    /**
     * Progress the quota by the given amount.
     *
     * @param amount the company credits to add to quota progress
     */
    public void progressQuota(int amount) {
        currentQuotaProgress += amount;
        if (currentQuotaProgress >= baseQuota) {
            quotaMetThisCycle = true;
        }
    }

    /**
     * Check if the deadline has been reached.
     *
     * @return true if current turn >= time limit
     */
    public boolean isDeadlineReached() {
        return currentTurn >= timeLimit;
    }

    /**
     * Progress to the next rank and increase quota/time limit.
     *
     * @return result message
     */
    private String progressRank() {
        currentRank++;
        baseQuota = (int) Math.ceil(baseQuota * (1 + QUOTA_INCREASE_PERCENTAGE));
        timeLimit = (int) Math.ceil(timeLimit * (1 + TIME_LIMIT_INCREASE_PERCENTAGE));
        currentTurn = 0;

        String message = "\n*** QUOTA MET! ***\n" +
                "Company Rank has advanced to: " + currentRank + "\n" +
                "New Base Quota: " + baseQuota + " Company Credits\n" +
                "New Time Limit: " + timeLimit + " turns\n";

        System.out.println(message);
        return message;
    }

    /**
     * Fire adjacent workers when quota is not met by deadline.
     *
     * @param gameMap the current game map
     * @return result message
     */
    private String fireAdjacentWorkers(GameMap gameMap) {
        List<Actor> adjacentWorkers = new ArrayList<>();
        
        // Get all adjacent locations from the supercomputer
        for (Exit exit : supercomputerLocation.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                Actor actor = adjacent.getActor();
                if (actor.hasAbility(Ability.WORKER)) {
                    adjacentWorkers.add(actor);
                }
            }
        }

        // Fire the workers by rendering them unconscious
        String message = "\n*** QUOTA NOT MET! ***\n" +
                "The Supercomputer has fired " + adjacentWorkers.size() + " worker(s)!\n";

        System.out.println(message);

        for (Actor worker : adjacentWorkers) {
            // Render unconscious by dealing maximum damage
            int maxHealth = worker.getMaximumStatistic(ActorStatistics.HEALTH);
            worker.hurt(maxHealth);
            System.out.println(worker + " has been rendered unconscious and fired!");
        }

        // Reset for next cycle
        currentTurn = 0;
        currentQuotaProgress = 0;
        quotaMetThisCycle = false;

        return message;
    }

    // Getters
    public int getCurrentRank() {
        return currentRank;
    }

    public int getBaseQuota() {
        return baseQuota;
    }

    public int getCurrentQuotaProgress() {
        return currentQuotaProgress;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isQuotaMet() {
        return currentQuotaProgress >= baseQuota;
    }

    public String getQuotaStatus() {
        return String.format("Rank: %d | Quota: %d/%d | Turn: %d/%d",
                currentRank, currentQuotaProgress, baseQuota, currentTurn, timeLimit);
    }
}
