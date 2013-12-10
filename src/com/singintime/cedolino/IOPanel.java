package com.singintime.cedolino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IOPanel extends JPanel {
  private static final String[] weekDays = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
  private Cedolino parent;
  private DocumentBuilderFactory dbf;
  private DocumentBuilder db;
  private TransformerFactory tf;
  private Transformer transformer;
  private JButton openButton, saveButton, printButton;
  
  private class OpenListener implements ActionListener {
    public void actionPerformed(ActionEvent arg0) {
      openXML();
	}
  }
  
  private class SaveListener implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
      parent.saveXML();
    }
  }
  
  private class PrintListener implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
      parent.print();
    }
  }
  
  public IOPanel(Cedolino parent) {
    this.parent = parent;
    dbf = DocumentBuilderFactory.newInstance();
    try {
      db = dbf.newDocumentBuilder();
    }
    catch (ParserConfigurationException e) {
      JOptionPane.showMessageDialog(parent,
              "Errore nella configurazione del parser XML.",
              "Errore", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    tf = TransformerFactory.newInstance();
    try {
      transformer = tf.newTransformer();
    }
    catch (TransformerConfigurationException e) {
      JOptionPane.showMessageDialog(parent,
              "Errore nella configurazione del trasformatore XML.",
              "Errore", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    openButton = new JButton("Apri");
    openButton.addActionListener(new OpenListener());
    saveButton = new JButton("Salva");
    saveButton.addActionListener(new SaveListener());
    printButton = new JButton("Stampa");
    printButton.addActionListener(new PrintListener());
    add(openButton);
    add(saveButton);
    add(printButton);
  }
  
  public void openXML() {
    final JFileChooser fc = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Cedolino file", "cdl", "xml");
	fc.setFileFilter(filter);
    int result = fc.showOpenDialog(parent);
    if (result != JFileChooser.APPROVE_OPTION) return;
    File fin = fc.getSelectedFile();
    openXML(fin);
  }
  
  public void openXML(String filename) {
    File fin = new File(filename);
    openXML(fin);
  }
  
  public void openXML(File fin) {
    Document doc;
    try {
      doc = db.parse(fin);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(parent,
              "Errore nell'apertura del file.",
              "Errore", JOptionPane.ERROR_MESSAGE);
      return;
    }
    parent.reset();
    try {
      Element root = doc.getDocumentElement();
      Element company = (Element)root.getElementsByTagName("company").item(0);
      String companyName = company.getAttribute("name");
      parent.setCompanyName(companyName);
      Element date = (Element)root.getElementsByTagName("date").item(0);
      int month = Integer.parseInt(date.getAttribute("month"));
      int year = Integer.parseInt(date.getAttribute("year"));
      parent.setDate(month, year);
      NodeList workers = root.getElementsByTagName("worker");
      for (int i = 0; i < workers.getLength(); i++) {
        Element worker = (Element)workers.item(i);
        String name = worker.getAttribute("name");
        Element defaults = (Element)worker.getElementsByTagName("defaults").item(0);
        String[] defaultHours = new String[7];
        for (int j = 0; j < 7; j++) {
          Element weekDay = (Element)defaults.getElementsByTagName(weekDays[j]).item(0);
          defaultHours[j] = weekDay.getAttribute("hours");
        }
        parent.addWorker(name, defaultHours);
        NodeList shifts = worker.getElementsByTagName("shift");
        for (int j = 0; j < shifts.getLength(); j++) {
    	  Element shift = (Element)shifts.item(j);
    	  int day = Integer.parseInt(shift.getAttribute("day"));
    	  String hour = shift.getAttribute("hours");
    	  parent.setDayHour(day, hour);
        }
        parent.selectWorker(name);
      }
    }
    catch (NullPointerException e) {
      JOptionPane.showMessageDialog(parent, "Errore: il file aperto non sembra essere un documento valido.",
    		                        "Errore", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public void saveXML(String companyName,
		              HashMap<String,String[]> defaultHours,
		              HashMap<String,String[]> monthHours,
		              DateTime calendar,
		              File file) {
	Document doc = db.newDocument();
	Element root = doc.createElement("cedolino");
	doc.appendChild(root);
	Element company = doc.createElement("company");
	company.setAttribute("name", companyName);
	root.appendChild(company);
	Element date = doc.createElement("date");
	date.setAttribute("year", Integer.toString(calendar.year().get()));
	date.setAttribute("month", Integer.toString(calendar.monthOfYear().get()));
	root.appendChild(date);
	for (String name : defaultHours.keySet()) {
	  Element worker = doc.createElement("worker");
	  worker.setAttribute("name", name);
	  root.appendChild(worker);
	  Element defaults = doc.createElement("defaults");
	  String[] hours = defaultHours.get(name);
	  for (int i = 0; i < 7; i++) {
		Element weekDay = doc.createElement(weekDays[i]);
	    weekDay.setAttribute("hours", hours[i]);
	    defaults.appendChild(weekDay);
	  }
	  worker.appendChild(defaults);
	  hours = monthHours.get(name);
	  for (int i = 0; i < hours.length; i++) {
		Element shift = doc.createElement("shift");
		shift.setAttribute("day", Integer.toString(i+1));
		shift.setAttribute("hours", hours[i]);
		worker.appendChild(shift);
	  }
	}
	try {
	  transformer.transform(new DOMSource(doc), new StreamResult(file));
	}
	catch (Exception e) {
	      JOptionPane.showMessageDialog(parent,
	            "Errore nella scrittura del file.",
	            "Errore", JOptionPane.ERROR_MESSAGE);
      return;
	}
  }
}
