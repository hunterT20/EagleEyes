package vn.dmcl.eagleeyes.customView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.data.dto.Area;
import vn.dmcl.eagleeyes.data.dto.AreaFlyer;
import vn.dmcl.eagleeyes.data.dto.DCheckManageFlyerDTO;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.view.map.ExpandableListAdapter;
import vn.dmcl.eagleeyes.view.map.FlyerMenuAdapter;
import vn.dmcl.eagleeyes.view.map.MainMapActivity;

/**
 * Created by MyPC on 18/10/2016.
 */

public class DrawerFragment extends Fragment {

    boolean isFlyer = true;
    View view;
    TextView tv_title;
    private ListView mListView;
    private FlyerMenuAdapter flyerMenuAdapter;
    private NavCallback mCallback;
    SwipeRefreshLayout sw_list;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<DCheckManageFlyerDTO> dCheckManageFlyerDTOs;
    List<Area> areaDTOs;

    Area currentArea;

    int currentAreaWorkingIndex = -1, currentUserWorkingIndex = -1;

    public interface NavCallback {
        void onNavSelected(int position);

        void onNavDCheckSelected(int flyerPosition, int areaPosition);
    }


    /**
     * Create a static method to return this fragment
     *
     * @return - this fragment
     */
    public static DrawerFragment newInstance(boolean isFlyer) {
        DrawerFragment drawerFragment = new DrawerFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFlyer", isFlyer);
        drawerFragment.setArguments(bundle);
        return drawerFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MainMapActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.drawer_fragment, container, false);

        isFlyer = getArguments().getBoolean("isFlyer");
        findView();
        setViewData();
        setListener();


