package com.juzix.wallet.app;

/**
 * @author matrixelement
 */
public class Constants {

    public interface Extra {
        String EXTRA_PRIVATE_KEY = "extra_private_key";
        String EXTRA_PASSWORD = "extra_password";
        String EXTRA_KEYSTORE = "extra_keystore";
        String EXTRA_WALLET = "extra_walletEntity";
        String EXTRA_ADDRESS = "extra_address";
        String EXTRA_TO_ADDRESS = "extra_to_address";
        String EXTRA_TRANSACTION = "extra_transaction";
        String EXTRA_HASH = "extra_hash";
        String EXTRA_MNEMONIC = "extra_mnemonic";
        String EXTRA_PIC = "extra_pic";
        String EXTRA_SCAN_QRCODE_DATA = "extra_scan_qrcode_data";
        String EXTRA_TYPE = "extra_type";
        String EXTRA_SHARED_OWNERS = "extra_shared_owners";
        String EXTRA_REQUIRED_SIGNATURES = "extra_required_signatures";
        String EXTRA_WALLET_NAME = "extra_wallet_name";
        String EXTRA_WALLET_INDEX = "extra_index";
        String EXTRA_WALLET_SUB_INDEX = "extra_sub_index";
        String EXTRA_CANDIDATE = "extra_candidate_entity";
        String EXTRA_VOTE = "extra_vote_entity";
        String EXTRA_ID = "extra_id";
        String EXTRA_CANDIDATE_ID = "extra_candidate_id";
        String EXTRA_CANDIDATE_NAME = "extra_candidate_name";
        String EXTRA_NAME = "extra_name";
    }

    public interface Bundle {

        String BUNDLE_WALLET = "bundle_wallet";
        String BUNDLE_TRANSFER_AMOUNT = "bundle_transfer_amount";
        String BUNDLE_TO_ADDRESS = "bundle_to_address";
        String BUNDLE_FEE_AMOUNT = "bundle_fee_amount";
        String BUNDLE_UUID = "bundle_UUID";
        String BUDLE_PASSWORD = "bundle_password";
        String BUNDLE_SHARE_APPINFO_LIST = "bundle_share_appinfo_list";
        String BUNDLE_TYPE = "bundle_type";
        String BUNDLE_FEE = "bundle_fee";
        String BUNDLE_TEXT = "bundle_text";
        String BUNDLE_MAP = "bundle_map";
    }

    public interface Action {

        String ACTION_NONE = "action_done";
        String ACTION_GET_ADDRESS = "action_get_address";
        String ACTION_SWITCH_LANGUAGE = "action_switch_language";
        String ACTION_CREATE_WALLET = "action_create_wallet";
        String ACTION_IMPORT_WALLET = "action_import_wallet";
    }

    public interface Preference {
        String KEY_SERVICE_TERMS_FLAG = "serviceTermsFlag";
        String KEY_OPERATE_MENU_FLAG = "operateMenuFlag";
        String KEY_LANGUAGE = "language";
        String KEY_FACE_TOUCH_ID_FLAG = "faceTouchIdFlag";
        String KEY_FIRST_ENTER = "firstEnter";
        String KEY_SHOW_ASSETS_FLAG = "showAssetsFlag";
        String KEY_UPDATE_VERSION_TIME = "updateVersionTime";
    }

    public interface Permission {

    }

    public interface RequestCode {

        int REQUEST_CODE_SELEECT_WALLET_ADDRESS = 1;
        int REQUEST_CODE_SELEECT_WALLET = 2;
        int REQUEST_CODE_EDIT_ADDRESS = 3;
        int REQUEST_CODE_ADD_ADDRESS = 4;
        int REQUEST_CODE_GET_ADDRESS = 5;
        int REQUEST_CODE_SCAN_QRCODE = 6;

    }

    public interface URL {
                String WEB3J_URL = "http://192.168.120.82:6789";
//                String WEB3J_URL = "http://192.168.9.76:8792";
//                String WEB3J_URL = "http://10.10.8.209:6789";
//        String WEB3J_URL = "https://syde.platon.network/test";
        String IP_URL = "http://ip-api.com/batch/";
        String UPDATE_URL = "https://api.github.com/repos/PlatONnetwork/ATON-Android/releases/latest";
    }

    public interface DBName {
        //数据库名称
        String PORTAL = "portal";
        int VERSION = 105;
    }
}
