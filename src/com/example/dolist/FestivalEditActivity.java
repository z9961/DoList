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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FestivalEditActivity extends Activity {
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.festival_edit);

		getdata();

		editcontent = (EditText) findViewById(R.id.festivaleditcontent);
		TextView edittime = (TextView) findViewById(R.id.festivaledittime);
		alarmview = (ImageView) findViewById(R.id.festivalalarm);

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
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// �����ݿ�
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
				// TODO Auto-generated method stub
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// �����ݿ�
				if (flag == 1) {
					String unalarmflag = "update festival set flag = " + "'"
							+ 0 + "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					alarmview.setImageResource(R.drawable.disalarm);
					unalarm(idValue);
					flag = 0;
				} else {
					String unalarmflag = "update festival set flag = " + "'"
							+ 1 + "'" + "where dataid = " + "'" + idValue + "'";
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

		database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// �����ݿ�

		String text = editcontent.getText().toString();
		String newsql = "update festival set name = " + "'" + text + "'"
				+ "where dataid = " + "'" + idValue + "'";
		database.execSQL(newsql);
		Toast.makeText(getApplicationContext(), "���ո��ĺ���", Toast.LENGTH_SHORT)
				.show();
		database.close();
	}

	public void getdata() {
		// ��ȡ��������
		intent = getIntent();

		contentValue = intent.getStringExtra("content");// ����
		alarmValue = intent.getStringExtra("alarmtime");// ʱ��
		idValue = intent.getIntExtra("id", 0);// id
		flag = intent.getIntExtra("flag", 0);// flag
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		save();
		Intent mainIntent = new Intent(FestivalEditActivity.this,
				MainActivity.class);
		// FestivalEditActivity.this.startActivity(mainIntent);//
		// ��ת��MainActivity
		// FestivalEditActivity.this.finish();// ����FestivaleditActivity
		super.onDestroy();
	}

	// ȡ������
	public void unalarm(int id) {
		int requestCode = id;
		Intent intent = new Intent(Festival.aFestival,
				FestivalClockActivity.class);
		PendingIntent pi = PendingIntent.getActivity(Festival.aFestival,
				requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT); // ����id���ò�ͬ������

		// And cancel the alarm.
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		Toast.makeText(getApplicationContext(), "����ȡ����", Toast.LENGTH_LONG)
				.show();
	}

	// ��������
	public void setclickalarm(int id) {

		int requestCode = 0;

		requestCode = idValue;
		// ---------------------------------------------------------------------
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(Festival.aFestival,
				FestivalClockActivity.class);
		intent.putExtra("content", contentValue);// ��������
		intent.putExtra("alarmtime", alarmValue);// ��������ʱ��
		PendingIntent pi = PendingIntent.getActivity(Festival.aFestival,
				requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT); // ����id���ò�ͬ������
		Calendar alarmTime = Calendar.getInstance();
		long alarmdatetime = changedatetime(alarmValue); // ��ʱ��ת��Ϊlong��
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmdatetime, pi);
		Toast.makeText(getApplicationContext(), "�������ú���", Toast.LENGTH_LONG)
				.show();

	}// �����������

	// ת��ʱ��
	public long changedatetime(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
