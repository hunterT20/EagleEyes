package vn.dmcl.eagleeyes.view.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.dto.AreaDTO;
import vn.dmcl.eagleeyes.helper.ToastHelper;

import static android.content.ContentValues.TAG;

public class FlyerMenuAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private List<AreaDTO> arrData;
    private int currentSelectIndex = 0;

    public FlyerMenuAdapter(Activity activity, List<AreaDTO> list) {

        this.activity = activity;
        arrData = list;
    }

    @Override
    public int getCount() {
        if (arrData == null)
            return 0;
        return arrData.size();
    }

    @Override
    public AreaDTO getItem(int position) {
        if (arrData == null)
            return null;
        return arrData.get(position);
    }

    public void updateData(List<AreaDTO> list) {
        arrData = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowHolder holder = null;

        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item, parent, false);
            holder = getHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ShowHolder) convertView.getTag();
        }

        holder.pos = position;
        setChildViewTag(holder);
        setViewData(holder);

        return convertView;
    }

    private void setChildViewTag(ShowHolder holder) {
    }

    private ShowHolder getHolder(View convertView) {
        ShowHolder holder = new ShowHolder();


        holder.tv_name = convertView.findViewById(R.id.tv_name);
        holder.tv_position = convertView.findViewById(R.id.tv_position);
        holder.tv_count = convertView.findViewById(R.id.tv_count);
        holder.iv_check = convertView.findViewById(R.id.iv_check);
        holder.ll_menu_item = convertView.findViewById(R.id.ll_menu_item);

        return holder;
    }

    @Override
    public void onClick(View v) {
        ShowHolder holder;
        int cr;
        switch (v.getId()) {
            default:
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setViewData(ShowHolder holder) {
        try {
            if (arrData == null) return;
            if (arrData.size() < holder.pos)
                return;
            int pos = holder.pos;
            AreaDTO dto = arrData.get(pos);

            holder.tv_name.setText(dto.getName());
            holder.tv_position.setText(dto.getLat() + "-" + dto.getLng());
            holder.tv_count.setText("Số lượng: " + dto.getCount());
            holder.iv_check.setVisibility(View.VISIBLE);
            if (getItem(holder.pos).getStatus() == AppConst.AreaStatus.Started)
                holder.iv_check.setImageResource(R.drawable.ic_start);
            else if (getItem(holder.pos).getStatus() == AppConst.AreaStatus.Ended)
                holder.iv_check.setImageResource(R.drawable.ic_ischeck);
            else holder.iv_check.setVisibility(View.INVISIBLE);
            if (holder.pos == currentSelectIndex)
                holder.ll_menu_item.setBackgroundResource(R.color.area_stroke);
            else holder.ll_menu_item.setBackgroundResource(R.color.Transparent);

        } catch (Exception ex) {
            Log.e(TAG, "setViewData: " + ex);
        }
    }


    public void updateSelectedItem(int pos) {
        currentSelectIndex = pos;
        notifyDataSetChanged();
    }

    public int IndexOfItem(AreaDTO area) {
        if (area == null)
            return -1;
        return arrData.indexOf(area);
    }

    public int updateItemStatus(String areaId, int status) {
        int pos = getAreaPosition(areaId);
        if (pos < 0) {
            ToastHelper.showShortToast("Không tìm thấy khu vực chỉ định");
            return -1;
        }
        arrData.get(pos).setStatus(status);
        notifyDataSetChanged();
        return pos;
    }

    public boolean isCompleteAllArea() {
        for (int i = 0; i < arrData.size(); i++)
            if (arrData.get(i).getStatus() != AppConst.AreaStatus.Ended)
                return false;
        return true;
    }

    public boolean isWorking() {
        for (int i = 0; i < arrData.size(); i++)
            if (arrData.get(i).getStatus() != AppConst.AreaStatus.Started)
                return true;
        return false;
    }

    private int getAreaPosition(String areaId) {
        for (int i = 0; i < arrData.size(); i++)
            if (arrData.get(i).getId() == areaId)
                return i;
        return -1;
    }

    public List<AreaDTO> getArrData() {
        return arrData;
    }

    public void setArrData(List<AreaDTO> arrData) {
        this.arrData = arrData;
        //notifyDataSetChanged();
    }

    public void addData(List<AreaDTO> arrData) {
        this.arrData = arrData;
    }


    private class ShowHolder {
        int pos;
        TextView tv_name;
        TextView tv_position;
        TextView tv_count;
        ImageView iv_check;
        LinearLayout ll_menu_item;
    }
}