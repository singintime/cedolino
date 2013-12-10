package com.singintime.cedolino;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Cedolino extends JFrame {
  private String selectedWorker, companyName;
  private HashMap<String,String[]> defaultHours;
  private HashMap<String,String[]> monthHours;
  private DateTime calendar;
  private DatePanel datePanel;
  private IOPanel ioPanel;
  private CalendarPanel calendarPanel;
  private WorkersPanel workersPanel;
  private Printer printer;
  
  public static void main(String args[]) {
	if (args.length > 0) new Cedolino(args[0]);
	else new Cedolino();
  }

  public Cedolino() {
    setTitle("Cedolino");
    setSize(800,600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    selectedWorker = null;
    companyName = "";
    defaultHours = new HashMap<String,String[]>();
	monthHours = new HashMap<String,String[]>();
	calendar = new DateTime();
    Container p = getContentPane();
    JPanel wrapper = new JPanel();
    wrapper.setLayout(new BorderLayout());
    datePanel = new DatePanel(this, calendar);
    ioPanel = new IOPanel(this);
    calendarPanel = new CalendarPanel(this, calendar, 3);
    workersPanel = new WorkersPanel(this);
    printer = new Printer(this);
    wrapper.add(datePanel, BorderLayout.NORTH);
    wrapper.add(ioPanel, BorderLayout.SOUTH);
    wrapper.add(calendarPanel, BorderLayout.CENTER);
    p.add(wrapper, BorderLayout.CENTER);
    p.add(workersPanel, BorderLayout.WEST);
    setVisible(true);
  }

  public Cedolino(String filename) {
	this();
	ioPanel.openXML(filename);
  }
  
  public void addWorker(String name, String[] hours) {
    if (defaultHours.get(name) == null) {
      workersPanel.addWorker(name);
    }
    defaultHours.put(name, hours);
    if (monthHours.get(name) == null) {
      initMonthHours(name);
    }
    selectWorker(name);
  }
  
  public void delWorker(String name) {
	workersPanel.delWorker(name);
    defaultHours.remove(name);
    monthHours.remove(name);
  }
  
  public void print() {
	printer.printAll(companyName, monthHours, calendar);
  }
  
  public void selectWorker(String name) {
	selectedWorker = name;
	workersPanel.selectWorker(name);
	if (name != null) {
	  String[] hours = monthHours.get(name);
	  calendarPanel.showHours(hours);
	}
	else {
      calendarPanel.disableHours();
	}
  }

  public void setCompanyName (String companyName) {
    this.companyName = companyName;
    workersPanel.setCompanyName(companyName);
  }
  
  public void setDate(int month, int year) {
    calendar = calendar.withMonthOfYear(month);
    calendar = calendar.withYear(year);
    initMonthHours();
    calendarPanel.update(calendar);
    datePanel.update(calendar);
    selectWorker(selectedWorker);
  }
  
  public void setDayHour(int day, String hour) {
	if (selectedWorker == null) return;
	String[] hours = monthHours.get(selectedWorker);
	hours[day-1] = hour;
  }
  
  public void showWorkerFrame() {
	new WorkerFrame(this);
  }
  
  public void showWorkerFrame(String name) {
	String[] hours = defaultHours.get(name);
	new WorkerFrame(this, name, hours);
  }
  
  public void saveXML() {
	final CautiousFileChooser fc = new CautiousFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter("Cedolino file", "cdl", "xml");
	fc.setFileFilter(filter);
	int returnValue = fc.showSaveDialog(this);
	if (returnValue != JFileChooser.APPROVE_OPTION) return;
	ioPanel.saveXML(companyName, defaultHours, monthHours, calendar, fc.getSelectedFile());
  }
  
  public void reset() {
	defaultHours.clear();
	monthHours.clear();
	workersPanel.clearWorkers();
  }
  
  private void initMonthHours() {
	for (String name : defaultHours.keySet()) {
	  initMonthHours(name);
	}
  }
  
  private void initMonthHours(String name) {
    int maxDays = calendar.dayOfMonth().getMaximumValue();
	String[] hours = new String[maxDays];
	String[] defaults = defaultHours.get(name);
	for (int i = 0; i < maxDays; i++) {
	  calendar = calendar.withDayOfMonth(i+1);
	  int j = calendar.dayOfWeek().get() - 1;
	  hours[i] = defaults[j];
	}
	monthHours.put(name, hours);
  }
}
