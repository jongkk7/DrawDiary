package com.nainfox.drawview.view.load;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nainfox.drawview.R;
import com.nainfox.drawview.util.SharedData;
import com.nainfox.drawview.view.main.MainActivity;

/**
 * Created by yjk on 2018. 1. 12..
 */

public class TutorialFragment extends Fragment {
    private final String TAG = "### TutorialFragment ";
    private static final String PAGEKEY = "page";

    private ViewGroup rootView;
    private int pageNumber;

    public static TutorialFragment create(int pageNumber){
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(PAGEKEY, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(PAGEKEY);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        init(inflater, container, savedInstanceState);

        return rootView;
    }

    private void init(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        switch (pageNumber){
            case 0:
                rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial01, container, false);
                break;
            case 1:
                rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial02, container, false);
                break;
            case 2:
                rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial03, container, false);
                break;
            case 3:
                rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial04, container, false);

                final Button button = (Button) rootView.findViewById(R.id.tutorial_start_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                });
                button.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            button.setBackgroundResource(R.drawable.tutorial_start_button);
                        }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                            button.setBackgroundResource(R.drawable.tutorial_start_button_down);
                        }
                        new SharedData(getActivity()).setIsFirst(false);
                        return false;
                    }
                });
                break;

        }
    }
}
