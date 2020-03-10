package com.platon.framework.network;


import com.platon.framework.R;

public enum ApiErrorCode {

    SUCCESS(0, R.string.success),

    SYSTEM_ERROR(-1, R.string.system_error),

    NETWORK_ERROR(-2, R.string.network_error),

    REQUEST_PARAMS_ERROR(1, R.string.request_params_error),

    SYSTEM_CONFIG_ERROR(2, R.string.system_config_error),

    VERIFICATION_CODE_ERR(400, R.string.verification_code_error),

    VERIFICATION_CODE_SENT(401, R.string.verification_code_sent),

    SK_NOT_FOUND(1004, R.string.private_key_not_found);

    public final int code;

    public final int descId;

    ApiErrorCode(int code, int resId) {
        this.code = code;
        this.descId = resId;
    }

    public static ApiErrorCode fromCode(int code) {
        for (ApiErrorCode errorCode : ApiErrorCode.values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return NETWORK_ERROR;
    }
}
