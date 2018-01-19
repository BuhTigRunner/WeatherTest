package com.borisenkoda.weathertest.helpers;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.app.App;
import com.borisenkoda.weathertest.databinding.ItemDayForecastBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


public class BindingHelper {
    private static BindingHelper instance = new BindingHelper();
    private static HashMap<String, Typeface> hashMapFonts = new HashMap<>();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM");
    @Inject
    App app;

    private BindingHelper() {
        App.inject(this);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibility")
    public static void setVisibilityApp(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter({"imageIcon"})
    public static void loadImageIcon(ImageView view, String imageIcon) {
        String res = null;
        if (imageIcon != null && !imageIcon.isEmpty()) {
            res = "http://openweathermap.org/img/w/".concat(imageIcon).concat(".png");
        }
        loadImage(view, res);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        loadImage(view, url, null);
    }

    @BindingAdapter({"imageUrl", "placeholder"})
    public static void loadImage(ImageView view, String url, Drawable placeholder) {
        if (url != null && url.equals("")) url = null;
        Picasso.with(instance.app.getApplicationContext()).load(url).placeholder(placeholder).fit().centerCrop().into(view);
    }

    @BindingAdapter("list")
    public static void inflateLinearLayout(LinearLayout linearLayout, List listObjects) {
        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }
        if (listObjects == null) return;

        for (int i = 0; i < listObjects.size(); i++) {
            ItemDayForecastBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(linearLayout.getContext()), R.layout.item_day_forecast, linearLayout, false);
            com.borisenkoda.weathertest.net.List list = (com.borisenkoda.weathertest.net.List) listObjects.get(i);
            itemBinding.setList(list);
            itemBinding.tvTitle.setText(getDateAfterNow(i+1)+" "+list.weather.get(0).description);
            linearLayout.addView(itemBinding.getRoot());
        }
    }

    private static String getDateAfterNow(int i) {
        Calendar calendar = Calendar.getInstance();
        Date res = new Date(calendar.getTime().getTime()+86400000*i);
        return simpleDateFormat.format(res);
    }

}