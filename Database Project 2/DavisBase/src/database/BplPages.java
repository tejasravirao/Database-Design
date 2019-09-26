package database;

import java.io.*;

import java.text.*;
import java.util.*;
import java.lang.*;

public class BplPages{
	
	
	public static int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	
	public static int Form_inpage(RandomAccessFile file)
	{
		int numofpages = 0;
		try{
			numofpages = (int)(file.length()/(new Long(pageSize)));
			numofpages = numofpages + 1;
			file.setLength(pageSize * numofpages);
			file.seek((numofpages-1)*pageSize);
			file.writeByte(0x05);  
		}catch(Exception e){
			System.out.println("Error at Form_inpage");
		}

		return numofpages;
	}

	
	public static int Form_lfpage(RandomAccessFile file)
	{
		int numofpages = 0;
		try{
			numofpages = (int)(file.length()/(new Long(pageSize)));
			numofpages = numofpages + 1;
			file.setLength(pageSize * numofpages);
			file.seek((numofpages-1)*pageSize);
			file.writeByte(0x0D);  
		}
		catch(Exception e)
		{
			System.out.println("Error at Form_lfpage");
		}

		return numofpages;

	}


	public static int RetrieveMidVal(RandomAccessFile file, int page)
	{
		int val = 0;
		try{
			file.seek((page-1)*pageSize);
			byte typeofpage = file.readByte();
			
			int numofcells = Rtr_Cell_num(file, page);
			
			int mid = (int) Math.ceil((double) numofcells / 2);
			long loc = Rtr_Loc_Cell(file, page, mid-1);
			file.seek(loc);

			switch(typeofpage){
				case 0x05:
					val = file.readInt(); 
					val = file.readInt();
					break;
				case 0x0D:
					val = file.readShort();
					val = file.readInt();
					break;
			}

		}catch(Exception e)
		{
			System.out.println("Error at RetrieveMidVal");
		}

		return val;
	}
	
	public static boolean Iskey_present(RandomAccessFile file, int page, int key)
	{
		int[] array3 = RetrievekeyVals(file, page);
		for(int i : array3)
			if(key == i)
				return true;
		return false;
	}

	
	public static long Rtr_Loc_Cell(RandomAccessFile file, int page, int id)
	{
		long loc = 0;
		try{
			file.seek((page-1)*pageSize+12+id*2);
			short offset = file.readShort();
			long firstelem = (page-1)*pageSize;
			loc = firstelem + offset;
		}
		catch(Exception e)
		{
			System.out.println("Error at Rtr_Loc_Cell");
		}
		return loc;
	}

	
	public static void Div_Lfpage(RandomAccessFile file, int current_page, int new_page){
		try{
			
			int numofcells = Rtr_Cell_num(file, current_page);
			
			int mid = (int) Math.ceil((double) numofcells / 2);

			int nofCell_one = mid - 1;
			int nofCell_two = numofcells - nofCell_one;
			int content = 512;

			for(int i = nofCell_one; i < numofcells; i++){
				long loc = Rtr_Loc_Cell(file, current_page, i);
				
				file.seek(loc);
				int Size_cell = file.readShort()+6;
				content = content - Size_cell;
			
				file.seek(loc);
				byte[] cell = new byte[Size_cell];
				file.read(cell);
				
				file.seek((new_page-1)*pageSize+content);
				file.write(cell);
				
				Put_Cell_ofst(file, new_page, i - nofCell_one, content);
			}

		
			file.seek((new_page-1)*pageSize+2);
			file.writeShort(content);

		
			short offset = Rtr_Cell_ofst(file, current_page, nofCell_one-1);
			file.seek((current_page-1)*pageSize+2);
			file.writeShort(offset);

	
			int rightMost = Rtr_Right_Extr(file, current_page);
			Put_Right_Extr(file, new_page, rightMost);
			Put_Right_Extr(file, current_page, new_page);

		
			int parent = Rtr_parent(file, current_page);
			Makeit_Parent(file, new_page, parent);

		
			byte num = (byte) nofCell_one;
			Put_Cell_num(file, current_page, num);
			num = (byte) nofCell_two;
			Put_Cell_num(file, new_page, num);
		}catch(Exception e){
			System.out.println("Error at Div_Lfpage");
			e.printStackTrace();
		}
	}
	
	public static void Dividelf2(RandomAccessFile file, int page){
		int new_page = Form_lfpage(file);
		int val_key_mid = RetrieveMidVal(file, page);
		Div_Lfpage(file, page, new_page);
		int parent = Rtr_parent(file, page);
		if(parent == 0)
		{
			int root_page = Form_inpage(file);
			Makeit_Parent(file, page, root_page);
			Makeit_Parent(file, new_page, root_page);
			Put_Right_Extr(file, root_page, new_page);
			Put_Inte_Cell(file, root_page, page, val_key_mid);
		}
		else
		{
			long ploc = Rtr_Ptr_loc(file, page, parent);
			Makeit_ptr_loc(file, ploc, parent, new_page);
			Put_Inte_Cell(file, parent, page, val_key_mid);
			SortAllCells(file, parent);
			while(Cal_Space_Inte(file, parent)){
				parent = DivideInte2(file, parent);
			}
		}
	}
	
