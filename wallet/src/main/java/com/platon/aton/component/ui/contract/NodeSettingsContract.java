package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Node;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;
import com.platon.framework.base.IView;

import java.util.List;

/**
 * @author matrixelement
 */
public class NodeSettingsContract {

    public interface View extends BaseViewImp {

        void notifyDataChanged(List<Node> nodeEntityList);

        void setChecked(int position);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchNodes();

        void updateNode(Node nodeEntity, boolean isChecked);
    }
}
