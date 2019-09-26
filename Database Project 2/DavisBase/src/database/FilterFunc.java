package database;

import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.*;

public class FilterFunc {

	
	public static void Selefilter5(RandomAccessFile file, String[] compare, String[] col_name, String[] type, Buffer buffersamp1){
		try{
			int numofpgs = Formtbls.pages(file);
			
			for(int page = 1; page <= numofpgs; page++)
			{
				file.seek((page-1)*Formtbls.pageSize);
				byte type_ofpage = file.readByte();
				if(type_ofpage == 0x05)
					continue;
				else{
					byte num_ofcells = BplPages.Rtr_Cell_num(file, page);

					for(int i=0; i < num_ofcells; i++){
						long loc = BplPages.Rtr_Loc_Cell(file, page, i);
						file.seek(loc+2); 
						int rowid = file.readInt(); 
						int num_cols = new Integer(file.readByte()); 

						String[] payload = Formtbls.Getcontentpl(file, loc);

						for(int j=0; j < type.length; j++)
							if(type[j].equals("DATE") || type[j].equals("DATETIME"))
								payload[j] = "'"+payload[j]+"'";
					
						boolean check = rowidcomparison(payload, rowid, compare, col_name);

						
						for(int j=0; j < type.length; j++)
							if(type[j].equals("DATE") || type[j].equals("DATETIME"))
								payload[j] = payload[j].substring(1, payload[j].length()-1);

						if(check)
							buffersamp1.add(rowid, payload);
					}
				}
			}

			buffersamp1.col_name = col_name;
			buffersamp1.varfrmt = new int[col_name.length];

		}
		catch(Exception e)
		{
			System.out.println("Error at select filter");
			e.printStackTrace();
		}

	}

	
	public static void Nulfilter4(RandomAccessFile file, String[] compare, String[] col_name, Buffer buffersamp2){
		try{
			int numofpgs = Formtbls.pages(file);
			
			for(int page = 1; page <= numofpgs; page++){
				file.seek((page-1)*Formtbls.pageSize);
				byte type_ofpage = file.readByte();
				if(type_ofpage == 0x05)
					continue;
				else{
					byte num_ofcells = BplPages.Rtr_Cell_num(file, page);

					for(int i=0; i < num_ofcells; i++){
						long loc = BplPages.Rtr_Loc_Cell(file, page, i);
						file.seek(loc+2); 
						int rowid = file.readInt(); 
						int num_cols = new Integer(file.readByte()); 
						String[] payload = Formtbls.Getcontentpl(file, loc);

						boolean check = rowidcomparison(payload, rowid, compare, col_name);
						if(check)
							buffersamp2.add(rowid, payload);
					}
				}
			}

			buffersamp2.col_name = col_name;
			buffersamp2.varfrmt = new int[col_name.length];

		}catch(Exception e){
			System.out.println("Error at filter");
			e.printStackTrace();
		}

	}
	
	public static boolean rowidcomparison(String[] payload, int rowid, String[] compare, String[] col_name)
	{

		boolean check = false;
		if(compare.length == 0)
		{
			check = true;
		}
		else{
			int colPos = 1;
			for(int i = 0; i < col_name.length; i++){
				if(col_name[i].equals(compare[0])){
					colPos = i + 1;
					break;
				}
			}
			String ineq = compare[1];
			String val = compare[2];
			if(colPos == 1)
			{
				switch(ineq)
				{
					case "=": if(rowid == Integer.parseInt(val)) 
								check = true;
							  else
							  	check = false;
							  break;
					case ">": if(rowid > Integer.parseInt(val)) 
								check = true;
							  else
							  	check = false;
							  break;
					case "<": if(rowid < Integer.parseInt(val)) 
								check = true;
							  else
							  	check = false;
							  break;
					case ">=": if(rowid >= Integer.parseInt(val)) 
								check = true;
							  else
							  	check = false;	
							  break;
					case "<=": if(rowid <= Integer.parseInt(val)) 
								check = true;
							  else
							  	check = false;	
							  break;
					case "<>": if(rowid != Integer.parseInt(val))  
								check = true;
							  else
							  	check = false;	
							  break;						  							  							  							
				}
			}else{
				if(val.equals(payload[colPos-1]))
					check = true;
				else
					check = false;
			}
		}
		return check;
	}
	
}
