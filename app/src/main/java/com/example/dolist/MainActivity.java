package com.example.dolist;

import android.R.color;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity {

	private PopupMenu pm;

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageButton btn_menu = (ImageButton) findViewById(R.id.btn_menu);   

		btn_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null == pm) {
					pm = new PopupMenu(v.getContext(), v);
					pm.inflate(R.menu.main);

					pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

						public boolean onMenuItemClick(MenuItem item) {
							String text = null;
							switch (item.getItemId()) {
							case R.id.menu_about:
								Intent AboutIntent = new Intent(
										MainActivity.this, About.class);
								MainActivity.this.startActivity(AboutIntent);// 跳转到About
								break;
							default:
								break;
							}
							return true;
						}
					});
				}
				pm.show();

			}
		});

		Intent intent = new Intent();
		intent.setClass(this, List.class);
		Intent intent2 = new Intent();
		intent2.setClass(this, Festival.class);
		TabHost tabHost = getTabHost(); // 获取选项卡
		TabSpec tabSpec1 = tabHost.newTabSpec("spec1"); //
		tabSpec1.setIndicator("待办清单").setContent(intent);
		tabHost.addTab(tabSpec1);
		TabSpec tabSpec2 = tabHost.newTabSpec("spec2");
		tabSpec2.setIndicator("重要节日").setContent(intent2);
		tabHost.addTab(tabSpec2);

		changeTabBackGround();// 先设置一次背景颜色

		// 改变选项卡的背景颜色和字体大小
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {
				changeTabBackGround();
			}
		});

	}

	private void changeTabBackGround() { // 改变选项卡的颜色
		// 得到当前选中选项卡的索引
		int index = getTabHost().getCurrentTab();
		// 调用tabhost中的getTabWidget()方法得到TabWidget
		TabWidget tabWidget = getTabHost().getTabWidget();
		// 得到选项卡的数量
		int count = tabWidget.getChildCount();
		// 循环判断，只有点中的索引值改变背景颜色，其他的则恢复未选中的颜色
		for (int i = 0; i < count; i++) {
			View view = tabWidget.getChildAt(i);
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setTextSize(20);
			if (index == i) {
				view.setBackgroundResource(color.holo_blue_dark);
			} else {
				view.setBackgroundResource(color.holo_blue_light);
			}
		}
	}

	@Override
	public void onBackPressed() {// 改写返回键,2秒内连续按2次返回键，就退出应用
		long curTime = System.currentTimeMillis();
		if (curTime - exitTime <= 2000) {
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "再按一次退出DoList",
					Toast.LENGTH_SHORT).show();
			exitTime = curTime;
		}
	}

}
