/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author DELL
 */
public class sa160287_CourierOperations implements CourierOperations{

    @Override
    public boolean insertCourier(String username, String driveLic) {
        String insert = 
            "insert into Courier(IDUser, DriverLicense, Status, DeliveryNum, Profit)\n" +
            "values ((select IDUser from [User] where Username = ?), ?, 0, 0, 0) \n";

        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setString(1, username);
            stmt.setString(2, driveLic);

            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteCourier(String username) {
        String insert = "delete from Courier where IDUser = (select IDUser from [User] where Username = ?)";

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
    public List<String> getCouriersWithStatus(int i) {
        List<String> allCour = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement
                    ("select Username from Courier C join [User] U on C.IDUser = U.IDUser "
                    + "where C.Status = ?");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allCour.add(rs.getString("Username"));
            }
            return allCour;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCour;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> allCour = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select Username from Courier C join [User] U on C.IDUser = U.IDUser");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allCour.add(rs.getString("Username"));
            }
        //  for(Integer i : allCities){
	//	System.out.println(i);
        //  }
            return allCour;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCour;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
    //select AVG(Profit) from Courier where DeliveryNum = 4
        String averageDelivery = 
            "select AVG(Profit) from Courier where DeliveryNum = ?";
        String averageAll = 
            "select AVG(Profit) from Courier";

        try {
            Connection con = DB.getInstance().getConnection();
            if (i != -1){
                PreparedStatement stmt = con.prepareStatement(averageDelivery);
                stmt.setInt(1, i);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getBigDecimal(1);
            }
            else {
                PreparedStatement stmt = con.prepareStatement(averageAll);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getBigDecimal(1);
            }
            
        } catch (SQLException e) {
            return BigDecimal.valueOf(0);
        }
    }
    
}
