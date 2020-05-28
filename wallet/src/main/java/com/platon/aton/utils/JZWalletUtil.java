package com.platon.aton.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.stetho.common.LogUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.AddressMatchingResultType;

import org.bitcoinj.crypto.MnemonicCode;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.bech32.AddressBech32;
import org.web3j.crypto.bech32.AddressManager;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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

    public static String generateMnemonic() {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        return JZMnemonicUtil.generateMnemonic(initialEntropy);
    }

    public static String encryptMnemonic(String keystore, String mnemonic, String password) {
        WalletFile walletFile = toWalletFile(keystore);
        if (walletFile == null) {
            return mnemonic;
        }
        WalletFile.Crypto crypto = walletFile.getCrypto();
        if (!(crypto.getKdfparams() instanceof WalletFile.ScryptKdfParams)) {
            return mnemonic;
        }
        WalletFile.ScryptKdfParams scryptKdfParams = (WalletFile.ScryptKdfParams) crypto.getKdfparams();
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        int dklen = scryptKdfParams.getDklen();
        int n = scryptKdfParams.getN();
        int p = scryptKdfParams.getP();
        int r = scryptKdfParams.getR();
        byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
        Charset charset = Charset.forName("UTF-8");
        byte[] derivedKey = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(charset), salt, n, r, p, dklen);
        byte[] mnemonicBytes = mnemonic.getBytes(charset);
        byte[] cipherText = performCipherOperation(Cipher.ENCRYPT_MODE, iv, derivedKey, mnemonicBytes);
        return Numeric.toHexStringNoPrefix(cipherText);
    }

    public static String decryptMnenonic(String keystore, String encryptMnemonic, String password) {
        WalletFile walletFile = toWalletFile(keystore);
        if (walletFile == null) {
            return encryptMnemonic;
        }
        WalletFile.Crypto crypto = walletFile.getCrypto();
        if (!(crypto.getKdfparams() instanceof WalletFile.ScryptKdfParams)) {
            return encryptMnemonic;
        }
        WalletFile.ScryptKdfParams scryptKdfParams = (WalletFile.ScryptKdfParams) crypto.getKdfparams();
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(encryptMnemonic);
        int dklen = scryptKdfParams.getDklen();
        int n = scryptKdfParams.getN();
        int p = scryptKdfParams.getP();
        int r = scryptKdfParams.getR();
        byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
        Charset charset = Charset.forName("UTF-8");
        byte[] derivedKey = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(charset), salt, n, r, p, dklen);
//        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 32);
        byte[] mnemonicBytes = performCipherOperation(Cipher.DECRYPT_MODE, iv, derivedKey, cipherText);
        return new String(mnemonicBytes, charset);
    }

    public static WalletFile toWalletFile(String json) {
        try {
            return loadWalletFileByJson(json);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
            return null;
        }
    }

    public static WalletFile loadWalletFileByJson(String json) throws IOException {

        return objectMapper.readValue(json, WalletFile.class);
    }

    public static String writeWalletFileAsString(WalletFile walletFile) throws IOException {
        return objectMapper.writeValueAsString(walletFile);
    }

    public static Credentials getCredentials(String password, String json) throws IOException, CipherException {
        Credentials credentials = null;
        try {
            WalletFile walletFile = loadWalletFileByJson(json);
            credentials = Credentials.create(Wallet.decrypt(password, walletFile));
        } catch (CipherException e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
        } catch (IOException e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
        }
        return credentials;
    }

    public static String getWalletFileName(String address) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        return dateFormat.format(new Date()) + address + ".json";
    }

    public static boolean isValidPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
    }

    public static boolean isValidAddress(String input) {
       /* if (TextUtils.isEmpty(input)) {
            return false;
        }
        String cleanInput = Numeric.cleanHexPrefix(input);
        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;*/


        if (TextUtils.isEmpty(input)) {
            return false;
        }

        if(input.length() > 5){
            String prefix = input.subSequence(0,4).toString();
            if(!(prefix.equalsIgnoreCase("lat1") || prefix.equalsIgnoreCase("lax1"))){
                return false;
            }
        }

        if(input.length() < 42){
            return false;
        }
        String suffix = input.subSequence(36,input.length()).toString();
        if((suffix.contains("1")||suffix.contains("b")||suffix.contains("i")||suffix.contains("o"))){
           return false;
        }
        return true;
    }



    public static int isValidAddressMatchingNet(String input) {
        if(input.length() > 5){
            String prefix = input.subSequence(0, 4).toString();
            if (WalletManager.getInstance().isMainNetWalletAddress()) {
                if (prefix.equalsIgnoreCase("lat1")) {//主网格式
                    return AddressMatchingResultType.ADDRESS_MAINNET_MATCHING;
                } else {
                    return AddressMatchingResultType.ADDRESS_MAINNET_MISMATCHING;
                }
            } else if (!WalletManager.getInstance().isMainNetWalletAddress()) {
                if (prefix.equalsIgnoreCase("lax1")) {//测试网格式
                    return AddressMatchingResultType.ADDRESS_TESTNET_MATCHING;
                } else {
                    return AddressMatchingResultType.ADDRESS_TESTNET_MISMATCHING;
                }
            }
        }
        return 0;
    }



        public static boolean isValidKeystore(String keystore) {
        try {
            WalletFile walletFile = loadWalletFileByJson(keystore);
            if (walletFile == null) {
                return false;
            }
            if (TextUtils.isEmpty(walletFile.getOriginalAddress())) {
                return false;
            }
            if (TextUtils.isEmpty(walletFile.getId())) {
                return false;
            }
            if (walletFile.getVersion() == 0) {
                return false;
            }
            WalletFile.Crypto crypto = walletFile.getCrypto();
            if (crypto == null) {
                return false;
            }
            if (TextUtils.isEmpty(crypto.getCipher())) {
                return false;
            }
            if (TextUtils.isEmpty(crypto.getMac())) {
                return false;
            }
            if (TextUtils.isEmpty(crypto.getKdf())) {
                return false;
            }
            WalletFile.CipherParams cipherparams = crypto.getCipherparams();
            if (cipherparams == null || TextUtils.isEmpty(cipherparams.getIv())) {
                return false;
            }
            if (crypto.getKdfparams() == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
            return false;
        }
    }

    public static boolean isValidMnemonic(String mnemonic) {
        try {
            MnemonicCode.INSTANCE.check(Arrays.asList(mnemonic.split(" ")));
            return true;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
            return false;
        }
    }

    public static ECKeyPair decrypt(String keystore, String password) {
        try {
            return Wallet.decrypt(password, loadWalletFileByJson(keystore));
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
            return null;
        }
    }

    private static byte[] performCipherOperation(int mode, byte[] iv, byte[] encryptKey, byte[] text) {

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
            return null;
        }
    }
}

