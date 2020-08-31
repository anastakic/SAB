/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author DELL
 */
public class sa160287_VehicleOperations implements VehicleOperations{

    @Override
    public boolean insertVehicle(String licencePlateNumber,int fuelType, BigDecimal fuelConsumtion, BigDecimal capacity) {
        String insert = "insert into Vehicle(Capacity, ConsumPerKm, FuelType, RegPlate, isFree)\n" +
                        "values (?, ?, ?, ?, 1)";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setBigDecimal(1, capacity);
            stmt.setBigDecimal(2, fuelConsumtion);
            stmt.setInt(3, fuelType);
            stmt.setString(4, licencePlateNumber);
            
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int deleteVehicles(String... strings) {
        String del = "delete from Vehicle where RegPlate = ?";
        int dels = 0;
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(del);
            
            for (String string : strings) {
                stmt.setString(1, string);
                dels += stmt.executeUpdate();
            }
            return dels;
        } catch (SQLException e) {
            return dels;
        }
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> allVeh = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select RegPlate from Vehicle");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allVeh.add(rs.getString("RegPlate"));
            }
            return allVeh;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allVeh;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        String update = "update Vehicle \n" +
                        "set FuelType = ? \n" +
                        "where RegPlate = ?  and isFree = 1 and IDStock is not null";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setInt(1, i);
            stmt.setString(2, string);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        String update = "update Vehicle \n" +
                        "set ConsumPerKm = ? \n" +
                        "where RegPlate = ?  and isFree = 1 and IDStock is not null";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setBigDecimal(1, bd);
            stmt.setString(2, string);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean changeCapacity(String string, BigDecimal bd) {
        String update = "update Vehicle \n" +
                        "set Capacity = ? \n" +
                        "where RegPlate = ? and isFree = 1 and IDStock is not null";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setBigDecimal(1, bd);
            stmt.setString(2, string);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean parkVehicle(String string, int i) {
        String update = "update Vehicle \n" +
        "set IDStock = ?, isFree = 1, CurrLocation = (select IDAddr from Stockroom where IDStock = ?) \n" +
        "where RegPlate = ? ";
        
        String updatePackFromVeh = "update Package \n" +
            "set inStock = 1, IDVehicle = null, CurrLocation = (select IDAddr from Stockroom where IDStock = ?) \n" +
            "where IDPackage in\n" +
            "(\n" +
            "	select IDPackage\n" +
            "	from Vehicle V join Package P on V.IDVehicle = P.IDVehicle\n" +
            "	where V.RegPlate = ? and inStock = 0 \n" +
            ")";
        
        try {
            Connection con = DB.getInstance().getConnection();
            
            PreparedStatement st = con.prepareStatement(updatePackFromVeh);
            st.setInt(1, i);
            st.setString(2, string);
            st.executeUpdate();
            st.close();
            
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setInt(1, i);
            stmt.setInt(2, i);
            stmt.setString(3, string);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }
    
}
