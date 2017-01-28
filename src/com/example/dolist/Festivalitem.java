package com.example.dolist;

public class Festivalitem {
	public String name = null;
	public String date = null;
	public int flag = 0;

	public Festivalitem(String name, String date, int flag) {
		this.name = name;
		this.date = date;
		this.flag = flag;

		// TODO Auto-generated constructor stub
	}

	public String getname() {
		return name;
	}

	public String getdate() {
		return date;
	}

	public int getflag() {
		return flag;
	}

}
