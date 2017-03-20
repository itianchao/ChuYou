package com.jueda.ndian.utils;

import android.app.Activity;
import android.content.Context;

import com.jueda.ndian.entity.Photo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/3.
 */
public class Constants {
    public static ArrayList<Activity> activityArrayList=new ArrayList<>();//存储临时要删除的activity

    /***是否打开日志*/
    public final static boolean isOpenLog=true;
    /**机器码*/
    public static String DeviceId="";
    /**页数*/
    public final static int Page=10;
    /**本地缓存*/
    public final static String CONFIGURATION="configuration";

    //记录本地已经安装过的软件
//    public static ArrayList<HashMap<String,String>> taskComplete=new ArrayList<>();

    // 填写从短信SDK应用后台注册得到的APPKEY
    public final static String APPKEY = "117ba76152275";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    public final static String APPSECRET = "283c5cad6fd164afceab2d40a5c65f40";
    // QQ的appid
    public final static String APP_ID="1105206685";
    //微信appid
    public final static String WX_APP_ID="wx72d79cde827389cc";
    public final static String WX_APP_SECRET="d41ffb7e810cca0d17fec4981df56e27";

    //联网状态
    public static final int TYPE_WIFI=1;//无线网
    public static final int TYPE_MOBILE=2;//移动网络
    public static final int TYPE_NONE=3;//没有网络
    public static int currentNetworkType;//当前网络状态

    /**判断是否被迫下线*/
    public static boolean isOut=false;//判断是否被迫下线


    /**各种handler的what值*/
    public final  static int TAKING_PICTURES=4001;//相机拍照
    public final static int PHOTO_ALBUM=4002;//相册
    public final static int TIMING_OF=4003;//计时中
    public final static int CMT_TIME_MARKSTOP=4004;//计时结束
    public final static int CUT_IMAGE=4005;//裁剪图片完成

    public static final int ON_SUCCEED=2000;//数据请求成功
    public static final int ON_SUCCEED_IMAGE=2001;//图片数据上传成功
    public static final int UNREGISTERED=2002;//第三方未注册
    public static final int FAILURE=2003;//数据获取返回失败
    public static final int ON_SUCEED_TWO=2004;//第二个接口调用成功
    public static final int ON_SUCEED_THREE=2005;//第三个接口调用成功
    public static final int FAILURE_TWO=2006;//数据获取返回失败
    public static final int FAILURE_THREE=2007;//数据获取返回失败
    public static final int FAILURE_IMAGE=2008;//图片数据上传失败
    public static final int USER_FAILURE=2009;//用户失效
    public static final int FAILURE_FOUR=2010;//数据获取返回失败
    public static final int ON_SUCEED_FOUR=2011;//第四个接口调用成功
    public static final int FAILURE_FIVE=2012;//返回数据失败

    //支付
    public static final int ZFB_PAY_SUCCEED=2013;//支付成功
    public static final int ZFB_PAY_FAILURE=2014;//支付失败
    public static final int WX_ORDER_SUCCEED=10;//微信订单获取成功
    public static final int WX_ORDER_FAILURE=20;//微信订单获取失败
    public static final int WX_PAY_SUCCEED=30;//微信支付完成



    /** 表示多张图片选择*/
    public static ArrayList<Photo> checkList = new ArrayList<Photo>();
    /** 表示通过Intent传递到下一个Activity的图片列表。 */
    public static final String ARG_PHOTO_LIST = "com.github.yanglw.selectimages.PHOTO_LIST";
    /** 表示通过Intent传递到上一个Activity的图片列表。 */
    public static final String RES_PHOTO_LIST = "com.github.yanglw.selectimages.PHOTO_LIST";
    /**选择图片后是  完成（1）、取消（2）*/
    public static int IS_CANCEL=2;
    /** 记录选中的图片个数 */
    public static int IMAGE_NUMBER=0;
    /** 表示选择的图片发生了变化。 */
    public static final int RESULT_CHANGE = 10010;

