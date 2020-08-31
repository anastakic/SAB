DBCC CHECKIDENT ('[Offer]', RESEED, 1);
go
DBCC CHECKIDENT ('[Package]', RESEED, 1);
go
DBCC CHECKIDENT ('[User]', RESEED, 1);
go
DBCC CHECKIDENT ('[Vehicle]', RESEED, 1);
go
DBCC CHECKIDENT ('[Stockroom]', RESEED, 1);
go
DBCC CHECKIDENT ('[Address]', RESEED, 1);
go
DBCC CHECKIDENT ('[City]', RESEED, 1);
go

SET NOCOUNT ON

delete from Package
delete from NextStop
delete from Drive
delete from AllDrivings
delete from Courier
delete from CourierRequest
delete from Vehicle
delete from Stockroom
delete from [User]
delete from Address
delete from City
delete from Offer


declare @bg int, @ns int, @su int, @kg int;
declare @addrBg1 int, @addrBg2 int, @addrBg3 int, @addrBg4 int, @addrBg5 int; 
declare @addrNs1 int, @addrNs2 int, @addrNs3 int;
declare @addrSu1 int, @addrSu2 int, @addrSu3 int;
declare @addrKg1 int, @addrKg2 int, @addrKg3 int, @addrKg4 int;
declare @stockBg int, @stockSu int, @stockKg int, @stockNs int;
declare @user int, @kurirBg int, @kurirSu int, @kurirKg int, @kurirNs int;



insert into City(Name, PostalCode) 
values ('Beograd', '11111')
set @bg = SCOPE_IDENTITY();
insert into City(Name, PostalCode) 
values ('Novi Sad', '22222')
set @ns = SCOPE_IDENTITY();
insert into City(Name, PostalCode) 
values ('Subotica', '33333')
set @su = SCOPE_IDENTITY();
insert into City(Name, PostalCode) 
values ('Kragujevac', '44444')
set @kg = SCOPE_IDENTITY();

insert into Address (IDCity, Street, Num, XPos, YPos)
values (@bg, 'Beograd ulica', 1, 70, 120)
set @addrBg1 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@bg, 'Beograd ulica', 2, 80, 120)
set @addrBg2 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@bg, 'Beograd ulica', 3, 90, 130)
set @addrBg3 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@bg, 'Beograd ulica', 4, 90, 110)
set @addrBg4 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@bg, 'Beograd ulica', 5, 100, 120)
set @addrBg5 = SCOPE_IDENTITY();

insert into Address (IDCity, Street, Num, XPos, YPos)
values (@ns, 'Novi Sad ulica', 1, 20, 160)
set @addrNs1 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@ns, 'Novi Sad ulica', 2, 40, 160)
set @addrNs2 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@ns, 'Novi Sad ulica', 3, 20, 150)
set @addrNs3 = SCOPE_IDENTITY();

insert into Address (IDCity, Street, Num, XPos, YPos)
values (@su, 'Subotica ulica', 1, 60, 220)
set @addrSu1 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@su, 'Subotica ulica', 2, 70, 230)
set @addrSu2 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@su, 'Subotica ulica', 3, 70, 210)
set @addrSu3 = SCOPE_IDENTITY();

insert into Address (IDCity, Street, Num, XPos, YPos)
values (@kg, 'Kragujevac ulica', 1, 100, 40)
set @addrKg1 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@kg, 'Kragujevac ulica', 2, 110, 40)
set @addrKg2 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@kg, 'Kragujevac ulica', 3, 110, 30)
set @addrKg3 = SCOPE_IDENTITY();
insert into Address (IDCity, Street, Num, XPos, YPos)
values (@bg, 'Kragujevac ulica', 4, 120, 30)
set @addrKg4 = SCOPE_IDENTITY();

insert into Stockroom(IDAddr) values (@addrBg1)
set @stockBg = SCOPE_IDENTITY();
insert into Stockroom(IDAddr) values (@addrNs1)
set @stockNs = SCOPE_IDENTITY();
insert into Stockroom(IDAddr) values (@addrSu1)
set @stockSu = SCOPE_IDENTITY();
insert into Stockroom(IDAddr) values (@addrKg1)
set @stockKg = SCOPE_IDENTITY();

