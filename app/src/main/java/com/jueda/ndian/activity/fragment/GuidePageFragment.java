package com.jueda.ndian.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.utils.ImageLoaderUtil;


/**
 * Created by Administrator on 2016/3/3.
 * 导航栏
 */
public class GuidePageFragment extends Fragment{
    private View view;
    private ImageView imageview;
    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.guide_page_fragment,null);
        InitView();
        return view;
    }

    private void InitView() {
        imageview=(ImageView)view.findViewById(R.id.imageView);
        textView=(TextView)view.findViewById(R.id.textView);

         int image= getArguments().getInt("image");
        boolean last=getArguments().getBoolean("last");
//        ImageLoaderUtil.ImageLoaderHome("drawable://" + image, imageview, image);
        imageview.setImageResource(image);

        if(last){
            textView.setVisibility(View.VISIBLE);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
//                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent1);

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
