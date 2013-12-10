package com.singintime.cedolino;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CautiousFileChooser extends JFileChooser {
  @Override
  public File getSelectedFile() {
    File selected = super.getSelectedFile();
    if (selected != null) {
      String path = selected.getAbsolutePath();
      if (!path.endsWith(".cdl")) {
        selected = new File(path + ".cdl");
      }
    }
    return selected;
  }

  @Override
  public void approveSelection() {
    File f = getSelectedFile();
    if(f.exists() && getDialogType() == SAVE_DIALOG) {
      int result = JOptionPane.showConfirmDialog(this,
    		  "Il file esiste già. Sovrascrivere?",
    		  "Attenzione!", JOptionPane.YES_NO_CANCEL_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        super.approveSelection();
      }
      else if (result == JOptionPane.CANCEL_OPTION) {
    	cancelSelection();
      }
      return;
    }
    super.approveSelection();
  }
}
