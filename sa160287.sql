use DeliveryService;
go

DROP TABLE IF EXISTS [CourierRequest]
go

DROP TABLE IF EXISTS [Offer]
go

DROP TABLE IF EXISTS [NextStop]
go

DROP TABLE IF EXISTS [Package]
go

DROP TABLE IF EXISTS [Drive]
go

DROP TABLE IF EXISTS [AllDrivings]
go

DROP TABLE IF EXISTS [Courier]
go

DROP TABLE IF EXISTS [User]
go

DROP TABLE IF EXISTS [Vehicle]
go

DROP TABLE IF EXISTS[Stockroom]
go

DROP TABLE IF EXISTS [Address]
go

DROP TABLE IF EXISTS [City]
go

CREATE TABLE [Address]
( 
	[Street]             varchar(100)  NOT NULL ,
	[Num]                integer  NOT NULL ,
	[XPos]               integer  NULL ,
	[YPos]               integer  NULL ,
	[IDCity]             integer  NOT NULL ,
	[IDAddr]             integer  IDENTITY  NOT NULL
)
go

CREATE TABLE [AllDrivings]
( 
	[IDAll]              integer  IDENTITY  NOT NULL ,
	[IDVehicle]          integer  NULL ,
	[IDUser]             integer  NULL 
)
go

CREATE TABLE [City]
( 
	[IDCity]             integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NOT NULL ,
	[PostalCode]         varchar(100)  UNIQUE  NOT NULL 
)
go

CREATE TABLE [Courier]
( 
	[Status]             integer  NOT NULL 
	CONSTRAINT [CourierStatusValues_1010847340]
		CHECK  ( [Status]=0 OR [Status]=1 ),
	[DeliveryNum]        integer  NULL ,
	[IDUser]             integer  NOT NULL ,
	[Profit]             decimal(10,3)  NULL ,
	[IDVehicle]          integer  NULL ,
	[DriverLicense]      varchar(100)  UNIQUE  NOT NULL 
)
go

CREATE TABLE [CourierRequest]
( 
	[IDUser]             integer  NOT NULL ,
	[DriverLicense]      varchar(100)  UNIQUE  NULL 
)
go

CREATE TABLE [Drive]
( 
	[IDUser]             integer  UNIQUE  NOT NULL ,
	[Distance]           decimal(10,3)  NULL ,
	[IDVehicle]          integer  UNIQUE  NOT NULL ,
	[IDDrive]            integer  IDENTITY  NOT NULL ,
	[PackPrice]          decimal(10,3)  NULL 
)
go

CREATE TABLE [NextStop]
( 
	[IDNextStop]         integer  IDENTITY  NOT NULL ,
	[IDPackage]          integer  NULL ,
	[isDelivery]         integer  NOT NULL 
	CONSTRAINT [PackageNextStopStatus_1039399403]
		CHECK  ( [isDelivery]=0 OR [isDelivery]=1 OR [isDelivery]=2 OR [isDelivery]=3),
	[IDDrive]            integer  NOT NULL 
)
go

