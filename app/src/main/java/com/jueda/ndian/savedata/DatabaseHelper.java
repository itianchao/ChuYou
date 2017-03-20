package com.jueda.ndian.savedata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.utils.ConstantsUser;

public class DatabaseHelper extends SQLiteOpenHelper{
	//版本升级添加字段surplus 、count_post、love_bean
	private String CREATE_user = "CREATE TABLE IF NOT EXISTS user"+"("+ ConstantsUser.uid+" INTEGER PRIMARY KEY ON CONFLICT REPLACE,"+ ConstantsUser.avater+" VARCHAR,"+ConstantsUser.birth+" VARCHAR,"
			+ConstantsUser.devotion+" VARCHAR,"+ConstantsUser.education+" VARCHAR,"+ConstantsUser.inviteCode+" VARCHAR,"+ConstantsUser.job+" VARCHAR,"+ConstantsUser.lastTime+" VARCHAR,"
			+ConstantsUser.phoneNumber+" VARCHAR,"+ConstantsUser.sex+" VARCHAR,"+ConstantsUser.signature+" VARCHAR,"+ConstantsUser.userToken+" VARCHAR,"+ConstantsUser.uname+" VARCHAR,"
			+ConstantsUser.RyToken+" VARCHAR,"+ConstantsUser.countDonationsDost+" VARCHAR,"+ConstantsUser.love_bean+" VARCHAR,"+ConstantsUser.count_post+" VARCHAR,"+ConstantsUser.surplus+" VARCHAR,"
			+ConstantsUser.count_ticket+" VARCHAR,"+ConstantsUser.add_location+" VARCHAR,"+ConstantsUser.add_phoneNumber+" VARCHAR,"+ConstantsUser.add_uname+" VARCHAR"+")";
	private String CREATE_TEMP_user = "alter table user rename to _temp_user";
	private String INSERT_DATA = "insert into user select *,'','','','','','',''from _temp_user";//添加几个值就打几个冒号
	private String DROP_user = "drop table _temp_user";

	private static final String DB_NAME = "Ndian.db"; //数据库名称
	private static final int version = 2; //版本
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql= "CREATE TABLE IF NOT EXISTS user"+"("+ ConstantsUser.uid+" INTEGER PRIMARY KEY ON CONFLICT REPLACE,"+ ConstantsUser.avater+" VARCHAR,"+ConstantsUser.birth+" VARCHAR,"
				+ConstantsUser.devotion+" VARCHAR,"+ConstantsUser.education+" VARCHAR,"+ConstantsUser.inviteCode+" VARCHAR,"+ConstantsUser.job+" VARCHAR,"+ConstantsUser.lastTime+" VARCHAR,"
				+ConstantsUser.phoneNumber+" VARCHAR,"+ConstantsUser.sex+" VARCHAR,"+ConstantsUser.signature+" VARCHAR,"+ConstantsUser.userToken+" VARCHAR,"+ConstantsUser.uname+" VARCHAR,"
				+ConstantsUser.RyToken+" VARCHAR,"+ConstantsUser.countDonationsDost+" VARCHAR,"+ConstantsUser.love_bean+" VARCHAR,"+ConstantsUser.count_post+" VARCHAR,"+ConstantsUser.surplus+" VARCHAR,"
				+ConstantsUser.count_ticket+" VARCHAR,"+ConstantsUser.add_location+" VARCHAR,"+ConstantsUser.add_phoneNumber+" VARCHAR,"+ConstantsUser.add_uname+" VARCHAR"+")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion==1){
			db.execSQL(CREATE_TEMP_user);
			db.execSQL(CREATE_user);
			db.execSQL(INSERT_DATA);
			db.execSQL(DROP_user);
		}
	}

}
