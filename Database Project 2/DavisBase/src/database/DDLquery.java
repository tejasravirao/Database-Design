package database;

import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.*;

public class DDLquery {
	
	private static RandomAccessFile davisTblsmetalog;
	private static RandomAccessFile daviscolsmetalog;
	public static final int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	
	
		public static void show()
		{
			String[] cols = {"table_name"};
			String[] cmp = new String[0];
			String table = "davisbase_tables";
			
			VDLquery.select("data\\catalog\\"+table+".tbl",table, cols, cmp);
		}
		
		public static void createTable(String table, String[] col)
		{
			try{	
				
				File catalog = new File("data\\"+DavisBase.samplebase+"\\"+table);
				
				catalog.mkdir();
				RandomAccessFile file = new RandomAccessFile("data\\"+DavisBase.samplebase+"\\"+table+"\\"+table+".tbl", "rw");
				file.setLength(pageSize);
				file.seek(0);
				file.writeByte(0x0D);
				file.close();
				
				
				file = new RandomAccessFile("data\\catalog\\davisbase_tables.tbl", "rw");
				int numPages = Formtbls.pages(file);
				int page = 1;
				for(int p = 1; p <= numPages; p++)
				{
					int rm = BplPages.Rtr_Right_Extr(file, p);
					if(rm == 0)
						page = p;
				}
				int[] keyArray = BplPages.RetrievekeyVals(file, page);
				int l = keyArray[0];
				for(int i = 0; i < keyArray.length; i++)
					if(l < keyArray[i])
						l = keyArray[i];
				file.close();
				String[] values = {Integer.toString(l+1), DavisBase.samplebase+"."+table};
				DMLquery.insertInto("davisbase_tables", values);

				RandomAccessFile cfile = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] col_name = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				String[] cmp = {};
				FilterFunc.Nulfilter4(cfile, cmp, col_name, buffer);
				l = buffer.content.size();

				for(int i = 0; i < col.length; i++){
					l = l + 1;
					String[] placetk = col[i].split(" ");
					String n = "YES";
					if(placetk.length > 2)
						n = "NO";
					String colname1 = placetk[0];
					String dt = placetk[1].toUpperCase();
					String pos = Integer.toString(i+1);
					String[] v = {Integer.toString(l), DavisBase.samplebase+"."+table, colname1, dt, pos, n};
					DMLquery.insertInto("davisbase_columns", v);
				}
				cfile.close();
				file.close();
			}catch(Exception e){
				System.out.println("Error at createTable");
				e.printStackTrace();
			}
		}
		
	
		
		public static void drop(String table,String db)
		{
			try{
				
				RandomAccessFile file = new RandomAccessFile("data\\catalog\\davisbase_tables.tbl", "rw");
				int numPages = Formtbls.pages(file);
				for(int page = 1; page <= numPages; page ++)
				{
					file.seek((page-1)*pageSize);
					byte type = file.readByte();
					if(type == 0x05)
						continue;
					else{
						short[] cells = BplPages.RetrieveAllCells(file, page);
						int i = 0;
						for(int j = 0; j < cells.length; j++){
							long loc = BplPages.Rtr_Loc_Cell(file, page, j);
							String[] pl = Formtbls.Getcontentpl(file, loc);
							String tb = pl[1];
							if(!tb.equals(DavisBase.samplebase+"."+table)){
								BplPages.Put_Cell_ofst(file, page, i, cells[j]);
								i++;
							}
						}
						BplPages.Put_Cell_num(file, page, (byte)i);
					}
				}

				
				file = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
				numPages = Formtbls.pages(file);
				for(int page = 1; page <= numPages; page ++){
					file.seek((page-1)*pageSize);
					byte type = file.readByte();
					if(type == 0x05)
						continue;
					else{
						short[] cells = BplPages.RetrieveAllCells(file, page);
						int i = 0;
						for(int j = 0; j < cells.length; j++){
							long loc = BplPages.Rtr_Loc_Cell(file, page, j);
							String[] pl = Formtbls.Getcontentpl(file, loc);
							String tb = pl[1];
							if(!tb.equals(DavisBase.samplebase+"."+table))
							{
								BplPages.Put_Cell_ofst(file, page, i, cells[j]);
								i++;
							}
						}
						BplPages.Put_Cell_num(file, page, (byte)i);
					}
				}
				file.close();
				
				File dropTable = new File("data\\"+db+"\\"+table);
				String[] listFiles = dropTable.list();
				for(String f:listFiles){
					File dropFile = new File("data\\"+db+"\\"+table,f);
					dropFile.delete();
				}
				dropTable = new File("data\\"+db, table); 
				dropTable.delete();
			}
			catch(Exception e)
			{
				System.out.println("Error at drop");
				System.out.println(e);
			}

		}	
}
