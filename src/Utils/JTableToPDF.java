/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JTable;

/**
 *
 * @author Diego
 */
public class JTableToPDF {
     
    private String titleReport;  
    private String[] nameHeaders;  
    private JTable jTable;    
  
    public JTableToPDF(String reportTitle, String[] headerNames) {  
        this.titleReport = reportTitle;  
        this.nameHeaders = headerNames;  
    }  
  
    public void runReport(JTable jtable, String pathSaveFile, String fileName) throws Exception {  
        this.jTable = jtable;  
  
        Document document = new Document();
        String caminho = pathSaveFile + "/" + fileName + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(caminho));  
        document.open();  
        document.add(getPdfPTable());  
        document.close();
        
        if (Desktop.isDesktopSupported()) {
             try {              
                File myFile = new File(caminho);
                Desktop.getDesktop().open(myFile);
         } catch (IOException e) {
             System.out.println(e.getMessage());
         }
  
      }
    }
  
    private PdfPTable getPdfPTable() throws Exception {  
        PdfPTable tab = new PdfPTable(nameHeaders.length);  
          
        for (int i = 0; i < nameHeaders.length; i++) {  
            tab.addCell(nameHeaders[i]);  
        }  
  
        int rowCount = jTable.getRowCount();  
        int collumCount = jTable.getColumnCount();  
        for (int x = 0; x < rowCount; x++) {  
            for (int y = 0; y < collumCount; y++) {  
  
                tab.addCell(GetData(jTable, x, y).toString().replace("<html>", "").replace("<br>", "\n").replace("</html>", ""));  
                
            }  
  
        }  
        return tab;  
    }  
  
    private Object GetData(JTable table, int row_index, int col_index) throws Exception {  
        return table.getModel().getValueAt(row_index, col_index);  
    }  
}
