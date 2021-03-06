package org.wiredwidgets.cow.ac.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.wiredwidgets.cow.ac.util.CowUtils;

/**
 * A customized dialog to facilitate capturing info needed to close out a Task.
 * Can be provided a list of outcomes (Strings), one of which must then be
 * selected.
 *
 * @author RYANMILLER
 */
public class CompleteTaskDialog extends javax.swing.JDialog {

    private boolean taskComplete = false;
    private List<String> outcomes;
    private boolean hasOutcome = true;
    private String nameDefault =
            org.openide.util.NbBundle.getMessage(
            CompleteTaskDialog.class, "CompleteTaskDialog.noteName1.text");
    private String noteDefault = org.openide.util.NbBundle.getMessage(
            CompleteTaskDialog.class, "CompleteTaskDialog.note1.text");

    /**
     * Creates a new dialog (but does not show it).
     *
     * @param parent The parent window
     * @param modal Sets the dialog's modality
     * @param outcomes An optional list of outcomes one of which must be
     * selected to complete the task (use null if none).
     */
    public CompleteTaskDialog(java.awt.Frame parent, boolean modal,
            List<String> outcomes) {

        super(parent, modal);
        initComponents();
        super.setLocationRelativeTo(null);  // center the dialog on the screen  

        if (null == outcomes || 0 == outcomes.size()) {
            // hide the area for selecting an outcome and trip a flag to avoid
            // triggering the validation logic
            outcomesPanel.setVisible(false);
            hasOutcome = false;
        } else {
            this.outcomes = outcomes;
            setOutcomes();
        }

        // improve the text boxes' usability
        setTextFocusListeners();
    }

    /**
     * Gets the outcome selected by the user
     *
     * @return The selected outcome; null if the user did not complete the task.
     * Will also return null if outcomes were not provided.
     */
    public String getSelectedOutcome() {
        if (!hasOutcome || !taskComplete) {
            return null;
        }
        JRadioButton selectedBtn = CowUtils.getSelection(outcomesBtnGroup);
        return selectedBtn.getText();
    }

    /**
     * Returns a set of notes entered by the user. Notes are captured in a
     * key:value format. This dialog's UI is hard coded to gather at most 6
     * notes for now, but could be improved to be dynamic.
     *
     * @return Any notes entered by the user. Map will be empty if none.
     */
    public Map<String, String> getNotes() {
        Map<String, String> notes = new HashMap<String, String>();

        if (!noteName1.getText().equals(nameDefault)
                && !note1.getText().equals(noteDefault)) {
            notes.put(noteName1.getText(), note1.getText());
        }
        if (!noteName2.getText().equals(nameDefault)
                && !note2.getText().equals(noteDefault)) {
            notes.put(noteName2.getText(), note2.getText());
        }
        if (!noteName3.getText().equals(nameDefault)
                && !note3.getText().equals(noteDefault)) {
            notes.put(noteName3.getText(), note3.getText());
        }
        if (!noteName4.getText().equals(nameDefault)
                && !note4.getText().equals(noteDefault)) {
            notes.put(noteName4.getText(), note4.getText());
        }
        if (!noteName5.getText().equals(nameDefault)
                && !note5.getText().equals(noteDefault)) {
            notes.put(noteName5.getText(), note5.getText());
        }
        if (!noteName6.getText().equals(nameDefault)
                && !note6.getText().equals(noteDefault)) {
            notes.put(noteName6.getText(), note6.getText());
        }

        return notes;
    }

    /**
     * Indicates if the user entered the necessary information to complete the
     * task
     *
     * @return Task completion status
     */
    public boolean taskCompleted() {
        return taskComplete;
    }

