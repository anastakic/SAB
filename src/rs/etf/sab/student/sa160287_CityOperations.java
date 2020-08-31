/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;
/**
 *
 * @author DELL
 */
public class sa160287_CityOperations implements CityOperations{

    @Override
    public int insertCity(String name, String postalCode) {
        String insert = "INSERT INTO dbo.City(Name, PostalCode) VALUES(?, ?)";

        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setString(1, name);
            stmt.setString(2, postalCode);

            stmt.executeUpdate();

            try (PreparedStatement select = con.prepareStatement
            ("SELECT MAX(IDCity) AS Max FROM dbo.City WHERE PostalCode = ?")){				
            select.setString(1, postalCode);
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
    public int deleteCity(String... strings) {
        int ret = 0;
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from dbo.City where Name = ?");
            for(int i = 0; i < strings.length; i++){
                ps.setString(1, strings[i]);
                ret += ps.executeUpdate();
            }
            return ret;
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public boolean deleteCity(int i) {
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from dbo.City where IDCity = ?");
            ps.setInt(1, i);
            return ps.executeUpdate()==1;
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> allCities = new ArrayList<Integer>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDCity from dbo.City");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allCities.add(rs.getInt("IDCity"));
            }
        //  for(Integer i : allCities){
	//	System.out.println(i);
        //  }
            return allCities;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCities;
    }
    
}
