package com.jueda.ndian.activity.me.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.Share;

/**
 * 邀请好友
 */
public class InviteFriendsActivity extends AppCompatActivity {
    public static final String TAG=InviteFriendsActivity.class.getName();
    private RelativeLayout relativeLayout;//弹出相对位置
    private TextView code;//邀请码
    private TextView invitation;//邀请人
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_invite_friends);
        InitView();
    }


    private void InitView() {
        NdianApplication.instance.setTitle(this,getResources().getString(R.string.Invite_friends),true);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        code=(TextView)findViewById(R.id.code);
        invitation=(TextView)findViewById(R.id.invitation);

        code.setText(ConstantsUser.userEntity.getInviteCode());

        invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new Share(InviteFriendsActivity.this, relativeLayout, TAG, ConstantsUser.userEntity.getInviteCode(),"");
            }
        });

    }
}
