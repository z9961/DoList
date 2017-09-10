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
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

public class List extends Activity {
	private ListView listView;
	int flag = 0; // 是否有闹钟
	String text; // 输入的内容
	String datetime;// 日期+时间
	public int year = 0;
	public int hourOfDay = 0;
	public int minute = 0;
	public int monthOfYear = 0;
	public int dayOfMonth = 0;
	public int year2 = 0;
	public int hourOfDay2 = 0;
	public int minute2 = 0;
	public int monthOfYear2 = 0;
	public int dayOfMonth2 = 0;
	SQLiteDatabase database;// 数据库连接
	public ArrayList<Listitem> Listitems; // 存放listview用到的数据
	public BaseAdapter adapter;
	private AlarmManager alarmManager; // 闹钟
	private PendingIntent pi;
	private int mSelectedPosition; // 记录被选择的位置
	public Intent editIntent; // 要编辑的intent
	public int editidValue; // 要编辑的id
	public static List alist;
	int setdatetimeflag = 0;// 作为是否选择了日期的标志

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		alist = this;

		listView = (ListView) findViewById(R.id.listview);
		ImageButton button_cal = (ImageButton) findViewById(R.id.btn_cel);
		ImageButton button_add = (ImageButton) findViewById(R.id.btn_add);
		final EditText editText = (EditText) findViewById(R.id.editText);

		database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库

		// 时间选择
		button_cal.setOnClickListener(new OnClickListener() {

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
						List.this.year = year;
						List.this.monthOfYear = monthOfYear2;
						List.this.dayOfMonth = dayOfMonth;

					}
				};

