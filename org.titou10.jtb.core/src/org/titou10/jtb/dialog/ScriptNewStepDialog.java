/*
 * Copyright (C) 2015 Denis Forveille titou10.titou10@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.titou10.jtb.dialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.titou10.jtb.config.ConfigManager;
import org.titou10.jtb.jms.model.JTBDestination;
import org.titou10.jtb.jms.model.JTBSession;
import org.titou10.jtb.script.ScriptsUtils;
import org.titou10.jtb.script.gen.DataFile;
import org.titou10.jtb.script.gen.Script;
import org.titou10.jtb.script.gen.Step;
import org.titou10.jtb.ui.JTBStatusReporter;
import org.titou10.jtb.util.Constants;
import org.titou10.jtb.util.Utils;

/**
 * 
 * Dialog to create a new Step in a Script
 * 
 * @author Denis Forveille
 *
 */
public class ScriptNewStepDialog extends Dialog {

   private static final Logger log = LoggerFactory.getLogger(ScriptNewStepDialog.class);

   private IEventBroker        eventBroker;

   private JTBStatusReporter   jtbStatusReporter;

   private ConfigManager       cm;
   private Step                step;
   private Script              script;

   private String              templateName;
   private String              sessionName;
   private String              destinationName;
   private String              variablePrefix;
   private Integer             delay;
   private Integer             iterations;

   private Label               lblTemplateName;
   private Label               lblSessionName;
   private Label               lblDestinationName;
   private Label               lblVariablePrefix;
   private Spinner             delaySpinner;
   private Spinner             iterationsSpinner;

   private Button              btnChooseDestination;