	public static void Div_Inpage(RandomAccessFile file, int current_page, int new_page)
	{
		try{
		
			int numCells = Rtr_Cell_num(file, current_page);
			
			int mid = (int) Math.ceil((double) numCells / 2);

			int nofCell_one = mid - 1;
			int nofCell_two = numCells - nofCell_one - 1;
			short content = 512;

			for(int i = nofCell_one+1; i < numCells; i++){
				long loc = Rtr_Loc_Cell(file, current_page, i);
				
				short Sizeofcell = 8;
				content = (short)(content - Sizeofcell);
		
				file.seek(loc);
				byte[] cell = new byte[Sizeofcell];
				file.read(cell);
			
				file.seek((new_page-1)*pageSize+content);
				file.write(cell);
				
				file.seek(loc);
				int page = file.readInt();
				Makeit_Parent(file, page, new_page);
			
				Put_Cell_ofst(file, new_page, i - (nofCell_one + 1), content);
			}
			
			int tmp = Rtr_Right_Extr(file, current_page);
			Put_Right_Extr(file, new_page, tmp);
		
			long midLoc = Rtr_Loc_Cell(file, current_page, mid - 1);
			file.seek(midLoc);
			tmp = file.readInt();
			Put_Right_Extr(file, current_page, tmp);
		
			file.seek((new_page-1)*pageSize+2);
			file.writeShort(content);
		
			short offset = Rtr_Cell_ofst(file, current_page, nofCell_one-1);
			file.seek((current_page-1)*pageSize+2);
			file.writeShort(offset);

			
			int parent = Rtr_parent(file, current_page);
			Makeit_Parent(file, new_page, parent);
			
			byte num = (byte) nofCell_one;
			Put_Cell_num(file, current_page, num);
			num = (byte) nofCell_two;
			Put_Cell_num(file, new_page, num);
		}catch(Exception e){
			System.out.println("Error at Div_Inpage");
		}
	}

	public static int DivideInte2(RandomAccessFile file, int page){
		int new_page = Form_inpage(file);
		int val_key_mid = RetrieveMidVal(file, page);
		Div_Inpage(file, page, new_page);
		int parent = Rtr_parent(file, page);
		if(parent == 0){
			int root_page = Form_inpage(file);
			Makeit_Parent(file, page, root_page);
			Makeit_Parent(file, new_page, root_page);
			Put_Right_Extr(file, root_page, new_page);
			Put_Inte_Cell(file, root_page, page, val_key_mid);
			return root_page;
		}else{
			long ploc = Rtr_Ptr_loc(file, page, parent);
			Makeit_ptr_loc(file, ploc, parent, new_page);
			Put_Inte_Cell(file, parent, page, val_key_mid);
			SortAllCells(file, parent);
			return parent;
		}
	}

	public static void SortAllCells(RandomAccessFile file, int page)
	{
		 byte num = Rtr_Cell_num(file, page);
		 int[] val_key_arr = RetrievekeyVals(file, page);
		 short[] cell_arr1 = RetrieveAllCells(file, page);
		 int ltmp;
		 short rtmp;

		 for (int i = 1; i < num; i++) 
		 {
            for(int j = i ; j > 0 ; j--)
            {
                if(val_key_arr[j] < val_key_arr[j-1])
                {

                    ltmp = val_key_arr[j];
                    val_key_arr[j] = val_key_arr[j-1];
                    val_key_arr[j-1] = ltmp;

                    rtmp = cell_arr1[j];
                    cell_arr1[j] = cell_arr1[j-1];
                    cell_arr1[j-1] = rtmp;
                }
            }
         }

         try
         {
         	file.seek((page-1)*pageSize+12);
         	for(int i = 0; i < num; i++)
         	{
				file.writeShort(cell_arr1[i]);
			}
         }catch(Exception e)
         {
         	System.out.println("Error at SortAllCells");
         }
	}

	public static int[] RetrievekeyVals(RandomAccessFile file, int page){
		int num = new Integer(Rtr_Cell_num(file, page));
		int[] array = new int[num];

		try{
			file.seek((page-1)*pageSize);
			byte typeofpage = file.readByte();
			byte offset = 0;
			switch(typeofpage){
				case 0x05:
					offset = 4;
					break;
				case 0x0d:
					offset = 2;
					break;
				default:
					offset = 2;
					break;
			}

			for(int i = 0; i < num; i++){
				long loc = Rtr_Loc_Cell(file, page, i);
				file.seek(loc+offset);
				array[i] = file.readInt();
			}

		}catch(Exception e){
			System.out.println("Error at RetrievekeyVals");
		}

		return array;
	}

