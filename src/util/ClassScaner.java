package util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import annot.Service;
  
/** 
 * 扫描指定包（包括jar）下的class文件 <br> 
 */  
@SuppressWarnings("unchecked")
public class ClassScaner {  
  
	/** 
	 * logger 
	 */  
	public static final Log logger = LogFactory  
			.getLog(ClassScaner.class);  
  
	/** 
	 * 是否排除内部类 true->是 false->否 
	 */  
	public boolean excludeInner = true;  
	/** 
	 * 过滤规则适用情况 true—>搜索符合规则的 false->排除符合规则的 
	 */  
	public boolean checkInOrEx = true;  
  
	/** 
	 * 过滤规则列表 如果是null或者空，即全部符合不过滤 
	 */  
	public Class<?>[] classFilters = null;  
  
	/** 
	 * 无参构造器，默认是排除内部类、并搜索符合规则 
	 */  
	public ClassScaner() {  
	}  
  
	/** 
	 * excludeInner:是否排除内部类 true->是 false->否<br> 
	 * checkInOrEx：过滤规则适用情况 true—>搜索符合规则的 false->排除符合规则的<br> 
	 * classFilters：自定义过滤规则，如果是null或者空，即全部符合不过滤 
	 * @param excludeInner 
	 * @param checkInOrEx 
	 * @param classFilters 
	 */  
	public ClassScaner(Boolean excludeInner, Boolean checkInOrEx, Class<?>... classFilters) {  
		this.excludeInner = excludeInner;  
		this.checkInOrEx = checkInOrEx;  
		this.classFilters = classFilters;  
  
	}  
  
	/** 
	 * 扫描包 
	 * @param basePackage 基础包 
	 * @param recursive 是否递归搜索子包 
	 * @return Set 
	 */  
	public Set<Class<?>> getPackageAllClasses(String basePackage,  
			boolean recursive) {  
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();  
		String packageName = basePackage;  
		if (packageName.endsWith(".")) {  
			packageName = packageName  
					.substring(0, packageName.lastIndexOf('.'));  
		}  
		String package2Path = packageName.replace('.', '/');  
  
		Enumeration<URL> dirs;  
		try {  
			dirs = Thread.currentThread().getContextClassLoader().getResources(  
					package2Path);  
			while (dirs.hasMoreElements()) {  
				URL url = dirs.nextElement();  
				String protocol = url.getProtocol();  
				if ("file".equals(protocol)) {  
					//logger.info("扫描file类型的class文件....");  
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
					doScanPackageClassesByFile(classes, packageName, filePath,  
							recursive);  
				} else if ("jar".equals(protocol)) {  
					//logger.info("扫描jar文件中的类....");
					// XXX 不扫描jar
					//doScanPackageClassesByJar(packageName, url, recursive,  
					//		classes);  
				}  
			}  
		} catch (IOException e) {  
			logger.error("IOException error:", e);  
		}  
  
		return classes;  
	}  
  
	/** 
	 * 以jar的方式扫描包下的所有Class文件<br> 
	 * @param basePackage eg：michael.utils. 
	 * @param url 
	 * @param recursive 
	 * @param classes 
	 */  
	public void doScanPackageClassesByJar(String basePackage, URL url,  
			final boolean recursive, Set<Class<?>> classes) {  
		String packageName = basePackage;  
		String package2Path = packageName.replace('.', '/');  
		JarFile jar;  
		try {  
			jar = ((JarURLConnection) url.openConnection()).getJarFile();  
			Enumeration<JarEntry> entries = jar.entries();  
			while (entries.hasMoreElements()) {  
				JarEntry entry = entries.nextElement();  
				String name = entry.getName();  
				if (!name.startsWith(package2Path) || entry.isDirectory()) {  
					continue;  
				}  
  
				// 判断是否递归搜索子包  
				if (!recursive  
						&& name.lastIndexOf('/') != package2Path.length()) {  
					continue;  
				}  
				// 判断是否过滤 inner class  
				if (this.excludeInner && name.indexOf('$') != -1) {  
					logger.info("exclude inner class with name:" + name);  
					continue;  
				}  
				Class<?> clazz = null;
				// 判定是否符合过滤条件  
				try {  
					if ((clazz = this.filterClassName(name)) != null) {  
						classes.add(clazz);
					}  
				} catch (ClassNotFoundException e) {  
					logger.error("Class.forName error: " + name, e);  
				}  
			}  
		} catch (IOException e) {  
			logger.error("IOException error:", e);  
		}  
	}  
  
	/** 
	 * 以文件的方式扫描包下的所有Class文件 
	 *  
	 * @param packageName 
	 * @param packagePath 
	 * @param recursive 
	 * @param classes 
	 */  
	public void doScanPackageClassesByFile(Set<Class<?>> classes,  
			String packageName, String packagePath, boolean recursive) {  
		File dir = new File(packagePath);  
		if (!dir.exists() || !dir.isDirectory()) {  
			return;  
		}  
		final boolean fileRecursive = recursive;  
		for (File file : dir.listFiles()) {  
			if (file.isDirectory()) {
				if (fileRecursive) {
					doScanPackageClassesByFile(classes, packageName + "."  
							+ file.getName(), file.getAbsolutePath(), recursive);
				}
			} else {  
				String className = packageName + "." + file.getName();  
				try {  
					Class<?> clazz = filterClassName(className);
					if (clazz != null) {
						classes.add(clazz);
					}
				} catch (ClassNotFoundException e) {  
					logger.error("IOException error:", e);  
				}  
			}  
		}  
	}  
  
	/** 
	 * 根据过滤规则判断类名 
	 * @param className 
	 * @return 
	 */  
	public Class<?> filterClassName(String className) throws ClassNotFoundException {  
		if (!className.endsWith(".class")) {  
			return null;  
		}
		className = className.substring(0, className.length() - 6)
			.replaceAll("/", ".");
//		System.out.println(className);
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		if (null == this.classFilters || this.classFilters.length == 0) {  
			return clazz;  
		}  
		boolean flag = false;
		
		for (Class annoType : classFilters) {
			if (clazz.getAnnotation(annoType) != null) {
				flag = true;
				break;
			}
		}  
		return (checkInOrEx && flag) || (!checkInOrEx && !flag) ? clazz : null;  
	}  
  
	/** 
	 * @return the excludeInner 
	 */  
	public boolean isExcludeInner() {  
		return excludeInner;  
	}  
  
	/** 
	 * @return the checkInOrEx 
	 */  
	public boolean isCheckInOrEx() {  
		return checkInOrEx;  
	}
  
	/** 
	 * @param pExcludeInner the excludeInner to set 
	 */  
	public void setExcludeInner(boolean pExcludeInner) {  
		excludeInner = pExcludeInner;  
	}  
  
	/** 
	 * @param pCheckInOrEx the checkInOrEx to set 
	 */  
	public void setCheckInOrEx(boolean pCheckInOrEx) {  
		checkInOrEx = pCheckInOrEx;  
	}   
  
	/** 
	 * @param args 
	 */  
	public static void main(String[] args) {  
  
		// 自定义过滤规则  
  
		// 创建一个扫描处理器，排除内部类 扫描符合条件的类  
		ClassScaner handler = new ClassScaner(true, true, Service.class);  
  
		System.out  
				.println("开始递归扫描jar文件的包：org.apache.commons.io 下符合自定义过滤规则的类...");  
		Set<Class<?>> calssList = handler.getPackageAllClasses(  
				"org.apache.commons.io", true);  
		for (Class<?> cla : calssList) {  
			System.out.println(cla.getName());  
		}
	}  
}  