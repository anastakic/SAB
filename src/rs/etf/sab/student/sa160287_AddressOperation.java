/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author DELL
 */
public class sa160287_AddressOperation implements AddressOperations{

    @Override
    public int insertAddress(String street, int num, int city, int x, int y) {
        String insert = "INSERT INTO dbo.Address (IDCity, Street, Num, XPos, YPos) VALUES(?,?,?,?,?)";

        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setInt(1, city);
            stmt.setString(2, street);
            stmt.setInt(3, num);
            stmt.setInt(4, x);
            stmt.setInt(5, y);
            
            stmt.executeUpdate();

            try (PreparedStatement select = con.prepareStatement
            ("SELECT MAX(IDAddr) AS Max FROM dbo.Address")){				
            ResultSet rs = select.executeQuery();
            rs.next();
            Integer id = rs.getInt("Max");

            if(id > 0) return id;
            else return -1;
            }
            catch (SQLException e) {
                return -1;
            }
        } catch (SQLException e) {
            return -1;
        } 
    }

    @Override
    public int deleteAddresses(String street, int num) {
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from dbo.Address where Street = ? and Num = ?");
            ps.setString(1, street);
            ps.setInt(2, num);
            return ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public boolean deleteAdress(int i) {
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from dbo.Address where IDAddr = ?");
            ps.setInt(1, i);
            return ps.executeUpdate()==1;
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int deleteAllAddressesFromCity(int i) {
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from dbo.Address where IDCity = ?");
            ps.setInt(1, i);
            return ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public List<Integer> getAllAddresses() {
        List<Integer> allAddr = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDAddr from dbo.Address");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allAddr.add(rs.getInt("IDAddr"));
            }
            return allAddr;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allAddr;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int i) {
        List<Integer> allAddr = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDAddr from dbo.Address where IDCity = ?");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allAddr.add(rs.getInt("IDAddr"));
            }
            
            if (allAddr.isEmpty()) return null;
            return allAddr;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allAddr;
    
    }
    
}
