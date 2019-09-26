package database;



import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


	class Buffer
	{
		public int num_rows; 
		public HashMap<Integer, String[]> content; 
		public int[] varfrmt; 
		public String[] col_name; 

		
		public Buffer()
		{
			num_rows = 0;
			content = new HashMap<Integer, String[]>();
		}

		
		public void add(int rowid, String[] val)
		{
			content.put(rowid, val);
			num_rows = num_rows + 1;
		}

		
		public void Format_updt()
		{
			for(int i = 0; i < varfrmt.length; i++)
				varfrmt[i] = col_name[i].length();
			for(String[] i : content.values()){
				for(int j = 0; j < i.length; j++)
					if(varfrmt[j] < i[j].length())
						varfrmt[j] = i[j].length();
			}
		}


		public String correct(int len, String s) 
		{
			return String.format("%-"+(len+3)+"s", s);
		}

		
		public String strline(String s,int len) 
		{
			String a = "";
			for(int i=0;i<len;i++) 
			{
				a += s;
			}
			return a;
		}


		public void View(String[] col)
		{
			if(num_rows == 0)
			{
				System.out.println("");
			}
			else
			{
				
				Format_updt();
				
				if(col[0].equals("*"))
				{
					
					for(int l: varfrmt)
						System.out.print(strline("-", l+3));
					System.out.println();
				
					for(int j = 0; j < col_name.length; j++)
						System.out.print(correct(varfrmt[j], col_name[j])+"|");
					System.out.println();
					
					for(int l: varfrmt)
						System.out.print(strline("-", l+3));
					System.out.println();
				
					for(String[] i : content.values()){
						if(i[0].equals("-10000"))
							continue;
						for(int j = 0; j < i.length; j++)
							System.out.print(correct(varfrmt[j], i[j])+"|");
						System.out.println();
					}
					System.out.println();
			
				}
				else
				{
					int[] len_ctrl = new int[col.length];
					for(int j = 0; j < col.length; j++)
						for(int i = 0; i < col_name.length; i++)
							if(col[j].equals(col_name[i]))
								len_ctrl[j] = i;
				
					for(int j = 0; j < len_ctrl.length; j++)
						System.out.print(strline("-", varfrmt[len_ctrl[j]]+3));
					System.out.println();
					
					for(int j = 0; j < len_ctrl.length; j++)
						System.out.print(correct(varfrmt[len_ctrl[j]], col_name[len_ctrl[j]])+"|");
					System.out.println();
				
					for(int j = 0; j < len_ctrl.length; j++)
						System.out.print(strline("-", varfrmt[len_ctrl[j]]+3));
					System.out.println();
				
					for(String[] i : content.values())
					{
						for(int j = 0; j < len_ctrl.length; j++)
							System.out.print(correct(varfrmt[len_ctrl[j]], i[len_ctrl[j]])+"|");
						System.out.println();
					}
					System.out.println();
				}
			}
		}
	}
	public class Formtbls
	{
		public static final int pageSize = 512;
		public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
		private static RandomAccessFile davisTblsmetalog;
		private static RandomAccessFile daviscolsmetalog;

		public static String[] Getcontentpl(RandomAccessFile file, long loc)
		{
			String[] payload = new String[0];
			try{
				Long tmp;
				SimpleDateFormat formater = new SimpleDateFormat (datePattern);

			
				file.seek(loc);
				int plsize = file.readShort();
				int key = file.readInt();
				int num_cols = file.readByte();
				byte[] varsc1 = new byte[num_cols];
				int temp = file.read(varsc1);
				payload = new String[num_cols+1];
				payload[0] = Integer.toString(key);
				
				for(int i=1; i <= num_cols; i++){
					switch(varsc1[i-1]){
						case 0x00:  payload[i] = Integer.toString(file.readByte());
									payload[i] = "null";
									break;

						case 0x01:  payload[i] = Integer.toString(file.readShort());
									payload[i] = "null";
									break;

						case 0x02:  payload[i] = Integer.toString(file.readInt());
									payload[i] = "null";
									break;

						case 0x03:  payload[i] = Long.toString(file.readLong());
									payload[i] = "null";
									break;

						case 0x04:  payload[i] = Integer.toString(file.readByte());
									break;

						case 0x05:  payload[i] = Integer.toString(file.readShort());
									break;

						case 0x06:  payload[i] = Integer.toString(file.readInt());
									break;

						case 0x07:  payload[i] = Long.toString(file.readLong());
									break;

						case 0x08:  payload[i] = String.valueOf(file.readFloat());
									break;

						case 0x09:  payload[i] = String.valueOf(file.readDouble());
									break;

						case 0x0A:  tmp = file.readLong();
									Date dateTime = new Date(tmp);
									payload[i] = formater.format(dateTime);
									break;

						case 0x0B:  tmp = file.readLong();
									Date date = new Date(tmp);
									payload[i] = formater.format(date).substring(0,10);
									break;

						default:    int len = new Integer(varsc1[i-1]-0x0C);
									byte[] bytes = new byte[len];
									for(int j = 0; j < len; j++)
										bytes[j] = file.readByte();
									payload[i] = new String(bytes);
									break;
					}
				}

			}
			catch(Exception e)
			{
				System.out.println("Error at Getcontentpl");
			}

			return payload;
		}

		
		public static int EvContentSize(String table, String[] vals, byte[] varsc4)
		{
			String[] dataType = ObtDatatype(table);
			int size = 1;
			size = size + dataType.length - 1;
			for(int i = 1; i < dataType.length; i++){
				byte tmp = structC(vals[i], dataType[i]);
				varsc4[i - 1] = tmp;
				size = size + feildLength(tmp);
			}
			return size;
		}

		
		public static short feildLength(byte varsc5)
		{
			switch(varsc5)
			{
				case 0x00: return 1;
				case 0x01: return 2;
				case 0x02: return 4;
				case 0x03: return 8;
				case 0x04: return 1;
				case 0x05: return 2;
				case 0x06: return 4;
				case 0x07: return 8;
				case 0x08: return 4;
				case 0x09: return 8;
				case 0x0A: return 8;
				case 0x0B: return 8;
				default:   return (short)(varsc5 - 0x0C);
			}
		}

		
		public static byte structC(String val, String dataType)
		{
			if(val.equals("null"))
			{
				switch(dataType)
				{
					case "TINYINT":     return 0x00;
					case "SMALLINT":    return 0x01;
					case "INT":			return 0x02;
					case "BIGINT":      return 0x03;
					case "REAL":        return 0x02;
					case "DOUBLE":      return 0x03;
					case "DATETIME":    return 0x03;
					case "DATE":        return 0x03;
					case "TEXT":        return 0x03;
					default:			return 0x00;
				}							
			}
			else
			{
				switch(dataType)
				{
					case "TINYINT":     return 0x04;
					case "SMALLINT":    return 0x05;
					case "INT":			return 0x06;
					case "BIGINT":      return 0x07;
					case "REAL":        return 0x08;
					case "DOUBLE":      return 0x09;
					case "DATETIME":    return 0x0A;
					case "DATE":        return 0x0B;
					case "TEXT":        return (byte)(val.length()+0x0C);
					default:			return 0x00;
				}
			}
		}

		public static int Keyfind(RandomAccessFile file, int key)
		{
			int val = 1;
			try{
				int numPages = pages(file);
				for(int page = 1; page <= numPages; page++){
					file.seek((page - 1)*pageSize);
					byte pageType = file.readByte();
					if(pageType == 0x0D){
						int[] keys = BplPages.RetrievekeyVals(file, page);
						if(keys.length == 0)
							return 0;
						int rm = BplPages.Rtr_Right_Extr(file, page);
						if(keys[0] <= key && key <= keys[keys.length - 1]){
							return page;
						}else if(rm == 0 && keys[keys.length - 1] < key){
							return page;
						}
					}
				}
			}
			catch(Exception e)
			{
				System.out.println("Error at Keyfind");
				System.out.println(e);
			}

			return val;
		}


		public static String[] ObtDatatype(String table)
		{
			String[] dataType = new String[0];
			try{
				RandomAccessFile file = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] col_name = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				if(!table.equals("davisbase_columns") && !table.equals("davisbase_tables"))
					table = DavisBase.samplebase+"."+table;
				String[] cmp = {"table_name","=",table};
				FilterFunc.Nulfilter4(file, cmp, col_name, buffer);
				HashMap<Integer, String[]> content = buffer.content;
				ArrayList<String> array = new ArrayList<String>();
				for(String[] i : content.values()){
					array.add(i[3]);
				}
				dataType = array.toArray(new String[array.size()]);
				file.close();
				return dataType;
			}
			catch(Exception e)
			{
				System.out.println("Error in getting the data type");
				System.out.println(e);
			}
			return dataType;
		}

		public static String[] obtcolName(String table)
		{
			String[] c = new String[0];
			try{
				RandomAccessFile file = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] col_name = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				if(!table.equals("davisbase_columns") && !table.equals("davisbase_tables"))
					table = DavisBase.samplebase+"."+table;
				String[] cmp = {"table_name","=",table};
				FilterFunc.Nulfilter4(file, cmp, col_name, buffer);
				HashMap<Integer, String[]> content = buffer.content;
				ArrayList<String> array = new ArrayList<String>();
				for(String[] i : content.values()){
					array.add(i[2]);
				}
				c = array.toArray(new String[array.size()]);
				file.close();
				return c;
			}
			catch(Exception e)
			{
				System.out.println("Error in getting the column name");
				System.out.println(e);
			}
			return c;
		}

		public static String[] chknullablecol(String table)
		{
			String[] n = new String[0];
			try{
				RandomAccessFile file = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] col_name = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				if(!table.equals("davisbase_columns") && !table.equals("davisbase_tables"))
					table = DavisBase.samplebase+"."+table;
				String[] cmp = {"table_name","=",table};
				FilterFunc.Nulfilter4(file, cmp, col_name, buffer);
				HashMap<Integer, String[]> content = buffer.content;
				ArrayList<String> array = new ArrayList<String>();
				for(String[] i : content.values())
				{
					array.add(i[5]);
				}
				n = array.toArray(new String[array.size()]);
				file.close();
				return n;
			}catch(Exception e){
				System.out.println("Error at chknullablecol");
				System.out.println(e);
			}
			return n;
		}

		public static int pages(RandomAccessFile file)
		{
			int num_pages = 0;
			try
			{
				num_pages = (int)(file.length()/(new Long(pageSize)));
			}
			catch(Exception e)
			{
				System.out.println("Error at Form_inpage");
			}

			return num_pages;
		}

			

	}