	public static short[] RetrieveAllCells(RandomAccessFile file, int page){
		int num = new Integer(Rtr_Cell_num(file, page));
		short[] array = new short[num];

		try{
			file.seek((page-1)*pageSize+12);
			for(int i = 0; i < num; i++){
				array[i] = file.readShort();
			}
		}catch(Exception e){
			System.out.println("Error at RetrieveAllCells");
		}

		return array;
	}

	

	
	public static long Rtr_Ptr_loc(RandomAccessFile file, int page, int parent){
		long val = 0;
		try{
			int numofcells = new Integer(Rtr_Cell_num(file, parent));
			for(int i=0; i < numofcells; i++){
				long loc = Rtr_Loc_Cell(file, parent, i);
				file.seek(loc);
				int child_page = file.readInt();
				if(child_page == page){
					val = loc;
				}
			}
		}catch(Exception e){
			System.out.println("Error at Rtr_Ptr_loc");
		}

		return val;
	}


	public static void Makeit_ptr_loc(RandomAccessFile file, long loc, int parent, int page){
		try{
			if(loc == 0){
				file.seek((parent-1)*pageSize+4);
			}else{
				file.seek(loc);
			}
			file.writeInt(page);
		}catch(Exception e){
			System.out.println("Error at Makeit_ptr_loc");
		}
	} 


	public static void Put_Inte_Cell(RandomAccessFile file, int page, int child, int key)
	{
		try{
			
			file.seek((page-1)*pageSize+2);
			short content = file.readShort();
			if(content == 0)
				content = 512;
			content = (short)(content - 8);
		
			file.seek((page-1)*pageSize+content);
			file.writeInt(child);
			file.writeInt(key);
			
			file.seek((page-1)*pageSize+2);
			file.writeShort(content);
			
			byte num = Rtr_Cell_num(file, page);
			Put_Cell_ofst(file, page ,num, content);
			
			num = (byte) (num + 1);
			Put_Cell_num(file, page, num);

		}catch(Exception e){
			System.out.println("Error at Put_Inte_Cell");
		}
	}


	public static void Put_leaf_Cell(RandomAccessFile file, int page, int offset, short con_pl_size, int key, byte[] stc, String[] vals, String table){
		try{
			String s;
			file.seek((page-1)*pageSize+offset);
			String[] colName = Formtbls.obtcolName(table);
			
			file.seek((page-1)*pageSize+offset);
			file.writeShort(con_pl_size);
			file.writeInt(key);
			int col = vals.length - 1;
			
			
			file.writeByte(col);
			file.write(stc);
			
			for(int i = 1; i < vals.length; i++)
				
			{	
				switch(stc[i-1]){
					case 0x00:
						file.writeByte(0);
						break;
					case 0x01:
						file.writeShort(0);
						break;
					case 0x02:
						file.writeInt(0);
						break;
					case 0x03:
						file.writeLong(0);
						break;
					case 0x04:
						file.writeByte(new Byte(vals[i]));
						break;
					case 0x05:
						file.writeShort(new Short(vals[i]));
						break;
					case 0x06:
						file.writeInt(new Integer(vals[i]));
						break;
					case 0x07:
						file.writeLong(new Long(vals[i]));
						break;
					case 0x08:
						file.writeFloat(new Float(vals[i]));
						break;
					case 0x09:
						file.writeDouble(new Double(vals[i]));
						break;
					case 0x0A:
						s = vals[i];
						
						Date temp = new SimpleDateFormat(datePattern).parse(s);
						long time = temp.getTime();
						file.writeLong(time);
						break;
					case 0x0B:
						s = vals[i];
						
						s = s+"_00:00:00";
						Date temp2 = new SimpleDateFormat(datePattern).parse(s);
						long time2 = temp2.getTime();
						file.writeLong(time2);
						break;
					default:
						file.writeBytes(vals[i]);
						break;
				}
			}
			int n = Rtr_Cell_num(file, page);
			byte tmp = (byte) (n+1);
			Put_Cell_num(file, page, tmp);
			file.seek((page-1)*pageSize+12+n*2);
			file.writeShort(offset);
			file.seek((page-1)*pageSize+2);
			int content = file.readShort();
			if(content >= offset || content == 0){
				file.seek((page-1)*pageSize+2);
				file.writeShort(offset);
			}
		}catch(Exception e)
		{
			System.out.println("Error at Put_leaf_Cell");
			e.printStackTrace();
		}
	}

