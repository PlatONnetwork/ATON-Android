package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.Node;

import java.util.List;

/**
 * @author matrixelement
 */
public class NodeSettingsContract {


    public interface View extends IView {

        void notifyDataChanged(List<Node> nodeEntityList);

        void setChecked(int position);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchNodes();

        void updateNode(Node nodeEntity, boolean isChecked);
    }
}