CREATE TABLE [Offer]
( 
	[Price]              decimal(10,3)  NULL ,
	[IDPackage]          integer  NOT NULL ,
	[IDOffer]            integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [Package]
( 
	[IDPackage]          integer  IDENTITY  NOT NULL ,
	[IDAddrFrom]         integer  NOT NULL ,
	[IDAddrTo]           integer  NOT NULL ,
	[IDUser]             integer  NOT NULL ,
	[PackType]           integer  NOT NULL 
	CONSTRAINT [PackageTypeValues_843060496]
		CHECK  ( [PackType]=0 OR [PackType]=1 OR [PackType]=2 OR [PackType]=3 ),
	[Weight]             decimal(10,3)  NULL ,
	[DeliveryStatus]     integer  NULL 
	CONSTRAINT [PackageDeliveryStatusValues_1075649309]
		CHECK  ( [DeliveryStatus]=0 OR [DeliveryStatus]=1 OR [DeliveryStatus]=2 OR [DeliveryStatus]=3 OR [DeliveryStatus]=4 ),
	[TimeAccepted]       datetime  NULL ,
	[Price]              decimal(10,3)  NULL ,
	[TimeCreated]        datetime  NULL ,
	[IDVehicle]          integer  NULL ,
	[CurrLocation]       integer  NOT NULL , 
	[inStock]            bit  NOT NULL 
)
go

CREATE TABLE [Stockroom]
( 
	[IDStock]            integer  IDENTITY  NOT NULL ,
	[IDAddr]             integer  UNIQUE  NOT NULL 
)
go

CREATE TABLE [User]
( 
	[Username]           varchar(100)  UNIQUE  NOT NULL ,
	[LastName]           varchar(100)  NOT NULL ,
	[FirstName]          varchar(100)  NOT NULL ,
	[IsAdmin]            bit  NULL ,
	[IDUser]             integer  IDENTITY  NOT NULL ,
	[Password]           varchar(100)  NOT NULL ,
	[IDAddr]             integer  NOT NULL ,
	[PackNum]            integer  NULL 
	CONSTRAINT [UserPackageNumber_1646606388]
		CHECK  ( PackNum >= 0 )
)
go

CREATE TABLE [Vehicle]
( 
	[FuelType]           integer  NOT NULL 
	CONSTRAINT [VehicleFuelTypeValues_1128505471]
		CHECK  ( [FuelType]=0 OR [FuelType]=1 OR [FuelType]=2 ),
	[ConsumPerKm]        decimal(10,3)  NOT NULL ,
	[RegPlate]           varchar(100)  UNIQUE  NOT NULL ,
	[IDVehicle]          integer  IDENTITY  NOT NULL ,
	[Capacity]           decimal(10,3)  NOT NULL ,
	[isFree]             bit  NULL ,
	[IDStock]            integer  NULL  ,
	[CurrLocation]       integer  NULL 
)
go



ALTER TABLE [Address]
	ADD CONSTRAINT [XPKDistrict] PRIMARY KEY  CLUSTERED ([IDAddr] ASC)
go

--javni test AddressOperationsTest.deleteAddresses_multiple_existing()
--dodaju se dve iste adrese 
--ALTER TABLE [Address]
--	ADD CONSTRAINT [UniqueAddress] UNIQUE  NONCLUSTERED ([IDCity], [Street], [Num] ASC)
--go

ALTER TABLE [AllDrivings]
	ADD CONSTRAINT [XPKAllDrivings] PRIMARY KEY  CLUSTERED ([IDAll] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IDCity] ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XPKCourier] PRIMARY KEY  CLUSTERED ([IDUser] ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XPKCourierRequest] PRIMARY KEY  CLUSTERED ([IDUser] ASC)
go

ALTER TABLE [Drive]
	ADD CONSTRAINT [XPKDrive] PRIMARY KEY  CLUSTERED ([IDDrive] ASC)
go

ALTER TABLE [NextStop]
	ADD CONSTRAINT [XPKNextStop] PRIMARY KEY  CLUSTERED ([IDNextStop] ASC)
go

ALTER TABLE [Offer]
	ADD CONSTRAINT [XPKTransportOffer] PRIMARY KEY  CLUSTERED ([IDOffer] ASC)
go

ALTER TABLE [Package]
	ADD CONSTRAINT [XPKPackage] PRIMARY KEY  CLUSTERED ([IDPackage] ASC)
go

ALTER TABLE [Stockroom]
	ADD CONSTRAINT [XPKStockroom] PRIMARY KEY  CLUSTERED ([IDStock] ASC)
go

ALTER TABLE [User]
	ADD CONSTRAINT [XPKUser] PRIMARY KEY  CLUSTERED ([IDUser] ASC)
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [XPKVehicle] PRIMARY KEY  CLUSTERED ([IDVehicle] ASC)
go


