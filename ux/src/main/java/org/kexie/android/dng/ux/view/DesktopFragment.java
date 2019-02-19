package org.kexie.android.dng.ux.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kexie.android.dng.ux.R;
import org.kexie.android.dng.ux.databinding.FragmentDesktopBinding;
import org.kexie.android.dng.ux.viewmodel.DesktopViewModel;
import org.kexie.android.dng.ux.viewmodel.InfoViewModel;
import org.kexie.android.dng.ux.viewmodel.entity.Function;
import org.kexie.android.dng.ux.viewmodel.entity.SimpleUserInfo;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProviders;
import es.dmoral.toasty.Toasty;
import mapper.Mapper;
import mapper.Mapping;
import mapper.Request;

import static com.uber.autodispose.AutoDispose.autoDisposable;
import static com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from;

@Mapping("dng/ux/main")
public class DesktopFragment extends Fragment
{
    private FragmentDesktopBinding binding;
    private DesktopViewModel viewModel;
    private InfoViewModel infoViewModel;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_desktop, container,
                false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this)
                .get(DesktopViewModel.class);
        infoViewModel = ViewModelProviders.of(this).get(InfoViewModel.class);
        getLifecycle().addObserver(viewModel);
        //dataBinding
        binding.setOnItemClick((adapter, view1, position)
                -> viewModel.requestJumpBy((Function) adapter.getData().get(position)));
        binding.setActions(getActions());
        //liveData
        Transformations.map(infoViewModel.getUser(),
                input -> new SimpleUserInfo(input.headImage, input.username, input.carNumber))
                .observe(this, binding::setUser);
        viewModel.getTime().observe(this, binding::setTime);
        viewModel.getFunction()
                .observe(this,binding::setFunctions);
        viewModel.loadDefaultFunction();
        //rx
        viewModel.getOnErrorMessage()
                .as(autoDisposable(from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(s -> Toasty.error(getContext(), s).show());
        viewModel.getOnSuccessMessage()
                .as(autoDisposable(from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(s -> Toasty.success(getContext(), s).show());
        viewModel.getOnJumpTo()
                .as(autoDisposable(from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(this::jumpTo);
    }

    private void jumpTo(Request request)
    {
        getFragmentManager()
                .beginTransaction()
                .add(getId(), Mapper.getOn(this, request))
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public Map<String, View.OnClickListener> getActions()
    {
        return new ArrayMap<String, View.OnClickListener>()
        {
            {
                put("个人信息", v -> jumpTo(new Request.Builder().uri("dng/ux/info").build()));
                put("导航", v -> jumpTo(new Request.Builder().uri("dng/navi/query").build()));
            }
        };
    }
}
