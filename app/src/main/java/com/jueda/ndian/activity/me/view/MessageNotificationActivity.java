package com.jueda.ndian.activity.me.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.view.SwitchButton;

/**
 * 消息通知
 */
public class MessageNotificationActivity extends AppCompatActivity implements SwitchButton.OnOpenedListener {
    private SwitchButton voic_button;
    private SwitchButton vibration_button;
    private SwitchButton remind_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_message_notification);
        InitView();

    }

    private void InitView() {

        NdianApplication.instance.setTitle(this, getResources().getString(R.string.message_notification), true);
        voic_button=(SwitchButton) findViewById(R.id.voice_button);
        voic_button.setIsOpen(new Configuration().readaSound(MessageNotificationActivity.this));
        voic_button.setOnCheckChangedListener(this);
        vibration_button=(SwitchButton) findViewById(R.id.vibration_button);
        vibration_button.setIsOpen(new Configuration().readaVibration(MessageNotificationActivity.this));
        vibration_button.setOnCheckChangedListener(this);
        remind_button=(SwitchButton) findViewById(R.id.remind_button);
        remind_button.setIsOpen(new Configuration().readaComment(MessageNotificationActivity.this));
        remind_button.setOnCheckChangedListener(this);
    }

    @Override
    public void onChecked(View v, boolean isOpened) {
        switch (v.getId()){
            /**声音*/
            case R.id.voice_button:
                new Configuration().writeaSound(MessageNotificationActivity.this,isOpened);
                break;
            /**震动*/
            case R.id.vibration_button:
                new Configuration().writeaVibration(MessageNotificationActivity.this, isOpened);
                break;
            /**评论提醒*/
            case R.id.remind_button:
                new Configuration().writeaComment(MessageNotificationActivity.this,isOpened);
                break;
        }
    }
}
