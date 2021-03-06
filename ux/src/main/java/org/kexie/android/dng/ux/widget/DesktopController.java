//package org.kexie.android.dng.ux.widget;
//
//import android.animation.Animator;
//import android.animation.ObjectAnimator;
//import android.util.ArrayMap;
//import android.util.Pair;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AnticipateOvershootInterpolator;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.blankj.utilcode.util.TimeUtils;
//import com.bumptech.glide.Glide;
//
//import org.kexie.android.dng.common.contract.Module;
//import org.kexie.android.dng.ux.R;
//import org.kexie.android.dng.ux.databinding.ItemDesktopGroupBinding;
//import org.kexie.android.dng.ux.databinding.ItemDesktopTimerBinding;
//import org.kexie.android.dng.ux.databinding.ItemDsektopNormalBinding;
//import org.kexie.android.dng.ux.viewmodel.beans.DesktopItem;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import androidx.annotation.NonNull;
//import androidx.databinding.DataBindingUtil;
//import androidx.databinding.ViewDataBinding;
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleEventObserver;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.recyclerview.widget.RecyclerView;
//import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
//
//public final class DesktopController extends AnimationAdapter {
//
//    private static final class DesktopTimerViewHolder
//            extends NeoDesktopViewHolder
//            implements LifecycleEventObserver,
//            Runnable {
//
//        private DesktopTimerViewHolder(@NonNull ViewDataBinding viewDataBinding,
//                                       int type,
//                                       Lifecycle lifecycle) {
//            super(viewDataBinding, type);
//            lifecycle.addObserver(this);
//        }
//
//        @Override
//        public void run() {
//            ItemDesktopTimerBinding binding1 = (ItemDesktopTimerBinding) this.binding;
//            binding1.setTime(TimeUtils.getNowString());
//            itemView.postDelayed(this, 1000);
//        }
//
//        @Override
//        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
//
//            switch (event) {
//                case ON_RESUME: {
//                    itemView.post(this);
//                }
//                break;
//                case ON_PAUSE: {
//                    itemView.removeCallbacks(this);
//                }
//                break;
//            }
//        }
//    }
//
//
//    private static class NeoDesktopViewHolder extends RecyclerView.ViewHolder {
//        protected final ViewDataBinding binding;
//        protected final int type;
//
//        private NeoDesktopViewHolder(@NonNull ViewDataBinding viewDataBinding, int type) {
//            super(viewDataBinding.getRoot());
//            binding = viewDataBinding;
//            this.type = type;
//        }
//    }
//
//    private static final float SCALE_FORM = 0.9f;
//    private static final int VIEW_TYPE_NORMAL = 1;
//    private static final int VIEW_TYPE_GROUP = 2;
//    private static final int VIEW_TYPE_TIMER = 3;
//
//    @SuppressWarnings("unchecked")
//    public DesktopController(Lifecycle lifecycle, Action action) {
//        super((RecyclerView.Adapter) new Inner(lifecycle, action));
//        setFirstOnly(false);
//        setInterpolator(new AnticipateOvershootInterpolator());
//        setDuration(600);
//    }
//
//    private static DesktopItem item(String name, int imageId, String path) {
//        return new DesktopItem(name, imageId, path);
//    }
//
//    private static final class Inner
//            extends RecyclerView.Adapter<NeoDesktopViewHolder>
//            implements View.OnClickListener {
//
//        private final Action action;
//        private final Lifecycle lifecycle;
//
//        private final Map<View, String> mapping = new ArrayMap<>();
//        private final View.OnClickListener onClick;
//        private final Object[] items = {
//                item("导航", R.drawable.icon_navi, Module.Navi.navigator),
//                item("时间", R.drawable.icon_time, Module.Ux.time),
//                item("收音机", R.drawable.icon_fm, Module.Ux.fm),
//                item("视频和照片", R.drawable.icon_photo, Module.Media.gallery),
//                item("APPS", R.drawable.icon_apps, Module.Ux.apps),
//                item("音乐", R.drawable.icon_music, Module.Media.music),
//                item("个人中心", R.drawable.icon_info, Module.Ux.userInfo),
//                item("天气", R.drawable.icon_weather, Module.Ux.weather),
//                item("应用商店", R.drawable.icon_store, Module.Ux.appStore),
//                item("设置", R.drawable.icon_setting, Module.Ux.setting)
//        };
//
//        private Inner(Lifecycle lifecycle, Action action) {
//            this.action = action;
//            this.lifecycle = lifecycle;
//            onClick = this;
//        }
//
//
//        @Override
//        public void onClick(View v) {
//            action.onAction(mapping.get(v));
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            Object item = items[position];
//            if (item instanceof DesktopItem) {
//                return Module.Ux.time.equals(((DesktopItem) item).path)
//                        ? VIEW_TYPE_TIMER
//                        : VIEW_TYPE_NORMAL;
//            } else {
//                return VIEW_TYPE_GROUP;
//            }
//        }
//
//        @NonNull
//        @Override
//        public NeoDesktopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            if (viewType == VIEW_TYPE_TIMER) {
//                return new DesktopTimerViewHolder(DataBindingUtil.inflate(
//                        LayoutInflater.from(parent.getContext()),
//                        R.layout.item_desktop_timer,
//                        parent, false
//                ), viewType, lifecycle);
//            }
//            return new NeoDesktopViewHolder(DataBindingUtil.inflate(
//                    LayoutInflater.from(parent.getContext()),
//                    (viewType == VIEW_TYPE_GROUP)
//                            ? R.layout.item_desktop_group : R.layout.item_dsektop_normal,
//                    parent, false
//            ), viewType);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull NeoDesktopViewHolder holder, int position) {
//            if (holder.type == VIEW_TYPE_GROUP) {
//                ItemDesktopGroupBinding binding = (ItemDesktopGroupBinding) holder.binding;
//                List<Pair<ImageView, TextView>> views = Arrays.asList(
//                        Pair.create(binding.image1, binding.text1),
//                        Pair.create(binding.image2, binding.text2),
//                        Pair.create(binding.image3, binding.text3),
//                        Pair.create(binding.image4, binding.text4));
//                DesktopItem[] subItems = (DesktopItem[]) items[position];
//                for (int i = 0; i < 4; i++) {
//                    Pair<ImageView, TextView> view = views.get(i);
//                    DesktopItem item = subItems[i];
//                    Glide.with(view.first).load(item.image).into(view.first);
//                    view.second.setText(item.name);
//                    view.first.setOnClickListener(onClick);
//                    mapping.put(view.first, item.path);
//                }
//            } else {
//                DesktopItem item = (DesktopItem) items[position];
//                ImageView imageView;
//                TextView textView;
//                if (holder.type == VIEW_TYPE_NORMAL) {
//                    ItemDsektopNormalBinding binding = (ItemDsektopNormalBinding) holder.binding;
//                    imageView = binding.image;
//                    textView = binding.text;
//                } else {
//                    ItemDesktopTimerBinding binding = (ItemDesktopTimerBinding) holder.binding;
//                    imageView = binding.image;
//                    textView = binding.text;
//                }
//                Glide.with(imageView).load(item.image).into(imageView);
//                textView.setText(item.name);
//                imageView.setOnClickListener(onClick);
//                mapping.put(imageView, item.path);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return items.length;
//        }
//    }
//
//    @Override
//    protected Animator[] getAnimators(View view) {
//        return new Animator[]{
//                ObjectAnimator.ofFloat(view, "scaleY", SCALE_FORM, 1f),
//                ObjectAnimator.ofFloat(view, "scaleX", SCALE_FORM, 1f),
//                ObjectAnimator.ofFloat(view, "translationX",
//                        view.getMeasuredWidth() / 4f, 0)
//        };
//    }
//}