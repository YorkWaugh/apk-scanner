package com.apkscanner.core.installer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.apkscanner.tool.adb.AdbWrapper;
import com.apkscanner.util.ConsolCmd;
import com.apkscanner.util.FileUtil;
import com.apkscanner.util.Log;
import com.apkscanner.util.ConsolCmd.ConsoleOutputObserver;

public class ApkInstaller
{
	static public interface ApkInstallerListener {
		public static final int CMD_INSTALL = 1;
		public static final int CMD_UNINSTALL = 2;
		public static final int CMD_PUSH = 3;
		public static final int CMD_PULL = 4;

		public void OnMessage(String msg);
		public void OnError(int cmdType, String device);
		public void OnSuccess(int cmdType, String device);
		public void OnCompleted(int cmdType, String device);
	}

	private AdbWrapper adbCommander;
	private ApkInstallerListener listener;
	private ConsoleOutputObserver coutListener;
	private String device;
	
	public ApkInstaller() {
		this(null, null);	
	}
	
	public ApkInstaller(String device) {
		this(device, null);	
	}
	
	public ApkInstaller(String device, ApkInstallerListener listener) {
		adbCommander = new AdbWrapper(null, null);
		
		setDevice(device);
		setListener(listener);
	}
	
	public void setDevice(String device) {
		if(adbCommander == null) return;
		adbCommander.setDevice(device);
		this.device = device;
	}
	
	public void setListener(ApkInstallerListener listener) {
		this.listener = listener;
		
		if(adbCommander == null) {
			coutListener = null;
			return;
		}

		if(listener == null) {
			adbCommander.setListener(null);
			coutListener = null;
			return;
		}
		
		coutListener = new ConsoleOutputObserver() {
			@Override
			public boolean ConsolOutput(String output) {
				if(ApkInstaller.this.listener != null) {
					ApkInstaller.this.listener.OnMessage(output);
				}
				return false;
			}
		};
		adbCommander.setListener(coutListener);
	}
	
	public boolean uninstallApk(String packageName) {
		if(adbCommander == null) return false;
		String[] cmdResult = adbCommander.uninstall(packageName);
		return (cmdResult != null && cmdResult.length > 0 && "Success".equals(cmdResult[0]));
	}
	
	public boolean removeApk(String apkPath) {
		if(adbCommander == null) return false;

		boolean result = adbCommander.root();
		result = result && adbCommander.remount();
		if(result) {
			String[] cmdResult = adbCommander.shell(new String[] {"rm", "-r", apkPath});
			if(cmdResult.length > 0 && !cmdResult[0].isEmpty()) {
				result = false;
			}
		}
		return result;
	}
	
	public void PushApk(final String srcApkPath, final String destApkPath, final String libPath)
	{
		if(destApkPath == null || srcApkPath == null || srcApkPath.isEmpty()) {
			if(listener != null) {
				listener.OnError(ApkInstallerListener.CMD_PUSH, device);
				listener.OnCompleted(ApkInstallerListener.CMD_PUSH, device);
			}
			return;
		}

		new Thread(new Runnable() {
			public void run()
			{
				String[][] result;
				List<String[]> cmd = new ArrayList<String[]>();
				
				adbCommander.root();
				adbCommander.remount();
				adbCommander.shell(new String[] {"setenforce", "0"});
				adbCommander.push(srcApkPath, destApkPath);
				//Log.i(this.srcApkPath + " to " + this.destApkPath);
				
				//Log.i("libpath " + libPath);
				if(libPath != null && (new File(libPath)).exists()) {
					String[] selAbi = selectAbi(libPath);
					String abi32 = selAbi[0];
					String abi64 = selAbi[1];
					
					Iterator<String> libPaths = FileUtil.findFiles(new File(libPath), ".so", null).iterator();
					while(libPaths.hasNext()) {
						String path = libPaths.next();
						if(!(new File(path)).exists()) {
							Log.w("no such file : " + path);
							continue;
						}
						String abi = path.replaceAll(libPath.replace("\\", "\\\\")+"([^\\\\/]*).*","$1");
						//Log.i("abi = " + abi);
						if(abi.equals(abi32)) {
							adbCommander.push(path, "/system/lib/");
							//Log.i("push " + path + " " + "/system/lib/");
						} else if (abi.equals(abi64)) {
							adbCommander.push(path, "/system/lib64/");
							//Log.i("push " + path + " " + "/system/lib64/");						
						} else {
							//Log.w("ignored path : " + path);
						}
					}
				}
				//cmd.add(new String[] {adbCmd, "-s", this.device, "shell", "echo", "Compleated..."});
				
				result = ConsolCmd.exc(cmd.toArray(new String[0][0]),true,new ConsolCmd.ConsoleOutputObserver() {
					@Override
					public boolean ConsolOutput(String output) {
						//sendMessage(output.replaceAll("^.*adb(\\.exe)?", "adb"));
				    	if(output.equals("* failed to start daemon *")
				    		|| output.equals("error: device not found")
				    		|| output.equals("adbd cannot run as root in production builds")
				    		|| output.matches(".*Permission denied.*")
				    	) {
				    		//Log.e(">>>>>>>>>>>> fail : " + output);
				    		return false;
				    	}
				    	return true;
					}
				});

				//Log.i("cmd.size() " + cmd.size() + ", result.length " + result.length);
				if(listener != null) {
					listener.OnCompleted(ApkInstallerListener.CMD_PUSH, device);
					if(cmd.size() == result.length) {
						//sendMessage("Success...");
						listener.OnSuccess(ApkInstallerListener.CMD_PUSH, device);
					} else {
						//sendMessage("Failure...");
						listener.OnError(ApkInstallerListener.CMD_PUSH, device);
					}					
				}
			}
		}).start();

		return;
	}
	
