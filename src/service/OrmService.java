package service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;

import annot.Service;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import orm.ORMUtil;
import orm.dialect.Dialect;
import util.Consts;
import util.Exp;
import util.MetaMap;
import util.Strings;


@Service
public class OrmService {
	
	public static Configuration ormcfg = new Configuration();
	
	static {
		
		ormcfg.setDefaultEncoding("utf-8");
		// 指定模板文件从何处加载的数据源,这里设置成一个文件目录。
		try {
			ormcfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/ftl"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 指定模板如何检索数据模型,这是一个高级的主题了...
		// 但先可以这么来用:
		ormcfg.setObjectWrapper(new DefaultObjectWrapper());
		
		ormcfg.clearTemplateCache();
	}

	@annot.Transaction
	public static String save(MetaMap pkg, List<String> selectedClassNames, List<MetaMap> options, String rootDir) throws Exception {
		// 格式化
//		File file = new File(System.getProperty("") + "/orm/" + pkg.name() + ".orm");
		String path = null;// file.getAbsolutePath();
//		OutputStreamWriter writer = null;
//		
//		writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
//		writer.write(pkg.toJson());
//		writer.flush();
//		writer.close();

		// 持久化类
		ormcfg.clearTemplateCache();

		LinkedHashMap<String, Template> templates = new LinkedHashMap<String, Template>();
		
		for (MetaMap option : options) {
			Template tmpl = readTemplate(ormcfg, option.name() + ".ftl", option.label());
			templates.put(option.name(), tmpl);
		}

		ORMUtil.arrange(pkg);

		MetaMap data = new MetaMap();
		data.put("pkg", pkg);
		data.put("Exp", Exp.instance);
		data.put("DB", new Dialect());
		data.put("Consts", Consts.instance);
		data.put("ORMUtil", ORMUtil.instance);
		data.put("Strings", Strings.instance);
		//data.put("Dialect", Dialect..instance);
		
		
		for (MetaMap oclass : pkg.listmap("classes") ) {
			if (!selectedClassNames.contains(oclass.name()) 
				|| oclass.isTrue("outside")) {
				continue;
			}
			
			data.put("entity", oclass);
			
			// 从第5个选项开始都是在module目录输出文件，随意添加
			for (MetaMap option : options) {
				path = getFilePath(option, oclass, rootDir);
				Template tmpl = templates.get(option.name());
				if (tmpl == null) {
					System.out.println(option.name() + "模版无法获取，请重新检查目录文件名");
					continue;
				}
				writeTemplate(path, tmpl, data, oclass, option.label());
			}

			if (!oclass.isTrue("nottable")) {
				
			}
		}
		
		
		return "";
	}
	
	private static String checkDotDot(String path, boolean keepEnd) {
		int dotIndex = path.indexOf("..");
		if (dotIndex != -1) {
			String[] parts = path.split("/");
			
			//int endIdx = keepEnd ? 2 : 1;
			path = "";
			for (int i = 0; i < parts.length - 2; i++) {
				path = path + parts[i] + "/";
			}
			
			if (keepEnd) {
				path = path + parts[parts.length - 1];
			}
		}
		
		return path;
	}

	private static String getFilePath(MetaMap option, MetaMap oclass, String rootDir) {
		String dir = getDirPath(option, oclass, rootDir);
		String path = dir + option.str("columnName");
		String dir2 = path.substring(0, path.lastIndexOf("/"));
		dir2 = checkDotDot(dir2, false);
		(new File(dir2)).mkdirs();
		path = checkDotDot(path, true);
		return path;
	}

	private static String getDirPath(MetaMap option, MetaMap oclass, String rootDir) {
		File file;
		String dir = rootDir + option.str("datasource") + oclass.get("path");
		dir = checkDotDot(dir, false);
		file = new File(dir);
		file.mkdirs();
		String path = file.getAbsolutePath() + "/" + oclass.name();
		return path;
	}

	private static boolean writeTemplate(String path, Template tmpl, MetaMap data,
			MetaMap oclass, String label) throws UnsupportedEncodingException,
			FileNotFoundException, IOException {
		OutputStreamWriter writer = null;
		FileOutputStream fs = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			writer = 
				new OutputStreamWriter(stream, "utf-8");
			tmpl.process(data, writer);
			
			String content = stream.toString();//.substring(1);// 去掉加入的BOM字符\ufeff
			fs = new FileOutputStream(path);
			fs.write(content.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e.getLocalizedMessage());
			Consts.log.error(oclass.name() + "类生成"+label+"时错误\n" 
					+ e.getLocalizedMessage(), e);
			return false;
		} finally {
			if (writer != null)
				writer.close();
			
			if (fs != null)
				fs.close();
		}
		
		return true;
	}

	private static Template readTemplate(Configuration cfg, String name, String label) throws IOException {
		Template tmpl = null;
		try {
			tmpl = cfg.getTemplate(name);
		} catch (ParseException e) {
			System.out.println("解析"+label+"模板错误"
					+ e.lineNumber + "行" + e.lineNumber + "列\n" 
					+ e.getLocalizedMessage());
			
			Consts.log.error("解析"+label+"模板错误"
				+ e.lineNumber + "行" + e.lineNumber + "列\n" 
				+ e.getLocalizedMessage(), e);
		}
		return tmpl;
	}
}
