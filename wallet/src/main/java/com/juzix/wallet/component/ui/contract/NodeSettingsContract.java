package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Node;

import java.util.List;

/**
 * @author matrixelement
 */
public class NodeSettingsContract {


    public interface View extends IView {

        void showTitleView(boolean edited);

        void updateNodeList(List<Node> nodeEntityList);

        void notifyDataChanged(List<Node> nodeEntityList);

        String getNodeAddress(long id);

        List<Node> getNodeList();

        void removeNodeList(List<Node> nodeEntityList);

        void setChecked(int position);
    }

    public interface Presenter extends IPresenter<View> {

        void edit();

        void cancel();

        void fetchNodes();

        void save();

        void delete(Node nodeEntity);

        void updateNode(Node nodeEntity, boolean isChecked);

        boolean isEdit();
    }
}
