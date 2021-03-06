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
package ch.elexis.eigenleistung;

import org.eclipse.swt.SWT;

import ch.elexis.data.Eigenleistung;
import ch.elexis.data.PersistentObject;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.DefaultLabelProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.codesystems.CodeSelectorFactory;

public class EigenleistungCodeSelectorFactory extends CodeSelectorFactory {
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		return new ViewerConfigurer(
			// new LazyContentProvider(cv,dataloader,null),
			new EigenleistungLoader(cv), new DefaultLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"code=Code" //$NON-NLS-1$
			}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return Eigenleistung.class;
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return Eigenleistung.CODESYSTEM_NAME;
	}
	
}