insert into Vehicle(IDStock, CurrLocation, Capacity, ConsumPerKm, FuelType, isFree, RegPlate)
values (@stockBg, @stockBg, 300.0, 6.3, 2, 1, 'BG1675DA');
insert into Vehicle(IDStock, CurrLocation, Capacity, ConsumPerKm, FuelType, isFree, RegPlate)
values (@stockNs, @stockNs, 750.0, 6.3, 2, 1, 'NS1675DA');
insert into Vehicle(IDStock, CurrLocation, Capacity, ConsumPerKm, FuelType, isFree, RegPlate)
values (@stockSu, @stockSu, 300.0, 7.3, 1, 1, 'SU1675DA');
insert into Vehicle(IDStock, CurrLocation, Capacity, ConsumPerKm, FuelType, isFree, RegPlate)
values (@stockKg, @stockKg, 500.0, 7.3, 1, 1, 'KG1675DA');


insert into [User](FirstName, LastName, Password, Username, IsAdmin, PackNum, IDAddr)
values ('Svetislav', 'Kisprdilov', 'Test_123', 'crno.dete', 0, 0, @addrBg1);
set @user = SCOPE_IDENTITY();

insert into [User](FirstName, LastName, Password, Username, IsAdmin, PackNum, IDAddr)
values ('Pera', 'Peric', 'Postar_73', 'kurirBG', 0, 0, @addrBg2);
set @kurirBg = SCOPE_IDENTITY();
insert into [User](FirstName, LastName, Password, Username, IsAdmin, PackNum, IDAddr)
values ('Pera', 'Peric', 'Postar_73', 'kurirNS', 0, 0, @addrNs2);
set @kurirNs = SCOPE_IDENTITY();
insert into [User](FirstName, LastName, Password, Username, IsAdmin, PackNum, IDAddr)
values ('Pera', 'Peric', 'Postar_73', 'kurirSU', 0, 0, @addrSu2);
set @kurirSu = SCOPE_IDENTITY();
insert into [User](FirstName, LastName, Password, Username, IsAdmin, PackNum, IDAddr)
values ('Pera', 'Peric', 'Postar_73', 'kurirKG', 0, 0, @addrKg2);
set @kurirKg = SCOPE_IDENTITY();

insert into Courier(IDUser, DriverLicense, Status, Profit, DeliveryNum)
values (@kurirBg, '654321', 0, 0, 0);
insert into Courier(IDUser, DriverLicense, Status, Profit, DeliveryNum)
values (@kurirNs, '123456', 0, 0, 0);
insert into Courier(IDUser, DriverLicense, Status, Profit, DeliveryNum)
values (@kurirSu, '566552', 0, 0, 0);
insert into Courier(IDUser, DriverLicense, Status, Profit, DeliveryNum)
values (@kurirKg, '549847', 0, 0, 0);

insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrBg5, @addrBg2, @addrBg5, 1, @user, 0, 0, 30.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrBg2, @addrNs2, @addrBg2, 1, @user, 3, 0, 30.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrBg3, @addrNs3, @addrBg3, 1, @user, 1, 0, 30.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrBg4, @addrSu3, @addrBg4, 1, @user, 2, 0, 30.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrBg5, @addrSu2, @addrBg5, 1, @user, 1, 0, 30.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrBg4, @addrKg2, @addrBg4, 1, @user, 2, 0, 200.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrNs3, @addrKg3, @addrNs3, 1, @user, 1, 0, 60.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrNs2, @addrSu2, @addrNs2, 1, @user, 1, 0, 245.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrNs3, @addrBg5, @addrNs3, 1, @user, 0, 0, 5.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01';

insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrSu2, @addrKg2, @addrSu2, 1, @user, 1, 0, 5.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01'
insert into Package(IDAddrFrom, IDAddrTo, CurrLocation, DeliveryStatus, IDUser, PackType, inStock,  Weight, TimeCreated, TimeAccepted)
values (@addrSu3, @addrBg2, @addrSu3, 1, @user, 1, 0, 5.0, GETDATE(), GETDATE())
waitfor delay '00:00:00.01'



select * from Package
select * from NextStop
select * from Drive
select * from AllDrivings
select * from Courier
select * from CourierRequest
select * from Vehicle
select * from Stockroom
select * from [User]
select * from Address
select * from City
select * from Offer