        return view;
    }

    public void updateFlyerData(AreaFlyer areaFlyer) {
        areaDTOs = areaFlyer.getArea();
        flyerMenuAdapter.updateData(areaFlyer.getArea());
        if (areaFlyer.getArea() != null && areaFlyer.getArea().size() > 0) {
            currentAreaWorkingIndex = getCurrentAreaWorkingIndex();
            if (currentAreaWorkingIndex == -1) {
                currentArea = getFirstValidArea();
                flyerMenuAdapter.updateSelectedItem(flyerMenuAdapter.IndexOfItem(currentArea));
            }
            else {
                currentArea = areaFlyer.getArea().get(currentAreaWorkingIndex);
                flyerMenuAdapter.updateSelectedItem(currentAreaWorkingIndex);
            }
        }
    }

    public void updateDCheckData(List<DCheckManageFlyerDTO> dtos) {
        dCheckManageFlyerDTOs = dtos;
        listAdapter.updateData(dtos);
        if (dtos != null && dtos.size() > 0) {
            currentAreaWorkingIndex = getCurrentAreaWorkingIndex();
            if (currentAreaWorkingIndex == -1)
                currentArea = getFirstValidArea();
            else {
                currentArea = dCheckManageFlyerDTOs.get(currentUserWorkingIndex).getArea().get(currentAreaWorkingIndex);
                listAdapter.updateSelectedItem(currentUserWorkingIndex, currentAreaWorkingIndex);
            }
        }
    }


    public Area getCurrentArea() {
        return currentArea;
    }

    private void findView() {
        expListView = view.findViewById(R.id.lvExp);
        mListView = view.findViewById(R.id.listViewNav);
        tv_title = view.findViewById(R.id.tv_title);
        sw_list = view.findViewById(R.id.sw_list);
    }

    private void setListener() {
        if (isFlyer) {
            mListView.setOnItemClickListener(ListListener);
            flyerMenuAdapter = new FlyerMenuAdapter(getActivity(), null);
            mListView.setAdapter(flyerMenuAdapter);
        } else {
            listAdapter = new ExpandableListAdapter(getActivity(), null);
            // setting list adapter
            expListView.setAdapter(listAdapter);
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    if (currentAreaWorkingIndex == -1) {
                        //currentAreaWorkingIndex = childPosition;
                        //currentUserWorkingIndex = groupPosition;
                        listAdapter.updateSelectedItem(groupPosition, childPosition);
                        currentArea = listAdapter.getChild(groupPosition,childPosition);
                        if (mCallback != null)
                            mCallback.onNavDCheckSelected(groupPosition, childPosition);
                    } else if (currentAreaWorkingIndex == childPosition && currentUserWorkingIndex == groupPosition) {
                    } else
                        ToastHelper.showShortToast("Bạn vui lòng hoàn thành khu vực đã chọn để đến khu vực khác");
                    return false;
                }
            });
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    //ToastHelper.showShortToast(listDataHeader.get(groupPosition) + " Expanded");
                    int len = listAdapter.getGroupCount();
                    for (int i = 0; i < len; i++) {
                        if (i != groupPosition) {
                            expListView.collapseGroup(i);
                        }
                    }
                }
            });
        }
        sw_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(listener != null)
                    listener.onRefreshList();
                sw_list.setRefreshing(false);
            }
        });
        sw_list.setColorSchemeResources(R.color.blue,
                R.color.purple,
                R.color.green,
                R.color.orange);
    }

    private void setViewData() {
        if (isFlyer) {
            tv_title.setText("Danh sách Khu vực");
            expListView.setVisibility(View.GONE);
        } else {
            tv_title.setText("Danh sách Flyer");
            mListView.setVisibility(View.GONE);
            //prepareListData();
        }
    }

    // cập nhật trạng thái của khu vực trong danh sách
    public void updateCurrentAreaStatus(int status) {
        if (isFlyer) {
            int pos = flyerMenuAdapter.updateItemStatus(currentArea.getId(), status);
            if (status == AppConst.AreaStatus.Ended)
                currentAreaWorkingIndex = -1;
            else if (status == AppConst.AreaStatus.Started)
                currentAreaWorkingIndex = pos;
        }
        else {
            int pos = listAdapter.updateItemStatus(listAdapter.getCurrentUserIndex(), listAdapter.getCurrentDCheckAreaIndex(), status);
            if (status == AppConst.AreaStatus.Ended)
                currentAreaWorkingIndex = -1;
            else if (status == AppConst.AreaStatus.Started) {
                currentAreaWorkingIndex = pos;
                currentUserWorkingIndex = listAdapter.getCurrentUserIndex();
            }
        }
    }

    public Area getFirstValidArea() {
        if (isFlyer) {
            for (int i = 0; i < areaDTOs.size(); i++)
                if (areaDTOs.get(i).getStatus() == AppConst.AreaStatus.None)
                    return areaDTOs.get(i);
        } else {
            for (int i = 0; i < dCheckManageFlyerDTOs.size(); i++)
                for (int j = 0; j < dCheckManageFlyerDTOs.get(i).getArea().size(); j++)
                    if (dCheckManageFlyerDTOs.get(i).getArea().get(i).getStatus() == AppConst.AreaStatus.None)
                        return dCheckManageFlyerDTOs.get(i).getArea().get(i);
        }
        return new Area();
    }

    // kiểm tra hoàn thành các khu vực
    public boolean isCompleteAllArea(boolean isFlyer) {
        return isFlyer && flyerMenuAdapter.isCompleteAllArea();
    }

    // lắng nghe chọn khu vực nào
    private final AdapterView.OnItemClickListener ListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (currentAreaWorkingIndex > -1 && currentAreaWorkingIndex != position) {
                ToastHelper.showShortToast("Bạn vui lòng hoàn thành khu vực đã chọn để đến khu vực khác");
                return;
            }
            flyerMenuAdapter.updateSelectedItem(position);
            currentArea = flyerMenuAdapter.getItem(position);
            mCallback.onNavSelected(position);
        }
    };

    public FlyerMenuAdapter getFlyerAdapter() {
        return flyerMenuAdapter;
    }


    // lấy vị trí khu vực đang làm
    private int getCurrentAreaWorkingIndex() {
        if (isFlyer) {
            for (int i = 0; i < areaDTOs.size(); i++) {
                if (areaDTOs.get(i).getStatus() == AppConst.AreaStatus.Started) {
                    return i;
                }
            }
        } else {
            if (dCheckManageFlyerDTOs != null) {
                for (int i = 0; i < dCheckManageFlyerDTOs.size(); i++) {
                    List<Area> list = dCheckManageFlyerDTOs.get(i).getArea();
                    for (int j = 0; j < list.size(); j++)
                        if (list.get(j).getStatus() == AppConst.AreaStatus.Started) {
                            currentUserWorkingIndex = i;
                            return j;
                        }
                }
            }
        }
        return -1;
    }

    public boolean isWorking() {
        if (isFlyer)
            return flyerMenuAdapter.isWorking();
        else return false;
    }
    MenuListener listener;
    public void setMenuListener(MenuListener listener)
    {
        this.listener = listener;
    }
    public interface MenuListener{
        void onRefreshList();
    }
}
