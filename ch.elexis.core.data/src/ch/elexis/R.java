package ch.elexis;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class R {
	public static final String RezeptBlatt_ID = "ch.elexis.RezeptBlatt";
	public static final String KGPrintView_ID = "ch.elexis.views.KGPrintView";
	public static final String TextView_ID = "ch.elexis.TextView";
	public static final String PatientenListeView_ID = "ch.elexis.PatListView";
	public static final String LaborView_ID = "ch.elexis.Labor";
	
	
	public static final String Command_DBConnect_WizardDialog_ID = "ch.elexis.core.ui.command.wizard.dbconnect";
	
	public static final void Command_DBConnect_WizardDialog() {
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
        try {
			handlerService.executeCommand(R.Command_DBConnect_WizardDialog_ID, new Event());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotHandledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
