/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.core.ui.wizards;

import java.util.Hashtable;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.elexis.Hub;
import ch.elexis.data.PersistentObject;
import ch.elexis.status.ElexisStatus;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLinkException;
import ch.rgw.tools.StringTool;

public class DBConnectWizard extends Wizard {
	DBConnectFirstPage first = new DBConnectFirstPage(
		Messages.getString("DBConnectWizard.typeOfDB")); //$NON-NLS-1$
	DBConnectSecondPage sec = new DBConnectSecondPage(
		Messages.getString("DBConnectWizard.Credentials")); //$NON-NLS-1$
	
	public DBConnectWizard(){
		super();
		setWindowTitle(Messages.getString("DBConnectWizard.connectDB")); //$NON-NLS-1$
	}
	
	@Override
	public void addPages(){
		addPage(first);
		addPage(sec);
	}
	
	@Override
	public boolean performFinish(){
		int ti = first.dbTypes.getSelectionIndex();
		String server = first.server.getText();
		String db = first.dbName.getText();
		String user = sec.name.getText();
		String pwd = sec.pwd.getText();
		JdbcLink j = null;
		switch (ti) {
		case 0:
			j = JdbcLink.createMySqlLink(server, db);
			break;
		case 1:
			j = JdbcLink.createPostgreSQLLink(server, db);
			break;
		case 2:
			j = JdbcLink.createH2Link(db);
			break;
		default:
			j = null;
			return false;
		}
		try {
			j.connect(user, pwd);
		} catch (JdbcLinkException je) {
			ElexisStatus status =
				new ElexisStatus(ElexisStatus.ERROR, Hub.PLUGIN_ID, ElexisStatus.CODE_NOFEEDBACK,
					Messages.getString("DBConnectWizard.couldntConnect"), je);
			StatusManager.getManager().handle(status, StatusManager.BLOCK);
			return false;
		}
		// IPreferencesService service=Platform.getPreferencesService();
		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(PersistentObject.CFG_DRIVER, j.getDriverName());
		h.put(PersistentObject.CFG_CONNECTSTRING, j.getConnectString());
		h.put(PersistentObject.CFG_USER, user);
		h.put(PersistentObject.CFG_PWD, pwd);
		h.put(PersistentObject.CFG_TYPE, first.dbTypes.getItem(ti));
		try {
			String conn = StringTool.enPrintable(PersistentObject.flatten(h));
			ConfigurationScope pref = new ConfigurationScope();
			IEclipsePreferences node = pref.getNode("connection");
			node.put(Hub.getCfgVariant(), conn);
			node.flush();
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
		
		/*
		 * IPreferenceStore localstore = new SettingsPreferenceStore(Hub.localCfg);
		 * localstore.setValue(PreferenceConstants.DB_CLASS,j.getDriverName());
		 * localstore.setValue(PreferenceConstants.DB_CONNECT,j.getConnectString());
		 * localstore.setValue(PreferenceConstants.DB_USERNAME,user);
		 * localstore.setValue(PreferenceConstants.DB_PWD,pwd);
		 * localstore.setValue(PreferenceConstants.DB_TYP,first.dbTypes.getItem(ti));
		 * Hub.localCfg.flush();
		 */
		return PersistentObject.connect(j);
	}
}