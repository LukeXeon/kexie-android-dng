package org.kexie.android.dng.navi.viewmodel;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.bumptech.glide.Glide;

import org.kexie.android.dng.navi.R;
import org.kexie.android.dng.navi.model.BoxRoute;
import org.kexie.android.dng.navi.model.Point;
import org.kexie.android.dng.navi.model.Query;
import org.kexie.android.dng.navi.model.Route;
import org.kexie.android.dng.navi.viewmodel.entity.LiteRoute;
import org.kexie.android.dng.navi.viewmodel.entity.LiteStep;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.collection.ArrayMap;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class QueryViewModel extends AndroidViewModel
{
    private static final String DEBUG_TEXT = "火车站";
    private static final String CITY = "西安";
    private final RouteSearch routeSearch;
    private final Executor singleTask = Executors.newSingleThreadExecutor();
    private final MutableLiveData<String> queryText = new MutableLiveData<>();
    private final MutableLiveData<List<String>> tipTexts = new MutableLiveData<>();
    private final MutableLiveData<List<LiteRoute>> liteRoutes = new MutableLiveData<>();
    private final Map<LiteRoute, Route> routeMapping = new ArrayMap<>();
    private final IdentityHashMap<String, String> tipPoiIds = new IdentityHashMap<>();
    private final PublishSubject<String> onErrorMessage = PublishSubject.create();
    private final PublishSubject<String> onSuccessMessage = PublishSubject.create();
    private AMap mapController;

    public QueryViewModel(@NonNull Application application)
    {
        super(application);
        routeSearch = new RouteSearch(application);
    }

    public void initMapController(AMap aMap)
    {
        mapController = aMap;
    }

    @MainThread
    public void tipQueryBy(String text)
    {
        singleTask.execute(() -> {
            InputtipsQuery inputtipsQuery = new InputtipsQuery(text, CITY);
            Inputtips inputtips = new Inputtips(getApplication(), inputtipsQuery);
            try
            {
                Map<String, String> rawResult = StreamSupport
                        .stream(inputtips.requestInputtips())
                        .filter(tip -> !TextUtils.isEmpty(tip.getPoiID()))
                        .collect(Collectors.toMap(Tip::getName, Tip::getPoiID));
                tipPoiIds.clear();
                tipPoiIds.putAll(rawResult);
                tipTexts.postValue(new ArrayList<>(rawResult.keySet()));
                //loading.postValue(null);
            } catch (Exception e)
            {
                e.printStackTrace();
                onErrorMessage.onNext("输入提示查询失败,请检查网络连接");
                //loading.postValue(null);
            }
        });
    }

    @MainThread
    public void routeQueryBy(String tip)
    {
        String poiId = tipPoiIds.get(tip);
        if (poiId != null)
        {
            singleTask.execute(() -> {
                Point point = tipToPoint(tip, poiId);
                Query query = new Query.Builder().to(point).build();
                routeQuery(query);
            });
        }
    }

    @WorkerThread
    private void routeQuery(Query query)
    {
        List<List<LatLonPoint>> avoids = query.avoids == null
                ? Collections.emptyList()
                : StreamSupport.stream(query.avoids)
                .map(points -> StreamSupport.stream(points)
                        .map(point -> point.unBox(LatLonPoint.class))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<LatLonPoint> ways = query.ways == null
                ? Collections.emptyList()
                : StreamSupport.stream(query.ways)
                .map(point -> point.unBox(LatLonPoint.class))
                .collect(Collectors.toList());

        RouteSearch.DriveRouteQuery driveRouteQuery
                = new RouteSearch.DriveRouteQuery(
                new RouteSearch.FromAndTo(
                        query.from.unBox(LatLonPoint.class),
                        query.to.unBox(LatLonPoint.class)),
                query.mode,
                ways,
                avoids, "");

        try
        {
            Map<LiteRoute, Route> newMapping = StreamSupport.stream(routeSearch
                    .calculateDriveRoute(driveRouteQuery)
                    .getPaths())
                    .collect(Collectors.toMap(
                            path -> new LiteRoute.Builder()
                                    .name(getPathName(path))
                                    .length(getPathLength(path))
                                    .time(getPathTime(path))
                                    .steps(getPathStep(path))
                                    .build(),
                            path -> new BoxRoute(query.from, query.to, path)));

            routeMapping.clear();
            routeMapping.putAll(newMapping);
            liteRoutes.postValue(new ArrayList<>(routeMapping.keySet()));
        } catch (Exception e)
        {
            e.printStackTrace();
            onErrorMessage.onNext("路径规划失败,请检查网络连接");
            liteRoutes.postValue(null);
        }
    }

    @MainThread
    private Point tipToPoint(String tip, String poiId)
    {
        PoiSearch.Query query = new PoiSearch.Query(tip, "");
        query.setDistanceSort(false);
        query.requireSubPois(true);
        PoiSearch poiSearch = new PoiSearch(getApplication(), query);
        try
        {
            PoiItem item = poiSearch.searchPOIId(poiId);
            LatLonPoint latLonPoint
                    = ((latLonPoint = item.getEnter()) != null)
                    ? (latLonPoint)
                    : ((latLonPoint = item.getExit()) != null
                    ? latLonPoint
                    : item.getLatLonPoint());
            return Point.box(latLonPoint);
        } catch (AMapException e)
        {
            onErrorMessage.onNext("目标地点信息读取失败,请检查网络连接");
            e.printStackTrace();
            return null;
        }
    }

    private List<LiteStep> getPathStep(DrivePath path)
    {
        return StreamSupport.stream(path.getSteps())
                .map(DriveStep::getAction)
                .map(a -> {
                    Drawable drawable = null;
                    try
                    {
                        drawable = Glide.with(getApplication())
                                .load(getActionRes(a))
                                .submit()
                                .get();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return new LiteStep(a, drawable);
                }).collect(Collectors.toList());
    }

    private static int getActionRes(String actionName)
    {
        if (actionName == null || actionName.equals(""))
        {
            return R.mipmap.dir3;
        }
        if ("左转".equals(actionName))
        {
            return R.mipmap.dir2;
        }
        if ("右转".equals(actionName))
        {
            return R.mipmap.dir1;
        }
        if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName))
        {
            return R.mipmap.dir6;
        }
        if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName))
        {
            return R.mipmap.dir5;
        }
        if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName))
        {
            return R.mipmap.dir7;
        }
        if ("向右后方行驶".equals(actionName))
        {
            return R.mipmap.dir8;
        }
        if ("直行".equals(actionName))
        {
            return R.mipmap.dir3;
        }
        if ("减速行驶".equals(actionName))
        {
            return R.mipmap.dir4;
        }
        return R.mipmap.dir3;
    }

    private static String getPathTime(DrivePath path)
    {
        long second = path.getDuration();
        if (second > 3600)
        {
            long hour = second / 3600;
            long miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60)
        {
            long miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }

    @SuppressWarnings("All")
    private static String getPathLength(DrivePath path)
    {
        int lenMeter = (int) path.getDistance();
        if (lenMeter > 10000) // 10 km
        {
            float dis = lenMeter / 1000;
            return dis + "千米";
        }
        if (lenMeter > 1000)
        {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + "千米";
        }
        if (lenMeter > 100)
        {
            float dis = lenMeter / 50 * 50;
            return dis + "米";
        }
        float dis = lenMeter / 10 * 10;
        if (dis == 0)
        {
            dis = 10;
        }
        return dis + "米";
    }

    private static String getPathName(DrivePath path)
    {
        return path.getStrategy();
    }

    public Observable<String> getOnSuccessMessage()
    {
        return onSuccessMessage;
    }

    public Observable<String> getOnErrorMessage()
    {
        return onErrorMessage;
    }
}