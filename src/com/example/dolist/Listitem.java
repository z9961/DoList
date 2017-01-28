package com.example.dolist;

public class Listitem {
	public String content = null;
	public String alarm = null;
	public int flag = 0;

	public Listitem(String content, String alarm, int flag) {
		this.content = content;
		this.alarm = alarm;
		this.flag = flag;

		// TODO Auto-generated constructor stub
	}

	public String getcontent() {
		return content;
	}

	public String getalarm() {
		return alarm;
	}

	public int getflag() {
		return flag;
	}
}
