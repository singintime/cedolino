package com.singintime.cedolino;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class WorkersPanel extends JPanel {
  private Cedolino parent;
  private JList<String> workersList;
  private DefaultListModel<String> workers;
  private JButton addWorkerButton, editWorkerButton, delWorkerButton;
  private JTextField companyNameField;

  private class CompanyNameListener implements DocumentListener {
	public void execute() {
	  String companyName = companyNameField.getText();
	  parent.setCompanyName(companyName);
	}

	public void changedUpdate(DocumentEvent e) {
      execute();
	}

	public void insertUpdate(DocumentEvent e) {
      execute();
	}

	public void removeUpdate(DocumentEvent arg0) {
      execute();
	} 
  }
  
  private class AddWorkerListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	  parent.showWorkerFrame();
	} 
  }
  
  private class EditWorkerListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	  String name = workersList.getSelectedValue();
	  if (name != null) parent.showWorkerFrame(name);
	} 
  }
  
  private class DelWorkerListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	  String name = workersList.getSelectedValue();
	  if (name != null) parent.delWorker(name);
	} 
  }
  
  private class SelectWorkerListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent arg0) {
      String name = workersList.getSelectedValue();
      parent.selectWorker(name);
    }
  }
  
  public WorkersPanel(Cedolino parent) {
	this.parent = parent;
	setLayout(new BorderLayout());
	workers = new DefaultListModel<String>();
	workersList = new JList<String>(workers);
	workersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	workersList.setPreferredSize(new Dimension(200,0));
	workersList.addListSelectionListener(new SelectWorkerListener());
	JScrollPane scroll = new JScrollPane(workersList);
	addWorkerButton = new JButton("Aggiungi");
	addWorkerButton.addActionListener(new AddWorkerListener());
	editWorkerButton = new JButton("Modifica");
	editWorkerButton.addActionListener(new EditWorkerListener());
	delWorkerButton = new JButton("Elimina");
	delWorkerButton.addActionListener(new DelWorkerListener());
	JPanel buttons = new JPanel();
	buttons.add(addWorkerButton);
	buttons.add(editWorkerButton);
	buttons.add(delWorkerButton);
	JPanel northPanel = new JPanel();
	northPanel.setLayout(new GridLayout(3,1));
	JLabel companyLabel = new JLabel("Nome ditta:", JLabel.CENTER);
	companyLabel.setPreferredSize(new Dimension(200,40));
	companyNameField = new JTextField();
	companyNameField.setPreferredSize(new Dimension(200,40));
	companyNameField.getDocument().addDocumentListener(new CompanyNameListener());
	JLabel workersLabel = new JLabel("Dipendenti:", JLabel.CENTER);
	workersLabel.setPreferredSize(new Dimension(200,40));
	northPanel.add(companyLabel);
	northPanel.add(companyNameField);
	northPanel.add(workersLabel);
	add(northPanel, BorderLayout.NORTH);
	add(scroll, BorderLayout.CENTER);
	add(buttons, BorderLayout.SOUTH);
  }
  
  public void setCompanyName(String companyName) {
    String currName = companyNameField.getText();
	if (!companyName.equals(currName)) {
	  companyNameField.setText(companyName);
	}
  }
  
  public void addWorker(String name) {
    workers.addElement(name);
    Object[] sorted = workers.toArray();
    Arrays.sort(sorted);
    workers.clear();
    for (Object worker : sorted) {
      workers.addElement((String)worker);
    }
  }
  
  public void delWorker(String name) {
	workers.removeElement(name);
  }
  
  public void selectWorker(String name) {
    workersList.setSelectedValue(name, false);
  }
  
  public void clearWorkers() {
    workers.clear();
  }
}