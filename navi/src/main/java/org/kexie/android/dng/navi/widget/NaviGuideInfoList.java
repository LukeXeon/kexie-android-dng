package org.kexie.android.dng.navi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import org.kexie.android.dng.navi.viewmodel.entity.GuideInfo;

import java.util.List;

/**
 * Created by hongming.wang on 2017/6/22.
 */

public class NaviGuideInfoList extends ExpandableListView
{

    public NaviGuideInfoList(Context context)
    {
        super(context);
    }

    public NaviGuideInfoList(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NaviGuideInfoList(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    //设置参数
    private void setParams()
    {
        //隐藏分割线
        setDivider(null);
        //去掉默认的箭头
        setGroupIndicator(null);
    }

    public void setGuideData(List<GuideInfo> dataList)
    {
        setParams();
        if (null != dataList && dataList.size() > 0)
        {
            GuideInfo header = new GuideInfo();
            header.setGroupIconType(-1);
            header.setGroupName("起点");
            dataList.add(0, header);

            GuideInfo footer = new GuideInfo();
            footer.setGroupIconType(-2);
            footer.setGroupName("");
            dataList.add(dataList.size(), footer);

            NaviGuideAdapter adapter = new NaviGuideAdapter(getContext(), dataList);
            setAdapter(adapter);
        }
    }
}
