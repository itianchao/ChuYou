package com.jueda.ndian.savedata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;

import com.jueda.ndian.R;
import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ToastShow;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
	private DatabaseHelper helper;
	private SQLiteDatabase db;
	private Context context;
	public DBManager(Context context){
		this.context=context;
		helper=new DatabaseHelper(context);
		db=helper.getWritableDatabase();
	}

	/**
	 * 添加新数据
	 * @param userEntitys
	 */
	public void add(UserEntity userEntity){
		db.execSQL("DELETE FROM user");
		db.beginTransaction();
		try {
				db.execSQL("INSERT INTO user VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[]{userEntity.getUid(),userEntity.getAvater(),userEntity.getBirth(),userEntity.getDevotion(),userEntity.getEducation(),userEntity.getInviteCode(),userEntity.getJob()
						,userEntity.getLastTime(),userEntity.getPhoneNumber(),userEntity.getSex(),userEntity.getSignature(),userEntity.getUserToken(),userEntity.getUname(),userEntity.getRyToken()
						,userEntity.getCountDonationsDost(),userEntity.getLove_bean(),userEntity.getCount_post(),userEntity.getSurplus(),userEntity.getCount_ticket(),userEntity.getAdd_location()
						,userEntity.getAdd_phoneNumber(),userEntity.getAdd_uname()});
			db.setTransactionSuccessful();
		}catch (SQLiteFullException e){
			new ToastShow(context,"存储空间不足",1500);
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
//
//	/**
//	 * 修改用户数据
//	 * @param userEntity
//	 * @param string
//	 */
//	public boolean UpdataUser(UserEntity userEntity){
//		ContentValues values=new ContentValues();
//		//需要修改的数据
//		values.put(ConstantsUser.PHONE_NUMBER, userEntity.getPhoneNumber());
//		values.put(ConstantsUser.NICK_NAME, userEntity.getNickName());
//		//string + "=?"   条件
//		//修改添加参数  new String[]{userEntity.PhoneNumber}
//		try{
//			db.update("user", values, ConstantsUser.PHONE_NUMBER + "=?", new String[]{userEntity.PhoneNumber});
//			return true;
//		}catch(Exception ex){
//			return false;
//		}
//	}

	/**
	 * 读取数据
	 * @return
	 */
	public UserEntity query(){
		ArrayList<UserEntity> list=new ArrayList<>();
		UserEntity userEntity = new UserEntity();
		/**默认数据为空*/
		userEntity.setUid(0);
		userEntity.setPhoneNumber("");
		userEntity.setUname("");
		userEntity.setAvater("");
		userEntity.setSex("");
		userEntity.setBirth("");
		userEntity.setJob("");
		userEntity.setEducation("");
		userEntity.setSignature("");
		userEntity.setDevotion("0");
		userEntity.setLastTime("");
		userEntity.setInviteCode("");
		userEntity.setUserToken("");
		userEntity.setRyToken("");
		userEntity.setCountDonationsDost("0");
		userEntity.setLove_bean("0");
		userEntity.setCount_post("0");
		userEntity.setSurplus("0");
		userEntity.setCount_ticket("0");
		userEntity.setAdd_location("");
		userEntity.setAdd_phoneNumber("");
		userEntity.setAdd_uname("");

		Cursor c=queryTheCursor();
		while(c.moveToNext()){
			userEntity=new UserEntity();
			userEntity.uid=c.getInt(c.getColumnIndex(ConstantsUser.uid));
			userEntity.avater=c.getString(c.getColumnIndex(ConstantsUser.avater));
			userEntity.birth=c.getString(c.getColumnIndex(ConstantsUser.birth));
			userEntity.devotion=c.getString(c.getColumnIndex(ConstantsUser.devotion));
			userEntity.education=c.getString(c.getColumnIndex(ConstantsUser.education));
			userEntity.inviteCode=c.getString(c.getColumnIndex(ConstantsUser.inviteCode));
			userEntity.job=c.getString(c.getColumnIndex(ConstantsUser.job));
			userEntity.lastTime=c.getString(c.getColumnIndex(ConstantsUser.lastTime));
			userEntity.phoneNumber=c.getString(c.getColumnIndex(ConstantsUser.phoneNumber));
			userEntity.sex=c.getString(c.getColumnIndex(ConstantsUser.sex));
			userEntity.signature=c.getString(c.getColumnIndex(ConstantsUser.signature));
			userEntity.userToken=c.getString(c.getColumnIndex(ConstantsUser.userToken));
			userEntity.uname=c.getString(c.getColumnIndex(ConstantsUser.uname));
			userEntity.ryToken=c.getString(c.getColumnIndex(ConstantsUser.RyToken));
			userEntity.countDonationsDost=c.getString(c.getColumnIndex(ConstantsUser.countDonationsDost));
			userEntity.love_bean=c.getString(c.getColumnIndex(ConstantsUser.love_bean));
			userEntity.count_post=c.getString(c.getColumnIndex(ConstantsUser.count_post));
			userEntity.surplus=c.getString(c.getColumnIndex(ConstantsUser.surplus));
			userEntity.count_post=c.getString(c.getColumnIndex(ConstantsUser.count_post));
			userEntity.add_location=c.getString(c.getColumnIndex(ConstantsUser.add_location));
			userEntity.add_phoneNumber=c.getString(c.getColumnIndex(ConstantsUser.add_phoneNumber));
			userEntity.add_uname=c.getString(c.getColumnIndex(ConstantsUser.add_uname));
			list.add(userEntity);
		}
		c.close();
		/**返回最后一条*/
		return userEntity;
	}

	 public Cursor queryTheCursor() {  
	        Cursor c = db.rawQuery("SELECT * FROM user", null);
	        return c;  
	    }  
}
