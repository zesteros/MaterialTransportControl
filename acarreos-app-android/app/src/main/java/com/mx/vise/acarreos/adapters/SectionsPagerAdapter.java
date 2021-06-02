package com.mx.vise.acarreos.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private ArrayList mPages;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        this.mFragmentManager = fm;
        mPages = new ArrayList<Page>();
    }

    @Override
    public Fragment getItem(int position) {
        return ((Page) mPages.get(position)).getPage();
    }

    public void replacePage(int position, Page page) {
        mPages.remove(position);
        mPages.add(position, page);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    public void addPage(Page page) {
        if (!mPages.contains(page)) mPages.add(page);
        notifyDataSetChanged();
    }

    public void removePage(Page page) {
        mPages.remove(page);
        notifyDataSetChanged();
    }

    public void removePage(int position) {
        mPages.remove(position);
        notifyDataSetChanged();
    }


    public Page getPage(int position) {
        return (Page) mPages.get(position);
    }

    public boolean containsPage(Page page) {
        return mPages.contains(page);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((Page) mPages.get(position)).getTitle();
    }
}