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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DriveOperation;

/**
 *
 * @author DELL
 */
public class sa160287_DriveOperation implements DriveOperation{
    
    private class NextStop{
        protected int idLoc;
        protected int index;
        
        protected int idPack;
        protected BigDecimal packWight;
        protected BigDecimal packPrice;
        protected BigDecimal distance;
        protected Date timeCreate;
        
        protected int idCity;
        
        protected int isDel;
        
        protected int x;
        protected int y;  
        protected int addrFrom;
        
        protected int xTo;
        protected int yTo;  
        protected int addrTo;
        
        public NextStop(int location, int idpack, int isDelivery){
            idLoc = location;
            idPack = idpack;
            isDel = isDelivery;
        }
    }
    
    private double distance;
    private int currLastLoc; 
    private int XcurrLastLoc;
    private int YcurrLastLoc;
    private List<NextStop> allStops;
    
    private int getCourierId(String username){
        String sql = "select IDUser from [User] where Username = ?";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs  = ps.executeQuery();
            if (rs.next() == false) return -1;
            return rs.getInt("IDUser");            
        }catch (SQLException e) {
            return -1;
        }
    }
    
    private int getCourierCity(int id){
        String sql = "select IDCity from [User] U join Address A on U.IDAddr = A.IDAddr\n" +
                     "where U.IDUser = ?\n";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs  = ps.executeQuery();
            if (rs.next() == false) return -1;
            return rs.getInt("IDCity");            
        }catch (SQLException e) {
            return -1;
        }
    }
    
    private int getAddressCity(int id){
        String sql = "select IDCity from Address where IDAddr = ?";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() == false) return -1;
            return rs.getInt("IDCity");            
        }catch (SQLException e) {
            return -1;
        }
    }
   
    private int getStockFromCity(int city){
        String sql = "select IDStock from Stockroom S join Address A on S.IDAddr = A.IDAddr\n" +
                     "where A.IDCity = ?";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, city);
            ResultSet rs  = ps.executeQuery();
            if (rs.next() == false) return -1;
            return rs.getInt("IDStock");            
        }catch (SQLException e) {
            return -1;
        }
    }
    
    private int getVehicleFromStock(int stock){
        String sql = "select IDVehicle from Vehicle\n" +
                     "where IDStock = ? and isFree = 1";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, stock);
            ResultSet rs  = ps.executeQuery();
            if (rs.next() == false) return -1;
            return rs.getInt("IDVehicle");            
        }catch (SQLException e) {
            return -1;
        }
    }
    
    private BigDecimal getVehicleCapacity(int veh){
        String sql = "select Capacity from Vehicle\n" +
                     "where IDVehicle = ?";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, veh);
            ResultSet rs  = ps.executeQuery();
            if (rs.next() == false) return BigDecimal.valueOf(-1);
            return rs.getBigDecimal("Capacity");            
        }catch (SQLException e) {
            return BigDecimal.valueOf(-1);
        }
    }
    
    private void updateCourier(int idCour, int idVeh){
        String sql = "update Courier\n" +
                     "set Status = 1, IDVehicle = ? \n" +
                     "where IDUser = ? ";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idVeh);
            ps.setInt(2, idCour);
            ps.executeUpdate();            
        }catch (SQLException e) {
            return;
        }
    }
    
    private void updateVehicle(int idVeh){
        String sql = "update Vehicle\n" +
                     "set isFree = 0 \n" +
                     "where IDVehicle = ? ";
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idVeh);
            ps.executeUpdate();            
        }catch (SQLException e) {
        }
    }
    
    private int addDrive(int idUser, int idVeh){
        String insert = 
                "insert into Drive(IDUser, IDVehicle, Distance, PackPrice)\n" +
                "values (?, ?, 0, 0)\n" +
                "select id = SCOPE_IDENTITY();";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setInt(1, idUser);
            stmt.setInt(2, idVeh);

            ResultSet rs = stmt.executeQuery();
            if(rs.next() == false) return -1;
            return rs.getInt("id");
        } catch (SQLException e) {
            return -1;
        }
    }
    
    private List<NextStop> getAllUndelPackFromCityNOTinStock(int id){
        try{
            List<NextStop> fetchStops = new ArrayList<NextStop>();
            String sql = 
                    "select * from Package P join Address A on P.IDAddrFrom = A.IDAddr\n" +
                    "where IDCity = ? and inStock = 0 and DeliveryStatus = 1 "+ 
                    "and IDPackage not in (select IDPackage from NextStop where IDPackage is not null)" +
                    "order by TimeCreated, Weight";
                                    
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
               NextStop ns = new NextStop(rs.getInt("CurrLocation"), rs.getInt("IDPackage"), 0);
               ns.packWight = rs.getBigDecimal("Weight");
               ns.x = rs.getInt("XPos");
               ns.y = rs.getInt("YPos");
               ns.packPrice = rs.getBigDecimal("Price");
               ns.timeCreate = rs.getDate("TimeCreated");
               ns.addrFrom = rs.getInt("IDAddrFrom");
               ns.addrTo = rs.getInt("IDAddrTo");
               ns.idCity = id;
               fetchStops.add(ns);
            }
            return fetchStops;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private List<NextStop> getAllPackFromStock(int idStock){
        try{
            List<NextStop> stops = new ArrayList<>();
            String sql = 
                    "select * from Package P join Stockroom S on P.CurrLocation = S.IDAddr " +
                    "join Address A on A.IDAddr = S.IDAddr " +
                    "where P.inStock = 1 and S.IDStock = ? " +
                    "order by TimeCreated, Weight";
                                    
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1, idStock);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
               NextStop ns = new NextStop(rs.getInt("CurrLocation"), rs.getInt("IDPackage"), 2);
               ns.packWight = rs.getBigDecimal("Weight");
               ns.x = rs.getInt("XPos");
               ns.y = rs.getInt("YPos");
               ns.packPrice = rs.getBigDecimal("Price");
               ns.timeCreate = rs.getDate("TimeCreated");
               ns.idCity = rs.getInt("IDCity");
               ns.addrTo = rs.getInt("IDAddrTo");
               stops.add(ns);
            }
            return stops;
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
   
    private BigDecimal addPlanFetchPackFromCity(List<NextStop> stops, BigDecimal cap, int idDrive, int delStatus){
        BigDecimal mycap = cap;
        for(NextStop ns: stops){
            if (mycap.compareTo(ns.packWight) < 0) {break;}
            mycap = mycap.subtract(ns.packWight);
            currLastLoc = ns.idLoc;
            XcurrLastLoc = ns.x;
            YcurrLastLoc = ns.y;
            allStops.add(ns);
            //System.out.println("ubacuje se " + ns.idPack +" status: " + delStatus);
            addNextStop(ns.idPack, idDrive, delStatus);
        }
        
        return mycap;
    }
    
    private void addNextStop(int idPack, int idDrive, int isDel){
        String insert = 
            "insert into NextStop(IDPackage, IDDrive, isDelivery)\n" +
            "values (?, ?, ?)";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setInt(1, idPack);
            stmt.setInt(2, idDrive);
            stmt.setInt(3, isDel);
            stmt.executeUpdate();
        } catch (SQLException e) {
            
        }
    }
    
    private void addLastNextStop(int idDrive, int isDel){
        String insert = 
            "insert into NextStop(IDDrive, isDelivery)\n" +
            "values (?, ?)";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            
            stmt.setInt(1, idDrive);
            stmt.setInt(2, isDel);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            
        }
    }
    
    private void addInAllDrivings(int idCourier, int idVehicle){
        String insert = 
            "insert into AllDrivings(IDUser, IDVehicle)\n" +
            "values (?, ?)";
        try {
            Connection con = DB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(insert);
            stmt.setInt(1, idCourier);
            stmt.setInt(2, idVehicle);

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            
        }
    }
    
    private List<NextStop> addPlanDeliveryPack(List<NextStop> stops, int idDrive, int city, BigDecimal cap){
        try {
            String sql = "select XPos, YPos from Address where IDAddr = ?";
            Connection con = DB.getInstance().getConnection();
            
            for(NextStop ns: stops){
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, ns.addrTo);
                ResultSet rs = ps.executeQuery(); 
                if(rs.next() == false) return null;
                ns.xTo = rs.getInt("XPos");
                ns.yTo = rs.getInt("YPos");
                ns.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                ps.close();
            }
            
            int currCity = city;
            List<NextStop> newstops = new ArrayList<NextStop>();
            
            while(stops.size() >= 0){      
                Collections.sort(stops, (o1, o2)->o1.distance.compareTo(o2.distance));
                
                if(!stops.isEmpty() && currCity == getAddressCity(stops.get(0).addrTo)) {
                    newstops.add(stops.get(0));
                    addNextStop(stops.get(0).idPack, idDrive, 1);
                    currLastLoc = stops.get(0).addrTo;
                    XcurrLastLoc = stops.get(0).xTo;
                    YcurrLastLoc = stops.get(0).yTo;
                    cap = cap.add(stops.get(0).packWight);
                    
                    stops.remove(0);

                    for(NextStop ns: stops){
                        ns.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                    }
                }
                
                else if(currCity != city || (stops.size() == 1 && currCity != city)){
                    
                    int idStock = getStockFromCity(currCity);
                    List<NextStop> fetchStops = getAllUndelPackFromCityNOTinStock(currCity);
                    List<NextStop> packsInStock = getAllPackFromStock(idStock); 
                    
                    for(NextStop ns: fetchStops){
                        if (cap.compareTo(ns.packWight) < 0) {break;}
                        cap = cap.subtract(ns.packWight);
                        currLastLoc = ns.idLoc;
                        XcurrLastLoc = ns.x;
                        YcurrLastLoc = ns.y;
                        newstops.add(ns);
                        addNextStop(ns.idPack, idDrive, 0);
                        
                        for(NextStop nss: stops){
                            nss.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                        }
                    }
                    for(NextStop ns: packsInStock){
                        if (cap.compareTo(ns.packWight) < 0) {break;}
                        cap = cap.subtract(ns.packWight);
                        currLastLoc = ns.idLoc;
                        XcurrLastLoc = ns.x;
                        YcurrLastLoc = ns.y;
                        newstops.add(ns);
                        addNextStop(ns.idPack, idDrive, 2);

                        for(NextStop nss: stops){
                            nss.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                        }
                    }
                    if(stops.isEmpty()) break;
                    currCity = getAddressCity(stops.get(0).addrTo);
                }
                
                else if(!stops.isEmpty()){
                    currCity = getAddressCity(stops.get(0).addrTo);
                }
                else if(stops.isEmpty()) break;  
            }
            return newstops;
            
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private List<NextStop> addPlanDeliveryPackNew(List<NextStop> stops, int idDrive, int city, BigDecimal cap){
        try {
            String sql = "select XPos, YPos from Address where IDAddr = ?";
            Connection con = DB.getInstance().getConnection();
            
            for(NextStop ns: stops){
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, ns.addrTo);
                ResultSet rs = ps.executeQuery(); 
                if(rs.next() == false) return null;
                ns.xTo = rs.getInt("XPos");
                ns.yTo = rs.getInt("YPos");
                ns.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                ps.close();
            }
            
            int currCity = city;
            List<NextStop> newstops = new ArrayList<NextStop>();
            
            while(stops.size() >= 0){      
                Collections.sort(stops, (o1, o2)->o1.distance.compareTo(o2.distance));
                
                if(!stops.isEmpty() && currCity == getAddressCity(stops.get(0).addrTo)) {
                    newstops.add(stops.get(0));
                    addNextStop(stops.get(0).idPack, idDrive, 1);
                    currLastLoc = stops.get(0).addrTo;
                    XcurrLastLoc = stops.get(0).xTo;
                    YcurrLastLoc = stops.get(0).yTo;
                    cap = cap.add(stops.get(0).packWight);
                    
                    stops.remove(0);

                    for(NextStop ns: stops){
                        ns.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                    }
                    
                    if(currCity != city){
                    /**/    int idStock = getStockFromCity(currCity);
                        List<NextStop> fetchStops = getAllUndelPackFromCityNOTinStock(currCity);
                    /**/    List<NextStop> packsInStock = getAllPackFromStock(idStock); 

                        for(NextStop ns: fetchStops){
                            if (cap.compareTo(ns.packWight) < 0) {break;}
                            cap = cap.subtract(ns.packWight);
                            currLastLoc = ns.idLoc;
                            XcurrLastLoc = ns.x;
                            YcurrLastLoc = ns.y;
                            newstops.add(ns);
                            addNextStop(ns.idPack, idDrive, 0);

                            for(NextStop nss: stops){
                                nss.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                            }
                        }
                    /**/    for(NextStop ns: packsInStock){
                    /**/        if (cap.compareTo(ns.packWight) < 0) {break;}
                    /**/        cap = cap.subtract(ns.packWight);
                    /**/        currLastLoc = ns.idLoc;
                    /**/        XcurrLastLoc = ns.x;
                    /**/        YcurrLastLoc = ns.y;
                    /**/        newstops.add(ns);
                    /**/        addNextStop(ns.idPack, idDrive, 2);
                    /**/
                    /**/        for(NextStop nss: stops){
                    /**/            nss.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                    /**/        }
                    /**/   }
                        
                    }
                    
                }
                
                else if(currCity != city || (stops.size() == 1 && currCity != city)){
                    
                    int idStock = getStockFromCity(currCity);
                    List<NextStop> fetchStops = getAllUndelPackFromCityNOTinStock(currCity);
                    List<NextStop> packsInStock = getAllPackFromStock(idStock); 
                    
                    for(NextStop ns: fetchStops){
                        if (cap.compareTo(ns.packWight) < 0) {break;}
                        cap = cap.subtract(ns.packWight);
                        currLastLoc = ns.idLoc;
                        XcurrLastLoc = ns.x;
                        YcurrLastLoc = ns.y;
                        newstops.add(ns);
                        addNextStop(ns.idPack, idDrive, 0);
                        
                        for(NextStop nss: stops){
                            nss.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                        }
                    }
                    for(NextStop ns: packsInStock){
                        if (cap.compareTo(ns.packWight) < 0) {break;}
                        cap = cap.subtract(ns.packWight);
                        currLastLoc = ns.idLoc;
                        XcurrLastLoc = ns.x;
                        YcurrLastLoc = ns.y;
                        newstops.add(ns);
                        addNextStop(ns.idPack, idDrive, 2);

                        for(NextStop nss: stops){
                            nss.distance = StudentMain.distance(XcurrLastLoc, YcurrLastLoc, ns.xTo, ns.yTo);
                        }
                    }
                    if(stops.isEmpty()) break;
                    currCity = getAddressCity(stops.get(0).addrTo);
                }
                else if(!stops.isEmpty()){
                    currCity = getAddressCity(stops.get(0).addrTo);
                }
                else if(stops.isEmpty()) break;  
            }
            return newstops;
            
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    @Override
    @SuppressWarnings("UnusedAssignment")
    public boolean planingDrive(String courierUsername) {
        
        allStops = new ArrayList<>();
        distance = 0.0;
        currLastLoc = 0; 
        XcurrLastLoc = 0;
        YcurrLastLoc = 0;
        
        List<NextStop> fetchStops = new ArrayList<>();
        List<NextStop> packsInStock = new ArrayList<>();
        
        int idCourier = getCourierId(courierUsername);          if (idCourier == -1) { return false;}
        int idCity = getCourierCity(idCourier);                 if (idCity == -1) {return false;}
        int idStock = getStockFromCity(idCity);                 if (idStock == -1) {return false;}
        int idVehicle = getVehicleFromStock(idStock);           if (idVehicle == -1) {return false;}
        BigDecimal vehCapacity = getVehicleCapacity(idVehicle); if (vehCapacity == BigDecimal.valueOf(-1)) {return false;}
        updateCourier(idCourier, idVehicle);
        updateVehicle(idVehicle);
        int idDrive = addDrive(idCourier, idVehicle);           if (idDrive == -1) {return false;}
        addInAllDrivings(idCourier, idVehicle);
        
        fetchStops = getAllUndelPackFromCityNOTinStock(idCity); 
        packsInStock = getAllPackFromStock(idStock); 
        
        vehCapacity = addPlanFetchPackFromCity(fetchStops, vehCapacity, idDrive, 0);
        
        if (vehCapacity.compareTo(BigDecimal.valueOf(0.0)) > 0){
            vehCapacity = addPlanFetchPackFromCity(packsInStock, vehCapacity, idDrive, 2);
        }
        
        List<NextStop> newstops = addPlanDeliveryPackNew(allStops, idDrive, idCity, vehCapacity);
        
        
        addLastNextStop(idDrive, 3);
        if(newstops == null || newstops.isEmpty()){
           nextStop(courierUsername);
        }
        
        return true;
    }
    
    
    /***************************************************/
    
    private void updatePackage(int idPack, int idVeh){
        try{
            String sql = "update Package\n" +
            "set DeliveryStatus = 2, IDVehicle = ? , inStock = 0 \n" +
            "where IDPackage = ? ";
            
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idVeh);
            ps.setInt(2, idPack);
            ps.executeUpdate();
                   
        }catch(SQLException e){}
    }
    
    private void deleteNextStop(int idNextStop){
        try{
            String sql = 
                "delete NextStop\n" +
                "where IDNextStop = ? ";
            
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idNextStop);
            ps.executeUpdate();
                   
        }catch(SQLException e){}
    }
    
    private void updateDrivePriceAndDistance(int idDrive, BigDecimal price, int idAddrNext, int xNext, int yNext){
        try{
            String getCoord = 
                    "select XPos, YPos, D.IDVehicle from Drive D join Vehicle V on D.IDVehicle = V.IDVehicle " +
                    " join Address A on V.CurrLocation = A.IDAddr \n" +
                    " where IDDrive = ? ";
            Connection con = DB.getInstance().getConnection();
            
            PreparedStatement ps1 = con.prepareStatement(getCoord, ResultSet.TYPE_SCROLL_SENSITIVE);
            ps1.setInt(1, idDrive);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            int xCurr = rs1.getInt("XPos");
            int yCurr = rs1.getInt("YPos");
            int idVeh = rs1.getInt("IDVehicle");
            ps1.close();
            
            updateDrive(idDrive, xCurr, yCurr, xNext, yNext, BigDecimal.valueOf(0)); //price);
            updateVeh(idAddrNext, idVeh, false);
            
        }catch(SQLException e){}
    }
    
    private void updateDriveDistance(int idDrive, BigDecimal price, int idAddrNext){
        try{
            String getCoord = 
                    "select XPos, YPos, D.IDVehicle from Drive D join Vehicle V on D.IDVehicle = V.IDVehicle " +
                    " join Address A on V.CurrLocation = A.IDAddr \n" +
                    " where IDDrive = ? ";
            Connection con = DB.getInstance().getConnection();
            
            PreparedStatement ps1 = con.prepareStatement(getCoord, ResultSet.TYPE_SCROLL_SENSITIVE);
            ps1.setInt(1, idDrive);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            int xCurr = rs1.getInt("XPos");
            int yCurr = rs1.getInt("YPos");
            int idVeh = rs1.getInt("IDVehicle");
            ps1.close();
            
            String getCoordTo = "select XPos, YPos from Address where IDAddr = ?";
            
            PreparedStatement ps2 = con.prepareStatement(getCoordTo, ResultSet.TYPE_SCROLL_SENSITIVE);
            ps2.setInt(1, idAddrNext);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            int xNext = rs2.getInt("XPos");
            int yNext = rs2.getInt("YPos");
            ps1.close();
            
            updateDrive(idDrive, xCurr, yCurr, xNext, yNext, price); // BigDecimal.valueOf(0));
            updateVeh(idAddrNext, idVeh, false);
            
        }catch(SQLException e){}
    }
    
    private void updateDrive(int idDrive, int xCurr, int yCurr, int xNext, int yNext, BigDecimal price){
        String sql = "select * from Drive where IDDrive = ?";
        Connection con = DB.getInstance().getConnection();
        
        try {
            PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1, idDrive);
            ResultSet rs = ps.executeQuery();
            rs.next();
            
            BigDecimal newD = rs.getBigDecimal("Distance").add(StudentMain.distance(xCurr, yCurr, xNext, yNext));
            rs.updateBigDecimal("Distance", newD);
            rs.updateBigDecimal("PackPrice", rs.getBigDecimal("PackPrice").add(price));
            rs.updateRow();
        }
        catch(SQLException e){}
    }
    
    private void updateVeh(int idAddrNext, int idVeh, boolean isFree){
        String updateVeh = 
                "update Vehicle \n" +
                "set CurrLocation = ?, isFree = ? \n" +
                "where IDVehicle = ? ";
        Connection con = DB.getInstance().getConnection();
        
        try {
            PreparedStatement ps2 = con.prepareStatement(updateVeh);
            ps2.setInt(1, idAddrNext);
            ps2.setBoolean(2, isFree);
            ps2.setInt(3, idVeh);
            ps2.executeUpdate();
        }
        catch(SQLException e){}
    }
    
    private void updatePackDel(int idPack){
        try {
            String sql = 
                    "update Package\n" +
                    "set DeliveryStatus = 3, CurrLocation = IDAddrTo, IDVehicle = null\n" +
                    "where IDPackage = ?";
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idPack);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateCourirDel(int idCourier){
        try {
            String sql = 
                    "update Courier\n" +
                    "set DeliveryNum = DeliveryNum + 1\n" +
                    "where IDUser = ?";
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCourier);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
                        
    private void updateCourierEndDrive(int idCourier, int idDrive){
        try {
            String sql = 
                "declare @packPrice int, @perKm int, @consum decimal(10,3), @distance decimal(10,3)\n" +
                "select @packPrice = PackPrice, @consum = ConsumPerKm, @perKm = FuelType, @distance = Distance\n" +
                "from Drive D join Vehicle V on D.IDVehicle = V.IDVehicle\n" +
                "where D.IDDrive = ?;\n" +
                "\n" +
                "set @perKm = (\n" +
                "	case\n" +
                "		when @perKm=0 then 15 \n" +
                "		when @perKm=1 then 32\n" +
                "		when @perKm=2 then 36\n" +
                "	end);\n" +
                "\n" +
                "update Courier\n" +
                "set Status = 0, Profit += (@packPrice - @perKm*@consum*@distance)\n" +
                "where IDUser = ?";
            
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idDrive);
            ps.setInt(2, idCourier);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(sa160287_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void deleteDrive(int idDrive){
        try{
            String sql = 
                    "delete Drive\n" +
                    "where IDDrive = ? ";
            
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idDrive);
            ps.executeUpdate();
                   
        }catch(SQLException e){}
    }
    
    private boolean parkVehicle(int idVeh) {
        String update = 
            "update Vehicle \n" +
            "set isFree = 1, CurrLocation = (select IDAddr from Stockroom S join Vehicle V on S.IDStock = V.IDStock where V.IDVehicle = ?) \n" +
            "where IDVehicle = ? ";
        
        String updatePackFromVeh = "update Package \n" +
            "set inStock = 1, IDVehicle = null, CurrLocation = (select IDAddr from Stockroom S join Vehicle V on S.IDStock = V.IDStock where V.IDVehicle = ?) \n" +
            "where IDPackage in\n" +
            "(\n" +
            "	select IDPackage\n" +
            "	from Vehicle V join Package P on V.IDVehicle = P.IDVehicle\n" +
            "	where V.IDVehicle = ? and inStock = 0 \n" +
            ")";
        
        try {
            Connection con = DB.getInstance().getConnection();
            
            PreparedStatement st = con.prepareStatement(updatePackFromVeh);
            st.setInt(1, idVeh);
            st.setInt(2, idVeh);
            st.executeUpdate();
            st.close();
            
            PreparedStatement stmt = con.prepareStatement(update);
            
            stmt.setInt(1, idVeh);
            stmt.setInt(2, idVeh);
            return stmt.executeUpdate()==1;
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    private void updateDriveBackToStock(int idVeh, int idDrive){
        try{
            String getCoord = 
                "select * from Vehicle V join Stockroom S on V.IDStock = S.IDStock join Address A on S.IDAddr = A.IDAddr\n" +
                "where IDVehicle = ?";
            Connection con = DB.getInstance().getConnection();
            
            PreparedStatement ps1 = con.prepareStatement(getCoord);
            ps1.setInt(1, idVeh);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            int xCurr = rs1.getInt("XPos");
            int yCurr = rs1.getInt("YPos");
            int addr = rs1.getInt("IDAddr");
            
            ps1.close();
            
            String getCoordTo = "select * from Vehicle V join Address A on V.CurrLocation = A.IDAddr\n" +
                                "where IDVehicle = ?";
            
            PreparedStatement ps2 = con.prepareStatement(getCoordTo);
            ps2.setInt(1, idVeh);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            int xNext = rs2.getInt("XPos");
            int yNext = rs2.getInt("YPos");
            int addrTo = rs2.getInt("IDAddr");
            ps2.close();
            
            updateDrive(idDrive, xCurr, yCurr, xNext, yNext, BigDecimal.valueOf(0));
            updateVeh(addr, idVeh, true);
            
        }catch(SQLException e){}
    }
    
    
    /***************************************************/
    
                
    @Override
    public int nextStop(String username) {
        
        try{
            String sql = 
                    "select /*top 1*/ * from NextStop N join Drive D1 on N.IDDrive = D1.IDDrive\n" +
                    " left join Package P on N.IDPackage = P.IDPackage "+
                    " left join Address A on A.IDAddr = P.CurrLocation " +
                    "where N.IDDrive = (select IDDrive from Drive D join [User] C on D.IDUser = C.IDUser\n" +
                    "where C.Username = ?)";
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            int prevDel = 0;
            boolean check = false;
            
            int idPack, idVeh, idNextStop, isDelivery, idCourier=0, xNext, yNext, idDrive=0, idAddrNext;
            boolean packInStock;
            BigDecimal price;
            
            int addrTo;
            
            while(rs.next()){
                idPack = rs.getInt("IDPackage");
                idVeh = rs.getInt("IDVehicle");
                idNextStop = rs.getInt("IDNextStop");
                isDelivery = rs.getInt("isDelivery");
                packInStock = rs.getBoolean("inStock");
                idCourier = rs.getInt("IDUser");
                xNext = rs.getInt("XPos");
                yNext = rs.getInt("YPos");
                idDrive = rs.getInt("IDDrive");
                idAddrNext = rs.getInt("IDAddr");
                price = rs.getBigDecimal("Price");
                addrTo = rs.getInt("IDAddrTo");
                
                if (check && isDelivery != prevDel){ return -2; }
                switch (isDelivery) {
                    case 1:
                        
                        updatePackDel(idPack);
                        updateCourirDel(idCourier);
                        updateDriveDistance(idDrive, price, addrTo);
                        deleteNextStop(idNextStop);
                        return idPack;
                        
                    case 0:
                        
                        updatePackage(idPack, idVeh);
                        updateDrivePriceAndDistance(idDrive, price, idAddrNext, xNext, yNext);
                        deleteNextStop(idNextStop);
                        return -2;
                        
                    case 2:
                        
                        if (check && isDelivery != prevDel){ return -2; }
                        updatePackage(idPack, idVeh);
                        updateDrivePriceAndDistance(idDrive, price, idAddrNext, xNext, yNext);
                        deleteNextStop(idNextStop);
                        break;
                        
                    case 3:
                        
                        updateDriveBackToStock(idVeh, idDrive);
                        updateCourierEndDrive(idCourier, idDrive);
                        parkVehicle(idVeh);
                        deleteNextStop(idNextStop);
                        deleteDrive(idDrive);
                        return -1;
                        
                    default:
                        break;
                }
                
                prevDel = isDelivery;
                check = true;
            }
            return -1;
        }
        catch (SQLException ex) {
            Logger.getLogger(sa160287_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    
    /***************************************************/
    
    @Override
    public List<Integer> getPackagesInVehicle(String username) {
        List<Integer> allPack = new ArrayList<>();
        try{
            Connection con = DB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement
                ("select IDPackage from Package P join Vehicle V on V.IDVehicle = P.IDVehicle "+
                "join Drive D on D.IDVehicle = V.IDVehicle "+
                "join [User] U on U.IDUser = D.IDUser \n" +
                "where U.Username = ? ");
            ps.setString(1, username);
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
    
}
