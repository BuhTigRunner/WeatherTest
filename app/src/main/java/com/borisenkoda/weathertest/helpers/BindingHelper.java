package com.borisenkoda.weathertest.helpers;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.borisenkoda.weathertest.app.App;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.HashMap;

import javax.inject.Inject;


public class BindingHelper {
    private static BindingHelper instance = new BindingHelper();
    private static HashMap<String, Typeface> hashMapFonts = new HashMap<>();
    @Inject
    App app;

    private BindingHelper() {
        App.inject(this);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("app:visibility")
    public static void setVisibilityApp(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter({"bind:imageIcon"})
    public static void loadImageIcon(ImageView view, String imageIcon) {
        String res = null;
        if (imageIcon!=null && !imageIcon.isEmpty()){
            res = "http://openweathermap.org/img/w/".concat(imageIcon).concat(".png");
        }
        loadImage(view, res);
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        loadImage(view, url, null);
    }

    @BindingAdapter({"bind:imageUrl", "bind:placeholder"})
    public static void loadImage(ImageView view, String url, Drawable placeholder) {
        if (url != null && url.equals("")) url = null;
        Picasso.with(instance.app.getApplicationContext()).load(url).placeholder(placeholder).fit().centerCrop().into(view);
    }

}