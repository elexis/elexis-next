package ch.elexis.elexisevent;

import java.util.List;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.data.Patient;
import ch.elexis.data.Reminder;
import ch.elexis.preferences.PreferenceConstants;
import ch.elexis.util.SWTHelper;
import ch.elexis.views.Messages;

/**
 * Listener for patient events, registered within
 * {@link Hub#start(org.osgi.framework.BundleContext)}, de-registered within
 * {@link Hub#stop(org.osgi.framework.BundleContext)}
 */
public class PatientEventListener extends ElexisEventListenerImpl {
	
	public PatientEventListener(){
		super(Patient.class);
	}
	
	@Override
	public void runInUi(final ElexisEvent ev){
		Hub.setWindowText((Patient) ev.getObject());
		
		if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
			if (Hub.userCfg.get(PreferenceConstants.USR_SHOWPATCHGREMINDER, false)) {
				Desk.asyncExec(new Runnable() {
					
					public void run(){
						List<Reminder> list =
							Reminder.findRemindersDueFor((Patient) ev.getObject(), Hub.actUser,
								true);
						if (list.size() != 0) {
							StringBuilder sb = new StringBuilder();
							for (Reminder r : list) {
								sb.append(r.getMessage()).append("\n\n"); //$NON-NLS-1$
							}
							SWTHelper.alert(Messages
								.getString("ReminderView.importantRemindersCaption"), sb.toString()); //$NON-NLS-1$
						}
					}
					
				});
			}
		}
	}
	
}
