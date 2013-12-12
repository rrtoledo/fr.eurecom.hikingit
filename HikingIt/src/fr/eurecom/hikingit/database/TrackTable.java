package fr.eurecom.hikingit.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrackTable {

	// Database table
	public static final String TABLE_TRACK = "track";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DIFFICULTY = "difficulty";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_SUMMARY = "summary";
	public static final String COLUMN_NBCOORDS = "nbcoords";
	public static final String COLUMN_DURATION = "duration";
	public static final String COLUMN_STARTX = "startX";
	public static final String COLUMN_STARTY = "startY";
	public static final String COLUMN_COORDS = "coords";
	public static final String COLUMN_FLAGS = "flags";


	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " + TABLE_TRACK
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DIFFICULTY + " text not null, " + COLUMN_TITLE
			+ " text not null, " + COLUMN_SUMMARY + " text not null, " 
			+ COLUMN_NBCOORDS + " text not null, " + COLUMN_DURATION + " text not null, "
			+ COLUMN_STARTX + "double not null, " + COLUMN_STARTY + "double not null, " 
			+ COLUMN_COORDS + " text not null, " + COLUMN_FLAGS + "text not null);";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TrackTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
		onCreate(database);
	}
}