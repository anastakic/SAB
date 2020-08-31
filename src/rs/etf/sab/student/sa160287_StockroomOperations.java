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
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author DELL
 */
public class sa160287_StockroomOperations implements StockroomOperations{

    @Override
    public int insertStockroom(int idadr) {
        String check = "declare @citycheck int; " +
                        "select @citycheck = (select IDCity from Address where IDAddr = ?)\n" +
                        "select count(*) from dbo.Stockroom S join dbo.Address A on S.IDAddr = A.IDAddr\n" +
                        "where A.IDCity = @citycheck\n";
        try {
            Connection conn = DB.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(check);
            pstmt.setInt(1, idadr);
            ResultSet r = pstmt.executeQuery();
            r.next();
            Integer num = r.getInt(1);
            if(num > 0) return -1;
    
            String insert = "insert into dbo.Stockroom (IDAddr) values (?)";

            try {
                Connection con = DB.getInstance().getConnection();
                PreparedStatement stmt = con.prepareStatement(insert);
                stmt.setInt(1, idadr);
                if (stmt.executeUpdate() == 0) return -1;


                try (PreparedStatement select = con.prepareStatement
                ("SELECT MAX(IDStock) AS Max FROM dbo.Stockroom ")){				
                ResultSet rs = select.executeQuery();
                rs.next();
                Integer id = rs.getInt("Max");

                if(id > 0) return id;
                else return -1;
                }
                catch (SQLException  ex) {
                Logger.getLogger(sa160287_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
                }
            } catch (SQLException  ex) {
                Logger.getLogger(sa160287_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        } catch (SQLException  ex) {
            Logger.getLogger(sa160287_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public boolean deleteStockroom(int i) {
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from dbo.Stockroom where IDStock = ?");
            ps.setInt(1, i);
            return ps.executeUpdate()==1;
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int deleteStockroomFromCity(int i) {
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement
                ("select IDStock from Stockroom\n" +
                "where IDAddr = (\n" +
                "select top(1) IDAddr from Address A \n" +
                "where IDCity = ? and IDAddr = A.IDAddr\n" +
                ")");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if (rs.next() == false) return -1;
            Integer id = rs.getInt("IDStock");
            try{
                PreparedStatement p = con.prepareStatement
                    ("delete from Stockroom where IDStock = ?");
                p.setInt(1, id);
                return ((p.executeUpdate()==1)?id:-1);
                
            }
            catch (SQLException ex) {
                Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public List<Integer> getAllStockrooms() {
        List<Integer> allSt = new ArrayList<Integer>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("select IDStock from dbo.Stockroom");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                allSt.add(rs.getInt("IDStock"));
            }
            
            return allSt;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allSt;
    }
    
}
