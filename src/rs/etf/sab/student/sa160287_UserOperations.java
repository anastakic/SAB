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
import rs.etf.sab.operations.UserOperations;
/**
 *
 * @author DELL
 */
public class sa160287_UserOperations implements UserOperations{

    private boolean isNameValid(String name){
        char[] pass = name.toCharArray();
        
        for (int i = 0; i < pass.length; i++) {
            if (!(Character.isAlphabetic(pass[i]))) return false;
            if (Character.isLowerCase(pass[0])) return false;
            if (i != 0 && Character.isUpperCase(pass[i])) return false;
        }
        return true;
    }
    
    @Override
    public boolean insertUser(String username, String firstN, String lastN, String pass, int idAdr) {
        String insert = "insert into [User](Username, LastName, FirstName, IsAdmin, Password, IdAddr, PackNum)\n" +
                        "values (?,?,?,?,?,?,0)";

        try {
            if (!PasswordValidator.valid(pass)) return false;
            if (!isNameValid(firstN)) return false;
            if (!isNameValid(lastN)) return false;
            
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setString(1, username);
            stmt.setString(2, lastN);
            stmt.setString(3, firstN);
            stmt.setBoolean(4, false);
            stmt.setString(5, pass);
            stmt.setInt(6, idAdr);
            
            stmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        } 
    }

    @Override
    public boolean declareAdmin(String username) {
        try {
            String setAdmin = "select * from [User] where Username = ?";
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(setAdmin, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) return false;
            if (rs.getBoolean("isAdmin") == true) return false;
            
            rs.updateBoolean("isAdmin", true);
            rs.updateRow();

            return true;
            
        } catch (SQLException  ex) {
            Logger.getLogger(sa160287_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int getSentPackages(String... strings) {
        List<String> usernames = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) usernames.add(strings[i]);
        try {
            String setAdmin = "select * from [User]";
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(setAdmin, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            
            boolean noOne = true;
            int sum = 0;
            while (rs.next()){
                if (usernames.contains(rs.getString("Username"))){
                    noOne = false;
                    sum += rs.getInt("PackNum");
                }
            }
            if (noOne) return -1;
            return sum;
            
        } catch (SQLException  ex) {
            Logger.getLogger(sa160287_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public int deleteUsers(String... strings) {
        int ret = 0;
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from [User] where Username = ?");
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
    public List<String> getAllUsers() { 
        List<String> allU = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select Username from [User]");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allU.add(rs.getString("Username"));
            }
            return allU;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allU;
    }
    
}
