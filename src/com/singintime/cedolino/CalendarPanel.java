package com.singintime.cedolino;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.joda.time.DateTime;

public class CalendarPanel extends JPanel {
  private Cedolino parent;
  private int columns;
  private JTextField[] hourFields;

  private class HourListener implements ActionListener, DocumentListener {
	private int day;
	
	public HourListener(int day) {
      this.day = day;
	}
	
	public void actionPerformed(ActionEvent e) {
	  execute();
	}

	public void changedUpdate(DocumentEvent e) {
	  execute();
	}

	public void insertUpdate(DocumentEvent e) {
      execute();
	}

	public void removeUpdate(DocumentEvent e) {
      execute();
	}
	
	private void execute() {
      String hour = hourFields[day-1].getText();
      parent.setDayHour(day, hour);
	}
  }
  
  public CalendarPanel(Cedolino parent, DateTime calendar, int columns) {
    this.parent = parent;
    this.columns = columns;
    update(calendar);
  }

  public void update(DateTime calendar) {
    int maxDays = calendar.dayOfMonth().getMaximumValue();
    int stride = (maxDays + columns - 1) / columns;
    removeAll();
    hourFields = new JTextField[maxDays];
    GridLayout gridLayout = new GridLayout(0, columns * 2);
    setLayout(gridLayout);
    for (int i = 0; i < stride; i++) {
      for (int j = 0; j < columns; j++) {
        int day = j*stride+i+1;
        if (day <= maxDays) {
          calendar = calendar.withDayOfMonth(day);
          String dayOfWeek = calendar.dayOfWeek().getAsShortText(Locale.ITALIAN);
          JLabel dayLabel = new JLabel(dayOfWeek + " " + day + "   ", JLabel.RIGHT);
          add(dayLabel);
          JTextField hourField = new JTextField("0.00");
          HourListener hourListener = new HourListener(day);
          hourField.addActionListener(hourListener);
          hourField.getDocument().addDocumentListener(hourListener);
          hourField.setEnabled(false);
          add(hourField);
          hourFields[day-1] = hourField;
        }
        else {
          add(new JLabel(""));
          add(new JLabel(""));
        }
      }
    }
    revalidate();
  }
  
  public void showHours(String[] hours) {
    for (int i = 0; i < hourFields.length; i++) {
      hourFields[i].setText(hours[i]);
      hourFields[i].setEnabled(true);
    }
  }
  
  public void disableHours() {
	for (int i = 0; i < hourFields.length; i++) {
	  hourFields[i].setText("0.00");
	  hourFields[i].setEnabled(false);
	}
  }
}
