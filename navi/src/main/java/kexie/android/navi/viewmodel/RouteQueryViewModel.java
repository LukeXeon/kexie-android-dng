package kexie.android.navi.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.RouteSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kexie.android.navi.entity.Point;
import kexie.android.navi.entity.Query;
import kexie.android.navi.entity.Route;
import kexie.android.navi.util.Points;

public class RouteQueryViewModel extends AndroidViewModel
{
    private static final String CITY = "西安";
    private final RouteSearch routeSearch;
    private final Executor singleTask = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<Route>> routes = new MutableLiveData<>();
    private final MutableLiveData<List<Tip>> tips = new MutableLiveData<>();

    public RouteQueryViewModel(@NonNull Application application)
    {
        super(application);
        routeSearch = new RouteSearch(application);
    }

    public MutableLiveData<List<Tip>> getTips()
    {
        return tips;
    }

    public MutableLiveData<List<Route>> getRoutes()
    {
        return routes;
    }

    public void textQuery(final String text)
    {
        singleTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                final InputtipsQuery inputtipsQuery
                        = new InputtipsQuery(text.toString(), CITY);
                Inputtips inputtips = new Inputtips(getApplication(), inputtipsQuery);
                try
                {
                    List<Tip> rawResult = inputtips.requestInputtips();
                    List<Tip> result = new ArrayList<>();
                    for (Tip tip : rawResult)
                    {
                        if (!TextUtils.isEmpty(tip.getPoiID()))
                        {
                            result.add(tip);
                        }
                    }
                    tips.postValue(result);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    tips.postValue(null);
                }
            }
        });
    }

    private void routeQuery(final Query query)
    {
        List<List<LatLonPoint>> lists = new ArrayList<>();
        if (query.avoids != null)
        {
            for (List<Point> points : query.avoids)
            {
                lists.add(Points.toLatLatLonPoints(points));
            }
        }
        final RouteSearch.DriveRouteQuery driveRouteQuery
                = new RouteSearch.DriveRouteQuery(
                new RouteSearch.FromAndTo(query.from.toLatLonPoint(),
                        query.to.toLatLonPoint()),
                query.mode,
                Points.toLatLatLonPoints(query.ways),
                lists, "");
        singleTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final List<Route> routes = new ArrayList<>();
                    for (DrivePath path : routeSearch
                            .calculateDriveRoute(driveRouteQuery)
                            .getPaths())
                    {
                        routes.add(new Route.Builder()
                                .from(query.from)
                                .to(query.to)
                                .path(path)
                                .build());
                    }
                    RouteQueryViewModel.this.routes.setValue(routes);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    RouteQueryViewModel.this.routes.setValue(null);
                }
            }
        });
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();
    }
}