package com.example.dolist;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	private EditText etUser;
	private EditText etPassword;
	private EditText etPasswordcheck;

	private Button btnInsertWithSql;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		etUser = (EditText) findViewById(R.id.user);
		etPassword = (EditText) findViewById(R.id.password);
		etPasswordcheck = (EditText) findViewById(R.id.passwordcheck);

		btnInsertWithSql = (Button) findViewById(R.id.btn_insert);
		btnCancel = (Button) findViewById(R.id.btn_cancel);

		//
		btnCancel.setOnClickListener(new View.OnClickListener() {// 取消键

					@Override
					public void onClick(View arg0) {
						Intent LoginIntent = new Intent(Register.this,
								Login.class);
						Register.this.startActivity(LoginIntent);// 跳转回Login
						Register.this.finish();// 结束注册
					}
				});

		btnInsertWithSql.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SQLiteDatabase database;

				// 打开数据库
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);

				// 判断用户名和密码是否为空
				String user = etUser.getText().toString();
				String password = etPassword.getText().toString();
				String passwordcheck = etPasswordcheck.getText().toString();
				if (user.isEmpty()) {// 用户名为空
					Toast.makeText(v.getContext(), "用户名不能为空！",
							Toast.LENGTH_SHORT).show();
				} else {
					if (password.isEmpty()) {// 密码为空
						Toast.makeText(v.getContext(), "密码不能为空！",
								Toast.LENGTH_SHORT).show();
					} else if (!passwordcheck.equals(password)) {
						Toast.makeText(v.getContext(), "两次密码不一致！",
								Toast.LENGTH_SHORT).show();
					} else {
						// insert into users
						// (name,password)values('user','pass')
						String sql = "INSERT INTO users(name, password) values("
								+ "'" + user + "', '" + password + "'" + ")";

						try {
							database.execSQL(sql);
							Toast.makeText(v.getContext(), "注册成功!",
									Toast.LENGTH_SHORT).show();

							// 注册成功返回登陆界面
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									Intent LoginIntent = new Intent(
											Register.this, Login.class);
									Register.this.startActivity(LoginIntent);// 跳转到Login
									Register.this.finish();// 结束Register
								}
							}, 500);// 延时0.5
						} catch (Exception e) {// 抛出异常
							database.close();
							Toast.makeText(v.getContext(), "注册失败,用户已经存在!",
									Toast.LENGTH_SHORT).show();
						}
					}

				}

			}
		});

	}

}
