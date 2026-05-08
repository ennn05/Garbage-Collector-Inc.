package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import game.economy.Wallet;
import game.economy.WalletHolder;

/**
 * Abstract base class for actions that involve wallet transactions.
 */
public abstract class TransactionAction extends Action {

    /**
     * Check whether the actor can participate in a wallet transaction.
     *
     * @param actor the actor performing the transaction
     * @return true if the actor owns a wallet
     */
    protected boolean hasWallet(Actor actor) {
        return actor instanceof WalletHolder;
    }

    /**
     * Get the wallet owned by the actor.
     * This method should only be called after hasWallet(actor) returns true.
     *
     * @param actor the actor performing the transaction
     * @return the actor's wallet
     */
    protected Wallet getWallet(Actor actor) {
        WalletHolder walletHolder = (WalletHolder) actor;
        return walletHolder.getWallet();
    }

    /**
     * Message used when an actor without a wallet attempts a transaction.
     *
     * @param actor the actor attempting the transaction
     * @return result description
     */
    protected String noWalletMessage(Actor actor) {
        return actor + " does not have a wallet and cannot trade with the Supercomputer.";
    }
}