ALTER TABLE [Address]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IDCity]) REFERENCES [City]([IDCity])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [AllDrivings]
	ADD CONSTRAINT [R_34] FOREIGN KEY ([IDVehicle]) REFERENCES [Vehicle]([IDVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [AllDrivings]
	ADD CONSTRAINT [R_35] FOREIGN KEY ([IDUser]) REFERENCES [Courier]([IDUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Courier]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IDUser]) REFERENCES [User]([IDUser])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IDVehicle]) REFERENCES [Vehicle]([IDVehicle])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IDUser]) REFERENCES [User]([IDUser])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Drive]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([IDUser]) REFERENCES [Courier]([IDUser])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Drive]
	ADD CONSTRAINT [R_37] FOREIGN KEY ([IDVehicle]) REFERENCES [Vehicle]([IDVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [NextStop]
	ADD CONSTRAINT [R_42] FOREIGN KEY ([IDPackage]) REFERENCES [Package]([IDPackage])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [NextStop]
	ADD CONSTRAINT [R_44] FOREIGN KEY ([IDDrive]) REFERENCES [Drive]([IDDrive])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Offer]
	ADD CONSTRAINT [R_38] FOREIGN KEY ([IDPackage]) REFERENCES [Package]([IDPackage])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Package]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IDAddrFrom]) REFERENCES [Address]([IDAddr])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IDAddrTo]) REFERENCES [Address]([IDAddr])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IDUser]) REFERENCES [User]([IDUser])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_33] FOREIGN KEY ([IDVehicle]) REFERENCES [Vehicle]([IDVehicle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_41] FOREIGN KEY ([CurrLocation]) REFERENCES [Address]([IDAddr])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Stockroom]
	ADD CONSTRAINT [R_25] FOREIGN KEY ([IDAddr]) REFERENCES [Address]([IDAddr])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [User]
	ADD CONSTRAINT [R_27] FOREIGN KEY ([IDAddr]) REFERENCES [Address]([IDAddr])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Vehicle]
	ADD CONSTRAINT [R_29] FOREIGN KEY ([IDStock]) REFERENCES [Stockroom]([IDStock])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [R_45] FOREIGN KEY ([CurrLocation]) REFERENCES [Address]([IDAddr])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

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

use DeliveryService;
go

if OBJECT_ID('spAcceptCourierRequest', 'P') IS NOT NULL
	drop procedure [spAcceptCourierRequest];
go
create procedure [spAcceptCourierRequest]
	@username varchar(100)
as
begin
	declare @iduser int, @driver_license varchar(100);
	select @iduser = IDUser from [User] where Username = @username;
	select @driver_license = DriverLicense from CourierRequest where IDUser = @iduser;

	insert into Courier(IDUser, Status, Profit, DeliveryNum, DriverLicense)
	values (@iduser, 0, 0, 0, @driver_license);

	delete from CourierRequest
	where IDUser = @iduser;
end;
go


if OBJECT_ID('spDenyCourierRequest', 'P') IS NOT NULL
	drop procedure [spDenyCourierRequest];
go
create procedure [spDenyCourierRequest]
	@username varchar(100)
as
begin
	declare @iduser int;
	select @iduser = IDUser from [User] where Username = @username;
	
	delete from CourierRequest
	where IDUser = @iduser;
end;
go

use DeliveryService;
go

if OBJECT_ID('spPackagePrice', 'P') IS NOT NULL
	drop procedure [spPackagePrice];
go
create procedure [spPackagePrice]
	@idpackage int,
	@price int output
as
begin
	declare @type int, @weight decimal(10,3);
	declare @addrFrom int, @addrTo int;
	declare @x1 int, @x2 int, @y1 int, @y2 int;

	select @type = PackType, @weight = Weight, @addrFrom = IDAddrFrom, @addrTo = IDAddrTo
	from Package where IDPackage = @idpackage;

	select @x1 = XPos, @y1 = YPos
	from Address where IDAddr = @addrFrom
	
	select @x2 = XPos, @y2 = YPos
	from Address where IDAddr = @addrTo

	set @price = (
	case
		when @type=0 then 115 
		when @type=1 then 175 + @weight*100
		when @type=2 then 250 + @weight*100
		else 350 + @weight*500
	end)

	set @price = @price * SQRT((@x1-@x2)*(@x1-@x2) + (@y1-@y2)*(@y1-@y2))

