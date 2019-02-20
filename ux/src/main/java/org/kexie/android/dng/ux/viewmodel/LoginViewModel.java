package org.kexie.android.dng.ux.viewmodel;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;

import org.kexie.android.dng.ux.model.LoginModule;
import org.kexie.android.dng.ux.model.entity.JsonPollingResult;
import org.kexie.android.dng.ux.model.entity.JsonQrcode;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginViewModel extends AndroidViewModel
{

    private final MutableLiveData<Drawable> qrcode = new MutableLiveData<>();
    private final Executor singleTask = Executors.newSingleThreadExecutor();
    private final PublishSubject<String> onErrorMessage = PublishSubject.create();
    private final PublishSubject<String> onSuccessMessage = PublishSubject.create();
    private final LoginModule loginModule;

    public LoginViewModel(@NonNull Application application)
    {
        super(application);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .baseUrl("")
                .build();
        loginModule = retrofit.create(LoginModule.class);
    }

    public MutableLiveData<Drawable> getQrcode()
    {
        return qrcode;
    }

    public void requestQrcode()
    {
        singleTask.execute(() -> {
            try
            {
                Response<JsonQrcode> response
                        = loginModule.getQrcode().execute();
                if (response.code() == 200)
                {
                    JsonQrcode qrcode = response.body();
                    Drawable resource = Glide.with(getApplication())
                            .load(qrcode.value)
                            .submit().get();
                    this.qrcode.postValue(resource);
                    polling();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    private void polling() throws Exception
    {
        int statusCode = JsonPollingResult.CODE_STATE_CONTINUE;
        while (true)
        {
            Response<JsonPollingResult> pollingResponse
                    = loginModule.polling().execute();
            if (pollingResponse.code() != 200)
            {
                return;
            }
            switch (pollingResponse.body().statusCode)
            {
                case JsonPollingResult.CODE_STATE_CONTINUE:
                {
                    continue;
                }
                case JsonPollingResult.CODE_STATE_WAIT_FOR_CONTINUE:
                {
                    if (statusCode == JsonPollingResult.CODE_STATE_CONTINUE)
                    {
                        statusCode = JsonPollingResult.CODE_STATE_WAIT_FOR_CONTINUE;
                    }
                    continue;
                }
                case JsonPollingResult.CODE_STATE_SUCCESS:
                {
                    return;
                }
                case JsonPollingResult.CODE_STATE_INVALID:
                {
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }

    public PublishSubject<String> getOnErrorMessage()
    {
        return onErrorMessage;
    }

    public PublishSubject<String> getOnSuccessMessage()
    {
        return onSuccessMessage;
    }
}
