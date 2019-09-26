package database;

import java.io.RandomAccessFile;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;

public class DMLquery {
	
	private static RandomAccessFile davisTblsmetalog;
	private static RandomAccessFile daviscolsmetalog;
	public static final int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	
	public static void insertInto(RandomAccessFile file, String table, String[] values)
	{
		String[] dtype = Formtbls.ObtDatatype(table);
		String[] nullable = Formtbls.chknullablecol(table);

		for(int i = 0; i < nullable.length; i++)
			if(values[i].equals("null") && nullable[i].equals("NO")){
				System.out.println("NULL value constraint violation");
				System.out.println();
				return;
			}


		int key = new Integer(values[0]);
		int page = Formtbls.Keyfind(file, key);
		if(page != 0)
			if(BplPages.Iskey_present(file, page, key))
			{
				System.out.println("Unique key constraint violation");
				System.out.println();
				return;
			}
		if(page == 0)
			page = 1;


		byte[] varsc3 = new byte[dtype.length-1];
		short plSize = (short) Formtbls.EvContentSize(table, values, varsc3);
		int cellSize = plSize + 6;
		int offset = BplPages.Cal_Space_Leaf(file, page, cellSize);

		if(offset != -1)
		{
			BplPages.Put_leaf_Cell(file, page, offset, plSize, key, varsc3, values,table);
			
		}
		else
		{
			BplPages.Dividelf2(file, page);
			insertInto(file, table, values);
		}
	}

	public static void insertInto(String table, String[] values)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile("data\\catalog\\"+table+".tbl", "rw");
			insertInto(file, table, values);
			file.close();

		}
		catch(Exception e)
		{
			System.out.println("Error in inserting the data");
			e.printStackTrace();
		}
	}
	
	public static void update(String table, String[] set, String[] cmp)
	{
		try{
			List<Integer> key = new ArrayList<Integer>();
		
			RandomAccessFile file = new RandomAccessFile("data\\"+DavisBase.samplebase+"\\"+table+"\\"+table+".tbl", "rw");
			
			Buffer buffer = new Buffer();
			String[] col_name = Formtbls.obtcolName(table);
			String[] type = Formtbls.ObtDatatype(table);
			FilterFunc.Selefilter5(file, cmp, col_name, type, buffer);
			
			for(String[] i : buffer.content.values()){
				
				for(int j = 0; j < i.length; j++)
					if(buffer.col_name[j].equals(cmp[0]) && i[j].equals(cmp[2])){
						key.add(Integer.parseInt(i[0]));							
						break;
					}
			
			}
				
			
			for(int indKey:key){
			
				int numPages = Formtbls.pages(file);
				int page = 1;
	
				for(int p = 1; p <= numPages; p++)
					if(BplPages.Iskey_present(file, p, indKey)){
						page = p;
					}
				int[] array = BplPages.RetrievekeyVals(file, page);
				int id = 0;
				for(int i = 0; i < array.length; i++)
					if(array[i] == indKey)
						id = i;
				int offset = BplPages.Rtr_Cell_ofst(file, page, id);
				long loc = BplPages.Rtr_Loc_Cell(file, page, id);
				String[] array_s = Formtbls.obtcolName(table);
				int num_cols = array_s.length - 1;
				String[] values = Formtbls.Getcontentpl(file, loc);
	
	
				
				for(int i=0; i < type.length; i++)
					if(type[i].equals("DATE") || type[i].equals("DATETIME"))
						values[i] = "'"+values[i]+"'";
	
	
				
				for(int i = 0; i < array_s.length; i++)
					if(array_s[i].equals(set[0]))
						id = i;
				values[id] = set[2];
	
				
				String[] nullable = Formtbls.chknullablecol(table);
	
				for(int i = 0; i < nullable.length; i++){
					if(values[i].equals("null") && nullable[i].equals("NO")){
						System.out.println("NULL value constraint violation");
						System.out.println();
						return;
					}
				}
	
				byte[] varsc2 = new byte[array_s.length-1];
				int plsize = Formtbls.EvContentSize(table, values, varsc2);
				BplPages.Change_Lf_Cell(file, page, offset, plsize, indKey, varsc2, values,table);
			}
			file.close();

		}catch(Exception e){
			System.out.println("Error at update");
			System.out.println(e);
		}
	}

}
