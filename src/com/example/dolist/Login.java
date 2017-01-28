package com.example.dolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private TextView btnnewuser;
	private TextView btnforgetpassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// 绑定视图
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnnewuser = (TextView) findViewById(R.id.btn_newuser);
		btnforgetpassword = (TextView) findViewById(R.id.btn_forgetpassword);

		// 用户名框获取焦点
		etUsername.requestFocus();

		// 登陆
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();

				String rightpassword = null;

				// 打开数据库
				SQLiteDatabase database = openOrCreateDatabase("db.db",
						MODE_PRIVATE, null);

				// 根据用户名查找密码
				try {
					String selection = "name = '" + username + "'";
					String table = "users";
					String[] columns = { "password" };
					String[] selectionArgs = null;
					String groupBy = null;
					String having = null;
					String orderBy = null;
					String limit = null;
					Cursor cursor = database.query(table, columns, selection,
							selectionArgs, groupBy, having, orderBy, limit);
					// 未找到用户名
					if (cursor.getCount() == 0) {
						database.close();
						Toast.makeText(v.getContext(), "用户名或密码错误!",
								Toast.LENGTH_SHORT).show();
					} else {
						cursor.moveToFirst();// 跳转到第一行
						int nameColumnIndex = cursor.getColumnIndex("password");// 读取密码列号
						rightpassword = cursor.getString(nameColumnIndex);// 获取密码

						if (rightpassword.isEmpty()) {// 判断密码是否为空
							Toast.makeText(v.getContext(), "用户名或密码错误!",
									Toast.LENGTH_SHORT).show();
						} else if (rightpassword.equals(password)) {// 密码正确
							String msg = "登录成功!";

							database.close();
							Toast.makeText(v.getContext(), msg,
									Toast.LENGTH_SHORT).show();

							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {

									Intent mainIntent = new Intent(Login.this,
											MainActivity.class);
									Login.this.startActivity(mainIntent);// 跳转到MainActivity
									Login.this.finish();// 结束Login
								}
							}, 500);// 延迟0.5秒

						} else {// 密码错误
							database.close();
							Toast.makeText(v.getContext(), "用户名或密码错误!",
									Toast.LENGTH_SHORT).show();
						}
					}

				} catch (Exception e) {// 抛出异常
					database.close();
					Toast.makeText(v.getContext(), "用户名或密码错误!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		// 忘记密码
		btnforgetpassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent RegisterIntent = new Intent(Login.this, lostpass.class);
				Login.this.startActivity(RegisterIntent);// 跳转到lostpass
				Login.this.finish();
			}
		});

		// 注册新用户
		btnnewuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent RegisterIntent = new Intent(Login.this, Register.class);
				Login.this.startActivity(RegisterIntent);// 跳转到Register
				Login.this.finish();
			}
		});

	}
}
