package ch.elexis.core.ui;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	private static BundleContext context;

	private static FormToolkit formToolkit = null;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		
		if (formToolkit != null) {
			formToolkit.dispose();
		}
	}

	public static FormToolkit getToolkit(){
		if (formToolkit == null) {
			formToolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
		}
		return formToolkit;
	}
	
}
