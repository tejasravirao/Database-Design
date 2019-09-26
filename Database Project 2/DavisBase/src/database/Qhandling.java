package database;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;

public class Qhandling {
	public static void parsequery(String query) 
	{
		String[] querywords = query.split(" ");

		switch (querywords[0]) 
		{
		case "show":
			String obj = querywords[1];
			System.out.println();
			if(obj.equals("tables")){
				DDLquery.show();
			}		
			break;	

		case "create":
			if(querywords[1].equals("table"))
			{
				String create_tbl = querywords[2];	
				String[] var_temp = query.split(create_tbl);
	
				String varcols_temp = var_temp[1].trim();
				String[] crevar_cols = varcols_temp.substring(1, varcols_temp.length() - 1).split(",");
				for (int i = 0; i < crevar_cols.length; i++)
					crevar_cols[i] = crevar_cols[i].trim();
				if (DavisBase.tblpresent(create_tbl)) 
				{
					System.out.println("Table " + create_tbl + " already exists.");
					System.out.println();
					break;
				}
				DDLquery.createTable(create_tbl, crevar_cols);
				System.out.println("Table "+create_tbl+" created successfully.");
				
			}			
			else{
				System.out.println("Please check syntax and values.");				
			}
			
			break;
			
		case "drop":
			if(querywords[1].equals("table")){
				String dropTable = querywords[2];
				if (!DavisBase.tblpresent(dropTable)) 
				{
					System.out.println("Table " + dropTable + " does not exist.");
					System.out.println();
					break;
				}
				DDLquery.drop(dropTable,DavisBase.samplebase);
				System.out.println("Table "+dropTable+" dropped successfully.");
			}
			else{
				System.out.println("Please check syntax and values.");
			}
			System.out.println();
			break;

		case "insert":
			String insertintbl = querywords[4];
			String putvalues = query.split("values")[1].trim();
			putvalues = putvalues.substring(1, putvalues.length() - 1);
			String[] insert_values = putvalues.split(",");
			for (int i = 0; i < insert_values.length; i++)
				insert_values[i] = insert_values[i].trim();
			if (!DavisBase.tblpresent(insertintbl)) {
				System.out.println("Table " + insertintbl + " does not exist.");
				System.out.println();
				break;
			}
			RandomAccessFile file;
			try {
				file = new RandomAccessFile("data\\"+DavisBase.samplebase+"\\"+insertintbl+"\\"+insertintbl+".tbl", "rw");
				DMLquery.insertInto(file,insertintbl, insert_values);
				System.out.println("row inserted");
				file.close();
			} 
			catch (Exception e)
			{
				
				e.printStackTrace();
			}
			
			break;
			

		case "update":
			String updateTable = querywords[1];
			String[] update_temp1 = query.split("set");
			String[] update_temp2 = update_temp1[1].split("where");
			String update_cmp_s = update_temp2[1];
			String update_set_s = update_temp2[0];
			String[] set = DavisBase.queryequaSplit(update_set_s);
			String[] update_cmp = DavisBase.queryequaSplit(update_cmp_s);
			if (!DavisBase.tblpresent(updateTable)) 
			{
				System.out.println("Table " + updateTable + " does not exist.");
				System.out.println();
				break;
			}
			DMLquery.update(updateTable, set, update_cmp);
			System.out.println("Table "+updateTable+" updated successfully.");
			System.out.println();
			break;	
			
		case "select":
			String[] sel_comp;
			String[] colSelectarr;
			String[] tempSelect = query.split("where");
			String[] Qselect = tempSelect[0].split("from");
			String tblSelect1 = Qselect[1].trim();
			String colSelect1 = Qselect[0].replace("select", "").trim();
			
			if(tblSelect1.equals("davisbase_tables"))
			{
				if (colSelect1.contains("*")) 
				{
					colSelectarr = new String[1];
					colSelectarr[0] = "*";
				} 
				else 
				{
					colSelectarr = colSelect1.split(",");
					for (int i = 0; i < colSelectarr.length; i++)
						colSelectarr[i] = colSelectarr[i].trim();
				}
				if (tempSelect.length > 1) {
					String filter = tempSelect[1].trim();
					sel_comp = DavisBase.queryequaSplit(filter);
				} 
				else {
					sel_comp = new String[0];
				}
				VDLquery.select("data\\catalog\\davisbase_tables.tbl", tblSelect1, colSelectarr, sel_comp);
				System.out.println();
				break;
			}
			
			else if(tblSelect1.equals("davisbase_columns"))
			{
				if (colSelect1.contains("*")) 
				{
					colSelectarr = new String[1];
					colSelectarr[0] = "*";
				} 
				else 
				{
					colSelectarr = colSelect1.split(",");
					for (int i = 0; i < colSelectarr.length; i++)
						colSelectarr[i] = colSelectarr[i].trim();
				}
				if (tempSelect.length > 1) {
					String filter = tempSelect[1].trim();
					sel_comp = DavisBase.queryequaSplit(filter);
				} 
				else {
					sel_comp = new String[0];
				}
				VDLquery.select("data\\catalog\\davisbase_columns.tbl", tblSelect1, colSelectarr, sel_comp);
				System.out.println();
				break;
			}

			else
			{
				if(!DavisBase.tblpresent(tblSelect1)) {
		
					System.out.println("Table " + tblSelect1 + " doesn't exist.");
					System.out.println("Please enter the correct table name.");
					System.out.println();
					break;
				}
			}

			if (tempSelect.length > 1) 
			{
				String filter = tempSelect[1].trim();
				sel_comp = DavisBase.queryequaSplit(filter);
			} 
			else {
				sel_comp = new String[0];
			}

			if (colSelect1.contains("*")) {
				colSelectarr = new String[1];
				colSelectarr[0] = "*";
			} 
			else {
				colSelectarr = colSelect1.split(",");
				for (int i = 0; i < colSelectarr.length; i++)
					colSelectarr[i] = colSelectarr[i].trim();
			}
			
			VDLquery.select("data\\"+DavisBase.samplebase+"\\"+tblSelect1+"\\"+tblSelect1+".tbl", tblSelect1, colSelectarr, sel_comp);
			System.out.println();
			break;	

		case "exit":
			System.out.println();
			break;

		default:
			System.out.println();
			System.out.println("Incorrect. Please check project pdf for sql syntax.");
			System.out.println();
			break;

		}
	}
}
