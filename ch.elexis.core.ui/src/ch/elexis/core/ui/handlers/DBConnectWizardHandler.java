package ch.elexis.core.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.ui.wizards.DBConnectWizard;

public class DBConnectWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench()
				.getDisplay().getActiveShell(), new DBConnectWizard());
		wd.open();
		return null;
	}

}
