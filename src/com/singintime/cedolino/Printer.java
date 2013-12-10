package com.singintime.cedolino;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.joda.time.DateTime;

public class Printer implements Printable {
  private Cedolino parent;
  private Component[] toPrint;
  private Border grayline;

  public Printer(Cedolino parent) {
	this.parent = parent;
	grayline = BorderFactory.createLineBorder(Color.gray);
  }
  
  public int print(Graphics g, PageFormat format, int page_index) 
         throws PrinterException {
    if (page_index >= toPrint.length) {
      return Printable.NO_SUCH_PAGE;
    }
    Component comp = toPrint[page_index];
    
    // get the bounds of the component
    Dimension dim = comp.getSize();
    double cWidth = dim.getWidth();

    // get the bounds of the printable area
    double pWidth = format.getImageableWidth();

    double pXStart = format.getImageableX();
    double pYStart = format.getImageableY();

    double xRatio = pWidth / cWidth;

    Graphics2D g2 = (Graphics2D)g;
    g2.translate(pXStart, pYStart);
    g2.scale(xRatio, xRatio);
    JFrame frame = new JFrame();
    frame.add(comp);
    frame.pack();
    comp.paint(g2);

    return Printable.PAGE_EXISTS;
  }
  
  public void printAll(String companyName,
                       HashMap<String,String[]> monthHours,
                       DateTime calendar) {
	update(companyName, monthHours, calendar);
	PrinterJob pjob = PrinterJob.getPrinterJob();
    PageFormat pf = pjob.defaultPage();
    pf.setOrientation(PageFormat.PORTRAIT);
    if (pjob.printDialog()) {
      Book book = new Book();
      book.append(this, pf, toPrint.length);
      pjob.setPageable(book);
      try {
        pjob.print();
      }
      catch (PrinterException e) {
        JOptionPane.showMessageDialog(parent, "Errore: impossibile stampare il documento.",
        		                      "Errore", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  
  private void update(String companyName,
                      HashMap<String,String[]> monthHours,
                      DateTime calendar) {
    String[] workers = monthHours.keySet().toArray(new String[0]);
    Arrays.sort(workers);
    int pages = (workers.length + 2) / 3;
    toPrint = new Component[pages];
    for (int pIndex = 0; pIndex < pages; pIndex++) {
      JPanel panel = new JPanel();
      int yDimension = 50 + 250 * Math.min(3, workers.length - pIndex * 3);
      panel.setPreferredSize(new Dimension(600,yDimension));
      panel.setBackground(Color.white);
      panel.setOpaque(true);
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      JLabel companyLabel = new JLabel(companyName);
      companyLabel.setFont(new Font(companyLabel.getFont().getName(), Font.BOLD, 24));
      companyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      panel.add(companyLabel);
      String month = calendar.monthOfYear().getAsText(Locale.ITALIAN);
      int year = calendar.year().get();
      JLabel monthLabel = new JLabel(month + " " + year + " (" + (pIndex+1) + "/" + pages + ")");
      monthLabel.setFont(new Font(companyLabel.getFont().getName(), Font.BOLD, 16));
      monthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      panel.add(monthLabel);
      for (int w = 0; w < 3; w++) {
        int wIndex = pIndex * 3 + w;
        if (wIndex >= workers.length) break;
        panel.add(new JLabel(" "));
        String worker = workers[wIndex];
        JLabel workerLabel = new JLabel(worker);
        workerLabel.setFont(new Font(companyLabel.getFont().getName(), Font.BOLD, 12));
        workerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(workerLabel);
        String[] workerHours = monthHours.get(worker);
        JPanel hoursGrid = new JPanel();
        int columns = (workerHours.length + 2) / 3;
        hoursGrid.setLayout(new GridLayout(6, columns));
        for (int j = 0; j < 3; j++) {
          for (int i = 0; i < columns; i++) {
            int index = j * columns + i;
            JLabel dayLabel;
            if (workerHours.length > index) {
              dayLabel = new JLabel(Integer.toString(index+1), JLabel.CENTER);
            }
            else {
              dayLabel = new JLabel("", JLabel.CENTER);
            }
            dayLabel.setFont(new Font(companyLabel.getFont().getName(), Font.BOLD, 10));
            dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dayLabel.setBackground(Color.lightGray);
            dayLabel.setOpaque(true);
            dayLabel.setBorder(grayline);
            hoursGrid.add(dayLabel);
          }
          for (int i = 0; i < columns; i++) {
            int index = j * columns + i;
            JLabel hourLabel;
            if (workerHours.length > index) {
              hourLabel = new JLabel(workerHours[index], JLabel.CENTER);
            }
            else {
              hourLabel = new JLabel("", JLabel.CENTER);
            }
            hourLabel.setFont(new Font(companyLabel.getFont().getName(), Font.PLAIN, 10));
            hourLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            hourLabel.setBorder(grayline);
            hoursGrid.add(hourLabel);
          }
        }
        panel.add(hoursGrid);
      }
      toPrint[pIndex] = panel;
    }
  }
}