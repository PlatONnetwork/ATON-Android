package com.platon.aton.engine;

import com.platon.aton.App;
import com.platon.aton.R;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.JZMnemonicUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.utils.LogUtils;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.util.List;
import java.util.Random;

class WalletServiceImpl implements WalletService {

    private static final String PATH = "M/44H/486H/0H/0";
    private static final int N_STANDARD = 16384;
    private static final int P_STANDARD = 1;

    private WalletServiceImpl() {
    }

    public static WalletServiceImpl getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String generateMnemonic() {
        return JZWalletUtil.generateMnemonic();
    }

    private Wallet generateWallet(ECKeyPair ecKeyPair, String name, String password) {
        try {
            long time = System.currentTimeMillis();
            String filename = JZWalletUtil.getWalletFileName(Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
            WalletFile walletFile = org.web3j.crypto.Wallet.create(password, ecKeyPair, N_STANDARD, P_STANDARD);
            if (walletFile == null) {
                return null;
            }
            Bech32Address bech32Address = new Bech32Address(walletFile.getAddress().getMainnet(),
                                                            walletFile.getAddress().getTestnet());
            return new Wallet.Builder()
                    .uuid(walletFile.getId())
                    .key(JZWalletUtil.writeWalletFileAsString(walletFile))
                    .name(name)
                    .address(walletFile.getOriginalAddress())
                    .betch32Address(bech32Address)
                    .keystorePath(filename)
                    .createTime(time)
                    .updateTime(time)
                    .avatar(getWalletAvatar())
                    .build();
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return null;
    }

    @Override
    public Wallet createWallet(String mnemonic, String name, String password) {
        try {
            // 2.生成种子
            byte[] seed = JZMnemonicUtil.generateSeed(mnemonic, null);
            // 3. 生成根Keystore root private key 树顶点的master key ；bip32
            DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            // 4. 由根Keystore生成 第一个HD 钱包
            DeterministicHierarchy dh = new DeterministicHierarchy(rootPrivateKey);
            // 5. 定义父路径 H则是加强
            List<ChildNumber> parentPath = HDUtils.parsePath(PATH);
            // 6. 由父路径,派生出第一个子Keystore "new ChildNumber(0)" 表示第一个（PATH）
            DeterministicKey child = dh.deriveChild(parentPath, true, true, new ChildNumber(0));
            //7.通过Keystore生成公Keystore对
            ECKeyPair ecKeyPair = ECKeyPair.create(child.getPrivKeyBytes());
            return generateWallet(ecKeyPair, name, password);
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return null;
    }

    @Override
    public Wallet importKeystore(String store, String name, String password) {
        try {
            long time = System.currentTimeMillis();
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(store, password);
            LogUtils.e("解析keystore的时间为：" + (System.currentTimeMillis() - time));
            if (ecKeyPair == null) {
                return null;
            }
            return generateWallet(ecKeyPair, name, password);
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return null;
        }
    }

    @Override
    public Wallet importPrivateKey(String privateKey, String name, String password) {
        if (!JZWalletUtil.isValidPrivateKey(privateKey)) {
            return null;
        }
        try {
            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix(Numeric.cleanHexPrefix(privateKey)));
            if (ecKeyPair == null) {
                return null;
            }
            return generateWallet(ecKeyPair, name, password);
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return null;
        }
    }

    @Override
    public Wallet importMnemonic(String mnemonic, String name, String password) {
        return createWallet(mnemonic, name, password);
    }

    @Override
    public String exportKeystore(Wallet wallet, String password) {
        try {
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(wallet.getKey(), password);
            if (ecKeyPair == null) {
                return "";
            }
            return wallet.getKey();
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return "";
        }
    }

    @Override
    public String exportPrivateKey(Wallet wallet, String password) {
        try {
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(wallet.getKey(), password);
            if (ecKeyPair == null) {
                return "";
            }
            return Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey());
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return "";
    }

    @Override
    public String exportPrivateKey(String mnemonic) {
        try {
            // 2.生成种子
            byte[] seed = JZMnemonicUtil.generateSeed(mnemonic, null);
            // 3. 生成根Keystore root private key 树顶点的master key ；bip32
            DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            // 4. 由根Keystore生成 第一个HD 钱包
            DeterministicHierarchy dh = new DeterministicHierarchy(rootPrivateKey);
            // 5. 定义父路径 H则是加强
            List<ChildNumber> parentPath = HDUtils.parsePath(PATH);
            // 6. 由父路径,派生出第一个子Keystore "new ChildNumber(0)" 表示第一个（PATH）
            DeterministicKey child = dh.deriveChild(parentPath, true, true, new ChildNumber(0));
            //7.通过Keystore生成公Keystore对
            ECKeyPair ecKeyPair = ECKeyPair.create(child.getPrivKeyBytes());
            return Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey());
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return null;
    }

    @Override
    public String getWalletAvatar() {
        String[] avatarArray = App.getContext().getResources().getStringArray(R.array.wallet_avatar);
        return avatarArray[new Random().nextInt(avatarArray.length)];
    }

    private static class InstanceHolder {
        private static volatile WalletServiceImpl INSTANCE = new WalletServiceImpl();
    }
}
