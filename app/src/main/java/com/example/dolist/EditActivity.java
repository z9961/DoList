package com.example.dolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditActivity extends Activity {
	private Intent intent;
	private int idValue;
	private EditText editcontent;
	SQLiteDatabase database;
	private String alarmValue;
	private String contentValue;
	int flag;
	ImageView alarmview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		getdata(); // 获取要编辑的数据

		editcontent = (EditText) findViewById(R.id.editcontent); // 编辑内容
		Button edittime = (Button) findViewById(R.id.edittime); // 事件时间
		alarmview = (ImageView) findViewById(R.id.editalarm);

		if (flag == 1) {
			alarmview.setImageResource(R.drawable.alarm);
		} else {
			alarmview.setImageResource(R.drawable.disalarm);
		}

		edittime.setText(alarmValue);
		editcontent.setText(contentValue);
		alarmview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
				if (flag == 1) {
					String unalarmflag = "update list set flag = " + "'" + 0
							+ "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					alarmview.setImageResource(R.drawable.disalarm);
					unalarm(idValue);
					flag = 0;
				} else {
					String unalarmflag = "update list set flag = " + "'" + 1
							+ "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					setclickalarm(idValue);
					alarmview.setImageResource(R.drawable.alarm);
					flag = 1;
				}
				database.close();
			}
		});
		edittime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
				if (flag == 1) {
					String unalarmflag = "update list set flag = " + "'" + 0
							+ "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					alarmview.setImageResource(R.drawable.disalarm);
					unalarm(idValue);
					flag = 0;
				} else {
					String unalarmflag = "update list set flag = " + "'" + 1
							+ "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					setclickalarm(idValue);
					alarmview.setImageResource(R.drawable.alarm);
					flag = 1;
				}
				database.close();
			}
		});

	}

	public void save() {
		// 用于将变动的数据存入数据库
		database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库

		String text = editcontent.getText().toString();
		String newsql = "update list set content = " + "'" + text + "'"
				+ "where dataid = " + "'" + idValue + "'";
		database.execSQL(newsql);
		Toast.makeText(getApplicationContext(), "待办事项更改好了", Toast.LENGTH_SHORT)
				.show();
		database.close();
	}

	public void getdata() {
		// 获取传入数据
		intent = getIntent();

		contentValue = intent.getStringExtra("content");// 内容
		alarmValue = intent.getStringExtra("alarmtime");// 时间
		idValue = intent.getIntExtra("id", 0);// id
		flag = intent.getIntExtra("flag", 0);// flag

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		save();// 返回的时候保存数据
		Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
		EditActivity.this.startActivity(mainIntent);// 跳转到MainActivity
		EditActivity.this.finish();// 结束editActivity
		super.onDestroy();
	}

	// 取消闹钟
	public void unalarm(int id) {
		int requestCode = id;
		Intent intent = new Intent(List.alist, ClockActivity.class);
		PendingIntent pi = PendingIntent.getActivity(List.alist, requestCode,
				intent, PendingIntent.FLAG_CANCEL_CURRENT); // 根据id设置不同的闹钟

		// And cancel the alarm.
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		Toast.makeText(getApplicationContext(), "闹钟取消了", Toast.LENGTH_LONG)
				.show();
	}

	// 设置闹钟
	public void setclickalarm(int id) {

		int requestCode = 0;

		requestCode = idValue;
		// ---------------------------------------------------------------------
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(List.alist, ClockActivity.class);
		intent.putExtra("content", contentValue);// 传递内容
		intent.putExtra("alarmtime", alarmValue);// 传递闹铃时间
		PendingIntent pi = PendingIntent.getActivity(List.alist, requestCode,
				intent, PendingIntent.FLAG_CANCEL_CURRENT); // 根据id设置不同的闹钟
		Calendar alarmTime = Calendar.getInstance();
		long alarmdatetime = changedatetime(alarmValue); // 将时间转换为long型
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmdatetime, pi);
		Toast.makeText(getApplicationContext(), "闹钟设置好了", Toast.LENGTH_LONG)
				.show();
	}// 闹钟设置完毕

	// 转换时间
	public long changedatetime(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		Date date;

		try {
			date = format.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
