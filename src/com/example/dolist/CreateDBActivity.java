package com.example.dolist;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

//���ڴ������ݿ��ִ�н������
public class CreateDBActivity extends Activity {

	private static final String SQL_CREATE_TABLE = "create table IF NOT EXISTS users(name nvarchar(16) PRIMARY KEY, password nvarchar(16) not null);";
	// �û���
	private static final String sql = "create table IF NOT EXISTS list(content text not null,alarm datetime,flag int,dataid INTEGER PRIMARY KEY AUTOINCREMENT);";
	// �嵥���飬id��Ϊ��������
	private static final String sql_festival = "create table IF NOT EXISTS festival(name text not null,date date,flag int,dataid INTEGER PRIMARY KEY AUTOINCREMENT);";

	// ���ձ�

	public void createdb(View v) {
		CursorFactory factory = null;
		int version = 1;// �Զ�����1��ʼ
		String name = "db.db";// ���ݿ���
		Context Context = v.getContext();
		// ʹ��sqlliteopenhelpeʵ������ȡһ��sqldatabase
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
			// �������
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(sql);
			db.execSQL(sql_festival);
		}

		@Override
		// �������ݿ�
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}

		@Override
		// �������ݿ�
		public void onDowngrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			// TODO Auto-generated method stub
		}

		@Override
		// �����ݿ�
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			super.onOpen(db);
		}

	}

}
