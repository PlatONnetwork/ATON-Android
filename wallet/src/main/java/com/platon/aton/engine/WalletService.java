package com.platon.aton.engine;

import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletType;

import java.util.List;

public interface WalletService {

    /**
     * Generate a new mnemonic
     *
     * @return generated @return new mnemonic string if success
     */
    String generateMnemonic();

    /**
     * Create a new wallet
     *
     * @param mnemonic mnemonic phrase
     * @param name     wallet name
     * @param password key password
     * @param index wallet index :钱包pathIndex,HD的index值，普通钱包和HD母钱包都是0,用于创建指定index子钱包
     * @return created @return new {@link Wallet} if success
     */
    Wallet createWallet(String mnemonic, String name, String password,int... index);

    /**
     * Create a new walletList
     *
     * @param mnemonic mnemonic phrase
     * @param name     wallet name
     * @param password key password
     * @return created @return new {@link Wallet} if success
     */
    List<Wallet> createWalletList(String mnemonic, String name, String password);

    /**
     * Imports a mnemonic key.
     *
     * @param mnemonic wallet's mnemonic phrase
     * @param name     wallet name
     * @param password password to use for the imported private key
     * @param index wallet index
     * @return imported {@link Wallet} if success
     */
    Wallet importMnemonic(String mnemonic, String name, String password,int... index);

    /**
     * Imports a mnemonic key.
     *
     * @param mnemonic wallet's mnemonic phrase
     * @param name     wallet name
     * @param password password to use for the imported private key
     * @return imported {@link Wallet} if success
     */
    List<Wallet> importMnemonicWalletList(String mnemonic, String name, String password);

    /**
     * Include new existing keystore
     *
     * @param store    store to include
     * @param name     wallet name
     * @param password store password
     * @return included {@link Wallet} if success
     */
    Wallet importKeystore(String store, String name, String password);

    /**
     * Imports a private key.
     *
     * @param privateKey private key to import
     * @param name       wallet name
     * @param password   password to use for the imported private key
     * @return imported {@link Wallet} if success
     */
    Wallet importPrivateKey(String privateKey, String name, String password);



    /**
     * Exports a wallet as JSON data.
     *
     * @param wallet   wallet to export
     * @param password account password
     * @return keystore json
     */
    String exportKeystore(Wallet wallet, String password);

    /**
     * Exports a wallet as private key data.
     *
     * @param wallet   wallet to export
     * @param password account password
     * @return private key data for wallets
     */
    String exportPrivateKey(Wallet wallet, String password);

    /**
     * 助记词导出私钥
     *
     * @param mnemonic
     * @return
     */
    String exportPrivateKey(String mnemonic);

    String getWalletAvatar(@WalletType int walletType);
}
