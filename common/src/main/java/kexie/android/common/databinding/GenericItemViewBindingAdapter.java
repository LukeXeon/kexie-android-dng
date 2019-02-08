package kexie.android.common.databinding;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import kexie.android.common.R;

public final class GenericItemViewBindingAdapter
{
    private GenericItemViewBindingAdapter()
    {
        throw new AssertionError();
    }

    private interface PagerView
    {
        PagerAdapter getAdapter();

        void setAdapter(PagerAdapter adapter);

        void post(Runnable action);

        Object getTag(@IdRes int id);
    }

    private static final class RecyclerViewPostItem
            implements Runnable
    {
        private final WeakReference<RecyclerView> reference;
        private final List<?> items;

        private RecyclerViewPostItem(RecyclerView reference,
                                     List<?> items)
        {
            this.reference = new WeakReference<>(reference);
            this.items = items;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run()
        {
            RecyclerView recyclerView = reference.get();
            if (recyclerView != null)
            {
                RecyclerView.Adapter adapter
                        = recyclerView.getAdapter();
                if (adapter == null)
                {
                    recyclerView.post(this);
                } else
                {
                    if (adapter instanceof GenericItemRecyclerAdapter)
                    {
                        ((GenericItemRecyclerAdapter) adapter).setNewData(items);
                    }
                }
            }
        }
    }

    private static final class PagerViewPostItem
            implements Runnable
    {
        private final WeakReference<PagerView> reference;
        private final List<?> items;

        private PagerViewPostItem(PagerView reference, List<?> items)
        {
            this.reference = new WeakReference<>(reference);
            this.items = items;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run()
        {
            PagerView pagerView = reference.get();
            if (pagerView != null)
            {
                PagerAdapter adapter = pagerView.getAdapter();
                if (adapter == null)
                {
                    pagerView.post(this);
                } else
                {
                    if (adapter instanceof GenericItemPagerAdapter)
                    {
                        ((GenericItemPagerAdapter) adapter).setNewData(items);
                    }
                }
            }
        }
    }

    @BindingAdapter({"onAdapterCreated"})
    public static void setOnCreatedListener(RecyclerView view,
                                            GenericItemRecyclerAdapter
                                                    .OnCreatedListener listener)
    {
        view.setTag(R.id.on_adapter_created, listener);
    }

    @BindingAdapter({"onAdapterCreated"})
    public static void setOnCreatedListener(ViewPager view,
                                            GenericItemPagerAdapter
                                                    .OnCreatedListener listener)
    {
        view.setTag(R.id.on_adapter_created, listener);
    }

    @BindingAdapter({"item_name", "item_layout"})
    public static void setAdapter(RecyclerView view,
                                  String itemName,
                                  @LayoutRes int itemLayout)
    {
        RecyclerView.Adapter adapter = view.getAdapter();
        if (!(adapter instanceof GenericItemRecyclerAdapter))
        {
            GenericItemRecyclerAdapter<?> genericItemRecyclerAdapter
                    = new GenericItemRecyclerAdapter<>(itemName, itemLayout);
            view.setAdapter(genericItemRecyclerAdapter);
            Object listener = view.getTag(R.id.on_adapter_created);
            if (listener instanceof GenericItemRecyclerAdapter.OnCreatedListener)
            {
                ((GenericItemRecyclerAdapter.OnCreatedListener) listener)
                        .onCreated(genericItemRecyclerAdapter);
            }
        }
    }

    @BindingAdapter({"item_name", "item_layout"})
    public static void setAdapter(ViewPager view,
                                  String itemName,
                                  @LayoutRes int itemLayout)
    {
        setAdapter(getPagerView(view), itemName, itemLayout);
    }

    @BindingAdapter({"item_name", "item_layout"})
    public static void setAdapter(VerticalViewPager view,
                                  String itemName,
                                  @LayoutRes int itemLayout)
    {
        setAdapter(getPagerView(view), itemName, itemLayout);
    }

    @BindingAdapter({"item_source"})
    public static void setItems(ViewPager view,
                                List<?> dataSource)
    {
        setItems(getPagerView(view), dataSource);
    }

    @BindingAdapter({"item_source"})
    public static void setItems(RecyclerView view,
                                List<?> dataSource)
    {
        new RecyclerViewPostItem(view, dataSource).run();
    }

    @BindingAdapter({"item_source"})
    public static void setItems(VerticalViewPager view,
                                List<?> dataSource)
    {
        setItems(getPagerView(view), dataSource);
    }

    private static void setAdapter(PagerView view,
                                   String itemName,
                                   @LayoutRes int itemLayout)
    {
        PagerAdapter adapter = view.getAdapter();
        if (!(adapter instanceof GenericItemPagerAdapter))
        {
            GenericItemPagerAdapter genericItemPagerAdapter
                    = new GenericItemPagerAdapter<>(itemName, itemLayout);
            view.setAdapter(genericItemPagerAdapter);
            Object o = view.getTag(R.id.on_adapter_created);
            if (o instanceof GenericItemPagerAdapter.OnCreatedListener)
            {
                ((GenericItemPagerAdapter.OnCreatedListener) o)
                        .onCreated(genericItemPagerAdapter);
            }
        }
    }

    private static void setItems(PagerView view,
                                 List<?> dataSource)
    {
        new PagerViewPostItem(view, dataSource).run();
    }

    private static PagerView getPagerView(ViewPager view)
    {
        PagerView pagerView = (PagerView) view.getTag(R.id.pager_view);
        if (pagerView == null)
        {
            pagerView = new PagerView()
            {
                @Override
                public void setAdapter(PagerAdapter adapter)
                {
                    view.setAdapter(adapter);
                }

                @SuppressWarnings("unchecked")
                @Override
                public PagerAdapter getAdapter()
                {
                    return view.getAdapter();
                }

                @Override
                public void post(Runnable action)
                {
                    view.post(action);
                }

                @Override
                public Object getTag(int id)
                {
                    return view.getTag(id);
                }
            };
            view.setTag(R.id.pager_view, pagerView);
        }
        return pagerView;
    }

    private static PagerView getPagerView(VerticalViewPager view)
    {
        PagerView pagerView = (PagerView) view.getTag(R.id.pager_view);
        if (pagerView == null)
        {
            pagerView = new PagerView()
            {
                @Override
                public void setAdapter(PagerAdapter adapter)
                {
                    view.setAdapter(adapter);
                }

                @SuppressWarnings("unchecked")
                @Override
                public PagerAdapter getAdapter()
                {
                    return view.getAdapter();
                }

                @Override
                public void post(Runnable action)
                {
                    view.post(action);
                }

                @Override
                public Object getTag(int id)
                {
                    return view.getTag(id);
                }
            };
            view.setTag(R.id.pager_view, pagerView);
        }
        return pagerView;
    }
}
