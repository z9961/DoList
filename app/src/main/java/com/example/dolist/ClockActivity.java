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

//���ӽ���
public class ClockActivity extends Activity {
	private MediaPlayer mp;// mediaPlayer����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);

		Intent intent = getIntent();
		// ��ȡ�������
		String contentValue = intent.getStringExtra("content");
		String alarmValue = intent.getStringExtra("alarmtime");

		mp = MediaPlayer.create(ClockActivity.this, R.raw.alarm);// ����mediaplayer����
		mp.start();// ��ʼ����

		AlertDialog alertDialog = new AlertDialog.Builder(ClockActivity.this)
				.create();
		alertDialog.setCancelable(false); // ���öԻ�������ĵط��ͷ��ؼ�.
		alertDialog.show();
		Window window = alertDialog.getWindow();// �Զ���AlertDialog��ʽ
		window.setContentView(R.layout.clock);
		TextView tv_time = (TextView) window.findViewById(R.id.clocktime); // ��ʾʱ��
		tv_time.setText(alarmValue);
		TextView tv_message = (TextView) window.findViewById(R.id.clockcontent); // ��ʾ����
		tv_message.setText(contentValue);
		Button clockbutton = (Button) window.findViewById(R.id.btn_clock);// ȡ�����Ӱ�ť
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
		mp.release();// �ͷ���Դ
		super.onDestroy();
	}
}
