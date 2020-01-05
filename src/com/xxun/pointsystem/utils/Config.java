package com.xxun.pointsystem.utils;

import android.util.Log;

public class Config {
	private static final String TAG = "Config";
	private static final int TEST_TOTAL = 35;
	static String[][] testitem = new String[TEST_TOTAL][2];
	static int count = 0;
	static int i;

	public Config() {
	}

	public static void Clear_testitem() {
		for (i = 0; i < TEST_TOTAL; i++) {
			testitem[i][0] = null;
			testitem[i][1] = null;
		}
		count = 0;
	}

	public static int Set_item(int sequence, String name, String classname) {
		if (sequence > TEST_TOTAL)
			return -1;

		testitem[sequence][0] = name;
		testitem[sequence][1] = classname;
		if (count < sequence)
			count = sequence;
		return sequence;
	}

	public static int get_count() {
		Log.d(TAG, "get test count = " + (count + 1));
		return count + 1;
	}

	public static String get_item_name(int item_num) {
		return testitem[item_num][0];
	}

	public static String get_item_classname(int item_num) {
		return testitem[item_num][1];
	}
}
