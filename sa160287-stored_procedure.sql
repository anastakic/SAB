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

