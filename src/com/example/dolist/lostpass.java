package com.example.dolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class lostpass extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lostpass);

		Button button = (Button) findViewById(R.id.btn_lost);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent LoginIntent = new Intent(lostpass.this, Login.class);
				lostpass.this.startActivity(LoginIntent);// Ìø×ªµ½Login
				lostpass.this.finish();// ½áÊølostpass

			}
		});
	}
}
