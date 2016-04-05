package com.arjun.app.memorysisya;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager
{
	private static DatabaseManager instance;
	private static DatabaseHelper mDatabaseHelper;

	public static synchronized void initializeInstance(DatabaseHelper helper)
	{
		if (instance == null)
		{
			instance = new DatabaseManager();
			mDatabaseHelper = helper;
		}
	}

	public static synchronized DatabaseManager getInstance()
	{
		if (instance == null)
		{
			throw new IllegalStateException(DatabaseManager.class.getSimpleName()
					+ " is not initialized, call initialize(..) method first.");
		}

		return instance;
	}

	public SQLiteDatabase getDatabase()
	{
		return mDatabaseHelper.getWritableDatabase();
	}
}
