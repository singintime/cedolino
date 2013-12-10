package com.singintime.cedolino;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WorkerFrame extends JFrame {
  private Cedolino parent;
  private WorkerFrame self;
  private JTextField nameField;
  private JTextField[] hourFields;
  private JButton okButton, cancelButton;
  private final static String[] days = {"lun", "mar", "mer", "gio", "ven", "sab", "dom"};
  
  private class OkListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	  String name = nameField.getText();
	  if (name.equals("")) {
        JOptionPane.showConfirmDialog(self,
                "Per continuare, è necessario inserire il nome del dipendente.",
                "Attenzione!", JOptionPane.DEFAULT_OPTION);
        return;
	  }
	  String[] defaultHours = new String[7];
	  for (int i = 0; i < 7; i++) {
		  defaultHours[i] = hourFields[i].getText();
	  }
	  parent.addWorker(name, defaultHours);
	  self.dispose();
	}  
  }
  
  private class CancelListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
      self.dispose();
	}
  }
  
  public WorkerFrame(Cedolino parent) {
    setTitle("Aggiungi dipendente");
    setSize(700,240);
    setLocationRelativeTo(parent);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.parent = parent;
    self = this;
    hourFields = new JTextField[7];
    Container p = this.getContentPane();
    p.setLayout(new BorderLayout());
    JPanel namePanel = new JPanel();
    JLabel nameLabel = new JLabel("Nome: ");
    namePanel.add(nameLabel);
    nameField = new JTextField();
    nameField.setPreferredSize(new Dimension(600,32));
    namePanel.add(nameField);
    p.add(namePanel, BorderLayout.NORTH);
    JPanel weekPanel = new JPanel();
    weekPanel.setLayout(new GridLayout(3,7));
    for (int i = 0; i < 3; i++) {
      weekPanel.add(new JLabel(""));
    }
    weekPanel.add(new JLabel("Settimana:"));
    for (int i = 0; i < 3; i++) {
      weekPanel.add(new JLabel(""));
    }
    for (int i = 0; i < 7; i++) {
      weekPanel.add(new JLabel(days[i], JLabel.CENTER));
    }
    for (int i = 0; i < 7; i++) {
      hourFields[i] = new JTextField("0.00");
      weekPanel.add(hourFields[i]);
    }
    p.add(weekPanel, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(new OkListener());
    buttonPanel.add(okButton);
    cancelButton = new JButton("Annulla");
    cancelButton.addActionListener(new CancelListener());
    buttonPanel.add(cancelButton);
    p.add(buttonPanel, BorderLayout.SOUTH);
    setVisible(true);
    requestFocus();
  }
  
  public WorkerFrame(Cedolino parent, String name, String[] defaultHours) {
    this(parent);
    nameField.setText(name);
    nameField.setEditable(false);
    for (int i = 0; i < 7; i++) {
      hourFields[i].setText(defaultHours[i]);
    }
  }
}
