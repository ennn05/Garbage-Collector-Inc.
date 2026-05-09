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
     * Get the wallet owned by the actor.
     * This uses WalletHolder capability instead of checking a concrete actor class.
     *
     * @param actor the actor performing the transaction
     * @return the actor's wallet, or null if the actor has no wallet
     */
    protected Wallet getWallet(Actor actor) {
        WalletHolder walletHolder = actor.asCapability(WalletHolder.class).orElse(null);

        if (walletHolder == null) {
            return null;
        }

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