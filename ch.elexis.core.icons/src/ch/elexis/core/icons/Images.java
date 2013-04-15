/*******************************************************************************
 * Copyright (c) 2013, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Descher - initial implementation
 *******************************************************************************/
package ch.elexis.core.icons;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Central image repository. Supersedes the images managed in {@link Desk}. This image registry
 * takes its values from a properties file, namely <code>ch.elexis.iconset.properties</code>. This
 * properties file is to be provided, together with the respective icons by a contributing fragment.
 * See <code>ch.elexis.core.icons</code> for the basic open source icons contribution.
 * 
 * @author M. Descher / MEDEVIT Austria
 */
public enum Images {
	/** Returning to some home place */
	IMG_HOME,
	/** An Address label */
	IMG_ADRESSETIKETTE,
	/** a label with patient data */
	IMG_PATIENTETIKETTE,
	/** a label with some identity number (e.g. for lab orders) */
	IMG_VERSIONEDETIKETTE,
	/** a bomb icon */
	IMG_BOMB,
	/** an organisation icon */
	IMG_ORGANISATION,
	/** an organisation add icon */
	IMG_ORGANISATION_ADD,
	/** a transparent and empty 16x16 image */
	IMG_EMPTY_TRANSPARENT,
	/** a telephone icon */
	IMG_TELEPHONE,
	/** a mobile phone */
	IMG_MOBILEPHONE,
	/** a fax */
	IMG_FAX,
	/** a mail icon */
	IMG_MAIL,
	/** a web icon */
	IMG_WEB,
	/** a contact detail icon */
	IMG_CONTACT_DETAIL,
	/** deleting items */
	IMG_DELETE,
	/** a male */
	IMG_MANN,
	/** a female */
	IMG_FRAU,
	/** a group of two persons */
	IMG_GROUP,
	/** a money icon */
	IMG_MONEY,
	/** a Very Important Person/Patient */
	IMG_VIP,
	/** a Very Important Person/Patient overlay Icon */
	IMG_VIP_OVERLAY,
	/** a printer */
	IMG_PRINTER,
	/** a filter */
	IMG_FILTER,
	/** creating a new Object */
	IMG_NEW,
	/** importing items */
	IMG_IMPORT,
	/** exporting items */
	IMG_EXPORT, IMG_GOFURTHER,
	/** the 48x48 pixel version of the elexis(tm) logo */
	IMG_LOGO48,
	/** editing an item */
	IMG_EDIT,
	/** warning */
	IMG_ACHTUNG,
	/** red bullet */
	IMG_BULLET_RED,
	/** green bullet */
	IMG_BULLET_GREEN,
	/** yellow bullet */
	IMG_BULLET_YELLOW,
	/** grey bullet */
	IMG_BULLET_GREY,
	/** ok */
	IMG_OK,
	/** tick */
	IMG_TICK,
	/** error */
	IMG_FEHLER,
	/** refresh/reload */
	IMG_REFRESH,
	/** wizard/doing things automagically */
	IMG_WIZARD,
	/** add something to an existing object */
	IMG_ADDITEM,
	/** remove something from an existing object */
	IMG_REMOVEITEM,
	/** excalamation mark red */
	IMG_AUSRUFEZ_ROT,
	/** exclamantion mark */
	IMG_AUSRUFEZ,
	/** computer network */
	IMG_NETWORK,
	/** a book */
	IMG_BOOK,
	/** a person */
	IMG_PERSON,
	/** a person with an OK mark */
	IMG_PERSON_OK,
	/** a person with an ADD sign */
	IMG_PERSON_ADD,
	/** a greyed out person */
	IMG_PERSON_GREY,
	/** a diskette symbol */
	IMG_DISK,
	/** a closed lock */
	IMG_LOCK_CLOSED,
	/** An opened lock */
	IMG_LOCK_OPEN,
	/** Clipboard symbol */
	IMG_CLIPBOARD,
	/** Arrow right */
	IMG_NEXT,
	/** Arrow left */
	IMG_PREVIOUS,
	/** Arrow up */
	IMG_ARROWUP,
	/** Arrow down */
	IMG_ARROWDOWN,
	/** Arrow down to rectangle */
	IMG_ARROWDOWNTORECT,
	/** undo */
	IMG_UNDO,
	/** a 8px pencil symbol */
	IMG_PENCIL_8PX,
	/** a pill symbol */
	IMG_PILL,
	/** a link symbol */
	IMG_LINK,
	/** Move to upper list */
	IMG_MOVETOUPPERLIST,
	/** Move to lower list */
	IMG_MOVETOLOWERLIST,
	/** clear input field */
	IMG_CLEAR,
	/** Perspective Konsultation */
	IMG_PERSPECTIVE_KONS,
	/** Perspective Contacts */
	IMG_PERSPECTIVE_CONTACTS,
	/** Perspective letters */
	IMG_PERSPECTIVE_LETTERS,
	/** Perspective leistungen */
	IMG_PERSPECTIVE_LEISTUNGEN,
	/** Perspective articles */
	IMG_PERSPECTIVE_ARTICLES,
	/** Perspective reminder */
	IMG_PERSPECTIVE_REMINDERS,
	/** Perspective bills */
	IMG_PERSPECTIVE_BILLS,
	/** Perspective blackboard */
	IMG_PERSPECTIVE_BBS,
	/** Perspective orders */
	IMG_PERSPECTIVE_ORDERS;
	
	private Images(){}
	
	/**
	 * Returns an image. Clients do not need to dispose the image, it will be disposed
	 * automatically.
	 * 
	 * @return an {@link Image}
	 */
	public Image getImage(){
		Image image = JFaceResources.getImageRegistry().get(this.name());
		if (image == null) {
			addImageDescriptor(this.name());
			image = JFaceResources.getImageRegistry().get(this.name());
		}
		return image;
	}
	
	/**
	 * @return {@link ImageDescriptor} for the current image
	 */
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor id = null;
		id = JFaceResources.getImageRegistry().getDescriptor(this.name());
		if (id == null) {
			addImageDescriptor(this.name());
			id = JFaceResources.getImageRegistry().getDescriptor(this.name());
		}
		return id;
	}
	
	/**
	 * Opportunistic lookup for a probably existing key.<br>
	 * There may exist keys within the <code>iconset.properties</code> file which are not managed by
	 * this Enumeration. This method allows an opportunistic lookup of such registered images.
	 * 
	 * @param iconKey
	 * @return <code>null</code> if no such image is existent
	 */
	public static Image lookupImage(String iconKey){
		Image image = JFaceResources.getImageRegistry().get(iconKey);
		if (image == null) {
			boolean ret = addImageDescriptor(iconKey);
			if (!ret)
				return null;
			image = JFaceResources.getImageRegistry().get(iconKey);
		}
		return image;
	}
	
	private static boolean addImageDescriptor(String name){
		try {
			ResourceBundle iconsetProperties = ResourceBundle.getBundle("iconset");
			String fileName = iconsetProperties.getString(name);
			ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, fileName);
			JFaceResources.getImageRegistry().put(name, id);
		} catch (MissingResourceException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
}
