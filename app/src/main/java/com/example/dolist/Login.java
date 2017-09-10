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

		// ����ͼ
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnnewuser = (TextView) findViewById(R.id.btn_newuser);
		btnforgetpassword = (TextView) findViewById(R.id.btn_forgetpassword);

		// �û������ȡ����
		etUsername.requestFocus();

		// ��½
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();

				String rightpassword = null;

				// �����ݿ�
				SQLiteDatabase database = openOrCreateDatabase("db.db",
						MODE_PRIVATE, null);

				// �����û�����������
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
					// δ�ҵ��û���
					if (cursor.getCount() == 0) {
						database.close();
						Toast.makeText(v.getContext(), "�û������������!",
								Toast.LENGTH_SHORT).show();
					} else {
						cursor.moveToFirst();// ��ת����һ��
						int nameColumnIndex = cursor.getColumnIndex("password");// ��ȡ�����к�
						rightpassword = cursor.getString(nameColumnIndex);// ��ȡ����

						if (rightpassword.isEmpty()) {// �ж������Ƿ�Ϊ��
							Toast.makeText(v.getContext(), "�û������������!",
									Toast.LENGTH_SHORT).show();
						} else if (rightpassword.equals(password)) {// ������ȷ
							String msg = "��¼�ɹ�!";

							database.close();
							Toast.makeText(v.getContext(), msg,
									Toast.LENGTH_SHORT).show();

							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {

									Intent mainIntent = new Intent(Login.this,
											MainActivity.class);
									Login.this.startActivity(mainIntent);// ��ת��MainActivity
									Login.this.finish();// ����Login
								}
							}, 500);// �ӳ�0.5��

						} else {// �������
							database.close();
							Toast.makeText(v.getContext(), "�û������������!",
									Toast.LENGTH_SHORT).show();
						}
					}

				} catch (Exception e) {// �׳��쳣
					database.close();
					Toast.makeText(v.getContext(), "�û������������!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		// ��������
		btnforgetpassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent RegisterIntent = new Intent(Login.this, lostpass.class);
				Login.this.startActivity(RegisterIntent);// ��ת��lostpass
				Login.this.finish();
			}
		});

		// ע�����û�
		btnnewuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent RegisterIntent = new Intent(Login.this, Register.class);
				Login.this.startActivity(RegisterIntent);// ��ת��Register
				Login.this.finish();
			}
		});

	}
}
