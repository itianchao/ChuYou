package com.jueda.ndian.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import java.util.ArrayList;

public class personActivity extends FragmentActivity {
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;//界面集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        InitView();
    }

    private void InitView() {
        viewPager=(ViewPager)findViewById(R.id.id_stickynavlayout_viewpager);
        fragmentList = new ArrayList<>();
        FragmentManager manager = this.getSupportFragmentManager();
        GuidepageAdapter adapter = new GuidepageAdapter(manager, fragmentList);
        viewPager.setAdapter(adapter);
    }
}
