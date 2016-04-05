package com.arjun.app.memorysisya;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{

	private Context mContext;
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "background_kill";
    private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER, " +
                "package_name TEXT, " +
                "app_name TEXT);";

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(context, "sisya.db", factory, DATABASE_VERSION);
		mContext = context;
	}

	public DatabaseHelper(Context context)
	{
		super(context, "sisya.db", null, DATABASE_VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

}
