package com.jueda.ndian.savedata.rong;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.circle.view.CircleMemberActivity;
import com.jueda.ndian.utils.ChangeTitle;

/**
 * Created by Bob on 2015/4/16.
 */
public class ConversationActivity extends ActionBarActivity {

    private static final String TAG = ConversationActivity.class.getSimpleName();
    private ImageView member;//成员
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        //唯一主要的代码，加载一个 layout
        setContentView(R.layout.conversation);
        //继承的是ActionBarActivity，直接调用 自带的 Actionbar，下面是Actionbar 的配置，如果不用可忽略...
        NdianApplication.instance.setTitle(this,getIntent().getData().getQueryParameter("title"),true);
        member=(ImageView)findViewById(R.id.member);
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ConversationActivity.this,CircleMemberActivity.class);
                intent.putExtra("entity", CircleContentActivity.instance.topicList.get(0).getCircleList().get(0));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
