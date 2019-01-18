package com.juzix.wallet.utils;

import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.crypto.MnemonicCode;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static org.web3j.crypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX;

/**
 * Utility functions for working with Wallet files.
 */
public class JZWalletUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom secureRandom = JZSecureRandomUtil.secureRandom();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String generateMnemonic(){
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        return JZMnemonicUtil.generateMnemonic(initialEntropy);
    }

    public static WalletFile loadWalletFileByJson(String json) throws IOException {
        return objectMapper.readValue(json, WalletFile.class);
    }

    public static String writeWalletFileAsString(WalletFile walletFile) throws IOException {
        return objectMapper.writeValueAsString(walletFile);
    }

    public static Credentials loadCredentials(String password, String json) throws IOException, CipherException {
        WalletFile walletFile = loadWalletFileByJson(json);
        return Credentials.create(Wallet.decrypt(password, walletFile));
    }

    public static String getWalletFileName(String address) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        return dateFormat.format(new Date()) + address + ".json";
    }

//    public static String getDefaultKeyDirectory() {
//        return getDefaultKeyDirectory(System.getProperty("os.name"));
//    }
//
//    static String getDefaultKeyDirectory(String osName1) {
//        String osName = osName1.toLowerCase();
//
//        if (osName.startsWith("mac")) {
//            return String.format(
//                    "%s%sLibrary%sEthereum", System.getProperty("user.home"), File.separator,
//                    File.separator);
//        } else if (osName.startsWith("win")) {
//            return String.format("%s%sEthereum", System.getenv("APPDATA"), File.separator);
//        } else {
//            return String.format("%s%s.ethereum", System.getProperty("user.home"), File.separator);
//        }
//    }
//
//    public static String getMainnetKeyDirectory() {
//        return String.format("%s%skeystore", getDefaultKeyDirectory(), File.separator);
//    }

    public static boolean isValidPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }

    public static boolean isValidKeystore(String keystore){
        try {
            WalletFile walletFile = loadWalletFileByJson(keystore);
            if (walletFile == null){
                return false;
            }
            if (TextUtils.isEmpty(walletFile.getAddress())){
                return false;
            }
            if (TextUtils.isEmpty(walletFile.getId())){
                return false;
            }
            if (walletFile.getVersion() == 0){
                return false;
            }
            WalletFile.Crypto crypto = walletFile.getCrypto();
            if (crypto == null){
                return false;
            }
            if (TextUtils.isEmpty(crypto.getCipher())){
                return false;
            }
            if (TextUtils.isEmpty(crypto.getMac())){
                return false;
            }
            if (TextUtils.isEmpty(crypto.getKdf())){
                return false;
            }
            WalletFile.CipherParams cipherparams = crypto.getCipherparams();
            if (cipherparams == null || TextUtils.isEmpty(cipherparams.getIv())){
                return false;
            }
            if (crypto.getKdfparams() == null){
                return false;
            }
            return true;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    public static boolean isValidMnemonic(String mnemonic){
        try {
            MnemonicCode.INSTANCE.check(Arrays.asList(mnemonic.split(" ")));
            return true;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    public static ECKeyPair decrypt(String keystore, String password){
        try {
            return Wallet.decrypt(password, loadWalletFileByJson(keystore));
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
}

