package com.singintime.cedolino;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import org.joda.time.DateTime;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DatePanel extends JPanel {
  private static final String[] months = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
                                          "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
  private Cedolino parent;
  private JComboBox<String> monthBox;
  private JTextField yearField;
  int monthIndex;
  boolean dontTrigger;
  private String yearText;

  private class MonthListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	  if (dontTrigger) return;
	  confirmDate();
  	}
  }
  
  private class YearListener implements ActionListener, FocusListener {
    public void focusGained(FocusEvent e) {
	}
    
	public void focusLost(FocusEvent e) {
	  if (dontTrigger) return;
      confirmDate();
	}
	  
	public void actionPerformed(ActionEvent e) {
	  if (dontTrigger) return;
      monthBox.requestFocusInWindow();
	}
  }
  
  public DatePanel(Cedolino parent, DateTime calendar) {
    this.parent = parent;
    dontTrigger = false;
    JLabel monthLabel = new JLabel("Mese: ");
    monthBox = new JComboBox<String>(months);
    monthBox.setPreferredSize(new Dimension(200,30));
    monthBox.addActionListener(new MonthListener());
    JLabel yearLabel = new JLabel("Anno: ");
    yearField = new JTextField();
    yearField.setPreferredSize(new Dimension(60,30));
    YearListener yearListener = new YearListener();
    yearField.addActionListener(yearListener);
    yearField.addFocusListener(yearListener);
    monthIndex = monthBox.getSelectedIndex();
    yearText = yearField.getText();
    add(monthLabel);
    add(monthBox);
    add(yearLabel);
    add(yearField);
    update(calendar);
  }
  
  public void update(DateTime calendar) {
	int month = calendar.monthOfYear().get();
	String year = Integer.toString(calendar.year().get());
	dontTrigger = true;
	monthBox.setSelectedIndex(month - 1);
	yearField.setText(year);
	dontTrigger = false;
	monthIndex = month - 1;
	yearText = year;
  }
  
  private void confirmDate() {
	int result = JOptionPane.showConfirmDialog(parent,
            "Cambiando data, le ore di TUTTI i dipendenti verranno riportate ai valori predefiniti. Continuare?",
            "Attenzione!", JOptionPane.OK_CANCEL_OPTION);
	if (result == JOptionPane.OK_OPTION) {
	  int month = monthBox.getSelectedIndex() + 1;
	  int year = Integer.parseInt(yearField.getText());
      parent.setDate(month, year);
	}
	else {
      monthBox.setSelectedIndex(monthIndex);
      yearField.setText(yearText);
	}
  }
}