    /** This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        outcomesBtnGroup = new javax.swing.ButtonGroup();
        outcomesPanel = new javax.swing.JPanel();
        notesPanel = new javax.swing.JPanel();
        noteName1 = new javax.swing.JTextField();
        note1 = new javax.swing.JTextField();
        noteName2 = new javax.swing.JTextField();
        note2 = new javax.swing.JTextField();
        noteName3 = new javax.swing.JTextField();
        note3 = new javax.swing.JTextField();
        noteName4 = new javax.swing.JTextField();
        note4 = new javax.swing.JTextField();
        noteName5 = new javax.swing.JTextField();
        note5 = new javax.swing.JTextField();
        noteName6 = new javax.swing.JTextField();
        note6 = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        btnSubmit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(315, 300));
        setName("dlgCompleteTask"); // NOI18N
        setResizable(false);

        outcomesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.outcomesPanel.border.title"))); // NOI18N
        outcomesPanel.setLayout(new javax.swing.BoxLayout(outcomesPanel, javax.swing.BoxLayout.PAGE_AXIS));
        getContentPane().add(outcomesPanel, java.awt.BorderLayout.NORTH);

        notesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.notesPanel.border.title"))); // NOI18N
        notesPanel.setMinimumSize(new java.awt.Dimension(300, 170));
        notesPanel.setPreferredSize(new java.awt.Dimension(300, 170));
        notesPanel.setLayout(new java.awt.GridBagLayout());

        noteName1.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName1.text")); // NOI18N
        noteName1.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName1.toolTipText")); // NOI18N
        noteName1.setMinimumSize(new java.awt.Dimension(80, 20));
        noteName1.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        notesPanel.add(noteName1, gridBagConstraints);

        note1.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note1.text")); // NOI18N
        note1.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note1.toolTipText")); // NOI18N
        note1.setMinimumSize(new java.awt.Dimension(180, 20));
        note1.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        notesPanel.add(note1, gridBagConstraints);

        noteName2.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName2.text")); // NOI18N
        noteName2.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName2.toolTipText")); // NOI18N
        noteName2.setMinimumSize(new java.awt.Dimension(80, 20));
        noteName2.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        notesPanel.add(noteName2, gridBagConstraints);

        note2.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note2.text")); // NOI18N
        note2.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note2.toolTipText")); // NOI18N
        note2.setMinimumSize(new java.awt.Dimension(180, 20));
        note2.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        notesPanel.add(note2, gridBagConstraints);

        noteName3.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName3.text")); // NOI18N
        noteName3.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName3.toolTipText")); // NOI18N
        noteName3.setMinimumSize(new java.awt.Dimension(80, 20));
        noteName3.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        notesPanel.add(noteName3, gridBagConstraints);

        note3.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note3.text")); // NOI18N
        note3.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note3.toolTipText")); // NOI18N
        note3.setMinimumSize(new java.awt.Dimension(180, 20));
        note3.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        notesPanel.add(note3, gridBagConstraints);

        noteName4.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName4.text")); // NOI18N
        noteName4.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName4.toolTipText")); // NOI18N
        noteName4.setMinimumSize(new java.awt.Dimension(80, 20));
        noteName4.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        notesPanel.add(noteName4, gridBagConstraints);

        note4.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note4.text")); // NOI18N
        note4.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note4.toolTipText")); // NOI18N
        note4.setMinimumSize(new java.awt.Dimension(180, 20));
        note4.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        notesPanel.add(note4, gridBagConstraints);

        noteName5.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName5.text")); // NOI18N
        noteName5.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName5.toolTipText")); // NOI18N
        noteName5.setMinimumSize(new java.awt.Dimension(80, 20));
        noteName5.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        notesPanel.add(noteName5, gridBagConstraints);

        note5.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note5.text")); // NOI18N
        note5.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note5.toolTipText")); // NOI18N
        note5.setMinimumSize(new java.awt.Dimension(180, 20));
        note5.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        notesPanel.add(note5, gridBagConstraints);

        noteName6.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName6.text")); // NOI18N
        noteName6.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.noteName6.toolTipText")); // NOI18N
        noteName6.setMinimumSize(new java.awt.Dimension(80, 20));
        noteName6.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        notesPanel.add(noteName6, gridBagConstraints);

        note6.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note6.text")); // NOI18N
        note6.setToolTipText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.note6.toolTipText")); // NOI18N
        note6.setMinimumSize(new java.awt.Dimension(180, 20));
        note6.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        notesPanel.add(note6, gridBagConstraints);

        getContentPane().add(notesPanel, java.awt.BorderLayout.CENTER);

        btnSubmit.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.btnSubmit.text")); // NOI18N
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        buttonPanel.add(btnSubmit);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(CompleteTaskDialog.class, "CompleteTaskDialog.btnCancel.text")); // NOI18N
        btnCancel.setAlignmentX(1.0F);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        buttonPanel.add(btnCancel);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();  // don't need to save anything
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // need to enforce a choice selection
        if (hasOutcome && CowUtils.getSelection(outcomesBtnGroup) == null) {
            JOptionPane.showMessageDialog(null, "Please select an outcome", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // check the notes for completeness
        String noteIssues = "";

        // TODO: for the love of pete, make this dynamic!
        int i = 1;
        if (noteName1.getText().trim().equals("") || note1.getText().trim().equals("")) {
            noteIssues += "The values for note #" + i + " cannot be blank.\n";
        } else if (!noteName1.getText().equals(nameDefault) && note1.getText().equals(noteDefault)) {
            noteIssues += "The name for note #" + i + " is filled in, but there are no notes.\n";
        } else if (!note1.getText().equals(noteDefault) && noteName1.getText().equals(nameDefault)) {
            noteIssues += "The note for note #" + i + " is filled in, but there is no name.\n";
        }
        i++;

        if (noteName2.getText().trim().equals("") || note2.getText().trim().equals("")) {
            noteIssues += "The values for note #" + i + " cannot be blank.\n";
        } else if (!noteName2.getText().equals(nameDefault) && note2.getText().equals(noteDefault)) {
            noteIssues += "The name for note #" + i + " is filled in, but there are no notes.\n";
        } else if (!note2.getText().equals(noteDefault) && noteName2.getText().equals(nameDefault)) {
            noteIssues += "The note for note #" + i + " is filled in, but there is no name.\n";
        }
        i++;

        if (noteName3.getText().trim().equals("") || note3.getText().trim().equals("")) {
            noteIssues += "The values for note #" + i + " cannot be blank.\n";
        } else if (!noteName3.getText().equals(nameDefault) && note3.getText().equals(noteDefault)) {
            noteIssues += "The name for note #" + i + " is filled in, but there are no notes.\n";
        } else if (!note3.getText().equals(noteDefault) && noteName3.getText().equals(nameDefault)) {
            noteIssues += "The note for note #" + i + " is filled in, but there is no name.\n";
        }
        i++;

        if (noteName4.getText().trim().equals("") || note4.getText().trim().equals("")) {
            noteIssues += "The values for note #" + i + " cannot be blank.\n";
        } else if (!noteName4.getText().equals(nameDefault) && note4.getText().equals(noteDefault)) {
            noteIssues += "The name for note #" + i + " is filled in, but there are no notes.\n";
        } else if (!note4.getText().equals(noteDefault) && noteName4.getText().equals(nameDefault)) {
            noteIssues += "The note for note #" + i + " is filled in, but there is no name.\n";
        }
        i++;

        if (noteName5.getText().trim().equals("") || note5.getText().trim().equals("")) {
            noteIssues += "The values for note #" + i + " cannot be blank.\n";
        } else if (!noteName5.getText().equals(nameDefault) && note5.getText().equals(noteDefault)) {
            noteIssues += "The name for note #" + i + " is filled in, but there are no notes.\n";
        } else if (!note5.getText().equals(noteDefault) && noteName5.getText().equals(nameDefault)) {
            noteIssues += "The note for note #" + i + " is filled in, but there is no name.\n";
        }
        i++;

        if (noteName6.getText().trim().equals("") || note6.getText().trim().equals("")) {
            noteIssues += "The values for note #" + i + " cannot be blank.\n";
        } else if (!noteName6.getText().equals(nameDefault) && note6.getText().equals(noteDefault)) {
            noteIssues += "The name for note #" + i + " is filled in, but there are no notes.\n";
        } else if (!note6.getText().equals(noteDefault) && noteName6.getText().equals(nameDefault)) {
            noteIssues += "The note for note #" + i + " is filled in, but there is no name.\n";
        }
        i++;

        if (!noteIssues.equals("")) {
            JOptionPane.showMessageDialog(null, "There is a problem with the notes.\n\n" + noteIssues, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // trip the flag to allow the caller to get the user's input
        taskComplete = true;
        // hide the dialog only so that we can allow access to the user's input
        this.setVisible(false);


    }//GEN-LAST:event_btnSubmitActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextField note1;
    private javax.swing.JTextField note2;
    private javax.swing.JTextField note3;
    private javax.swing.JTextField note4;
    private javax.swing.JTextField note5;
    private javax.swing.JTextField note6;
    private javax.swing.JTextField noteName1;
    private javax.swing.JTextField noteName2;
    private javax.swing.JTextField noteName3;
    private javax.swing.JTextField noteName4;
    private javax.swing.JTextField noteName5;
    private javax.swing.JTextField noteName6;
    private javax.swing.JPanel notesPanel;
    private javax.swing.ButtonGroup outcomesBtnGroup;
    private javax.swing.JPanel outcomesPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Adds radio buttons into the outcomes panel based on the provided list of
     * outcomes
     */
    private void setOutcomes() {
        for (int i = 0; i < outcomes.size(); i++) {
            JRadioButton btn = new JRadioButton(outcomes.get(i));
            outcomesPanel.add(btn);
            outcomesBtnGroup.add(btn);
        }
    }

    private void setTextFocusListeners() {
        setTextFocusListener(noteName1);
        setTextFocusListener(note1);
        setTextFocusListener(noteName2);
        setTextFocusListener(note2);
        setTextFocusListener(noteName3);
        setTextFocusListener(note3);
        setTextFocusListener(noteName4);
        setTextFocusListener(note4);
        setTextFocusListener(noteName5);
        setTextFocusListener(note5);
        setTextFocusListener(noteName6);
        setTextFocusListener(note6);
    }

    /**
     * A usability helper that sets a
     * <code>FocusEvent</code> onto each text field that automatically selects
     * all the text in the field.
     *
     * @param f
     */
    private void setTextFocusListener(final JTextField f) {
        f.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        f.selectAll();
                    }
                });
            }
        });
    }
}
