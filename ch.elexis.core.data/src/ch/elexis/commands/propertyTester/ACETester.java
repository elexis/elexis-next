/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     G. Weirich - initial API and implementation
 ******************************************************************************/
package ch.elexis.commands.propertyTester;

import java.lang.reflect.Field;

import org.eclipse.core.expressions.PropertyTester;

import ch.elexis.Hub;
import ch.elexis.admin.ACE;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.util.Log;

public class ACETester extends PropertyTester {
	
	private static Log logger = Log.get(ACETester.class.getName());
	
	public ACETester(){}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("ACE".equals(property)) {
			if (args.length > 0) {
				String right = ((String) args[0]);
				try {
					Field ACEfield = AccessControlDefaults.class.getField(right);
					if (ACEfield.getType().equals(ACE.class)) {
						return Hub.acl.request((ACE) ACEfield.get(null));
					}
				} catch (SecurityException e) {
					logger.log(e, "Security Exception on right " + right, Log.ERRORS);
					return false;
				} catch (NoSuchFieldException e) {
					logger.log(e, "NoSuchFieldException on right " + right, Log.ERRORS);
					return false;
				} catch (IllegalArgumentException e) {
					logger.log(e, "IllegalArgumentException on right " + right, Log.ERRORS);
					return false;
				} catch (IllegalAccessException e) {
					logger.log(e, "IllegalAccessException on right " + right, Log.ERRORS);
					return false;
				}
			}
		}
		
		return false;
	}
	
}
