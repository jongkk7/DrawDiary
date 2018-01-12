package com.nainfox.drawview.view.load;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nainfox.drawview.R;
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
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.tutorial_textview);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.tutorial_imageview);
        Button button = (Button) rootView.findViewById(R.id.tutorial_start_button);

        switch (pageNumber){
            case 0:
                textView.setText(R.string.tutorial01);
                imageView.setBackgroundResource(R.drawable.tutorial01);
                break;
            case 1:
                textView.setText(R.string.tutorial02);
                imageView.setBackgroundResource(R.drawable.tutorial02);
                break;
            case 2:
                textView.setText(R.string.tutorial01);
                imageView.setBackgroundResource(R.drawable.tutorial01);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                });
                break;

        }
    }
}
