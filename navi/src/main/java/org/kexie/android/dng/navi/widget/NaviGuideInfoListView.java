package org.kexie.android.dng.navi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import org.kexie.android.dng.navi.viewmodel.beans.StationDescription;

import java.util.List;

import androidx.databinding.BindingAdapter;

/**
 * Created by hongming.wang on 2017/6/22.
 */

public class NaviGuideInfoListView extends ExpandableListView {

    public NaviGuideInfoListView(Context context) {
        super(context);
    }

    public NaviGuideInfoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NaviGuideInfoListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置参数
    private void setParams() {
        //隐藏分割线
        setDivider(null);
        //去掉默认的箭头
        setGroupIndicator(null);
    }

    public void setGuideData(List<StationDescription> dataList) {
        setParams();
        if (null != dataList && dataList.size() > 0) {
            StationDescription header = new StationDescription();
            header.setGroupIconType(-1);
            header.setGroupName("当前地点");
            dataList.add(0, header);
            StationDescription footer = new StationDescription();
            footer.setGroupIconType(-2);
            footer.setGroupName("");
            dataList.add(dataList.size(), footer);

            NaviGuideAdapter adapter = new NaviGuideAdapter(getContext(), dataList);
            setAdapter(adapter);
        }
    }

    @BindingAdapter("data")
    public static void setGuideData(NaviGuideInfoListView view, List<StationDescription> dataList) {
        view.setGuideData(dataList);
    }
}
