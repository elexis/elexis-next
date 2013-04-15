/*******************************************************************************
 * Copyright (c) 2008-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.preferences.inputs;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A file editor that allows an inexistent file to be chosen.
 * 
 * @author gerry
 * 
 */
public class InexistingFileOKFileFieldEditor extends FileFieldEditor {
	public InexistingFileOKFileFieldEditor(String name, String title, Composite parent){
		super(name, title, parent);
		setEmptyStringAllowed(true);
	}
	
	@Override
	public boolean isValid(){
		return true;
	}
	
}
