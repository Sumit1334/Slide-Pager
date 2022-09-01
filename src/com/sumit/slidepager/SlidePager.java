package com.sumit.slidepager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.HVArrangement;
import com.google.appinventor.components.runtime.util.YailList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SlidePager extends AndroidNonvisibleComponent implements Component {
    private final Activity activity;
    private final String TAG = "PageSlider";
    private final ArrayList<AndroidViewComponent> views = new ArrayList<>();

    private final PageSlider slider;
    private MyAdapter adapter;

    private int duration;

    public SlidePager(ComponentContainer container) {
        super(container.$form());
        this.activity = container.$context();
        this.slider = new PageSlider(activity);
        this.slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                PageChanged(i + 1);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        Log.i(TAG, "Extension Initialized");
    }

    @SimpleEvent(description = "This event raises when page is changed")
    public void PageChanged(int position) {
        EventDispatcher.dispatchEvent(this, "PageChanged", position);
    }

    @SimpleFunction(description = "Initialize the pager into given layout")
    public void Initialize(HVArrangement container, YailList views) {
        Log.i(TAG, "Initialize: I am adding the slider in " + container);
        this.slider.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        ((LinearLayout) ((ViewGroup) container.getView()).getChildAt(0)).addView(slider);
        this.adapter = new MyAdapter();
        for (Object object : views.toArray()){
            AndroidViewComponent component=(AndroidViewComponent) object;
            this.views.add(component);
            component.Visible(false);
        }
        slider.setAdapter(adapter);
        slider.setCurrentItem(0);
    }

    @SimpleFunction(description = "Add an item view into the pager")
    public void AddView(AndroidViewComponent view) {
        views.add(view);
        adapter.notifyDataSetChanged();
    }

    @SimpleFunction(description = "Add an item view at given position")
    public void AddViewAt(int position, AndroidViewComponent view) {
        views.add(position - 1, view);
        adapter.notifyDataSetChanged();
    }

    @SimpleFunction(description = "Removes an item view from given position")
    public void RemoveView(int position) {
        views.remove(position - 1);
        adapter.notifyDataSetChanged();
    }

    @SimpleFunction(description = "Removes all the pages from the pager")
    public void ClearAll() {
        views.clear();
        this.adapter = new MyAdapter();
        slider.setAdapter(adapter);
    }

    @SimpleFunction(description = "Set the current page to given position")
    public void SetCurrentPage(int position) {
        slider.setCurrentItem(position - 1, true);
    }

    @SimpleFunction(description = "Returns the position of current page")
    public int CurrentPage() {
        return slider.getCurrentItem() + 1;
    }

    @SimpleProperty(description = "Set the animation duration of the pager")
    @DesignerProperty(defaultValue = "500", editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER)
    public void AnimationDuration(int duration) {
        this.duration = duration;
    }

    private class PageSlider extends ViewPager {

        private PageSlider(Context context) {
            super(context);
            setMyScroller();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        private void setMyScroller() {
            try {
                Class<?> viewpager = ViewPager.class;
                Field scroller = viewpager.getDeclaredField("mScroller");
                scroller.setAccessible(true);
                scroller.set(this, new MyScroller(activity));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class MyScroller extends Scroller {
            public MyScroller(Context context) {
                super(context, new DecelerateInterpolator());
            }

            @Override
            public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                super.startScroll(startX, startY, dx, dy, SlidePager.this.duration);
            }
        }
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return SlidePager.this.views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup viewGroup, int n) {
            AndroidViewComponent component = SlidePager.this.views.get(n);
            component.Visible(true);
            View view = component.getView();
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            viewGroup.addView(view);
            return view;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            int n = SlidePager.this.views.indexOf(object);
            if (n == -1) {
                return -2;
            }
            return n;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int n, Object object) {
            viewGroup.removeView(SlidePager.this.views.get(n).getView());
        }
    }
}