end;
go

if OBJECT_ID('spAddPackageRequest', 'P') IS NOT NULL
	drop procedure [spAddPackageRequest];
go
create procedure [spAddPackageRequest]
	@addressFrom int,
	@addressTo int,
	@userName varchar(100),
	@type int,
	@weight decimal(10,3),
	@idpack int output
as
begin
	declare @iduser int;
	set @idpack = -1;

	select @iduser = IDUser
	from [User] where Username = @userName;
	insert into Package(IDAddrFrom, IDAddrTo, IDUser, PackType, Weight, TimeCreated)
	--output inserted.IDPackage
	values (@addressFrom, @addressTo, @iduser, @type, @weight, GETDATE());

	set @idpack = SCOPE_IDENTITY();

end;
go

use DeliveryService;
go

if OBJECT_ID('TR_TransportOffer_Delete', 'TR') IS NOT NULL
	drop trigger [TR_TransportOffer_Delete];
go
create trigger [TR_TransportOffer_Delete]
on Package
for delete
as 
begin
	declare @idpackage int

	select @idpackage = IDPackage 
	from deleted

	delete from Offer
	where IDPackage = @idpackage
end;
go 


if OBJECT_ID('TR_TransportOffer_Insert', 'TR') IS NOT NULL
	drop trigger [TR_TransportOffer_Insert];
go
create trigger [TR_TransportOffer_Insert]
on Package
for insert
as 
begin
	declare @idpackage int, @price decimal(10,3);

	select @idpackage = IDPackage 
	from inserted

	exec spPackagePrice @idpackage = @idpackage, @price = @price output


	insert into Offer(IDPackage, Price)
	values (@idpackage, @price)

	
	update Package
	set Price = @price
	where IDPackage = @idpackage;
end;
go


if OBJECT_ID('TR_TransportOffer_Update', 'TR') IS NOT NULL
	drop trigger [TR_TransportOffer_Update];
go
create trigger [TR_TransportOffer_Update]
on Package
for update
as 
begin
	declare @idpackage int, @price decimal(10,3);
	declare @kursor cursor;

	set @kursor = cursor for
	select IDPackage 
	from inserted

	open @kursor

	fetch from @kursor
	into @idpackage

	while @@FETCH_STATUS = 0
	begin
		exec spPackagePrice @idpackage = @idpackage, @price = @price output
		
		update Package
		set Price = @price
		where IDPackage = @idpackage;

		update Offer
		set Price = @price
		where IDPackage = @idpackage;

		fetch from @kursor
		into @idpackage
	end

	close @kursor
	deallocate @kursor
	
end;
go

/*
use [master]
go
create login [adminLogin] with password = N'123' must_change, default_database = [master], check_expiration = ON, check_policy = ON
go

use [DeliveryService]
go
create role [adminRole]
go

create user [adminUser] for login [adminLogin] with default_schema = [dbo]
go 

alter role [adminRole] add member [adminUser]
go 

create schema [adminSchema] 
go

alter schema [adminSchema] transfer object::dbo.spAcceptCourierRequest
go

--dozvola svega
grant control on schema::[adminSchema] to adminRole
go
--revoke povlaci dozvolu
deny create table to [adminRole]
go 
deny delete on schema::[adminSchema] to [adminRole]
go
*/

--index nonclustered sort po street, num
use [DeliveryService]
go

set ansi_padding on
go
create nonclustered index [NonClusteredIndex-20200608-102641] on [dbo].[Address]
(
	[Street] asc,
	[Num] asc
)with (pad_index = OFF, statistics_norecompute = OFF, sort_in_tempdb = OFF, drop_existing = OFF, online = OFF, allow_row_locks = ON, allow_page_locks = ON)
go





