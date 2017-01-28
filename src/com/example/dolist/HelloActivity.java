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
		View v = getWindow().getDecorView(); // ��ȡ��ǰview�����ڴ������ݿ�
		// ����ʱ���ж����ݿ��Ƿ���ڣ����������򴴽�
		try {
			File file = getApplicationContext().getDatabasePath("db.db"); // �õ�db.db���ݿ�·��
			if (!file.exists()) {
				CreateDBActivity createDBActivity = new CreateDBActivity();
				createDBActivity.createdb(v);
				insertsql(); // ��һ������ʱ��������
			}
		} catch (Exception e) {
			// TODO: handle exception
			CreateDBActivity createDBActivity = new CreateDBActivity();
			createDBActivity.createdb(v);
			insertsql();
		}

		new Handler().postDelayed(new Runnable() {
			// ��ӭҳ�����1s
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent LoginIntent = new Intent(HelloActivity.this, Login.class);
				HelloActivity.this.startActivity(LoginIntent);// ��ת��Login
				HelloActivity.this.finish();// ����HelloActivity
			}
		}, 1000);// ��postDelayed()�����ӳ�1��
	}

	public void insertsql() {
		// ��һ������ʱ��������
		SQLiteDatabase database = openOrCreateDatabase("db.db", MODE_PRIVATE,
				null);// �����ݿ�
		Cursor cursor2 = database.query("users", null, null, null, null, null,
				null, null);
		if (cursor2.getCount() == 0) {
			database.execSQL("INSERT INTO festival (name,date,flag) VALUES ('Ԫ��','2017-1-1',1),('����','2017-1-28',1),('Ԫ����','2017-2-11',1),('�Ͷ���','2017-5-1',1),('�����','2017-10-1',1)");
		}
		database.close();
	}
}
