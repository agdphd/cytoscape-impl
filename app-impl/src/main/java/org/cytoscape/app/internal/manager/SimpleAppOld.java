package org.cytoscape.app.internal.manager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.exception.AppDisableException;
import org.cytoscape.app.internal.exception.AppInstallException;
import org.cytoscape.app.internal.exception.AppInstanceException;
import org.cytoscape.app.internal.exception.AppUninstallException;
import org.cytoscape.app.swing.CySwingAppAdapter;

public class SimpleAppOld extends App {
	/** 
	 * The name of the key in the app jar's manifest file that indicates the fully-qualified name 
	 * of the class to instantiate upon app installation. 
	 * */
	public static final String APP_CLASS_TAG = "Cytoscape-App";
	
	/**
	 * The name of the key in the app jar's manifest file indicating the human-readable
	 * name of the app
	 */
	public static final String APP_READABLE_NAME_TAG = "Cytoscape-App-Name";

	/**
	 * The name of the key in the app jar's manifest file indicating the version of the app
	 * in the format major.minor.patch[-tag], eg. 3.0.0-SNAPSHOT or 1.2.3
	 */
	public static final String APP_VERSION_TAG = "Cytoscape-App-Version";
	
	/**
	 * The name of the key in the app jar's manifest file indicating the major versions of
	 * Cytoscape that the app is known to be compatible with in comma-delimited form
	 */
	public static final String APP_COMPATIBLE_TAG = "Cytoscape-API-Compatibility";
	
	/**
	 * A regular expression representing valid app versions, which are in the format major.minor[.patch][-tag],
	 * eg. 3.0.0-SNAPSHOT, or 3.0.
	 */
	public static final Pattern APP_VERSION_TAG_REGEX = Pattern.compile("(0|([1-9]+\\d*))\\.(\\d)+(\\.(\\d)+)?(-.*)?");

	@Override
	public Object createAppInstance(CySwingAppAdapter appAdapter) throws AppInstanceException {
		
		File installFile = this.getAppTemporaryInstallFile();
		URL appURL = null;
		try {
			appURL = installFile.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new AppInstanceException("Unable to obtain URL for file: " 
					+ installFile + ". Reason: " + e.getMessage());
		}
		
		// TODO: Currently uses the CyAppAdapter's loader to load apps' classes. Is there reason to use a different one?
		ClassLoader appClassLoader = new URLClassLoader(
				new URL[]{appURL}, appAdapter.getClass().getClassLoader());
		
		// Attempt to load the class
		Class<?> appEntryClass = null;
		try {
			 appEntryClass = appClassLoader.loadClass(this.getEntryClassName());
		} catch (ClassNotFoundException e) {
			
			throw new AppInstanceException("Class " + this.getEntryClassName() + " not found in URL: " + appURL);
		}
		
		// Attempt to obtain the constructor
		Constructor<?> constructor = null;
		try {
			try {
				constructor = appEntryClass.getConstructor(CyAppAdapter.class);
			} catch (SecurityException e) {
				throw new AppInstanceException("Access to the constructor for " + appEntryClass + " denied.");
			} catch (NoSuchMethodException e) {
				throw new AppInstanceException("Unable to find a constructor for " + appEntryClass 
						+ " that takes a CyAppAdapter as its argument.");
			}
		} catch (AppInstanceException e) {
			try {
				constructor = appEntryClass.getConstructor(CySwingAppAdapter.class);
			} catch (SecurityException e2) {
				throw new AppInstanceException("Access to the constructor for " + appEntryClass 
						+ " taking a CySwingAppAdapter as its argument denied.");
			} catch (NoSuchMethodException e2) {
				throw new AppInstanceException("Unable to find an accessible constructor that takes either" 
						+ " a CyAppAdapter or a CySwingAppAdapter as its argument.");
			}
		}
		
		// Attempt to instantiate the app's class that extends AbstractCyApp or AbstractCySwingApp.
		Object appInstance = null;
		try {
			appInstance = constructor.newInstance(appAdapter);
		} catch (IllegalArgumentException e) {
			throw new AppInstanceException("Illegal arguments passed to the constructor for the app's entry class: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new AppInstanceException("Error instantiating the class " + appEntryClass + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new AppInstanceException("Access to constructor denied: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new AppInstanceException("App constructor threw exception: " + e.toString());
		}
		
		return appInstance;
	}

	@Override
	public void install(AppManager appManager) throws AppInstallException {
		// Use the default installation method of copying over the file,
		// creating an instance, and registering with the app manager.
		defaultInstall(appManager);
	}

	@Override
	public void uninstall(AppManager appManager) throws AppUninstallException {
		
		// Use the default uninstallation procedure which is to move the app to
		// the uninstalled apps directory
		defaultUninstall(appManager);
				
		// Simple apps require a Cytoscape restart to be uninstalled
		setStatus(AppStatus.TO_BE_UNINSTALLED);
	}
	
	@Override
	public void disable(AppManager appManager) throws AppDisableException {
		// TODO Auto-generated method stub
		
	}

}
