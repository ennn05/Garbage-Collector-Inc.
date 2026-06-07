package game.economy;

/**
 * Represents an object that owns a wallet.
 */
public interface WalletHolder {
    /**
     * Get the wallet owned by this object.
     *
     * @return the wallet
     */
    Wallet getWallet();
}