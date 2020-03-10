package com.platon.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.platon.framework.util.MapUtils;
import com.platon.wallet.R;

import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.platon.FunctionType;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class PlatOnFunction implements Parcelable {

    /**
     * @see org.web3j.platon.FunctionType
     */
    private int type;

    private List<Map<String, Object>> parameters;

    public static PlatOnFunction createTransferFunction() {
        return new PlatOnFunction(FunctionType.TRANSFER);
    }

    public PlatOnFunction() {

    }

    private PlatOnFunction(int type) {
        this.type = type;
    }

    protected PlatOnFunction(Parcel in) {
        type = in.readInt();
        parameters = in.readArrayList(Map.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeList(parameters);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlatOnFunction> CREATOR = new Creator<PlatOnFunction>() {
        @Override
        public PlatOnFunction createFromParcel(Parcel in) {
            return new PlatOnFunction(in);
        }

        @Override
        public PlatOnFunction[] newArray(int size) {
            return new PlatOnFunction[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Map<String, Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    public int getTxnInfoRes() {
        switch (type) {
            case FunctionType.DELEGATE_FUNC_TYPE:
                return R.string.msg_delegate_info;
            case FunctionType.WITHDREW_DELEGATE_FUNC_TYPE:
                return R.string.msg_withdraw_info;
            default:
                return R.string.msg_transfer_info;
        }
    }

    public List<org.web3j.platon.PlatOnFunction> createPlatOnFunctionList() {
        switch (type) {
            case FunctionType.TRANSFER:
                return Arrays.asList(new org.web3j.platon.PlatOnFunction(type));
            case FunctionType.DELEGATE_FUNC_TYPE:
            case FunctionType.WITHDREW_DELEGATE_FUNC_TYPE:
                return buildPlatOnFunctionList(type);
            default:
                return new ArrayList<>();
        }

    }

    private org.web3j.platon.PlatOnFunction mapToPlatOnFunction(Map<String, Object> map, int functionType) {
        if (functionType == FunctionType.DELEGATE_FUNC_TYPE) {
            return new org.web3j.platon.PlatOnFunction(functionType,
                    Arrays.asList(new Uint16(BigInteger.valueOf(MapUtils.getLong(map, "type")))
                            , new BytesType(Numeric.hexStringToByteArray(MapUtils.getString(map, "nodeId")))
                            , new Uint256(new BigInteger(MapUtils.getString(map, "amount")))));
        } else if (functionType == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
            return new org.web3j.platon.PlatOnFunction(functionType,
                    Arrays.asList(new Uint64(new BigInteger(MapUtils.getString(map, "stakingBlockNum")))
                            , new BytesType(Numeric.hexStringToByteArray(MapUtils.getString(map, "nodeId")))
                            , new Uint256(new BigInteger(MapUtils.getString(map, "amount")))));
        }
        return null;
    }

    private List<org.web3j.platon.PlatOnFunction> buildPlatOnFunctionList(int functionType) {

        if (parameters == null || parameters.isEmpty()) {
            return new ArrayList<>();
        }

        return Flowable
                .fromIterable(parameters)
                .map(new Function<Map<String, Object>, org.web3j.platon.PlatOnFunction>() {
                    @Override
                    public org.web3j.platon.PlatOnFunction apply(Map<String, Object> map) throws Exception {
                        return mapToPlatOnFunction(map, functionType);
                    }
                })
                .toList()
                .blockingGet();
    }
}
