package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juzix.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ChangeMemberNameDialogFragment extends BaseDialogFragment {

    @BindView(R.id.et_new_member_name)
    EditText etNewMemberName;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    Unbinder unbinder;

    public static ChangeMemberNameDialogFragment newInstance() {
        ChangeMemberNameDialogFragment dialogFragment = new ChangeMemberNameDialogFragment();
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_change_member_name, null);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.CENTER);
        unbinder = ButterKnife.bind(this, contentView);
        return baseDialog;
    }

    @OnClick({R.id.tv_cancel, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_submit:
                if (TextUtils.isEmpty(etNewMemberName.getText().toString())) {
                    Toast.makeText(getContext(), "member name can't be null", Toast.LENGTH_SHORT).show();
                    return;
                }
                //todo
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
