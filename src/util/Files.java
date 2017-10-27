package util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Files {
	public static Files instance = new Files();
	
	public static void write(String path, String charset, String content)
	throws Exception {
		File file = new File(path);
		
		  OutputStreamWriter writer = null;
		  try {
		  		writer = new OutputStreamWriter(
		  			new FileOutputStream(file), charset);
				writer.write(content);
				writer.flush();
		  } finally {
				writer.close();
		  }
	}
	
	public static String read(String path, String charset)
	throws Exception {
		File file = new File(path);
		if (!file.exists())
			return "";
		
		StringBuffer content = new StringBuffer();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(
				new FileInputStream(file), charset);
			while (reader.ready()) {
				content.append((char) reader.read());
			}
		} finally {
			if (reader != null)
				reader.close();
		}
		
		return content.toString();
	}
}
