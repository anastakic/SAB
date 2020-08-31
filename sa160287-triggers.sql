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
