/*******************************************************************************
 * Copyright (c) 2005-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.GlobalActions;
import ch.elexis.actions.LazyTreeLoader;
import ch.elexis.data.BBSEntry;
import ch.elexis.data.Query;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.TreeContentProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Tree;

/**
 * Bulletin Board System - ein Schwarzes Brett. Im Prinzip Erweiterung des Reminder-Konzepts zu
 * Threads ähnlich newsreader und Webforen. Farben können mit &lt;span
 * color="rot"&gt;...&lt;/span&gt; kontrolliert werden (bzw. "grün" und "blau")
 * 
 * @author gerry
 */
@Deprecated
public class BBSView extends ViewPart implements ISelectionChangedListener, ISaveablePart2 {
	public static final String ID = "ch.elexis.BBSView"; //$NON-NLS-1$
	private CommonViewer headlines;
	private ViewerConfigurer vc;
	private ScrolledForm form;
	private FormToolkit tk;
	private Query<BBSEntry> qbe;
	private LazyTreeLoader<BBSEntry> loader;
	private Label origin;
	private FormText msg;
	private Text input;
	
	@Override
	public void createPartControl(Composite parent){
		SashForm sash = new SashForm(parent, SWT.NONE);
		qbe = new Query<BBSEntry>(BBSEntry.class);
		loader = new LazyTreeLoader<BBSEntry>("BBS", qbe, "reference", new String[] { //$NON-NLS-1$ //$NON-NLS-2$
				"datum", "time", "Thema" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			});
		headlines = new CommonViewer();
		vc =
			new ViewerConfigurer(new TreeContentProvider(headlines, loader),
				new ViewerConfigurer.TreeLabelProvider(), new DefaultControlFieldProvider(
					headlines, new String[] {
						"Thema" //$NON-NLS-1$
					}), new NewThread(), new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TREE,
					SWT.NONE, null));
		headlines.create(vc, sash, SWT.NONE, getViewSite());
		
		tk = Desk.getToolkit();
		form = tk.createScrolledForm(sash);
		form.getBody().setLayout(new GridLayout(1, false));
		form.setText(Messages.getString("BBSView.PleaseEnterSubject")); //$NON-NLS-1$
		origin = tk.createLabel(form.getBody(), ""); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		origin.setLayoutData(gd);
		msg = tk.createFormText(form.getBody(), false);
		gd =
			new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
				| GridData.FILL_VERTICAL);
		msg.setLayoutData(gd);
		msg.setColor(Messages.getString("BBSView.rot"), Desk.getColor(Desk.COL_RED)); //$NON-NLS-1$
		msg.setColor(Messages.getString("BBSView.gruen"), Desk.getColor(Desk.COL_GREEN)); //$NON-NLS-1$
		msg.setColor(Messages.getString("BBSView.blau"), Desk.getColor(Desk.COL_BLUE)); //$NON-NLS-1$
		input = tk.createText(form.getBody(), "", SWT.WRAP | SWT.MULTI | SWT.BORDER); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		input.setLayoutData(gd);
		Button send =
			tk.createButton(form.getBody(), Messages.getString("BBSView.DoSend"), SWT.PUSH); //$NON-NLS-1$
		send.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e){
				
				Object[] sel = headlines.getSelection();
				if (sel == null || sel.length == 0) {
					return;
				}
				Tree item = (Tree) sel[0];
				BBSEntry en = (BBSEntry) (item).contents;
				BBSEntry ne = new BBSEntry(en.getTopic(), Hub.actUser, en, input.getText());
				Tree child = item.add(ne);
				((TreeViewer) headlines.getViewerWidget()).add(sel[0], child);
				((TreeViewer) headlines.getViewerWidget()).setSelection(new StructuredSelection(
					child), true);
			}
			
		});
		headlines.getViewerWidget().addSelectionChangedListener(this);
		((TreeContentProvider) headlines.getConfigurer().getContentProvider()).startListening();
		setDisplay();
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (Kein Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		((TreeContentProvider) headlines.getConfigurer().getContentProvider()).stopListening();
		super.dispose();
	}
	
	@SuppressWarnings("unchecked")
	public void setDisplay(){
		Object[] sel = headlines.getSelection();
		if (sel == null || sel.length == 0) {
			form.setText(Messages.getString("BBSView.14")); //$NON-NLS-1$
			return;
		}
		BBSEntry en = ((Tree<BBSEntry>) sel[0]).contents;
		form.setText(en.getTopic());
		StringBuilder sb = new StringBuilder();
		sb.append(en.getAuthor().getLabel())
			.append(Messages.getString("BBSView.15")).append(en.getDate()).append( //$NON-NLS-1$
				Messages.getString("BBSView.16")).append(en.getTime()).append(Messages.getString("BBSView.17")); //$NON-NLS-1$ //$NON-NLS-2$
		origin.setText(sb.toString());
		try {
			msg.setText(
				Messages.getString("BBSView.18") + en.getText() + Messages.getString("BBSView.19"), true, true); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception ex) {
			ExHandler.handle(ex);
			
		}
		input.setText(Messages.getString("BBSView.20")); //$NON-NLS-1$
	}
	
	class NewThread implements ViewerConfigurer.ButtonProvider {
		
		public Button createButton(Composite parent){
			Button ret = new Button(parent, SWT.PUSH);
			ret.setText(Messages.getString("BBSView.21")); //$NON-NLS-1$
			ret.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e){
					new BBSEntry(
						headlines.getConfigurer().getControlFieldProvider().getValues()[0],
						Hub.actUser, null, Messages.getString("BBSView.22")); //$NON-NLS-1$
					loader.invalidate();
					headlines.notify(CommonViewer.Message.update);
					setDisplay();
				}
				
			});
			return ret;
		}
		
		public boolean isAlwaysEnabled(){
			return false;
		}
		
	}
	
	public void selectionChanged(SelectionChangedEvent event){
		setDisplay();
	}
	
	/*
	 * Die folgenden 6 Methoden implementieren das Interface ISaveablePart2 Wir benötigen das
	 * Interface nur, um das Schliessen einer View zu verhindern, wenn die Perspektive fixiert ist.
	 * Gibt es da keine einfachere Methode?
	 */
	public int promptToSaveOnClose(){
		return GlobalActions.fixLayoutAction.isChecked() ? ISaveablePart2.CANCEL
				: ISaveablePart2.NO;
	}
	
	public void doSave(IProgressMonitor monitor){ /* leer */}
	
	public void doSaveAs(){ /* leer */}
	
	public boolean isDirty(){
		return true;
	}
	
	public boolean isSaveAsAllowed(){
		return false;
	}
	
	public boolean isSaveOnCloseNeeded(){
		return true;
	}
}
