package util;

import java.util.*;
import java.text.*;

/**
 * 日期助手.
 */
public class Dates
{
	public static String date2string(Date date, String format) throws Exception
	{
		if ( date == null || format == null || format.equals("") )
		{
			return "";
		}
		
//		String result = "";
	    
	    StringBuffer     sb  = new StringBuffer();
        FieldPosition    fp  = new FieldPosition( 0 );
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date, sb, fp).toString();
	}
	
	public static String getCurrentDateTimeString() throws Exception
	{
        return date2string(new Date(), "yyyy-MM-dd HH:mm");
	}
	
	public static String getCurrentDateString() throws Exception
	{
		
        return date2string(new Date(), "yyyy-MM-dd");
	}
	
	public static String getCurrentTimeString() throws Exception
	{
//	    String result = "";
//	    
//	    StringBuffer     sb  = new StringBuffer();
//        FieldPosition    fp  = new FieldPosition( 0 );
        SimpleDateFormat sdf = new SimpleDateFormat();

        sdf.applyPattern("HH:mm");
        return date2string(new Date(), "HH:mm");
	}
	
	public static String getDateString(int monthDiff, int dateDiff) throws Exception
	{
//	    String result = "";
	    
	    StringBuffer     sb  = new StringBuffer();
        FieldPosition    fp  = new FieldPosition( 0 );
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        
        Calendar clndr = Calendar.getInstance();
        clndr.add(Calendar.MONTH, monthDiff);
        clndr.add(Calendar.DATE, dateDiff);

        return sdf.format(clndr.getTime(), sb, fp).toString();
	}

	public static List<String[]> getYears(int up, int down) throws Exception
	{
		int year = Calendar.getInstance().get(Calendar.YEAR);
	    List<String[]> list = new ArrayList<String[]>();
		for (int i = year + up; i >= year - down; i--)
		{
			String[] row = new String[2];
			row[1] = row[0] = Integer.toString(i);
			list.add(row);
		}
        return list;
	}
}