    /** 最多能够选择的图片个数，如果数值小于0，则表示没有上限限制。 */
    public static  int MAX_SIZE = 9;


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 七牛图片处理模板
     */
    //处理宽度等比压缩
    public static final String QINIU1="?imageView2/2/w/";
    /**
     * 接口数据
     */
    public static final String HEAD="http://app.ndian365.com/api_v3.php";//头部
    public static final String HEAD_IMAGE_TOKEN=HEAD+"/circles/get_uptoken";//获取上传给七牛的头像地址
    public static final String CHECK_PHONE=HEAD+"/account/check_phone";//检查手机号是否注册
    public static final String REGISTER_USER=HEAD+"/account/register";//普通注册账号信息
    public static final String REGISTER_USER_SNS=HEAD+"/account/register_sns";//第三方注册
    public static final String LOGIN=HEAD+"/account/login";//登录或判断第三方注册没
    public static final String TASK=HEAD+"/assignments/lists";//获取任务列表
    public static final String JOIN_CIRCLE=HEAD+"/circles/user_circles";//获取加入的圈子
    public static final String RECOMMEND_CIRCLE=HEAD+"/circles/recommend";//获取推荐圈子
    public static final String SEARCH_CIRCLES=HEAD+"/circles/search";//搜索圈子
    public static final String CIRCLES_DESC=HEAD+"/circles/desc";//获取圈子详情
    public static final String MONEYBAG_DETAIL=HEAD+"/moneybag_detail";//收支明细
    public static final String TOPIC_DESC=HEAD+"/circles/post_desc";//话题详情
    public static final String TOPIC=HEAD+"/circles/posts";//话题列表
    public static final String CIRCLES_MEMBER=HEAD+"/circles/member";//圈子成员
    public static final String WRITE_APPLY_CIRCLE=HEAD+"/write/apply_circle";//加圈
    public static final String SET_ADD_CIRCLE=HEAD+"/write/set_add_circle";//设置是否允许外人加圈
    public static final String KICK_MEMBER_CIRCLE=HEAD+"/write/kick_member_circle";//踢出圈子成员
    public static final String EXIT_CIRCLE=HEAD+"/write/exit_circle";//退出圈子
    public static final String CREAT_POST=HEAD+"/write/create_post";//发布话题
    public static final String CREATE_CIRRCLE=HEAD+"/write/create_circle";//新建圈子
    public static final String MONEYBAG_SUPLUS=HEAD+"/user/moneybag_surplus";//用户账户余额
    public static final String UPDATE_INFO=HEAD+"/user/update_info";//更改用户信息
    public static final String GET_INFO=HEAD+"/account/info";//获取用户信息
    public static final String COMMENT_POSTS=HEAD+"/write/comment_posts";//评论话题
    public static final String COMMENT_TRAVE=HEAD+"/write/comment_tour";//评论旅游路线
    public static final String MESSAGE_LIST=HEAD+"/user/message_list";//获取消息列表
    public static final String CASH=HEAD+"/write/cash";//提现
    public static final String CLEAR_MESSAGE=HEAD+"/write/clear_message";//清空消息列表
    public static final String LOGIN_OUT=HEAD+"/account/login_out";//用户退出登录
    public static final String CHANGE_PHONE=HEAD+"/account/change_phone";//更改手机号
    public static final String FIND_PASSWORD=HEAD+"/account/forgot_password";//找回密码
    public static final String VERSION=HEAD+"/account/check_versions";//获取版本号
    public static final String MY_TASK=HEAD+"/assignments/my_assignments";//获取我的审核任务
    public static final String MY_TASK_ACTIVITY=HEAD+"/user/my_publish_activities";//获取我的发布活动
    public static final String MY_TASK_QUESTIONNAIRE=HEAD+"/user/my_publish_epq";//获取我的发布问卷
    public static final String MODIFY_CIRCLE_INFORMATION=HEAD+"/write/update_c_info";//修改圈信息
    public static final String BANNER=HEAD+"/assignments/banner";//广告横幅
    public static final String DEMETE_MESSAGE=HEAD+"/write/del_message";//删除单条消息
    public static final String DELETE_TOPIC=HEAD+"/write/del_post";//删除话题
    public static final String COMPLETE_TASK=HEAD+"/write/creat_assignment_proof";//任务完成提交
    public static final String LOCATION=HEAD+"/user/map_api";//定位
    public static final String COMMODITY_POP=HEAD+"/shop/popup";//商品弹窗显示
    public static final String COMMODITY_DETAILS=HEAD+"/shop/goods_desc";//商品详情
    public static final String SUBMIT_ORDERS=HEAD+"/shop/creat_orders";//提交订单
    public static final String ORDERS_LIST=HEAD+"/user/my_product_list";//买家订单列表
    public static final String SELLER_ORDERS_LIST=HEAD+"/user/my_seller_orders";//卖家订单列表
    public static final String RECHARGE_ORDERS=HEAD+"/shop/charge_orders";//充值生产订单
    public static final String ORDERS_DETAILS=HEAD+"/user/my_product_desc";//买家订单详情
    public static final String EXCHANGE_OEDERS_DETAILS=HEAD+"/user/my_goods_desc";//兑换商品订单详情
    public static final String LOGISTICS=HEAD+"/user/my_goods_logistics";//兑换商品物流信息
    public static final String BUYERS_LOGISIICS=HEAD+"/user/my_product_logistics";//买家物流信息
    public static final String WX_COMMODITY=HEAD+"/shop/wx_pay";//微信购买商品生成预支付
    public static final String WX_RECHARGE=HEAD+"/shop/charge_wx_pay";//微信充值生成预支付
    public static final String ZFB_ASY_COMMODITY_URL=HEAD+"/shop/alipay_notify_url";//支付宝商品回调地址
    public static final String ZFB_PAY_URL=HEAD+"/shop/charge_alipay_notify";//支付宝充值回调地址
    public static final String SING=HEAD+"/user/registration";//签到
    public static final String MAREQUEE=HEAD+"/circles/index_define";//获取跑马灯文字和爱心任务火热
    public static final String THUMB=HEAD+"/write/like";//点赞
    public static final String TOPIC_HOT=HEAD+"/circles/post_hot";//热门话题
    public static final String FEEDBACK=HEAD+"/account/feedback";//意见反馈
    public static final String EXPEROEMCE=HEAD+"/assignments/desc";//体验任务详情和问卷详情
    public static final String TASK_ACTIVITY_LIST=HEAD+"/assignments/lists_activities";//活动任务列表
    public static final String TASK_ACTIVITY_DETAILS=HEAD+"/assignments/desc_activity";//活动任务详情
    public static final String SIGN_ACTIVITY=HEAD+"/write/activity_enroll";//报名活动
    public static final String GET_RECHARGE=HEAD+"/shop/charge_define";
    public static final String EXCHANGE=HEAD+"/write/change_money";//爱心豆兑换钱
    public static final String TOURISM=HEAD+"/shop/tour_list";//旅游列表
    public static final String TOURISM_ROUTE=HEAD+"/shop/tour_desc";//旅游路线
    public static final String EXCHANGE_GOOD=HEAD+"/write/change_tour";//兑换旅游票
    public static final String TICKET_LIST=HEAD+"/user/my_tour_list";//我的旅游票列表
    public static final String EXCHANGE_GOODS=HEAD+"/shop/goods_list";//兑换商品列表
    public static final String EXCHANGE_GOODS_DETAILS=HEAD+"/shop/goods_desc";//兑换商品详情
    public static final String EXCHANGE_GOODS_DETAILS_SUMBIT=HEAD+"/write/change_goods";//兑换商品
    public static final String TASK_SHARE_LIST=HEAD+"/shop/product_list";//分享赚豆列表
    public static final String TASK_SHARE_DETALIS=HEAD+"/shop/product_desc";//分享赚豆详情
    public static final String RELEASE_GOODS=HEAD+"/write/creat_product";//发布商品
    public static final String MY_EXCHANGE_GOODS_LIST=HEAD+"/user/my_goods_list";//我的兑换商品列表
    public static final String MY_EXCHANGE_ORDER_CONFIRM=HEAD+"/user/my_goods_receipt";//我的兑换商品订单确认
    public static final String MY_BUYERS_ORDER_CONFIRM=HEAD+"/user/my_product_receipt";//买家订单商品订单确认
    public static final String MY_RELEASE_GOODS_LIST=HEAD+"/user/my_publish_product_list";//我发布的商品
    public static final String MY_RELEASE_GOODS_DELECT=HEAD+"/user/del_publish_product";//删除我发布的商品
    public static final String TASK_SEND_ACTIVITY=HEAD+"/write/publish_activities";//发布任务活动
    public static final String TASK_SEND_QUESTIONNAIRE=HEAD+"/write/publish_epq";//发布任务问卷
    public static final String DELECT_ACTIVITY=HEAD+"/user/del_activity";//删除活动任务
    public static final String DELECT_QUESTION=HEAD+"/user/del_epq";//删除问卷任务
}
