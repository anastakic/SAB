
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author DELL
 */
public class sa160287_CourierRequestOperation implements CourierRequestOperation{

    @Override
    public boolean insertCourierRequest(String username, String driveLic) {
        String insert = 
            "if not exists (select * from Courier where IDUser = (select IDUser from [User] where Username = ?)) " +
            "begin " +
            "insert into CourierRequest(IDUser, DriverLicense) " +
            "values ((select IDUser from [User] where Username = ?), ?) " +
            "end";

        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, driveLic);

            return stmt.executeUpdate()==1;
            
            
        } catch (SQLException e) {
            return false;
        } 
    }

    @Override
    public boolean deleteCourierRequest(String username) {
        String insert = "{ call dbo.spDenyCourierRequest(?) }";

        try {
            Connection con = DB.getInstance().getConnection();
            CallableStatement stmt = con.prepareCall(insert);
            stmt.setString(1, username);

            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String username, String driveLic) {
        try {
            String setAdmin = "update CourierRequest \n" +
                                "set DriverLicense = ? \n" +
                                "where IDUser = ( \n" +
                                "select IDUser from [User] \n" +
                                "where Username = ?)";
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(setAdmin, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(2, username);
            stmt.setString(1, driveLic);
            
            return stmt.executeUpdate()==1;
            
        } catch (SQLException  ex) {
            Logger.getLogger(sa160287_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> allReq = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select Username from CourierRequest C join [User] U on C.IDUser = U.IDUser");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allReq.add(rs.getString("Username"));
            }
        //  for(Integer i : allCities){
	//	System.out.println(i);
        //  }
            return allReq;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allReq;
    }

    @Override
    public boolean grantRequest(String username) {
        String insert = "{ call dbo.spAcceptCourierRequest(?) }";

        try {
            Connection con = DB.getInstance().getConnection();
            CallableStatement stmt = con.prepareCall(insert);
            stmt.setString(1, username);

            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        } 
    }
    
}
