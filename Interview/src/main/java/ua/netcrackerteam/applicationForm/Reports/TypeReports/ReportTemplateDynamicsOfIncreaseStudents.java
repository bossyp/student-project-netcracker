/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.netcrackerteam.applicationForm.Reports.TypeReports;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.data.category.DefaultCategoryDataset;
import ua.netcrackerteam.DAO.DAOReport;
import ua.netcrackerteam.applicationForm.Reports.Chart;
import ua.netcrackerteam.applicationForm.Reports.ReportTemplateBuilder;

/**
 *
 * @author home
 */
public class ReportTemplateDynamicsOfIncreaseStudents extends ReportTemplateBuilder{
    
    private List reportData;
    
    public ReportTemplateDynamicsOfIncreaseStudents() {
        
         DAOReport reportDAO = new DAOReport();
         reportData = reportDAO.getReportDynamicsOfIncreaseStudents();
         
         if(reportData == null){
             reportData = new ArrayList();
         }
    }
    
    @Override
    public PdfPCell buildTitle() {
        PdfPCell cell= new PdfPCell();
        try {
            cell = report.setTitle("Статистика увеличения записанных студентов на собеседования");
        } catch (IOException ex) {
            Logger.getLogger(ReportTemplateDynamicsOfIncreaseStudents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ReportTemplateDynamicsOfIncreaseStudents.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cell;
    }

    @Override
    public PdfPCell buildTable() {
        
        String[] header = new String[]{"Дата собеседования", "Всего", "Зарегистрировано", "Свободно"};       
        String[] footer = getFooter(reportData);
        
        float[] size = new float[]{2f, 1.5f, 1.5f, 1.5f};
       
        
        PdfPCell cell = new PdfPCell();
        try {
            cell = report.setTable(header, reportData, footer, size);
        } catch (DocumentException ex) {
            Logger.getLogger(ReportTemplateDynamicsOfIncreaseStudents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportTemplateDynamicsOfIncreaseStudents.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cell;
        
    }

    @Override
    public PdfPCell buildChart() {
       
        Chart chartTemplate = new Chart(getCategoryDataSet(), "Динамика регистрации на собеседования");
        chartTemplate.createChartBar3D();
    
        PdfPCell cell = new PdfPCell();
        try {
            cell = report.setChart(chartTemplate);
        } catch (BadElementException ex) {
            Logger.getLogger(ReportTemplateDynamicsOfIncreaseStudents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportTemplateDynamicsOfIncreaseStudents.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cell;
    }
        
        private DefaultCategoryDataset getCategoryDataSet(){
          
          DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
          ListIterator iterator = reportData.listIterator();         
          for(int i = 0; iterator.hasNext(); i++){ 
             
             Object[] rowReportData = (Object[])iterator.next();
            
             int maxForms = Integer.parseInt(rowReportData[1].toString());
             int summaForms = Integer.parseInt(rowReportData[2].toString());
             
             double percent = new BigDecimal((maxForms == 0? 0: 100*summaForms/maxForms)).setScale(2, RoundingMode.UP).doubleValue();
             dataSet.addValue(percent, "percent", rowReportData[0].toString());
             
           } 
           return dataSet;             
        }
  
    
   private String[] getFooter(List dataReport){
       
       int amount = 0;
       int free = 0;
       
       ListIterator iterator = dataReport.listIterator();
       while(iterator.hasNext()){
           
           Object[] row = (Object[])iterator.next();
           amount += Integer.parseInt(row[1].toString());
           free += Integer.parseInt(row[3].toString());
       }
      return new String[]{"Итого", "" + amount, "" + (amount - free), "" + free};
      
 
   }

    @Override
    public List dataReport() {
        return reportData;
    }

    @Override
    public byte[] getChart(int widht, int height) {
        
        Chart chartTemplate = new Chart(getCategoryDataSet(),"Динамика регистрации на собеседования");
        chartTemplate.createChartBar3D();
        
        return chartTemplate.getByteChart(500, 400);
    }

   
    }