/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author DELL
 */
public class sa160287_GeneralOperations implements GeneralOperations{

    @Override
    public void eraseAll() {
        try {
            Connection con = DB.getInstance().getConnection();
            String erase = 
                    "delete from Package\n" +
                    "delete from NextStop\n" +
                    "delete from Drive\n" +
                    "delete from AllDrivings\n" +
                    "delete from Courier\n" +
                    "delete from CourierRequest\n" +
                    "delete from Vehicle\n" +
                    "delete from Stockroom\n" +
                    "delete from [User]\n" +
                    "delete from Address\n" +
                    "delete from City\n" +
                    "delete from Offer";
            PreparedStatement ps  = con.prepareStatement(erase);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
