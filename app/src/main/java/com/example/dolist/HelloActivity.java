package com.example.dolist;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class HelloActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello);
		View v = getWindow().getDecorView(); // 获取当前view，用于创建数据库
		// 启动时先判断数据库是否存在，若不存在则创建
		try {
			File file = getApplicationContext().getDatabasePath("db.db"); // 得到db.db数据库路径
			if (!file.exists()) {
				CreateDBActivity createDBActivity = new CreateDBActivity();
				createDBActivity.createdb(v);
				insertsql(); // 第一次启动时插入数据
			}
		} catch (Exception e) {
			// TODO: handle exception
			CreateDBActivity createDBActivity = new CreateDBActivity();
			createDBActivity.createdb(v);
			insertsql();
		}

		new Handler().postDelayed(new Runnable() {
			// 欢迎页面持续1s
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent LoginIntent = new Intent(HelloActivity.this, Login.class);
				HelloActivity.this.startActivity(LoginIntent);// 跳转到Login
				HelloActivity.this.finish();// 结束HelloActivity
			}
		}, 1000);// 给postDelayed()方法延迟1秒
	}

	public void insertsql() {
		// 第一次启动时插入数据
		SQLiteDatabase database = openOrCreateDatabase("db.db", MODE_PRIVATE,
				null);// 打开数据库
		Cursor cursor2 = database.query("users", null, null, null, null, null,
				null, null);
		if (cursor2.getCount() == 0) {
			database.execSQL("INSERT INTO festival (name,date,flag) VALUES ('元旦','2017-1-1',1),('春节','2017-1-28',1),('元宵节','2017-2-11',1),('劳动节','2017-5-1',1),('国庆节','2017-10-1',1)");
		}
		database.close();
	}
}
