package kexie.android.dng.viewmodel.desktop;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kexie.android.dng.R;
import kexie.android.dng.entity.desktop.Function;
import kexie.android.dng.entity.desktop.User;
import kexie.android.common.util.ZoomTransformation;
import kexie.android.dng.view.users.UsersActivity;
import kexie.android.navi.view.RouteQueryActivity;
import okhttp3.OkHttpClient;

public class DesktopViewModel
        extends AndroidViewModel implements LifecycleObserver
{

    private static class FunctionLoadTask
    {
        private FutureTarget<Drawable> innerTask;
        private Function function;

        private Function get() throws Exception
        {
            innerTask.get();
            return function;
        }
    }

    private final MutableLiveData<Map<String,View.OnClickListener>> simpleFunctions
            = new MutableLiveData<>();
    private final MutableLiveData<User> userInfo = new MutableLiveData<>();
    private final MutableLiveData<String> time = new MutableLiveData<>();
    private final MutableLiveData<List<Function>> listFunctions = new MutableLiveData<>();
    private Timer updateTimer;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    public DesktopViewModel(Application application)
    {
        super(application);
        initFunctions();
        initDefaultUserInfo();
    }

    private void initDefaultUserInfo()
    {
        Glide.with(getApplication())
                .load(R.mipmap.image_head_man)
                .listener(new RequestListener<Drawable>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource)
                    {
                        new BitmapDrawable(BitmapFactory
                                .decodeResource(getApplication().getResources(),
                                        R.mipmap.image_head_man));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource)
                    {
                        User user = new User(resource,
                                "未登陆");
                        userInfo.setValue(user);
                        return true;
                    }
                }).submit();
    }

    private void startTimer()
    {
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                time.postValue(TimeUtils.getNowString());
            }
        }, 0, 1000);
    }

    private void endTimer()
    {
        updateTimer.cancel();
    }

    private FunctionLoadTask loadFunction(final String name,
                                          int mipmap,
                                          final View.OnClickListener action)
    {
        final FunctionLoadTask loadTask = new FunctionLoadTask();
        loadTask.innerTask = Glide.with(getApplication())
                .load(mipmap)
                .apply(RequestOptions.bitmapTransform(new ZoomTransformation(250)))
                .listener(new RequestListener<Drawable>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource)
                    {
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource)
                    {
                        loadTask.function = new Function.Builder()
                                .action(action)
                                .icon(resource)
                                .name(name)
                                .build();
                        return true;
                    }
                }).submit();
        return loadTask;
    }

    private void initFunctions()
    {
        Observable.just(getApplication())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Application>()
                {
                    @Override
                    public void accept(Application application) throws Exception
                    {
                        List<FunctionLoadTask> targets = new LinkedList<FunctionLoadTask>()
                        {
                            {
                                add(loadFunction("天气",
                                        R.mipmap.image_weather,
                                        new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {

                                            }
                                        }));
                                add(loadFunction("多媒体",
                                        R.mipmap.image_media,
                                        new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {

                                            }
                                        }));
                                add(loadFunction("APPS",
                                        R.mipmap.image_apps,
                                        new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {

                                            }
                                        }));
                            }
                        };
                        List<Function> functions = new ArrayList<>(targets.size());
                        for (FunctionLoadTask task : targets)
                        {
                            functions.add(task.get());
                        }
                        DesktopViewModel.this.listFunctions.postValue(functions);
                    }
                });
        simpleFunctions.setValue(new HashMap<String, View.OnClickListener>()
        {
            {
                put("个人信息", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        UsersActivity.startOf(v.getContext());
                    }
                });
                put("导航", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        RouteQueryActivity.startOf(v.getContext());
                    }
                });
            }
        });
    }

    public MutableLiveData<Map<String, View.OnClickListener>> getSimpleFunctions()
    {
        return simpleFunctions;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause()
    {
        endTimer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume()
    {
        startTimer();
    }

    public MutableLiveData<List<Function>> getListFunctions()
    {
        return listFunctions;
    }

    public MutableLiveData<String> getTime()
    {
        return time;
    }

    public MutableLiveData<User> getUserInfo()
    {
        return userInfo;
    }

    @Override
    protected void onCleared()
    {
    }
}