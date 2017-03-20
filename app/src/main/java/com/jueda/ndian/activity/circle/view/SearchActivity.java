package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.RecommendAdapter;
import com.jueda.ndian.activity.circle.biz.SearchCircleBiz;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 搜索
 */
public class SearchActivity extends Activity implements View.OnClickListener{
    public static SearchActivity instance=null;
    public static String TAG="SearchActivity";
    private Button back;//返回
    private MyRefreshListView refreshListView;
    private ArrayList<CircleEntity> entitydList;//推荐圈子数据
    private RecommendAdapter recommendAdapter;//推荐圈子适配器
    private View no_search;//没有搜索到时显示
    private TextView search;//搜索按钮
    private View failure;//断网时显示
    private EditText searchEditText;//搜索内容
    private int page=1;//默认为1 搜索页数
    private String content;//记录输入的数据

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**搜索成功*/
                case Constants.ON_SUCCEED:
                    entitydList= (ArrayList<CircleEntity>) msg.obj;
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if(entitydList.size()==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    no_search.setVisibility(View.GONE);
                    refreshListView.setVisibility(View.VISIBLE);

                    recommendAdapter.notifyDataSetChanged();
                    /**取消加载动画*/
                    if(page>1){
                        refreshListView.onFinish(true);
                    }
                    break;

                /**没有搜索到数据*/
                case Constants.FAILURE:
                    if(page==1){
                        no_search.setVisibility(View.VISIBLE);
                        refreshListView.setVisibility(View.GONE);
                    }else{
                        /**根据返回的数据数量判断是否开始上滑加载*/
                        if(entitydList.size()!=page*Constants.Page){
                            refreshListView.loading(false);
                        }else{
                            refreshListView.loading(true);
                        }
                        refreshListView.onFinish(true);
                        new ToastShow(SearchActivity.this,getResources().getString(R.string.No_more),1000);
                        refreshListView.loading(false);
                    }
                    break;
                /**获取数据失败*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        no_search.setVisibility(View.VISIBLE);
                        refreshListView.setVisibility(View.GONE);
                    }else{
                        --page;
                    }
                    refreshListView.onFinish(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_search);
        instance=this;
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                ++page;
                new SearchCircleBiz(SearchActivity.this,handler,page,content,entitydList,false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    private void InitView() {
        entitydList=new ArrayList<>();
        searchEditText=(EditText)findViewById(R.id.searchEditText);
        failure=(View)findViewById(R.id.failure);
        search=(TextView)findViewById(R.id.search);
        back=(Button)findViewById(R.id.back);
        no_search=(View)findViewById(R.id.no_search);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        recommendAdapter=new RecommendAdapter(SearchActivity.this,entitydList,TAG);
        refreshListView.setAdapter(recommendAdapter);

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            //搜索
            case R.id.search:
                new KeyboardManage().CloseKeyboard(search,SearchActivity.this);
                if(searchEditText.getText().toString().equals("")){
                    new ToastShow(SearchActivity.this,getResources().getString(R.string.Please_enter_circle),1000);
                }else {
                    if (Constants.currentNetworkType != Constants.TYPE_NONE) {
                        entitydList.clear();
                        content = searchEditText.getText().toString().trim();
                        page = 1;//初始化页数
                        failure.setVisibility(View.GONE);
                        new SearchCircleBiz(SearchActivity.this, handler, page, content, entitydList, true);
                    } else {
                        NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network1), failure);
                        failure.setVisibility(View.VISIBLE);
                        no_search.setVisibility(View.GONE);
                        refreshListView.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }
}
