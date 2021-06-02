package com.mx.vise.acarreos.fragments;

import android.nfc.NdefMessage;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;
import com.mx.vise.acarreos.adapters.Page;
import com.mx.vise.acarreos.adapters.SectionsPagerAdapter;
import com.mx.vise.acarreos.singleton.Singleton;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.nfc.MifareClassicCompatibilityStatus;
import com.mx.vise.nfc.NFCIdListener;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el miércoles 03 de abril del 2019 a las 13:40
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos-app-android
 */

public class TagDataPagerFragment extends Fragment {

    private static final String TAG = "VISE";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String[] mTitles;
    private Page[] mPages;
    private ViewPager mViewPager;
    private AppBarLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.appBarLayout);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), getActivity());
        mTitles = new String[]{"TEST"};

        // Set up the ViewPager with the sections adapter.
        //addInitialPages();
        mSectionsPagerAdapter.addPage(new Page(new ViewTagDataFragment(),"VER DATOS DE TAG"));
        //TODO: Habilitar esta parte en una proxima entrega
        //mSectionsPagerAdapter.addPage(new Page(new ReestablishTagFragment(),"REESTABLECER TAG"));



        mViewPager = view.findViewById(R.id.viewPager);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               if(tab.getText().equals(getString(R.string.view_tag_data))){
                   SingletonGlobal.getInstance().isViewTagDataFragment(true);
               }
               else if(tab.getText().equals(getString(R.string.reestablish_tag))){
                   SingletonGlobal.getInstance().isViewTagDataFragment(false);
               }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
