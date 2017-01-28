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
		btnCancel.setOnClickListener(new View.OnClickListener() {// ȡ����

					@Override
					public void onClick(View arg0) {
						Intent LoginIntent = new Intent(Register.this,
								Login.class);
						Register.this.startActivity(LoginIntent);// ��ת��Login
						Register.this.finish();// ����ע��
					}
				});

		btnInsertWithSql.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SQLiteDatabase database;

				// �����ݿ�
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);

				// �ж��û����������Ƿ�Ϊ��
				String user = etUser.getText().toString();
				String password = etPassword.getText().toString();
				String passwordcheck = etPasswordcheck.getText().toString();
				if (user.isEmpty()) {// �û���Ϊ��
					Toast.makeText(v.getContext(), "�û�������Ϊ�գ�",
							Toast.LENGTH_SHORT).show();
				} else {
					if (password.isEmpty()) {// ����Ϊ��
						Toast.makeText(v.getContext(), "���벻��Ϊ�գ�",
								Toast.LENGTH_SHORT).show();
					} else if (!passwordcheck.equals(password)) {
						Toast.makeText(v.getContext(), "�������벻һ�£�",
								Toast.LENGTH_SHORT).show();
					} else {
						// insert into users
						// (name,password)values('user','pass')
						String sql = "INSERT INTO users(name, password) values("
								+ "'" + user + "', '" + password + "'" + ")";

						try {
							database.execSQL(sql);
							Toast.makeText(v.getContext(), "ע��ɹ�!",
									Toast.LENGTH_SHORT).show();

							// ע��ɹ����ص�½����
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									Intent LoginIntent = new Intent(
											Register.this, Login.class);
									Register.this.startActivity(LoginIntent);// ��ת��Login
									Register.this.finish();// ����Register
								}
							}, 500);// ��ʱ0.5
						} catch (Exception e) {// �׳��쳣
							database.close();
							Toast.makeText(v.getContext(), "ע��ʧ��,�û��Ѿ�����!",
									Toast.LENGTH_SHORT).show();
						}
					}

				}

			}
		});

	}

}
