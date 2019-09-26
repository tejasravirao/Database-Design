package database;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;



public class DavisBase {

	static String prompt = "davissql> ";
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");

	static String samplebase = "tejas_data";
	
	public static void main(String[] args) {
		startdavisbase();		
		System.out.println("Project 2 Davis Base");		
		String query = "";

		while (!query.equals("exit")) 
		{
			System.out.print(prompt);
			
			query = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			Qhandling.parsequery(query);
		}
		System.out.println("Exiting");

	}

	public static void startdavisbase(){ 	
		try {
			File data = new File("data");
			if (!data.exists()) {
				data.mkdir();
			}
			File databasemeta = new File("data\\catalog");
			if (databasemeta.mkdir()) {
				System.out.println("Creating davisbase metadata!");
				DataInputStore.StartinputData();
			} else {
				boolean catalog = false;
				String meta_columns = "davisbase_columns.tbl";
				String meta_tables = "davisbase_tables.tbl";
				String[] tableList = databasemeta.list();

				for (int i = 0; i < tableList.length; i++) {
					if (tableList[i].equals(meta_columns))
						catalog = true;
				}
				if (!catalog) {
					System.out.println("Creating davisbase_columns in catalog");
					System.out.println();
					DataInputStore.StartinputData();
				}
				catalog = false;
				for (int i = 0; i < tableList.length; i++) {
					if (tableList[i].equals(meta_tables))
						catalog = true;
				}
				if (!catalog) {
					System.out.println("Creating davisbase_tables in catalog");
					System.out.println();
					DataInputStore.StartinputData();
				}
			}
		} catch (SecurityException se) {
			System.out.println("Catalog files not yet present " + se);

		}

	}
	
	public static boolean tblpresent(String table) {
		boolean checktble = false;
		
		try {
			File user_tables = new File("data\\"+samplebase);
			if (user_tables.mkdir()) {
				System.out.println("Creating tejas_data");
				
			}
			String[] tableList;
			tableList = user_tables.list();
			for (int i = 0; i < tableList.length; i++) {
				if (tableList[i].equals(table))
					return true;
			}
		} catch (SecurityException se) {
			System.out.println("Unable to create data" + se);
		}

		return checktble;
	}
	
	public static String[] queryequaSplit(String equ) 
	{
		String ineq[] = new String[3];
		String temp[] = new String[2];
		if (equ.contains("=")) {
			temp = equ.split("=");
			ineq[0] = temp[0].trim();
			ineq[1] = "=";
			ineq[2] = temp[1].trim();
		}

		if (equ.contains(">")) {
			temp = equ.split(">");
			ineq[0] = temp[0].trim();
			ineq[1] = ">";
			ineq[2] = temp[1].trim();
		}

		if (equ.contains("<")) {
			temp = equ.split("<");
			ineq[0] = temp[0].trim();
			ineq[1] = "<";
			ineq[2] = temp[1].trim();
		}

		if (equ.contains(">=")) {
			temp = equ.split(">=");
			ineq[0] = temp[0].trim();
			ineq[1] = ">=";
			ineq[2] = temp[1].trim();
		}

		if (equ.contains("<=")) {
			temp = equ.split("<=");
			ineq[0] = temp[0].trim();
			ineq[1] = "<=";
			ineq[2] = temp[1].trim();
		}

		if (equ.contains("<>")) {
			temp = equ.split("<>");
			ineq[0] = temp[0].trim();
			ineq[1] = "<>";
			ineq[2] = temp[1].trim();
		}

		return ineq;
	}

}
