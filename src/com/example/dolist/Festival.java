package com.example.dolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Festival extends Activity {
	private int flag; // 是否有闹铃的标志
	private ListView FestivalView; // 存放节日
	private String text; // 输入的内容
	private String datetime;// 日期
	private int year = 0; // 年
	private int monthOfYear = 0; // 月
	private int dayOfMonth = 0; // 日
	private int year2 = 0; // 选择的年
	private int monthOfYear2 = 0; // 选择的月
	private int dayOfMonth2 = 0; // 选择的日
	SQLiteDatabase database;// 数据库连接
	public ArrayList<Festivalitem> Festivalitems; // 存放FestivalView用到的数据
	private BaseAdapter adapter; // 适配器，处理数据
	private AlarmManager alarmManager; // 闹钟
	private PendingIntent pi; // 闹钟用
	public Intent editFestivalIntent; // 要编辑的intent
	private int editidValue; // 要编辑的id
	public static Festival aFestival;
	int setdatetimeflag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_festival);
		aFestival = this;
		FestivalView = (ListView) findViewById(R.id.Festivalview);
		ImageButton button_Festivalcal = (ImageButton) findViewById(R.id.btn_Festivalcel);
		ImageButton button_Festivaladd = (ImageButton) findViewById(R.id.btn_Festivaladd);
		final EditText editFestivalText = (EditText) findViewById(R.id.editFestivalText);

		database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库

		// 时间选择按钮
		button_Festivalcal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				if ((!database.isOpen()) || database == null) {
					database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
				}
				Calendar calendar = Calendar.getInstance();// 日历
				DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						int monthOfYear2 = monthOfYear + 1;
						Toast.makeText(
								view.getContext(),
								"您选择的日期是:" + year + "年" + monthOfYear2 + "月"
										+ dayOfMonth + "日", Toast.LENGTH_SHORT)
								.show();
						Festival.this.year = year;
						Festival.this.monthOfYear = monthOfYear2;
						Festival.this.dayOfMonth = dayOfMonth;

					}
				};

				// 选择日期
				year = calendar.get(Calendar.YEAR);
				monthOfYear = calendar.get(Calendar.MONTH) + 1;// 月是从0开始的，需要+1
				dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
				int monthOfYear2 = monthOfYear - 1;
				DatePickerDialog dialog = new DatePickerDialog(context,
						callBack, year, monthOfYear2, dayOfMonth); // -1
				DatePicker datecal = dialog.getDatePicker();
				datecal.setMinDate(new Date().getTime() - 1000);// 设置最小日期为当前日期之前
																// 最小日期必须不等于当前日期
				dialog.show();

			}
		});

		// 添加待办事项
		button_Festivaladd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text = editFestivalText.getText().toString(); // 获取输入的字符串

				if (text.isEmpty()) {// 内容是否为空
					Toast.makeText(v.getContext(), "想好做什么再来吧 ¬_¬",
							Toast.LENGTH_SHORT).show();
				} else if (setdatetimeflag == 0) {
					Toast.makeText(v.getContext(), "还没有选择日期和时间！",
							Toast.LENGTH_SHORT).show();
				} else {
					// 是否设置闹钟
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Festival.this);
					builder.setTitle("闹钟选项");
					builder.setMessage("要设置闹钟吗？");
					builder.setPositiveButton("需要^.^",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									flag = 1;
									add();// 执行添加操作
									setalarm(); // 设置闹钟
								}
							});
					builder.setNegativeButton("不要~_~",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									flag = 0;
									add();// 执行添加操作
								}
							});

					builder.show();
					// 弹出对话框

				}
			}
		});

		// listview设置
		Festivalitems = new ArrayList<Festivalitem>(); // 用于存放节日信息
		getdata();// 获取数据
		adapter = new myadapter();
		FestivalView.setAdapter(adapter); // 使用适配器

		// 点击item设置/取消闹钟
		FestivalView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(android.widget.AdapterView<?> arg0,
							View v, int arg2, long arg3) {
						if ((!database.isOpen()) || database == null) {
							database = openOrCreateDatabase("db.db",
									MODE_PRIVATE, null);// 打开数据库
						}
						String table = "festival";
						String[] columns = { "name", "date", "flag", "dataid" };// 需要获取的数据
						String selection = null;
						String[] selectionArgs = null;
						Cursor cursor = database.query(table, columns,
								selection, selectionArgs, null, null, null);
						cursor.moveToPosition(arg2); // 移动到要编辑的行
						int idColumnIndex = cursor.getColumnIndex("dataid");
						int alarmColumnIndex = cursor.getColumnIndex("date");
						int flagColumnIndex = cursor.getColumnIndex("flag");
						String strtime = cursor.getString(alarmColumnIndex);
						int intflag = cursor.getInt(flagColumnIndex);
						int idValue = cursor.getInt(idColumnIndex); // 得到要编辑行的id
						if (intflag == 1) {// 取消闹钟
							String unalarmflag = "update festival set flag = "
									+ "'" + 0 + "'" + "where dataid = " + "'"
									+ idValue + "'";
							database.execSQL(unalarmflag);
							unalarm(idValue);
							refresh();
						} else {// 设置闹钟
							String unalarmflag = "update festival set flag = "
									+ "'" + 1 + "'" + "where dataid = " + "'"
									+ idValue + "'";
							database.execSQL(unalarmflag);
							setclickalarm(arg2);
							refresh();
						}

					};
				});

		// 设置item长按事件,弹出菜单
		FestivalView
				.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("请选择操作：");
						menu.add(0, 0, 0, "编辑");
						menu.add(0, 1, 0, "删除");
					}
				});

		database.close();
	}

	// ---------------------------------------------------------------------------
	// onCreate结束

	// item长按事件操作
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// info.id得到listview中选择的条目绑定的id
		int id = info.position;
		switch (item.getItemId()) {
		case 0:
			// 修改数据操作
			// 得到数据
			String table = "festival";
			String[] columns = { "name", "date", "flag", "dataid" };// 需要获取的数据
			String selection = null;
			String[] selectionArgs = null;
			Cursor cursor = database.query(table, columns, selection,
					selectionArgs, null, null, null);
			cursor.moveToPosition(id); // 移动到要编辑的行
			int idColumnIndex = cursor.getColumnIndex("dataid");
			int nameColumnIndex = cursor.getColumnIndex("name");
			int alarmColumnIndex = cursor.getColumnIndex("date");
			int flagColumnIndex = cursor.getColumnIndex("flag");
			String strValue = cursor.getString(nameColumnIndex);
			String strtime = cursor.getString(alarmColumnIndex);
			int intflag = cursor.getInt(flagColumnIndex);
			int idValue = cursor.getInt(idColumnIndex); // 得到要编辑行的id

			Intent FestivaleditIntent = new Intent(Festival.this,
					FestivalEditActivity.class);

			FestivaleditIntent.putExtra("content", strValue);// 传递内容
			FestivaleditIntent.putExtra("alarmtime", strtime);// 传递时间
			FestivaleditIntent.putExtra("id", idValue);// 传递id
			FestivaleditIntent.putExtra("flag", intflag);// 传递flag

			Festival.this.startActivity(FestivaleditIntent);// 跳转到编辑界面
			refresh();
			return true;
		case 1:
			// 删除数据操作
			String table2 = "festival";
			String[] columns2 = { "dataid" };
			String selection2 = null;
			String[] selectionArgs2 = null;
			Cursor cursor2 = database.query(table2, columns2, selection2,
					selectionArgs2, null, null, null);
			cursor2.moveToPosition(id); // 移动到要删除的行
			int idColumnIndex2 = cursor2.getColumnIndex("dataid");
			int idValue2 = cursor2.getInt(idColumnIndex2); // 得到要删除行的id
			String deletesql = "DELETE from festival WHERE dataid = "
					+ idValue2;
			database.execSQL(deletesql);
			refresh();// 刷新列表
			Toast.makeText(getApplicationContext(), "该事项已删除",
					Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	// 拼接日期和时间，插入数据库
	public void add() {
		// 整合日期时间
		if (dayOfMonth == 0) {
			Toast.makeText(getApplicationContext(), "还没有选择日期！",
					Toast.LENGTH_SHORT).show();
		} else {

			datetime = year + "-" + monthOfYear + "-" + dayOfMonth;

			// 向数据库插入数据

			try {

				String sql = "INSERT INTO festival(name,date,flag) values("
						+ "'" + text + "', '" + datetime + "', '" + flag + "'"
						+ ")";
				database.execSQL(sql);
				refresh();// 刷新列表
				Toast.makeText(getApplicationContext(), "节日保存好了",
						Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "有些地方出错了",
						Toast.LENGTH_SHORT).show();
				Log.i("SQL", text + "," + datetime + ',' + flag);
			}
		}// 整合时间if
	}

	// 获取cursor的数据
	public void getdata() {
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		Festivalitems.clear();
		// 新建游标用于获取list
		String table = "festival";
		String[] columns = { "name", "date", "flag" };// 需要获取的数据
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = database.query(table, columns, selection,
				selectionArgs, null, null, null);
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				// 根据列名获取列索引
				int nameColumnIndex = cursor.getColumnIndex("name");
				int alarmColumnIndex = cursor.getColumnIndex("date");
				int flagColumnIndex = cursor.getColumnIndex("flag");
				String strValue = cursor.getString(nameColumnIndex);
				String strtime = cursor.getString(alarmColumnIndex);
				int intflag = cursor.getInt(flagColumnIndex);
				Festivalitem Festivalitem = new Festivalitem(strValue, strtime,
						intflag);
				Festivalitems.add(Festivalitem);
			}
		}
	}

	// 逐行取出数据
	public class myadapter extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(getBaseContext(), R.layout.festival_item,
						null);
			} else {
				view = convertView;
			}

			// 从Festivalitems中取出一行数据，position相当于数组下标,可以实现逐行取数据
			Festivalitem aFestivalitem = Festivalitems.get(position);
			TextView content = (TextView) view.findViewById(R.id.festival);
			TextView alarm = (TextView) view.findViewById(R.id.festivaldate);
			ImageView flag = (ImageView) view.findViewById(R.id.festivalflag);

			content.setText(aFestivalitem.getname()); // 显示相关的信息
			alarm.setText(aFestivalitem.getdate());
			int flagtemp = aFestivalitem.getflag();
			if (flagtemp == 1) {
				flag.setImageResource(R.drawable.alarm); // 为闹钟设置不同的图片来区分是否有闹钟
			} else {
				flag.setImageResource(R.drawable.disalarm);
			}
			view.setTag(aFestivalitem);
			return view;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Festivalitems.size(); // 返回集合中元素的个数
		}

	}

	// 刷新列表
	public void refresh() {
		getdata();
		adapter.notifyDataSetChanged();
	}

	// 设置闹钟
	public void setalarm() {

		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		int requestCode = 0;
		// ------------------------------获取数据
		String table = "festival";
		String[] columns = { "name", "date", "flag", "dataid" };
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = database.query(table, columns, selection,
				selectionArgs, null, null, null);
		cursor.moveToPosition(cursor.getCount() - 1);// 移动到最后一行
		int idColumnIndex = cursor.getColumnIndex("dataid");
		int idValue = cursor.getInt(idColumnIndex); // 得到最后一行的id
		int contentColumnIndex = cursor.getColumnIndex("name");
		String contentValue = cursor.getString(contentColumnIndex); // 得到最后一行的内容
		int alarmColumnIndex = cursor.getColumnIndex("date");
		String alarmValue = cursor.getString(alarmColumnIndex); // 得到最后一行的闹铃时间
		requestCode = idValue;
		// ---------------------------------------------------------------------
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(Festival.this, FestivalClockActivity.class);
		intent.putExtra("content", contentValue);// 传递内容
		intent.putExtra("alarmtime", alarmValue);// 传递闹铃时间
		pi = PendingIntent.getActivity(Festival.this, requestCode, intent,
				PendingIntent.FLAG_CANCEL_CURRENT); // 根据id设置不同的闹钟
		Calendar alarmTime = Calendar.getInstance();
		long alarmdatetime = changedatetime(alarmValue); // 将时间转换为long型
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmdatetime, pi);
		Toast.makeText(getApplicationContext(), "闹钟设置好了", Toast.LENGTH_LONG)
				.show();
		database.close();// 关闭数据库连接
	}// 闹钟设置完毕

	// 取消闹钟
	public void unalarm(int id) {
		int requestCode = id;
		Intent intent = new Intent(Festival.this, ClockActivity.class);
		pi = PendingIntent.getActivity(Festival.this, requestCode, intent,
				PendingIntent.FLAG_CANCEL_CURRENT); // 根据id设置不同的闹钟

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		Toast.makeText(getApplicationContext(), "闹钟取消了", Toast.LENGTH_LONG)
				.show();
		database.close();// 关闭数据库连接
	}

	// 点击事件设置闹钟
	public void setclickalarm(int id) {
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		int requestCode = 0;
		// ------------------------------
		String table = "festival";
		String[] columns = { "name", "date", "flag", "dataid" };
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = database.query(table, columns, selection,
				selectionArgs, null, null, null);
		cursor.moveToPosition(id);// 移动到id行
		int idValue = id;
		int contentColumnIndex = cursor.getColumnIndex("name");
		String contentValue = cursor.getString(contentColumnIndex); // 得到id行的内容
		int alarmColumnIndex = cursor.getColumnIndex("date");
		String alarmValue = cursor.getString(alarmColumnIndex); // 得到id行的闹铃时间
		requestCode = idValue;
		// ---------------------------------------------------------------------
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(Festival.this, ClockActivity.class);
		intent.putExtra("content", contentValue);// 传递内容
		intent.putExtra("alarmtime", alarmValue);// 传递闹铃时间
		pi = PendingIntent.getActivity(Festival.this, requestCode, intent,
				PendingIntent.FLAG_CANCEL_CURRENT); // 根据id设置不同的闹钟
		Calendar alarmTime = Calendar.getInstance();
		long alarmdatetime = changedatetime(alarmValue); // 将时间转换为long型
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmdatetime, pi);
		Toast.makeText(getApplicationContext(), "闹钟设置好了", Toast.LENGTH_LONG)
				.show();
		database.close();// 关闭数据库连接
	}// 闹钟设置完毕

	// 转换时间
	public long changedatetime(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date;

		try {
			date = format.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected void onDestroy() {
		if (database.isOpen()) {
			database.close();// 关闭数据库连接
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		refresh();// 刷新列表
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (database.isOpen()) {
			database.close();// 关闭数据库连接
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (database.isOpen()) {
			database.close();// 关闭数据库连接
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		refresh();// 刷新列表
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		super.onRestart();
	}

	@Override
	protected void onStart() {
		refresh();// 刷新列表
		super.onStart();
	}
}
