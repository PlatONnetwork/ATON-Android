package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.UnlockWithPasswordContract;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.Wallet;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class UnlockWithPasswordPresenterTest extends BaseTestCase {

    @Mock
    UnlockWithPasswordContract.View view;

    UnlockWithPasswordPresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(UnlockWithPasswordContract.View.class);
        presenter = new UnlockWithPasswordPresenter();
        presenter.attachView(view);
    }

   @Test
    public void setSelectWallet(){
       Wallet wallet = new Wallet();
       Bech32Address bech32Address = new Bech32Address("lat1jxeg784p2vuemglc7cy59mzgq50heg3gjt7fca","lax1jxeg784p2vuemglc7cy59mzgq50heg3gawvxkj");
       wallet.setBech32Address(bech32Address);
       wallet.setKeystorePath("UTC--2020-07-13T14-46-35.629--2fe04afd39ac4bfd9374ee50ee5d3315b33038c81c618e19477d1ecf38d047162c0ca1bb640c864d730a5e1ef09630cb7704c1f564b2b0dd41fc79cf690adfdc.json");
       wallet.setName("有钱");
       wallet.setChainId("101");
       String key = "\"{\"address\":{\"mainnet\":\"lat1jxeg784p2vuemglc7cy59mzgq50heg3gjt7fca\",\"testnet\":\"lax1jxeg784p2vuemglc7cy59mzgq50heg3gawvxkj\"},\"id\":\"d04685eb-8374-40e4-91f2-f7bbad28e83d\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"2e3868606fff20dd9382c41a52dbd76c\"},\"ciphertext\":\"c867aea0d7619cd776419d6973a1250f8cd643787aeb6b707ad8acaf0ac099ec\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":16384,\"p\":1,\"r\":8,\"salt\":\"253bf6760852211172ca68f3f01fb7196a7643c5a887385dff3454a6f4da895f\"},\"mac\":\"390b4330a3317e7e1f65c12797c874ef73d35c372d36274e70fa6dbe96765c5d\"}}\"";
       wallet.setKey(key);
       wallet.setUuid("d04685eb-8374-40e4-91f2-f7bbad28e83d");
       presenter.setSelectWallet(wallet);
       Mockito.verify(view).updateWalletInfo(Mockito.any());
    }


    @Test
    public void unlock(){

        Wallet wallet = new Wallet();
        Bech32Address bech32Address = new Bech32Address("lat1jxeg784p2vuemglc7cy59mzgq50heg3gjt7fca","lax1jxeg784p2vuemglc7cy59mzgq50heg3gawvxkj");
        wallet.setBech32Address(bech32Address);
        wallet.setKeystorePath("UTC--2020-07-13T14-46-35.629--2fe04afd39ac4bfd9374ee50ee5d3315b33038c81c618e19477d1ecf38d047162c0ca1bb640c864d730a5e1ef09630cb7704c1f564b2b0dd41fc79cf690adfdc.json");
        wallet.setName("有钱");
        wallet.setChainId("101");
        String key = "\"{\"address\":{\"mainnet\":\"lat1jxeg784p2vuemglc7cy59mzgq50heg3gjt7fca\",\"testnet\":\"lax1jxeg784p2vuemglc7cy59mzgq50heg3gawvxkj\"},\"id\":\"d04685eb-8374-40e4-91f2-f7bbad28e83d\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"2e3868606fff20dd9382c41a52dbd76c\"},\"ciphertext\":\"c867aea0d7619cd776419d6973a1250f8cd643787aeb6b707ad8acaf0ac099ec\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":16384,\"p\":1,\"r\":8,\"salt\":\"253bf6760852211172ca68f3f01fb7196a7643c5a887385dff3454a6f4da895f\"},\"mac\":\"390b4330a3317e7e1f65c12797c874ef73d35c372d36274e70fa6dbe96765c5d\"}}\"";
        wallet.setKey(key);
        wallet.setUuid("d04685eb-8374-40e4-91f2-f7bbad28e83d");
        presenter.setSelectWallet(wallet);

        String pws = "qq123456";
        presenter.unlock(pws);

    }


}