package com.example.dolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

//闹钟界面
public class ClockActivity extends Activity {
	private MediaPlayer mp;// mediaPlayer对象

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);

		Intent intent = getIntent();
		// 获取相关数据
		String contentValue = intent.getStringExtra("content");
		String alarmValue = intent.getStringExtra("alarmtime");

		mp = MediaPlayer.create(ClockActivity.this, R.raw.alarm);// 创建mediaplayer对象
		mp.start();// 开始播放

		AlertDialog alertDialog = new AlertDialog.Builder(ClockActivity.this)
				.create();
		alertDialog.setCancelable(false); // 禁用对话框以外的地方和返回键.
		alertDialog.show();
		Window window = alertDialog.getWindow();// 自定义AlertDialog样式
		window.setContentView(R.layout.clock);
		TextView tv_time = (TextView) window.findViewById(R.id.clocktime); // 显示时间
		tv_time.setText(alarmValue);
		TextView tv_message = (TextView) window.findViewById(R.id.clockcontent); // 显示内容
		tv_message.setText(contentValue);
		Button clockbutton = (Button) window.findViewById(R.id.btn_clock);// 取消闹钟按钮
		clockbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.stop();
				mp.reset();
				ClockActivity.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (mp.isPlaying()) {
			mp.stop();
		}
		mp.release();// 释放资源
		super.onDestroy();
	}
}
