package database;

import java.io.RandomAccessFile;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;


public class VDLquery {
	
	private static RandomAccessFile davisTblsmetalog;
	private static RandomAccessFile daviscolsmetalog;
	public static final int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	
	
	public static void select(String file, String table, String[] cols, String[] compare)
	{
		try
		{
			Buffer buffer = new Buffer();
			String[] col_name = Formtbls.obtcolName(table);
			String[] type = Formtbls.ObtDatatype(table);

			RandomAccessFile randFile = new RandomAccessFile(file, "rw");
			FilterFunc.Selefilter5(randFile, compare, col_name, type, buffer);
			buffer.View(cols);
			randFile.close();
		}
		catch(Exception e)
		{
			System.out.println("Error at select");
			System.out.println(e);
		}
	}

}