	public void InstallApk(final String apkPath)
	{
		//Log.i("InstallApk() device : " + name + ", apkPath: " + apkPath);
		if(apkPath == null || apkPath.isEmpty()) {
			if(listener != null) {
				listener.OnError(ApkInstallerListener.CMD_INSTALL, device);
				listener.OnCompleted(ApkInstallerListener.CMD_INSTALL, device);
			}
			return;
		}

		new Thread(new Runnable() {
			public void run()
			{
				String[] result = adbCommander.install(apkPath);

				if(listener != null) {
					listener.OnCompleted(ApkInstallerListener.CMD_INSTALL, device);
					if(result.length >= 3 && result[2].equals("Success")) {
						listener.OnSuccess(ApkInstallerListener.CMD_INSTALL, device);
					} else {
						listener.OnError(ApkInstallerListener.CMD_INSTALL, device);
					}					
				}
			}
		}).start();
		
		return;
	}
	
	public void PullApk(final String srcApkPath, final String destApkPath)
	{
		//Log.i("PullApk() device : " + name + ", apkPath: " + srcApkPath);
		if(destApkPath == null || srcApkPath == null || srcApkPath.isEmpty()) {
			if(listener != null) {
				listener.OnError(ApkInstallerListener.CMD_PULL, device);
				listener.OnCompleted(ApkInstallerListener.CMD_PULL, device);
			}
			return;
		}

		new Thread(new Runnable() {
			public void run()
			{
				boolean result = adbCommander.pull(srcApkPath, destApkPath);

				if(listener != null) {
					listener.OnCompleted(ApkInstallerListener.CMD_PULL, device);
					if(result) {
						listener.OnSuccess(ApkInstallerListener.CMD_PULL, device);
					} else {
						listener.OnError(ApkInstallerListener.CMD_PULL, device);
					}					
				}
			}
		}).start();

		return;
	}

	private String[] selectAbi(String LibSourcePath) {
		String abiList32 = adbCommander.getProp("ro.product.cpu.abilist32");
		String abiList64 = adbCommander.getProp("ro.product.cpu.abilist64");
		if(!abiList32.isEmpty()) abiList32 += ",";
		if(!abiList64.isEmpty()) abiList64 += ",";
		
		String abi64 = null;
		String abi32 = null;
		for (String s : (new File(LibSourcePath)).list()) {
			if(s.startsWith("arm64")) {
				if(abiList64.matches(".*" + s + ",.*")) {
					//Log.i("device support this abi : " + s);
					if(abi64 == null) {
						abi64 = s;
					} else {
						int old_ver = Integer.parseInt(abi64.replaceAll("arm64[^0-9]*([0-9]*).*", "0$1"));
						int new_ver = Integer.parseInt(s.replaceAll("arm64[^0-9]*([0-9]*).*", "0$1"));
						if(old_ver < new_ver) {
							abi64 = s;
						} else {
							//Log.w("The version is lower than previous versions. : " + s + " < " + abi64);
						}
					}
				} else {
					//Log.w("device not support this abi : " + s);
				}
			} else if(s.startsWith("armeabi")) {
				if(abiList32.matches(".*" + s + ",.*")) {
					//Log.i("device support this abi : " + s);
					if(abi32 == null) {
						abi32 = s;
					} else {
						int old_ver = Integer.parseInt(abi32.replaceAll("armeabi[^0-9]*([0-9]*).*", "0$1"));
						int new_ver = Integer.parseInt(s.replaceAll("armeabi[^0-9]*([0-9]*).*", "0$1"));
						if(old_ver < new_ver) {
							abi32 = s;
						} else {
							//Log.w("The version is lower than previous versions. : " + s + " < " + abi32);
						}
					}
				} else {
					//Log.w("device not support this abi : " + s);
				}
			} else {
				//Log.w("Unknown abi type : " + s);
			}
			//Log.i("LibSourcePath list = " + s.replaceAll("([^-]*)", "$1"));
		}
		//Log.i("abi64 : " + abi64 + ", abi32 : " + abi32);
		return new String[] { abi32, abi64 };
	}
}
