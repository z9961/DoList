package com.example.dolist;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

//用于创建数据库和执行建表操作
public class CreateDBActivity extends Activity {

	private static final String SQL_CREATE_TABLE = "create table IF NOT EXISTS users(name nvarchar(16) PRIMARY KEY, password nvarchar(16) not null);";
	// 用户表
	private static final String sql = "create table IF NOT EXISTS list(content text not null,alarm datetime,flag int,dataid INTEGER PRIMARY KEY AUTOINCREMENT);";
	// 清单详情，id列为自增主键
	private static final String sql_festival = "create table IF NOT EXISTS festival(name text not null,date date,flag int,dataid INTEGER PRIMARY KEY AUTOINCREMENT);";

	// 节日表

	public void createdb(View v) {
		CursorFactory factory = null;
		int version = 1;// 自订，从1开始
		String name = "db.db";// 数据库名
		Context Context = v.getContext();
		// 使用sqlliteopenhelpe实例来获取一个sqldatabase
		SQLiteOpenHelper helper = new MySQLiteOpenHelper(Context, name,
				factory, version);
		SQLiteDatabase db = helper.getWritableDatabase();

	}

	public class MySQLiteOpenHelper extends SQLiteOpenHelper {

		public MySQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// 创建表格
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(sql);
			db.execSQL(sql_festival);
		}

		@Override
		// 升级数据库
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}

		@Override
		// 降级数据库
		public void onDowngrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			// TODO Auto-generated method stub
		}

		@Override
		// 打开数据库
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			super.onOpen(db);
		}

	}

}
