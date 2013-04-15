/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import ch.elexis.data.PersistentObject;
import ch.elexis.dialogs.AddMultiplikatorDialog;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class MultiplikatorEditor extends Composite {
	// String myClass;
	List list;
	final Stm stm = PersistentObject.getConnection().getStatement();
	String typeName;
	
	public MultiplikatorEditor(final Composite prnt, final String clazz){
		super(prnt, SWT.NONE);
		setLayout(new GridLayout());
		setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		list = new List(this, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
		list.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Button bNew = new Button(this, SWT.PUSH);
		bNew.setText(Messages.getString("MultiplikatorEditor.add")); //$NON-NLS-1$
		bNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				AddMultiplikatorDialog amd = new AddMultiplikatorDialog(getShell());
				if (amd.open() == Dialog.OK) {
					TimeTool t = amd.getBegindate();
					String mul = amd.getMult();
					
					MultiplikatorList multis = new MultiplikatorList("VK_PREISE", typeName);
					multis.insertMultiplikator(t, mul);
					list.add(Messages.getString("MultiplikatorEditor.from") + t.toString(TimeTool.DATE_GER) + Messages.getString("MultiplikatorEditor.14") + mul); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});
		reload(clazz);
	}
	
	@Override
	public void dispose(){
		PersistentObject.getConnection().releaseStatement(stm);
	}
	
	@SuppressWarnings("unchecked")
	public void reload(final String typeName){
		this.typeName = typeName;
		ArrayList<String[]> daten = new ArrayList<String[]>();
		ResultSet res = stm.query("SELECT * FROM VK_PREISE WHERE TYP=" + JdbcLink.wrap(typeName)); //$NON-NLS-1$
		try {
			while ((res != null) && (res.next() == true)) {
				String[] row = new String[2];
				row[0] = res.getString("DATUM_VON"); //$NON-NLS-1$
				row[1] = res.getString("MULTIPLIKATOR"); //$NON-NLS-1$
				daten.add(row);
			}
			res.close();
			
			Collections.sort(daten, new Comparator() {
				
				public int compare(final Object o1, final Object o2){
					String[] s1 = (String[]) o1;
					String[] s2 = (String[]) o2;
					return s1[0].compareTo(s2[0]);
				}
				
			});
			
			TimeTool dis = new TimeTool();
			list.removeAll();
			for (String[] s : daten) {
				dis.set(s[0]);
				list.add(Messages.getString("MultiplikatorEditor.from") + dis.toString(TimeTool.DATE_GER) + Messages.getString("MultiplikatorEditor.5") + s[1]); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
	}
	
	/**
	 * Class for manipulation of Multiplikator. <br>
	 * 
	 * @author thomashu
	 * 
	 */
	public static class MultiplikatorList {
		private java.util.List<MultiplikatorInfo> list;
		private String typ;
		private String table;
		
		public MultiplikatorList(String table, String typ){
			this.typ = typ;
			this.table = table;
		}
		
		/**
		 * Update multiRes with ResultSet of all existing Multiplikators
		 */
		private void fetchResultSet(){
			Stm statement = PersistentObject.getConnection().getStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM ").append(table).append(" WHERE TYP=")
				.append(JdbcLink.wrap(typ));
			ResultSet res = statement.query(sql.toString());
			try {
				list = new ArrayList<MultiplikatorEditor.MultiplikatorList.MultiplikatorInfo>();
				while (res.next()) {
					list.add(new MultiplikatorInfo(res.getString("DATUM_VON"), res
						.getString("DATUM_BIS"), res.getString("MULTIPLIKATOR")));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (statement != null)
					PersistentObject.getConnection().releaseStatement(statement);
				
				if (res != null) {
					try {
						res.close();
						res = null;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		public void insertMultiplikator(TimeTool dateFrom, String value){
			TimeTool dateTo = null;
			Stm statement = PersistentObject.getConnection().getStatement();
			try {
				fetchResultSet();
				Iterator<MultiplikatorInfo> iter = list.iterator();
				// update existing multiplier for that date
				while (iter.hasNext()) {
					MultiplikatorInfo info = iter.next();
					TimeTool fromDate = new TimeTool(info.validFrom);
					TimeTool toDate = new TimeTool(info.validTo);
					if (dateFrom.isAfter(fromDate) && dateFrom.isBefore(toDate)) { // if contains
																					// update the to
																					// value of the
																					// existing
																					// multiplikator
						StringBuilder sql = new StringBuilder();
						// update the old to date
						TimeTool newToDate = new TimeTool(dateFrom);
						newToDate.addDays(-1);
						sql.append("UPDATE ")
							.append(table)
							.append(
								" SET DATUM_BIS="
									+ JdbcLink.wrap(newToDate.toString(TimeTool.DATE_COMPACT))
									+ " WHERE DATUM_VON="
									+ JdbcLink.wrap(fromDate.toString(TimeTool.DATE_COMPACT))
									+ " AND TYP=" + JdbcLink.wrap(typ));
						statement.exec(sql.toString());
						// set to date of new multiplikator to to date of old multiplikator
						dateTo = new TimeTool(toDate);
					} else if (dateFrom.isEqual(fromDate)) { // if from equals update the value
						StringBuilder sql = new StringBuilder();
						// update the value and return
						TimeTool newToDate = new TimeTool(dateFrom);
						newToDate.addDays(-1);
						sql.append("UPDATE ")
							.append(table)
							.append(
								" SET MULTIPLIKATOR=" + JdbcLink.wrap(value) + " WHERE DATUM_VON="
									+ JdbcLink.wrap(fromDate.toString(TimeTool.DATE_COMPACT))
									+ " AND TYP=" + JdbcLink.wrap(typ));
						statement.exec(sql.toString());
						return;
					}
				}
				// if we have not found a to Date yet search for oldest existing
				if (dateTo == null) {
					fetchResultSet();
					iter = list.iterator();
					dateTo = new TimeTool("99991231");
					while (iter.hasNext()) {
						MultiplikatorInfo info = iter.next();
						TimeTool fromDate = new TimeTool(info.validFrom);
						if (fromDate.isBefore(dateTo)) {
							dateTo.set(fromDate);
							dateTo.addDays(-1);
						}
					}
				}
				// create a new entry
				StringBuilder sql = new StringBuilder();
				sql.append("INSERT INTO ")
					.append(table)
					.append(
						" (DATUM_VON,DATUM_BIS,MULTIPLIKATOR,TYP) VALUES ("
							+ JdbcLink.wrap(dateFrom.toString(TimeTool.DATE_COMPACT)) + ","
							+ JdbcLink.wrap(dateTo.toString(TimeTool.DATE_COMPACT)) + ","
							+ JdbcLink.wrap(value) + "," + JdbcLink.wrap(typ) + ");");
				statement.exec(sql.toString());
			} finally {
				PersistentObject.getConnection().releaseStatement(statement);
			}
		}
		
		public synchronized double getMultiplikator(TimeTool date){
			// get Mutliplikator for date
			fetchResultSet();
			Iterator<MultiplikatorInfo> iter = list.iterator();
			while (iter.hasNext()) {
				MultiplikatorInfo info = iter.next();
				TimeTool fromDate = new TimeTool(info.validFrom);
				TimeTool toDate = new TimeTool(info.validTo);
				if (date.isAfterOrEqual(fromDate) && date.isBeforeOrEqual(toDate)) {
					String value = info.multiplikator;
					if (value != null && !value.isEmpty()) {
						try {
							return Double.parseDouble(value);
						} catch (NumberFormatException nfe) {
							ExHandler.handle(nfe);
							return 0.0;
						}
					}
				}
			}
			return 1.0;
		}
		
		private static class MultiplikatorInfo {
			String validFrom;
			String validTo;
			String multiplikator;
			
			MultiplikatorInfo(String validFrom, String validTo, String multiplikator){
				this.validFrom = validFrom;
				this.validTo = validTo;
				this.multiplikator = multiplikator;
			}
		}
	}
}