	public static void Change_Lf_Cell(RandomAccessFile file, int page, int offset, int con_pl_size, int key, byte[] stc, String[] vals, String table){
		try{
			String s;
			file.seek((page-1)*pageSize+offset);
			file.writeShort(con_pl_size);
			file.writeInt(key);
			int col = vals.length - 1;
			file.writeByte(col);
			file.write(stc);
			for(int i = 1; i < vals.length; i++){
				
				switch(stc[i-1]){
					case 0x00:
						file.writeByte(0);
						break;
					case 0x01:
						file.writeShort(0);
						break;
					case 0x02:
						file.writeInt(0);
						break;
					case 0x03:
						file.writeLong(0);
						break;
					case 0x04:
						file.writeByte(new Byte(vals[i]));
						break;
					case 0x05:
						file.writeShort(new Short(vals[i]));
						break;
					case 0x06:
						file.writeInt(new Integer(vals[i]));
						break;
					case 0x07:
						file.writeLong(new Long(vals[i]));
						break;
					case 0x08:
						file.writeFloat(new Float(vals[i]));
						break;
					case 0x09:
						file.writeDouble(new Double(vals[i]));
						break;
					case 0x0A:
						s = vals[i];
						Date temp = new SimpleDateFormat(datePattern).parse(s.substring(1, s.length()-1));
						long time = temp.getTime();
						file.writeLong(time);
						break;
					case 0x0B:
						s = vals[i];
						s = s.substring(1, s.length()-1);
						s = s+"_00:00:00";
						Date temp2 = new SimpleDateFormat(datePattern).parse(s);
						long time2 = temp2.getTime();
						file.writeLong(time2);
						break;
					default:
						file.writeBytes(vals[i]);
						break;
				}
			}
		}catch(Exception e){
			System.out.println("Error at BplPages.Change");
			System.out.println(e);
		}
	}

	public static int Rtr_Right_Extr(RandomAccessFile file, int page)
	{
		int val = 0;

		try
		{
			file.seek((page-1)*pageSize+4);
			val = file.readInt();
		}
		catch(Exception e)
		{
			System.out.println("Error in rightmost");
		}

		return val;
	}

	public static void Put_Right_Extr(RandomAccessFile file, int page, int rightMost)
	{
		try
		{
			file.seek((page-1)*pageSize+4);
			file.writeInt(rightMost);
		}
		catch(Exception e)
		{
			System.out.println("Error in putting rightmost");
		}

	}

	
	public static byte Rtr_Cell_num(RandomAccessFile file, int page)
	{
		byte val = 0;
		try
		{
			file.seek((page-1)*pageSize+1);
			val = file.readByte();
		}
		catch(Exception e)
		{
			System.out.println(e);
			System.out.println("Error at Rtr_Cell_num");
		}
		return val;
	}

	public static void Put_Cell_num(RandomAccessFile file, int page, byte num)
	{
		try{
			file.seek((page-1)*pageSize+1);
			file.writeByte(num);
		}catch(Exception e){
			System.out.println("Error at Put_Cell_num");
		}
	}

	public static boolean Cal_Space_Inte(RandomAccessFile file, int page)
	{
		byte numofcells = Rtr_Cell_num(file, page);
		if(numofcells > 30)
			return true;
		else
			return false;
	}

	
	public static int Cal_Space_Leaf(RandomAccessFile file, int page, int size)
	{
		int val = -1;

		try
		{
			file.seek((page-1)*pageSize+2);
			int content = file.readShort();
			if(content == 0)
				return pageSize - size;
			int numofcells = Rtr_Cell_num(file, page);
			int space = content - 20 - 2*numofcells;
			if(size < space)
				return content - size;
			
		}catch(Exception e)
		{
			System.out.println("Error at Cal_Space_Leaf");
		}

		return val;
	}

	
	

	public static short Rtr_Cell_ofst(RandomAccessFile file, int page, int id)
	{
		short offset = 0;
		try{
			file.seek((page-1)*pageSize+12+id*2);
			offset = file.readShort();
		}
		catch(Exception e)
		{
			System.out.println("Error at Rtr_Cell_ofst");
		}
		return offset;
	}

	public static void Put_Cell_ofst(RandomAccessFile file, int page, int id, int offset){
		try{
			file.seek((page-1)*pageSize+12+id*2);
			file.writeShort(offset);
		}
		catch(Exception e)
		{
			System.out.println("Error at Put_Cell_ofst");
		}
	}
	
	public static int Rtr_parent(RandomAccessFile file, int page){
		int val = 0;

		try{
			file.seek((page-1)*pageSize+8);
			val = file.readInt();
		}catch(Exception e){
			System.out.println("Error at Rtr_parent");
		}

		return val;
	}

	public static void Makeit_Parent(RandomAccessFile file, int page, int parent)
	{
		try{
			file.seek((page-1)*pageSize+8);
			file.writeInt(parent);
		}catch(Exception e){
			System.out.println("Error at Makeit_Parent");
		}
	}

}















