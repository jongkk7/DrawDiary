package com.nainfox.drawview.view.load;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nainfox.drawview.R;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by yjk on 2018. 1. 12..
 */

public class TutorialActivity extends FragmentActivity {

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private CircleIndicator indicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoria);

        initViewPager();
    }

    private void initViewPager(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);

    }
}
