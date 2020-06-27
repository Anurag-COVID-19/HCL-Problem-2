package com.dbInsertion;

import java.io.*;
import java.sql.*;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class IsertionIntoDB {
	public static void main(String args[]) {
		String jdbcURL = "jdbc:mysql:// localhost:3306/";
		String username = "root";
		String password = "root";
		
		String csvFilePath = "downloadFromSFTP.csv";
		int batchSize = 20;
		
		Connection con = null;
		
		ICsvBeanReader beanReader = null;
		CellProcessor[] processors = new CellProcessor[] {
				new ParseTimestamp(),
				new ParseDouble(),
				new NotNull(),
				new ParseDouble(),
				new ParseDouble()
		};
		try {
            long start = System.currentTimeMillis();
 
            con = DriverManager.getConnection(jdbcURL, username, password);
            con.setAutoCommit(false);
            String sql = "INSERT INTO review (Result_Time, Granularity_Period, Object_Name, Cell_ID, CallAttemps) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
 
            beanReader = new CsvBeanReader(new FileReader(csvFilePath),
                    CsvPreference.STANDARD_PREFERENCE);
            beanReader.getHeader(true);
            
            String[] header = {"Result_Time", "Granularity_Period", "Object_Name", "Cell_ID", "CallAttemps"};
            Review bean = null;
		
            int count = 0;
            
            while ((bean = beanReader.read(Review.class, header, processors)) != null) {
	            Timestamp resultTme = bean.getResultTme();
	            double granulPeriod = bean.getGranuPeriod();
	            String objName = bean.getObjName();
	            double callId = bean.getCellId();
	            double callAttempts = bean.getCallAttempts();
	            
	            statement.setTimestamp(1, resultTme);
  				statement.setDouble(2, granulPeriod);
  				statement.setString(3, objName);
  				statement.setDouble(4, callId);
  				statement.setDouble(5, callAttempts);
  				statement.addBatch();
  					
  			if (count % batchSize == 0) {
  				statement.executeBatch();
  			}
            }
            beanReader.close();
    		statement.executeBatch();
    
		    con.commit();
		    con.close();
		    
		    long end = System.currentTimeMillis();
		    System.out.println("Execution Time: " + (end - start));
		}catch (IOException ex) {
		    System.err.println(ex);
		} catch (SQLException ex) {
		    ex.printStackTrace();
		
		    try {
		        con.rollback();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	}
}
