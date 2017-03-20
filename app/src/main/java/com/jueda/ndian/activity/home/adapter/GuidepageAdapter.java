package com.jueda.ndian.activity.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class GuidepageAdapter extends FragmentPagerAdapter {
	private  List<Fragment> views;
	public GuidepageAdapter(FragmentManager manager, List<Fragment> views) {
		super(manager);
		this.views=views;
	}

	@Override
	public Fragment getItem(int position) {
		return views.get(position);
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
	}
}