   public ScriptNewStepDialog(Shell parentShell,
                              IEventBroker eventBroker,
                              JTBStatusReporter jtbStatusReporter,
                              ConfigManager cm,
                              Step step,
                              Script script) {
      super(parentShell);
      setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.PRIMARY_MODAL);
      this.eventBroker = eventBroker;
      this.jtbStatusReporter = jtbStatusReporter;
      this.cm = cm;
      this.step = step;
      this.script = script;
   }

   @Override
   protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setText(script.getName() + ": Add/Edit a step");
   }

   protected Point getInitialSize() {
      return new Point(600, 303);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite container = (Composite) super.createDialogArea(parent);
      container.setLayout(new GridLayout(3, false));

      // Template

      Label lbl1 = new Label(container, SWT.SHADOW_NONE);
      lbl1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lbl1.setAlignment(SWT.CENTER);
      lbl1.setText("Template:");

      lblTemplateName = new Label(container, SWT.BORDER | SWT.SHADOW_NONE);
      lblTemplateName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

      Button btnChooseTemplate = new Button(container, SWT.NONE);
      btnChooseTemplate.setText("Select...");
      btnChooseTemplate.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            // Dialog to choose a template
            TemplateChooserDialog dialog1 = new TemplateChooserDialog(getShell(), false, cm.getTemplateFolder());
            if (dialog1.open() == Window.OK) {

               IFile template = dialog1.getSelectedFile();
               if (template != null) {
                  templateName = "/" + template.getProjectRelativePath().removeFirstSegments(1).toPortableString();
                  lblTemplateName.setText(templateName);
               }
            }
         }
      });

      // Session

      Label lbl2 = new Label(container, SWT.NONE);
      lbl2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lbl2.setText("Session:");

      lblSessionName = new Label(container, SWT.BORDER | SWT.SHADOW_NONE);
      lblSessionName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

      Button btnChooseSession = new Button(container, SWT.NONE);
      btnChooseSession.setText("Select...");
      btnChooseSession.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            // Dialog to choose a Session
            SessionChooserDialog dialog1 = new SessionChooserDialog(getShell(), cm);
            if (dialog1.open() == Window.OK) {

               JTBSession jtbSession = dialog1.getSelectedJTBSession();
               if (jtbSession != null) {
                  // Reset Destination if session name changed
                  // TODO Could be done via JFace bindings.
                  if (!(sessionName.equals(jtbSession.getName()))) {
                     destinationName = "";
                     lblDestinationName.setText(destinationName);
                  }
                  sessionName = jtbSession.getName();
                  lblSessionName.setText(sessionName);

                  btnChooseDestination.setEnabled(true);
               }
            }
         }
      });

      // Destination

      Label lbl3 = new Label(container, SWT.NONE);
      lbl3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lbl3.setText("Destination:");

      lblDestinationName = new Label(container, SWT.BORDER | SWT.SHADOW_NONE);
      lblDestinationName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

      btnChooseDestination = new Button(container, SWT.NONE);
      btnChooseDestination.setText("Select...");
      btnChooseDestination.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            // Connect to session, get list of destinations
            final JTBSession jtbSession = cm.getJTBSessionByName(sessionName);
            if (!(jtbSession.isConnected())) {

               BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                  @Override
                  public void run() {
                     try {
                        jtbSession.connectOrDisconnect();
                        // Refresh Session Browser
                        eventBroker.send(Constants.EVENT_REFRESH_SESSION_BROWSER, false);
                     } catch (Throwable e) {
                        jtbStatusReporter.showError("Connect unsuccessful", e, jtbSession.getName());
                        return;
                     }
                  }
               });
            }
            // Retest to check is the connect was successfull...
            if (!(jtbSession.isConnected())) {
               return;
            }

            // Dialog to choose a destination

            DestinationChooserDialog dialog1 = new DestinationChooserDialog(getShell(), jtbSession);
            if (dialog1.open() == Window.OK) {

               JTBDestination jtbDestination = dialog1.getSelectedJTBDestination();
               if (jtbDestination != null) {
                  destinationName = jtbDestination.getName();
                  lblDestinationName.setText(destinationName);
               }
            }
         }
      });

      // DataFile

      Label lbl4 = new Label(container, SWT.SHADOW_NONE);
      lbl4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lbl4.setAlignment(SWT.CENTER);
      lbl4.setText("Loop on data file:");

      // Composite with label and clear button
      Composite dataFileComposite = new Composite(container, SWT.NONE);
      dataFileComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      GridLayout gl1 = new GridLayout(2, false);
      gl1.marginWidth = 0;
      dataFileComposite.setLayout(gl1);

      lblVariablePrefix = new Label(dataFileComposite, SWT.BORDER | SWT.SHADOW_NONE);
      lblVariablePrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      Button btnClear = new Button(dataFileComposite, SWT.NONE);
      btnClear.setToolTipText("Clear data file");
      btnClear.setImage(Utils.getImage(this.getClass(), "icons/cross-script.png"));
      btnClear.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            variablePrefix = null;
            lblVariablePrefix.setText("");
         }
      });

      Button btnChooseDataFile = new Button(container, SWT.NONE);
      btnChooseDataFile.setText("Select...");
      btnChooseDataFile.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            // Dialog to choose a Data File
            DataFileChooserDialog dialog1 = new DataFileChooserDialog(getShell(), script.getDataFile());
            if (dialog1.open() == Window.OK) {

               DataFile dataFile = dialog1.getSelectedDataFile();
               if (dataFile != null) {
                  variablePrefix = dataFile.getVariablePrefix();

                  log.debug("Data File Selected : [{}]", dataFile.getVariablePrefix());
                  lblVariablePrefix.setText(ScriptsUtils.buildDataFileDislayName(dataFile));
               }
            }
         }
      });
      if (script.getDataFile().isEmpty()) {
         btnChooseDataFile.setEnabled(false);
      }

      // Repeat

      Label lbl5 = new Label(container, SWT.NONE);
      lbl5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lbl5.setText("Repeat this step");

      Composite repeatComposite = new Composite(container, SWT.NONE);
      repeatComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
      GridLayout gl2 = new GridLayout(2, false);
      gl1.marginWidth = 0;
      repeatComposite.setLayout(gl2);

      iterationsSpinner = new Spinner(repeatComposite, SWT.BORDER);
      iterationsSpinner.setMinimum(1);
      iterationsSpinner.setSelection(1);

      Label lbl6 = new Label(repeatComposite, SWT.NONE);
      lbl6.setText(" time(s)");

      // Pause

      Label lbl7 = new Label(container, SWT.NONE);
      lbl7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lbl7.setText("Pause for");

      Composite composite = new Composite(container, SWT.NONE);
      composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
      GridLayout gl_composite = new GridLayout(2, false);
      gl_composite.marginWidth = 0;
      composite.setLayout(gl_composite);

      delaySpinner = new Spinner(composite, SWT.BORDER);
      delaySpinner.setMaximum(600);

      Label lbl8 = new Label(composite, SWT.NONE);
      lbl8.setText(" second(s) after this step");

      // Populate Fields
      templateName = step.getTemplateName();
      sessionName = step.getSessionName();
      destinationName = step.getDestinationName();
      variablePrefix = step.getVariablePrefix();
      delay = step.getPauseSecsAfter();
      iterations = step.getIterations();

      lblTemplateName.setText(templateName);
      lblSessionName.setText(sessionName);
      lblDestinationName.setText(destinationName);
      if (variablePrefix != null) {
         DataFile dataFile = ScriptsUtils.findDataFileByVariablePrefix(script, variablePrefix);
         lblVariablePrefix.setText(ScriptsUtils.buildDataFileDislayName(dataFile));
      }
      delaySpinner.setSelection(delay);
      iterationsSpinner.setSelection(iterations);

      if ((sessionName != null) && (!(sessionName.trim().isEmpty()))) {
         btnChooseDestination.setEnabled(true);
      } else {
         btnChooseDestination.setEnabled(false);
      }

      return container;
   }

   @Override
   protected void okPressed() {

      if (templateName.isEmpty()) {
         MessageDialog.openError(getShell(), "Error", "A template is mandatory");
         return;
      }

      if (sessionName.isEmpty()) {
         MessageDialog.openError(getShell(), "Error", "A session is mandatory");
         return;
      }

      if (destinationName.isEmpty()) {
         MessageDialog.openError(getShell(), "Error", "A destination is mandatory");
         return;
      }

      // Populate fields

      step.setTemplateName(templateName);
      step.setSessionName(sessionName);
      step.setDestinationName(destinationName);
      step.setVariablePrefix(variablePrefix);
      step.setPauseSecsAfter(delaySpinner.getSelection());
      step.setIterations(iterationsSpinner.getSelection());

      super.okPressed();
   }

   // ----------------
   // Standard Getters
   // ----------------
   public Step getStep() {
      return step;
   }

}