				// 选择时间
				TimePickerDialog.OnTimeSetListener timeBack = new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						Toast.makeText(view.getContext(),
								"时间是:" + hourOfDay + "时" + minute + "分",
								Toast.LENGTH_SHORT).show();
						List.this.hourOfDay = hourOfDay;
						List.this.minute = minute;
					}

				};
				hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);// 输出的是带时区的时间
				minute = calendar.get(Calendar.MINUTE);
				boolean is24HourView = true; // 设为24小时制
				TimePickerDialog timedialog = new TimePickerDialog(context,
						timeBack, hourOfDay, minute, is24HourView);
				timedialog.show();

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
				setdatetimeflag = 1; // 完成日期时间选择
			}
		});

		// 添加待办事项
		button_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text = editText.getText().toString(); // 获取输入的字符串

				if (text.isEmpty()) {
					Toast.makeText(v.getContext(), "想好做什么再来吧 ¬_¬",
							Toast.LENGTH_SHORT).show();
				} else if (setdatetimeflag == 0) {
					Toast.makeText(v.getContext(), "还没有选择日期和时间！",
							Toast.LENGTH_SHORT).show();
				} else {
					// 是否设置闹钟
					AlertDialog.Builder builder = new AlertDialog.Builder(
							List.this);
					builder.setTitle("闹钟选项");
					builder.setMessage("要设置闹钟吗？");
					builder.setPositiveButton("需要^.^",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									flag = 1;
									add();
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
									add();
								}
							});

					builder.show();
					// 弹出对话框

				}// 第一个if
			}// onclick
		});// btn listener

		// listview设置
		Listitems = new ArrayList<Listitem>();
		getdata();
		adapter = new myadapter();
		listView.setAdapter(adapter);

		// 点击item设置取消闹钟
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(android.widget.AdapterView<?> arg0, View v,
					int arg2, long arg3) {
				if ((!database.isOpen()) || database == null) {
					database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
				}
				String table = "list";
				String[] columns = { "alarm", "flag", "dataid" };// 需要获取的数据
				String selection = null;
				String[] selectionArgs = null;
				Cursor cursor = database.query(table, columns, selection,
						selectionArgs, null, null, null);
				cursor.moveToPosition(arg2); // 移动到要编辑的行
				int idColumnIndex = cursor.getColumnIndex("dataid");
				int alarmColumnIndex = cursor.getColumnIndex("alarm");
				int flagColumnIndex = cursor.getColumnIndex("flag");
				String strtime = cursor.getString(alarmColumnIndex);
				int intflag = cursor.getInt(flagColumnIndex);
				int idValue = cursor.getInt(idColumnIndex); // 得到要编辑行的id
				if (intflag == 1) {
					String unalarmflag = "update list set flag = " + "'" + 0
							+ "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					unalarm(idValue);
					refresh();
				} else {
					String unalarmflag = "update list set flag = " + "'" + 1
							+ "'" + "where dataid = " + "'" + idValue + "'";
					database.execSQL(unalarmflag);
					setclickalarm(arg2);
					refresh();
				}
				database.close();
			};
		});

		// 设置item长按事件
		listView.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("请选择操作：");
				menu.add(0, 0, 0, "编辑");
				menu.add(0, 1, 0, "删除");
			}
		});

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
			if ((!database.isOpen()) || database == null) {
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
			}
			String table = "list";
			String[] columns = { "content", "alarm", "flag", "dataid" };// 需要获取的数据
			String selection = null;
			String[] selectionArgs = null;
			Cursor cursor = database.query(table, columns, selection,
					selectionArgs, null, null, null);
			cursor.moveToPosition(id); // 移动到要编辑的行
			int idColumnIndex = cursor.getColumnIndex("dataid");
			int nameColumnIndex = cursor.getColumnIndex("content");
			int alarmColumnIndex = cursor.getColumnIndex("alarm");
			int flagColumnIndex = cursor.getColumnIndex("flag");
			String strValue = cursor.getString(nameColumnIndex);
			String strtime = cursor.getString(alarmColumnIndex);
			int intflag = cursor.getInt(flagColumnIndex);
			int idValue = cursor.getInt(idColumnIndex); // 得到要编辑行的id

			Intent editIntent = new Intent(List.this, EditActivity.class);

			editIntent.putExtra("content", strValue);// 传递内容
			editIntent.putExtra("alarmtime", strtime);// 传递时间
			editIntent.putExtra("id", idValue);// 传递id
			editIntent.putExtra("flag", intflag); // flag

			List.this.startActivity(editIntent);// 跳转到编辑界面
			refresh();
			return true;
		case 1:
			// 删除数据操作
			if ((!database.isOpen()) || database == null) {
				database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
			}
			String table2 = "list";
			String[] columns2 = { "dataid" };
			String selection2 = null;
			String[] selectionArgs2 = null;
			Cursor cursor2 = database.query(table2, columns2, selection2,
					selectionArgs2, null, null, null);
			cursor2.moveToPosition(id); // 移动到要删除的行
			int idColumnIndex2 = cursor2.getColumnIndex("dataid");
			int idValue2 = cursor2.getInt(idColumnIndex2); // 得到要删除行的id
			String deletesql = "DELETE from list WHERE dataid = " + idValue2;
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
		// ________________________________________________________
		// 整合日期时间
		if (dayOfMonth == 0) {
			Toast.makeText(getApplicationContext(), "还没有选择日期！",
					Toast.LENGTH_SHORT).show();
		} else {

			datetime = year + "-" + monthOfYear + "-" + dayOfMonth + " "
					+ hourOfDay + ":" + minute + ":" + "00";

			// 向数据库插入数据

			try {

				// create table IF NOT EXISTS
				// list(content text not
				// null,alarm time,flag int)
				String sql = "INSERT INTO list(content,alarm,flag) values("
						+ "'" + text + "', '" + datetime + "', '" + flag + "'"
						+ ")";
				database.execSQL(sql);
				refresh();// 刷新列表
				Toast.makeText(getApplicationContext(), "待办事项保存好了",
						Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "有些地方出错了",
						Toast.LENGTH_SHORT).show();
				Log.i("SQL", text + "," + datetime + ',' + flag);
			}
		}// 整合时间if
			// ________________________________________________________
	}

	// 获取cursor的数据
	public void getdata() {
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		Listitems.clear();
		// 新建游标用于获取list
		String table = "list";
		String[] columns = { "content", "alarm", "flag" };// 需要获取的数据
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = database.query(table, columns, selection,
				selectionArgs, null, null, null);
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				// 根据列名获取列索引
				int nameColumnIndex = cursor.getColumnIndex("content");
				int alarmColumnIndex = cursor.getColumnIndex("alarm");
				int flagColumnIndex = cursor.getColumnIndex("flag");
				String strValue = cursor.getString(nameColumnIndex);
				String strtime = cursor.getString(alarmColumnIndex);
				int intflag = cursor.getInt(flagColumnIndex);
				Listitem listitem = new Listitem(strValue, strtime, intflag);
				Listitems.add(listitem);
			}
		}
	}

	// 逐行取出数据
	public class myadapter extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(getBaseContext(), R.layout.item, null);
			} else {
				view = convertView;
			}

			// 从Listitems中取出一行数据，position相当于数组下标,可以实现逐行取数据
			Listitem alistitem = Listitems.get(position);
			TextView content = (TextView) view.findViewById(R.id.content);
			TextView alarm = (TextView) view.findViewById(R.id.time);
			ImageView flag = (ImageView) view.findViewById(R.id.flag);
			content.setText(alistitem.getcontent());
			alarm.setText(alistitem.getalarm());
			int flagtemp = alistitem.getflag();
			if (flagtemp == 1) {
				flag.setImageResource(R.drawable.alarm);
			} else {
				flag.setImageResource(R.drawable.disalarm);
			}
			view.setTag(alistitem);
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
			return Listitems.size();
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
		// ------------------------------
		String table = "list";
		String[] columns = { "content", "alarm", "flag", "dataid" };
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = database.query(table, columns, selection,
				selectionArgs, null, null, null);
		cursor.moveToPosition(cursor.getCount() - 1);// 移动到最后一行
		int idColumnIndex = cursor.getColumnIndex("dataid");
		int idValue = cursor.getInt(idColumnIndex); // 得到最后一行的id
		int contentColumnIndex = cursor.getColumnIndex("content");
		String contentValue = cursor.getString(contentColumnIndex); // 得到最后一行的内容
		int alarmColumnIndex = cursor.getColumnIndex("alarm");
		String alarmValue = cursor.getString(alarmColumnIndex); // 得到最后一行的闹铃时间
		requestCode = idValue;
		// ---------------------------------------------------------------------
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(List.this, ClockActivity.class);
		intent.putExtra("content", contentValue);// 传递内容
		intent.putExtra("alarmtime", alarmValue);// 传递闹铃时间
		pi = PendingIntent.getActivity(List.this, requestCode, intent,
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
		// database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		int requestCode = id;
		Intent intent = new Intent(List.this, ClockActivity.class);
		pi = PendingIntent.getActivity(List.this, requestCode, intent,
				PendingIntent.FLAG_CANCEL_CURRENT); // 根据id设置不同的闹钟

		// And cancel the alarm.
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
		String table = "list";
		String[] columns = { "content", "alarm", "flag", "dataid" };
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = database.query(table, columns, selection,
				selectionArgs, null, null, null);
		cursor.moveToPosition(id);// 移动到id行
		int idValue = id;
		int contentColumnIndex = cursor.getColumnIndex("content");
		String contentValue = cursor.getString(contentColumnIndex); // 得到id行的内容
		int alarmColumnIndex = cursor.getColumnIndex("alarm");
		String alarmValue = cursor.getString(alarmColumnIndex); // 得到id行的闹铃时间
		requestCode = idValue;
		// ---------------------------------------------------------------------
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(List.this, ClockActivity.class);
		intent.putExtra("content", contentValue);// 传递内容
		intent.putExtra("alarmtime", alarmValue);// 传递闹铃时间
		pi = PendingIntent.getActivity(List.this, requestCode, intent,
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (database.isOpen()) {
			database.close();// 关闭数据库连接
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		refresh();// 刷新列表
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (database.isOpen()) {
			database.close();// 关闭数据库连接
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (database.isOpen()) {
			database.close();// 关闭数据库连接
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		refresh();// 刷新列表
		if ((!database.isOpen()) || database == null) {
			database = openOrCreateDatabase("db.db", MODE_PRIVATE, null);// 打开数据库
		}
		super.onRestart();
	}
}
