package org.kexie.android.dng.media.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.kexie.android.danmakux.utils.FileUtils;
import org.kexie.android.dng.media.model.beans.Graph;
import org.kexie.android.dng.media.widget.VideoPlayerActivityHolder;
import org.kexie.android.dng.player.vedio.IjkPlayerView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VideoPlayerFragment
        extends Fragment {

    private IjkPlayerView player;

    private boolean isFormWindow = false;

    private FrameLayout playerContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        playerContainer = new FrameLayout(inflater.getContext());
        if (!isFormWindow) {
            player = new IjkPlayerView(inflater.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            player.setLayoutParams(params);
        }
        playerContainer.addView(player);
        return playerContainer;
    }

    public static Fragment formWindow(IjkPlayerView player) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        fragment.isFormWindow = true;
        fragment.player = player;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isFormWindow) {
            Bundle bundle = requireArguments();
            Graph info = bundle.getParcelable("media");
            if (info != null && info.data != null) {
                Glide.with(this)
                        .load(info.data)
                        .apply(RequestOptions.fitCenterTransform())
                        .into(player.mPlayerThumb);

                // set title
                player.init().setTitle(info.displayName)
                        .enableDanmaku()
                        .setVideoPath(info.data)
                        .alwaysFullScreen();
                List<File> files = FileUtils.getAttachedSubtitles(info.data);
                if (!files.isEmpty()) {
                    File file = files.get(0);
                    player.setDanmakuSource(file);
                }
                player.start();
            }
        }
        player.setOnClickBackListener(requireActivity()::onBackPressed);
        player.setFloatClickListener(v -> transformToWindow());
    }

    private void transformToWindow() {
        playerContainer.removeView(player);
        player.setFloatClickListener(null);
        VideoPlayerActivityHolder holderActivity
                = (VideoPlayerActivityHolder) requireActivity();
        holderActivity.holdByWindow(player);
        player = null;
        holderActivity.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        playerContainer = null;
        if (player != null) {
            player.onDestroy();
            player = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }
}