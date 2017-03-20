package com.jueda.ndian.savedata.rong;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;

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
	public void add(List<Friend> persons){
		db.execSQL("DELETE FROM friend");
		db.beginTransaction();
		try {
			for(Friend person:persons){
				db.execSQL("INSERT INTO friend VALUES(null,?,?,?)",new Object[]{person.getUserId(),person.getUserName(),person.getPortraitUri()});
			}
			db.setTransactionSuccessful();
		}catch (SQLiteFullException e){
			new ToastShow(context,"存储空间不足",1500);
			e.printStackTrace();
		}finally {
			db.endTransaction();
		}
	}
//	public void UpdataAge(Friend persons){
//		ContentValues values=new ContentValues();
//		values.put("age", persons.age);
//		db.update("person", values, "name=?", new String[]{persons.name});
//	}
//	public void delectOldPerson(Person persons){
//		db.delete("person", "name=?", new String[]{persons.name});
//	}
	public List<Friend> query(){
		ArrayList<Friend> list=new ArrayList<>();
		Cursor c=queryTheCursor();
		while(c.moveToNext()){
			Friend p=new Friend();
			p.userId=c.getString(c.getColumnIndex("userId"));
			p.userName=c.getString(c.getColumnIndex("userName"));
			p.portraitUri=c.getString(c.getColumnIndex("portraitUri"));
			list.add(p);
		}
		c.close();
		return list;
	}
	 public Cursor queryTheCursor() {  
	        Cursor c = db.rawQuery("SELECT * FROM friend", null);
	        return c;  
	 }
}
