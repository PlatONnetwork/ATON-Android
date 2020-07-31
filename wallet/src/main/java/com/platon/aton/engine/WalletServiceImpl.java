package com.platon.aton.engine;

import com.platon.aton.App;
import com.platon.aton.R;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletSelectedIndex;
import com.platon.aton.entity.WalletType;
import com.platon.aton.utils.JZMnemonicUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.utils.LogUtils;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.bech32.AddressBech32;
import org.web3j.crypto.bech32.AddressManager;
import org.web3j.crypto.bech32.Bech32;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
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

    private Wallet generateWallet(ECKeyPair ecKeyPair,ECKeyPair parentEcKeyPair, String name, String password, @WalletType int walletType,int HDPathIndex) {
        try {
            long time = System.currentTimeMillis();
            String filename = JZWalletUtil.getWalletFileName(Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));

            boolean isHD = false;
            boolean isShow = false;
            int pathIndex = 0;
            int sortIndex = 0;
            int selectedIndex = WalletSelectedIndex.UNSELECTED;
            String parentId = "";
            int depth = 0;
            String key = "";
            Bech32Address bech32Address = null;
            String uuid = "";
            String address = "";
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setAddr("");
            accountBalance.setFree("0");
            accountBalance.setLock("0");

            if(walletType == WalletType.ORDINARY_WALLET){
                isHD = false;
                WalletFile walletFile = org.web3j.crypto.Wallet.create(password, ecKeyPair, N_STANDARD, P_STANDARD);
                address = Bech32.addressDecodeHex(walletFile.getAddress().getMainnet());
                bech32Address = new Bech32Address(walletFile.getAddress().getMainnet(),walletFile.getAddress().getTestnet());
                key = JZWalletUtil.writeWalletFileAsString(walletFile);
                uuid = WalletManager.getInstance().isMainNetWalletAddress() ? bech32Address.getMainnet() : bech32Address.getTestnet();

            } else if(walletType == WalletType.HD_WALLET){
                isHD = true;
                WalletFile walletFile = org.web3j.crypto.Wallet.create(password, ecKeyPair, N_STANDARD, P_STANDARD);
                address = Bech32.addressDecodeHex(walletFile.getAddress().getMainnet());
                bech32Address = new Bech32Address(walletFile.getAddress().getMainnet(),walletFile.getAddress().getTestnet());
                key = JZWalletUtil.writeWalletFileAsString(walletFile);
                uuid = Numeric.toHexStringNoPrefixZeroPadded(ecKeyPair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
            }else{
                isHD = true;
                pathIndex = HDPathIndex;
                address = Keys.getAddress(ecKeyPair);
                AddressBech32 addressBech32 = AddressManager.getInstance().executeEncodeAddress(ecKeyPair);
                bech32Address = new Bech32Address(addressBech32.getMainnet(),addressBech32.getTestnet());
                depth = 1;
                uuid = Numeric.toHexStringNoPrefixZeroPadded(ecKeyPair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
                parentId = Numeric.toHexStringNoPrefixZeroPadded(parentEcKeyPair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
            }

            return new Wallet.Builder()
                    .uuid(uuid)
                    .isHD(isHD)
                    .pathIndex(pathIndex)
                    .sortIndex(sortIndex)
                    .selectedIndex(selectedIndex)
                    .parentId(parentId)
                    .depth(depth)
                    .key(key)
                    .name(name)
                    .address(address)
                    .betch32Address(bech32Address)
                    .keystorePath(filename)
                    .createTime(time)
                    .updateTime(time)
                    .avatar(getWalletAvatar(walletType))
                    .parentWalletName("")
                    .accountBalance(accountBalance)
                    .isShow(isShow)
                    .build();
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return null;
    }

    @Override
    public Wallet importMnemonic(String mnemonic, String name, String password, int... index) {
        return createWallet(mnemonic, name, password,index);
    }

    @Override
    public List<Wallet> importMnemonicWalletList(String mnemonic, String name, String password) {
        return createWalletList(mnemonic, name, password);
    }

    @Override
    public Wallet createWallet(String mnemonic, String name, String password, int... index) {
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
            DeterministicKey child;
            if(index.length == 0){
                 child = dh.deriveChild(parentPath, true, true, new ChildNumber(0));
            }else{
                child = dh.deriveChild(parentPath, true, true, new ChildNumber(index[0]));
            }

            //7.通过Keystore生成公Keystore对
            ECKeyPair ecKeyPair = ECKeyPair.create(child.getPrivKeyBytes());
            return generateWallet(ecKeyPair,null, name, password,WalletType.ORDINARY_WALLET,0);
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return null;
    }

    @Override
    public List<Wallet> createWalletList(String mnemonic, String name, String password) {
        List<Wallet> walletList = new ArrayList<>();
        try {
            // 2.生成种子
            byte[] seed = JZMnemonicUtil.generateSeed(mnemonic, null);
            // 3. 生成根Keystore root private key 树顶点的master key ；bip32
            DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            // 4. 由根Keystore生成 第一个HD 钱包
            DeterministicHierarchy dh = new DeterministicHierarchy(rootPrivateKey);
            ECKeyPair rootEcKeyPair = ECKeyPair.create(dh.getRootKey().getPrivKeyBytes());
            Wallet rootWallet = generateWallet(rootEcKeyPair,null, name, password,WalletType.HD_WALLET,0);
            walletList.add(rootWallet);

            // 5. 定义父路径 H则是加强
            List<ChildNumber> parentPath = HDUtils.parsePath(PATH);
            // 6. 由父路径,派生出第一个子Keystore "new ChildNumber(0)" 表示第一个（PATH）
            for (int i = 0; i < 30; i++) {
                DeterministicKey child = dh.deriveChild(parentPath, true, true, new ChildNumber(i));
                //7.通过Keystore生成公Keystore对
                ECKeyPair ecKeyPair = ECKeyPair.create(child.getPrivKeyBytes());
                Wallet subWallet =  generateWallet(ecKeyPair,rootEcKeyPair, name + "_" + (i + 1), password,WalletType.HD_SUB_WALLET,i);
                walletList.add(subWallet);
            }

        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return walletList;

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
            return generateWallet(ecKeyPair, null, name, password,WalletType.ORDINARY_WALLET,0);
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
            return generateWallet(ecKeyPair,null, name, password,WalletType.ORDINARY_WALLET,0);
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return null;
        }
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
    public String getWalletAvatar(@WalletType int walletType) {

        String[] avatarArray = App.getContext().getResources().getStringArray(R.array.wallet_avatar);
        if(walletType == WalletType.HD_WALLET){
           return "avatar_16";
        }else{
            return avatarArray[new Random().nextInt(avatarArray.length)];
        }
    }

    private static class InstanceHolder {
        private static volatile WalletServiceImpl INSTANCE = new WalletServiceImpl();
    }
}
