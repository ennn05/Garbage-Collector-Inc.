package game.interfaces;

import game.economy.QuotaSystem;

/**
 * Implemented by ground tiles that host a QuotaSystem.
 * Allows actions to access the quota system without casting to a concrete class.
 */
public interface QuotaHost {

    /**
     * Get the quota system managed by this host.
     *
     * @return the quota system, or null if not initialised
     */
    QuotaSystem getQuotaSystem();
}
