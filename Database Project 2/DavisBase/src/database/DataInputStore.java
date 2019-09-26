package database;

import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.*;

public class DataInputStore {
	
	private static RandomAccessFile davisTblsmetalog;
	private static RandomAccessFile daviscolsmetalog;
	public static final int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	
	public static void StartinputData() 
	{

		try {
			File catalog = new File("data\\catalog");
			
			String[] tblprevfiles;
			tblprevfiles = catalog.list();
			for (int i=0; i<tblprevfiles.length; i++) 
			{
				File prevfile1 = new File(catalog, tblprevfiles[i]); 
				prevfile1.delete();
			}
		}
		catch (SecurityException se) 
		{
			System.out.println("Unable to create catalog directory :"+se);
			
		}

		try {
			int size1=24;
			int size2=25;
			
			davisTblsmetalog = new RandomAccessFile("data\\catalog\\davisbase_tables.tbl", "rw");
			davisTblsmetalog.setLength(pageSize);
			davisTblsmetalog.seek(0);
			davisTblsmetalog.write(0x0D);
			davisTblsmetalog.write(0x02);
			int[] offset=new int[2];
			
			offset[0]=pageSize-size1;
			offset[1]=offset[0]-size2;
			davisTblsmetalog.writeShort(offset[1]);
			davisTblsmetalog.writeInt(0);
			davisTblsmetalog.writeInt(10);
			davisTblsmetalog.writeShort(offset[1]);
			davisTblsmetalog.writeShort(offset[0]);
			davisTblsmetalog.seek(offset[0]);
			davisTblsmetalog.writeShort(20);
			davisTblsmetalog.writeInt(1); 
			davisTblsmetalog.writeByte(1);
			davisTblsmetalog.writeByte(28);
			davisTblsmetalog.writeBytes("davisbase_tables");
			davisTblsmetalog.seek(offset[1]);
			davisTblsmetalog.writeShort(21);
			davisTblsmetalog.writeInt(2); 
			davisTblsmetalog.writeByte(1);
			davisTblsmetalog.writeByte(29);
			davisTblsmetalog.writeBytes("davisbase_columns");
		}
		catch (Exception e) 
		{
			System.out.println("Unable to create the database_tables file");
			System.out.println(e);
		}
		
		try 
		{
			daviscolsmetalog = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
			daviscolsmetalog.setLength(pageSize);
			daviscolsmetalog.seek(0);       
			daviscolsmetalog.writeByte(0x0D);
			daviscolsmetalog.writeByte(0x08); 
			int[] offset=new int[10];
			offset[0]=pageSize-43;
			offset[1]=offset[0]-47;
			offset[2]=offset[1]-44;
			offset[3]=offset[2]-48;
			offset[4]=offset[3]-49;
			offset[5]=offset[4]-47;
			offset[6]=offset[5]-57;
			offset[7]=offset[6]-49;
			offset[8]=offset[7]-49;
			daviscolsmetalog.writeShort(offset[8]); 
			daviscolsmetalog.writeInt(0); 
			daviscolsmetalog.writeInt(0); 
			
			for(int i=0;i<9;i++)
				daviscolsmetalog.writeShort(offset[i]);

			
			daviscolsmetalog.seek(offset[0]);
			daviscolsmetalog.writeShort(33); 
			daviscolsmetalog.writeInt(1); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(28);
			daviscolsmetalog.writeByte(17);
			daviscolsmetalog.writeByte(15);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			
			daviscolsmetalog.writeBytes("davisbase_tables"); 
			daviscolsmetalog.writeBytes("rowid"); 
			daviscolsmetalog.writeBytes("INT"); 
			daviscolsmetalog.writeByte(1); 
			daviscolsmetalog.writeBytes("NO"); 
			
			daviscolsmetalog.seek(offset[1]);
			daviscolsmetalog.writeShort(39); 
			daviscolsmetalog.writeInt(2); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(28);
			daviscolsmetalog.writeByte(22);
			daviscolsmetalog.writeByte(16);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
		
			daviscolsmetalog.writeBytes("davisbase_tables"); 
			daviscolsmetalog.writeBytes("table_name");   
			daviscolsmetalog.writeBytes("TEXT"); 
			daviscolsmetalog.writeByte(2); 
			daviscolsmetalog.writeBytes("NO");
			
			
			daviscolsmetalog.seek(offset[2]);
			daviscolsmetalog.writeShort(34); 
			daviscolsmetalog.writeInt(3); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(29);
			daviscolsmetalog.writeByte(17);
			daviscolsmetalog.writeByte(15);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			
			daviscolsmetalog.writeBytes("davisbase_columns");
			daviscolsmetalog.writeBytes("rowid");
			daviscolsmetalog.writeBytes("INT");
			daviscolsmetalog.writeByte(1);
			daviscolsmetalog.writeBytes("NO");
			
			
			daviscolsmetalog.seek(offset[3]);
			daviscolsmetalog.writeShort(40); 
			daviscolsmetalog.writeInt(4); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(29);
			daviscolsmetalog.writeByte(22);
			daviscolsmetalog.writeByte(16);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			
			daviscolsmetalog.writeBytes("davisbase_columns");
			daviscolsmetalog.writeBytes("table_name");
			daviscolsmetalog.writeBytes("TEXT");
			daviscolsmetalog.writeByte(2);
			daviscolsmetalog.writeBytes("NO");
			

			
			daviscolsmetalog.seek(offset[4]);
			daviscolsmetalog.writeShort(41); 
			daviscolsmetalog.writeInt(5); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(29);
			daviscolsmetalog.writeByte(23);
			daviscolsmetalog.writeByte(16);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			
			daviscolsmetalog.writeBytes("davisbase_columns");
			daviscolsmetalog.writeBytes("column_name");
			daviscolsmetalog.writeBytes("TEXT");
			daviscolsmetalog.writeByte(3);
			daviscolsmetalog.writeBytes("NO");
			
			
			daviscolsmetalog.seek(offset[5]);
			daviscolsmetalog.writeShort(39); 
			daviscolsmetalog.writeInt(6); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(29);
			daviscolsmetalog.writeByte(21);
			daviscolsmetalog.writeByte(16);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			
			daviscolsmetalog.writeBytes("davisbase_columns");
			daviscolsmetalog.writeBytes("data_type");
			daviscolsmetalog.writeBytes("TEXT");
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeBytes("NO");
			
			
			daviscolsmetalog.seek(offset[6]);
			daviscolsmetalog.writeShort(49);
			daviscolsmetalog.writeInt(7); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(29);
			daviscolsmetalog.writeByte(28);
			daviscolsmetalog.writeByte(19);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			
			daviscolsmetalog.writeBytes("davisbase_columns");
			daviscolsmetalog.writeBytes("ordinal_position");
			daviscolsmetalog.writeBytes("TINYINT");
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeBytes("NO");
		
			
			daviscolsmetalog.seek(offset[7]);
			daviscolsmetalog.writeShort(41); 
			daviscolsmetalog.writeInt(8); 
			daviscolsmetalog.writeByte(5);
			daviscolsmetalog.writeByte(29);
			daviscolsmetalog.writeByte(23);
			daviscolsmetalog.writeByte(16);
			daviscolsmetalog.writeByte(4);
			daviscolsmetalog.writeByte(14);
			daviscolsmetalog.writeBytes("davisbase_columns");
			daviscolsmetalog.writeBytes("is_nullable");
			daviscolsmetalog.writeBytes("TEXT");
			daviscolsmetalog.writeByte(6);
			daviscolsmetalog.writeBytes("NO");
		}
		catch (Exception e) 
		{
			System.out.println("Unable to create the database_columns file");
			System.out.println(e);
		}
	}
}
