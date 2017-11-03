package vn.dmcl.eagleeyes.view.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.dto.AreaDTO;
import vn.dmcl.eagleeyes.dto.DCheckManageFlyerDTO;
import vn.dmcl.eagleeyes.helper.ToastHelper;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<AreaDTO>> _listDataChild;
    private List<DCheckManageFlyerDTO> list;

    public int getCurrentUserIndex() {
        return currentUserIndex;
    }

    public void setCurrentUserIndex(int currentUserIndex) {
        this.currentUserIndex = currentUserIndex;
    }

    public int getCurrentDCheckAreaIndex() {
        return currentDCheckAreaIndex;
    }

    public void setCurrentDCheckAreaIndex(int currentDCheckAreaIndex) {
        this.currentDCheckAreaIndex = currentDCheckAreaIndex;
    }

    private int currentUserIndex, currentDCheckAreaIndex;

    public ExpandableListAdapter(Context context, List<DCheckManageFlyerDTO> dtos) {
        this._context = context;
        this.list = dtos;
        if (dtos != null)
            generateData(dtos);
        else {
            _listDataHeader = new ArrayList<>();
            _listDataChild = new HashMap<>();
        }
    }

    @Override
    public AreaDTO getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final AreaDTO childArea = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert infalInflater != null;
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_position = convertView.findViewById(R.id.tv_position);
        ImageView iv_check = convertView.findViewById(R.id.iv_check);
        LinearLayout ll_menu_item = convertView.findViewById(R.id.ll_menu_item);
        TextView tv_count = convertView.findViewById(R.id.tv_count);

        tv_name.setText(childArea.getName());
        tv_position.setText("000" + "-" + "000");
        tv_count.setText("Số lượng: " + childArea.getCount());
        iv_check.setVisibility(View.VISIBLE);
        if (childArea.getStatus() == AppConst.AreaStatus.Started)
            iv_check.setImageResource(R.drawable.ic_start);
        else if (childArea.getStatus() == AppConst.AreaStatus.Ended)
            iv_check.setImageResource(R.drawable.ic_ischeck);
        else iv_check.setVisibility(View.INVISIBLE);
        if (currentUserIndex == groupPosition && currentDCheckAreaIndex == childPosition)
            ll_menu_item.setBackgroundResource(R.color.area_stroke);
        else
            ll_menu_item.setBackgroundResource(R.color.Transparent);

        return convertView;
    }

    public boolean isCompleteAllArea() {
        for (DCheckManageFlyerDTO manageFlyerDTO : list)
            for (AreaDTO areaDTO : manageFlyerDTO.getArea())
                if (areaDTO.getStatus() != AppConst.AreaStatus.Ended)
                    return false;
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    public void updateData(List<DCheckManageFlyerDTO> dtos) {
        generateData(dtos);
        notifyDataSetChanged();
    }


    public void updateSelectedItem(int currentUserIndex, int currentDAreaIndex) {
        this.currentUserIndex = currentUserIndex;
        this.currentDCheckAreaIndex = currentDAreaIndex;
        notifyDataSetChanged();
    }

    private void generateData(List<DCheckManageFlyerDTO> list) {
        _listDataHeader.clear();
        _listDataChild.clear();
        for (int i = 0; i < list.size(); i++) {
            _listDataHeader.add(list.get(i).getName());
            _listDataChild.put(_listDataHeader.get(i), list.get(i).getArea());
        }
    }

    public int updateItemStatus(int flyerIndex, int areaIndex, int status) {
        if (flyerIndex < 0 || flyerIndex >= _listDataHeader.size()) {
            ToastHelper.showShortToast("Không tìm thấy flyer chỉ định");
            return -1;
        }
        if (areaIndex < 0 || areaIndex > _listDataChild.get(_listDataHeader.get(flyerIndex)).size()) {
            ToastHelper.showShortToast("Không tìm thấy khu vực chỉ định");
            return -1;
        }
        _listDataChild.get(_listDataHeader.get(flyerIndex)).get(areaIndex).setStatus(status);
        notifyDataSetChanged();
        return areaIndex;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
