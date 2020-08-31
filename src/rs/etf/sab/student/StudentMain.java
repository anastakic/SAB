package rs.etf.sab.student;

import java.math.BigDecimal;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class StudentMain {

    static BigDecimal distance(final int x1, final int y1, final int x2, final int y2) {
        return BigDecimal.valueOf(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }
    
    public static void main(String[] args) {
        AddressOperations addressOperations = new sa160287_AddressOperation(); // Change this to your implementation.
        CityOperations cityOperations =  new sa160287_CityOperations(); // Do it for all classes.
        CourierOperations courierOperations = new sa160287_CourierOperations(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new sa160287_CourierRequestOperation();
        DriveOperation driveOperation = new sa160287_DriveOperation();
        GeneralOperations generalOperations = new sa160287_GeneralOperations();
        PackageOperations packageOperations = new sa160287_PackageOperations();
        StockroomOperations stockroomOperations = new sa160287_StockroomOperations();
        UserOperations userOperations = new sa160287_UserOperations();
        VehicleOperations vehicleOperations = new sa160287_VehicleOperations();


        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

      
           TestRunner.runTests();
    }
}
