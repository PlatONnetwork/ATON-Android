package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.NodeEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class NodeSettingsContract {


    public interface View extends IView {

        void showTitleView(boolean edited);

        void updateNodeList(List<NodeEntity> nodeEntityList);

        void notifyDataChanged(List<NodeEntity> nodeEntityList);

        String getNodeAddress(long id);

        List<NodeEntity> getNodeList();

        void removeNodeList(List<NodeEntity> nodeEntityList);

        void setChecked(int position);
    }

    public interface Presenter extends IPresenter<View> {

        void edit();

        void cancel();

        void fetchNodes();

        void save();

        void delete(NodeEntity nodeEntity);

        void updateNode(NodeEntity nodeEntity, boolean isChecked);
    }
}
