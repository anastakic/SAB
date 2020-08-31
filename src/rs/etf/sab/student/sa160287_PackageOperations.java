/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author DELL
 */
public class sa160287_PackageOperations implements PackageOperations{

    @Override
    public int insertPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {
        String insert = 
            "insert into Package(IDAddrFrom, IDAddrTo, IDUser, PackType, Weight, TimeCreated, DeliveryStatus, CurrLocation, inStock)\n" +
            "values (?, ?, (select IDUser from [User] where Username = ?), ?, ?, GETDATE(), 0, ?, 0)";
        
        try {
            
            if(addressFrom == addressTo) return -1;
            
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setInt(1, addressFrom);
            stmt.setInt(2, addressTo);
            stmt.setString(3, userName);
            stmt.setInt(4, packageType);
            stmt.setBigDecimal(5, weight);
            stmt.setInt(6, addressFrom);
            stmt.executeUpdate();

            try (PreparedStatement select = con.prepareStatement
            ("SELECT MAX(IDPackage) AS Max FROM Package")){				
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
    public boolean acceptAnOffer(int i) {
        String acc = 
            "update Package \n" +
            "set DeliveryStatus = 1 , TimeAccepted = GETDATE() \n" +
            "where IDPackage = ? and DeliveryStatus = 0";
        String updateUser = 
            "update [User]\n" +
            "set PackNum = PackNum + 1\n" +
            "where IDUser = (select IDUser from Package where IDPackage = ?)";
        
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(updateUser);
            stmt.setInt(1, i);
            stmt.executeUpdate();
            stmt.close();
            
            PreparedStatement st = con.prepareStatement(acc);
            st.setInt(1, i);
            return st.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }        
        
    }

    @Override
    public boolean rejectAnOffer(int i) {
        String acc = 
            "update Package \n" +
            "set DeliveryStatus = 4\n" +
            "where IDPackage = ? and DeliveryStatus = 0";
        
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(acc);
            
            stmt.setInt(1, i);
            
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> allPack = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDPackage from Package");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allPack.add(rs.getInt("IDPackage"));
            }
            return allPack;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPack;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        List<Integer> allPack = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDPackage from Package where DeliveryStatus = ?");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allPack.add(rs.getInt("IDPackage"));
            }
            return allPack;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPack;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        List<Integer> allPack = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDPackage from Package where DeliveryStatus = 1 or DeliveryStatus = 2");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allPack.add(rs.getInt("IDPackage"));
            }
            return allPack;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPack;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int i) {
        List<Integer> allPack = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement
                   ("select IDPackage from Package P join Address A on P.IDAddrFrom = A.IDAddr\n" +
                    "where A.IDCity = ? and (DeliveryStatus = 1 or DeliveryStatus = 2)");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allPack.add(rs.getInt("IDPackage"));
            }
            return allPack;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPack;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int i) {
        List<Integer> allPack = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement
                ("select IDPackage from Package P join Address A on P.CurrLocation = A.IDAddr\n" +
                "where IDCity = ? and P.IDVehicle is null and (DeliveryStatus = 1 or DeliveryStatus = 2 or DeliveryStatus = 3)");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allPack.add(rs.getInt("IDPackage"));
            }
            return allPack;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPack;
    }

    @Override
    public boolean deletePackage(int i) {
        String del = "delete from Package where IDPackage = ?";
        String updateUser = 
            "if (select DeliveryStatus from Package \n" +
            "where IDPackage = ?) = 1 or (select DeliveryStatus from Package \n" +
            "where IDPackage = ?) = 2\n" +
            "begin\n" +
            "	update [User]\n" +
            "	set PackNum = PackNum - 1\n" +
            "	where IDUser = (select IDUser from Package where IDPackage = ?)\n" +
            "end";
        
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(updateUser);
            stmt.setInt(1, i);
            stmt.setInt(2, i);
            stmt.setInt(3, i);
            stmt.executeUpdate();
            stmt.close();
            
            PreparedStatement st = con.prepareStatement(del);
            st.setInt(1, i);
            return  st.executeUpdate()==1;
           
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean changeWeight(int id, BigDecimal w) {
        String update = "update Package \n" +
                        "set Weight = ? \n" +
                        "where IDPackage = ? and DeliveryStatus = 0 ";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setBigDecimal(1, w);
            stmt.setInt(2, id);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean changeType(int id, int t) {
        String update = "update Package \n" +
                        "set PackType = ? \n" +
                        "where IDPackage = ? and DeliveryStatus = 0 ";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setInt(1, t);
            stmt.setInt(2, id);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int getDeliveryStatus(int i) {
        String update = "select DeliveryStatus from Package\n" +
                        "where IDPackage = ? ";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            stmt.setInt(1, i);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) return -1; 
            return rs.getInt("DeliveryStatus");
        } catch (SQLException e) {
            return -1;
        }
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        String update = "select Price from Package\n" +
                        "where IDPackage = ? ";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            stmt.setInt(1, i);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) return BigDecimal.valueOf(-1); 
            return rs.getBigDecimal("Price");
        } catch (SQLException e) {
            return BigDecimal.valueOf(-1);
        }
    }

    @Override
    public int getCurrentLocationOfPackage(int i) {
        String update = "select IDCity from Package P join Address A on P.CurrLocation = A.IDAddr\n" +
                        "where IDPackage = ? and P.IDVehicle is null";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            stmt.setInt(1, i);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) return -1; 
            return rs.getInt("IDCity");
        } catch (SQLException e) {
            return -1;
        }
    }

    @Override
    public Date getAcceptanceTime(int i) {
        String update = "select TimeAccepted from Package where IDPackage = ?";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            stmt.setInt(1, i);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) return null; 
            return rs.getDate("TimeAccepted");
        } catch (SQLException e) {
            return null;
        }
    }
    
}
