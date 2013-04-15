/*******************************************************************************
 * Copyright (c) 2008-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *    $Id$
 *******************************************************************************/

package ch.elexis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.preferences.PreferenceInitializer;
import ch.elexis.preferences.SettingsPreferenceStore;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class Desk implements IApplication {
	private static FormToolkit theToolkit = null;
	
	private static Display theDisplay = null;
	private static ImageRegistry theImageRegistry = null;
	private static ColorRegistry theColorRegistry = null;
	private static HashMap<String, Cursor> cursors = null;
	private static Map<String, String> args = null;
	
	public static final String COL_RED = "rot"; //$NON-NLS-1$
	public static final String COL_GREEN = "gruen"; //$NON-NLS-1$
	public static final String COL_BLUE = "blau"; //$NON-NLS-1$
	public static final String COL_SKYBLUE = "himmelblau"; //$NON-NLS-1$
	public static final String COL_LIGHTBLUE = "hellblau"; //$NON-NLS-1$
	public static final String COL_BLACK = "schwarz"; //$NON-NLS-1$
	public static final String COL_GREY = "grau"; //$NON-NLS-1$
	public static final String COL_WHITE = "weiss"; //$NON-NLS-1$
	public static final String COL_DARKGREY = "dunkelgrau"; //$NON-NLS-1$
	public static final String COL_LIGHTGREY = "hellgrau"; //$NON-NLS-1$
	public static final String COL_GREY60 = "grau60"; //$NON-NLS-1$
	public static final String COL_GREY20 = "grau20"; //$NON-NLS-1$
	
	public static final String CUR_HYPERLINK = "cursor_hyperlink"; //$NON-NLS-1$
	
	public Desk(){}
	
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception{
		args = context.getArguments();
		if (args.containsKey("--clean-all")) { //$NON-NLS-1$
			String p = PreferenceInitializer.getDefaultDBPath();
			FileTool.deltree(p);
			Hub.localCfg.clear();
			Hub.localCfg.flush();
		}
		
		try {
			
			int returnCode =
				PlatformUI.createAndRunWorkbench(getDisplay(), new ApplicationWorkbenchAdvisor());
			// Die Funktion kehrt erst beim Programmende zurück.
			Hub.heart.suspend();
			System.out.println(Messages.Desk_37);
			Hub.localCfg.flush();
			if (Hub.globalCfg != null) {
				Hub.globalCfg.flush();
			}
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			ex.printStackTrace();
			return -1;
		} finally { // aufräumen
			if (theToolkit != null) {
				theToolkit.dispose();
			}
			if (theImageRegistry != null) {
				theImageRegistry.dispose();
			}
			/*
			 * if ((theDisplay != null) && (!theDisplay.isDisposed())) { theDisplay.dispose(); }
			 */
		}
		
	}
	
	public void stop(){}
	
	/**
	 * return a command line argument/launch parameter
	 * 
	 * @param name
	 *            name of the argument
	 * @return
	 */
	public static String getCommandLineArgument(String name){
		if (args == null) {
			return null;
		}
		return args.get(name);
	}
	
	/**
	 * get the base directory for images. This is dependend from the plaf chosen. If no plaf was
	 * chosen, "modern" will be assumed.
	 * 
	 * @return a string denoting the directory with the images for the current plaf
	 */
	static String getImageBase(){
		String imageBase = Hub.localCfg.get(PreferenceConstants.USR_PLAF, null);
		if (imageBase == null) {
			imageBase = "/rsc/plaf/modern/icons/"; //$NON-NLS-1$
		} else {
			imageBase += "/icons/"; //$NON-NLS-1$
		}
		return imageBase;
	}
	
	public static ImageRegistry getImageRegistry(){
		if (theImageRegistry == null) {
			theImageRegistry = new ImageRegistry(getDisplay());
			synchronized (theImageRegistry) {
			}
		}
		return theImageRegistry;
	}
	
	/**
	 * Return an ImageDescriptor. The Descriptor will be searched in the ImageRegistry first. If not
	 * found, it will be searched as image file in the directory denoted by the current plaf. Images
	 * with the extensions of png, gif and ico will be searched in this given order. If still no
	 * image is found, it will be searched in rsc/
	 * 
	 * @param imagename
	 *            the name of the image or the imagefile (without extension)
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String imagename){
		ImageDescriptor ret = getImageRegistry().getDescriptor(imagename);
		if (ret == null) {
			ret = Hub.getImageDescriptor(getImageBase() + imagename + ".png"); //$NON-NLS-1$
			if (ret == null) {
				ret = Hub.getImageDescriptor(getImageBase() + imagename + Messages.Desk_32);
			}
			if (ret == null) {
				ret = Hub.getImageDescriptor(getImageBase() + imagename + Messages.Desk_33);
			}
			if (ret == null) {
				ret = Hub.getImageDescriptor("rsc/" + imagename); //$NON-NLS-1$
			}
			if (ret != null) {
				getImageRegistry().put(imagename, ret);
			}
		}
		return ret;
	}
	
	/**
	 * Return an image with a specified name.
	 * 
	 * @see getImageDescriptor(String)
	 * @param name
	 *            the name of the image to retrieve
	 * @return the Image or null if no such image was found
	 *
	 */
	// TODO now only used by Sticker#getImage()
	public static Image getImage(String name){
		Image ret = getImageRegistry().get(name);
		if (ret == null) {
			ImageDescriptor id = getImageDescriptor(name);
			if (id != null) {
				ret = id.createImage();
				// getImageRegistry().remove(name);
				// getImageRegistry().put(name, ret);
			}
		}
		return ret;
	}
	
	/** shortcut for getColorRegistry().get(String col) */
	public static Color getColor(String desc){
		return getColorRegistry().get(desc);
	}
	
	public static ColorRegistry getColorRegistry(){
		
		if (theColorRegistry == null) {
			theColorRegistry = new ColorRegistry(getDisplay(), true);
		}
		return theColorRegistry;
	}
	
	public static FormToolkit getToolkit(){
		if (theToolkit == null) {
			theToolkit = new FormToolkit(getDisplay());
		}
		return theToolkit;
	}
	
	public static Display getDisplay(){
		if (theDisplay == null) {
			if (PlatformUI.isWorkbenchRunning()) {
				theDisplay = PlatformUI.getWorkbench().getDisplay();
				// theDisplay = PlatformUI.createDisplay();
			}
		}
		if (theDisplay == null) {
			theDisplay = PlatformUI.createDisplay();
		}
		return theDisplay;
	}
	
	public static void updateFont(String cfgName){
		FontRegistry fr = JFaceResources.getFontRegistry();
		FontData[] fd =
			PreferenceConverter.getFontDataArray(new SettingsPreferenceStore(Hub.userCfg), cfgName);
		fr.put(cfgName, fd);
	}
	
	public static Font getFont(String cfgName){
		FontRegistry fr = JFaceResources.getFontRegistry();
		if (!fr.hasValueFor(cfgName)) {
			FontData[] fd =
				PreferenceConverter.getFontDataArray(new SettingsPreferenceStore(Hub.userCfg),
					cfgName);
			fr.put(cfgName, fd);
		}
		return fr.get(cfgName);
	}
	
	public static Font getFont(String name, int height, int style){
		String key = name + ":" + Integer.toString(height) + ":" + Integer.toString(style); //$NON-NLS-1$ //$NON-NLS-2$
		FontRegistry fr = JFaceResources.getFontRegistry();
		if (!fr.hasValueFor(key)) {
			FontData[] fd = new FontData[] {
				new FontData(name, height, style)
			};
			fr.put(key, fd);
		}
		return fr.get(key);
	}
	
	public static Cursor getCursor(String name){
		if (cursors == null) {
			cursors = new HashMap<String, Cursor>();
		}
		Cursor ret = cursors.get(name);
		if (ret == null) {
			if (name.equals(CUR_HYPERLINK)) {
				ret = getDisplay().getSystemCursor(SWT.CURSOR_HAND);
				cursors.put(name, ret);
			}
		}
		return ret;
	}
	
	/**
	 * Eine Color aus einer RGB-Beschreibung als Hex-String herstellen
	 * 
	 * @param coldesc
	 *            Die Farbe als Beschreibung in Hex-Form
	 * @return die Farbe als Color, ist in Regisry gespeichert
	 */
	public static Color getColorFromRGB(final String coldesc){
		String col = StringTool.pad(StringTool.LEFT, '0', coldesc, 6);
		if (!getColorRegistry().hasValueFor(col)) {
			RGB rgb;
			try {
				rgb =
					new RGB(Integer.parseInt(col.substring(0, 2), 16), Integer.parseInt(col
						.substring(2, 4), 16), Integer.parseInt(col.substring(4, 6), 16));
			} catch (NumberFormatException nex) {
				ExHandler.handle(nex);
				rgb = new RGB(100, 100, 100);
			}
			getColorRegistry().put(col, rgb);
		}
		return getColorRegistry().get(col);
	}
	
	/**
	 * Eine Hex-String Beschreibung einer Farbe liefern
	 * 
	 * @param rgb
	 *            Die Farbe in RGB-Form
	 * @return
	 */
	public static String createColor(final RGB rgb){
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(StringTool.pad(StringTool.LEFT, '0', Integer.toHexString(rgb.red), 2))
				.append(StringTool.pad(StringTool.LEFT, '0', Integer.toHexString(rgb.green), 2))
				.append(StringTool.pad(StringTool.LEFT, '0', Integer.toHexString(rgb.blue), 2));
			String srgb = sb.toString();
			getColorRegistry().put(srgb, rgb);
			return srgb;
		} catch (NumberFormatException nex) {
			getColorRegistry().put("A0A0A0", new RGB(0xa0, 0xa0, 0xa0));
			return "A0A0A0";
		}
	}
	
	public static Shell getTopShell(){
		return getDisplay().getActiveShell();
	}
	
	/**
	 * Run a runnable asynchroneously in the UI Thread The method will immediately return (not wait
	 * for the runnable to exit)
	 */
	public static void asyncExec(Runnable runnable){
		Display disp = getDisplay();
		disp.asyncExec(runnable);
	}
	
	/**
	 * Run a runnable synchroneously in the UI Thread. The method will not return until the runnable
	 * exited
	 * 
	 * @param runnable
	 */
	public static void syncExec(Runnable runnable){
		getDisplay().syncExec(runnable);
		// BusyIndicator.showWhile(getDisplay(), runnable);
	}
}
