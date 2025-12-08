USE
[master]
GO

IF  EXISTS (SELECT name FROM sys.databases WHERE name = N'DWPubsSales')
BEGIN
     -- Close connections to the DWPubsSales database 
    ALTER
DATABASE [DWPubsSales] SET  SINGLE_USER WITH ROLLBACK IMMEDIATE
    DROP
DATABASE [DWPubsSales]
END
GO

CREATE
DATABASE [DWPubsSales]
GO

USE [DWPubsSales]
GO

/****** Create the Dimension Tables ******/

CREATE TABLE [dbo].[DimStores]
(
    [
    StoreKey] [
    int]
    NOT
    NULL
    PRIMARY
    KEY
    Identity, [
    StoreId] [
    nchar]
(
    4
) NOT NULL,
    [StoreName] [nvarchar]
(
    50
) NOT NULL,
    --[StoreState] [nChar](2) NOT NULL -- Removed for later demo
    )
    GO

CREATE TABLE [dbo].[DimPublishers]
(
    [
    PublisherKey] [
    int]
    NOT
    NULL
    PRIMARY
    KEY
    Identity, [
    PublisherId] [
    nchar]
(
    4
) NOT NULL,
    [PublisherName] [nvarchar]
(
    50
) NOT NULL
    )
    GO

CREATE TABLE [dbo].[DimAuthors]
(
    [
    AuthorKey] [
    int]
    NOT
    NULL
    PRIMARY
    KEY
    Identity, [
    AuthorId] [
    nchar]
(
    11
) NOT NULL,
    [AuthorName] [nvarchar]
(
    100
) NOT NULL,
    [AuthorState] [nchar]
(
    2
) NOT NULL
    )
    GO

CREATE TABLE [dbo].[DimTitles]
(
    [
    TitleKey] [
    int]
    NOT
    NULL
    PRIMARY
    KEY
    Identity, [
    TitleId] [
    nvarchar]
(
    6
) NOT NULL,
    [TitleName] [nvarchar]
(
    100
) NOT NULL,
    [TitleType] [nvarchar]
(
    50
) NOT NULL,
    [PublisherKey] [int] NOT NULL,
    [TitlePrice] [decimal]
(
    18,
    4
) NOT NULL,
    [PublishedDateKey] [int] NOT NULL
    )
    GO

-- We should create a date dimension table in the database
CREATE TABLE dbo.DimDates
(
    [
    DateKey]
    int
    NOT
    NULL
    PRIMARY
    KEY
    IDENTITY, [
    Date]
    datetime
    NOT
    NULL, [
    DateName]
    nVarchar
(
    50
)
    , [Month] int NOT NULL
, [MonthName] nVarchar(50) NOT NULL
, [Quarter] int NOT NULL
, [QuarterName] nVarchar(50) NOT NULL
, [Year] int NOT NULL
, [YearName] nVarchar(50) NOT NULL
)

-- Since the date table has no associated source table we can fill the data
-- using a SQL script or wait until the ETL process. Either way, here is the 
-- code to use.

-- Create variables to hold the start and end date
DECLARE
@StartDate datetime = '01/01/1990'
DECLARE
@EndDate datetime = '01/01/1995'

-- Use a while loop to add dates to the table
DECLARE
@DateInProcess datetime
SET @DateInProcess = @StartDate

WHILE @DateInProcess <= @EndDate
BEGIN
 -- Add a row into the date dimension table for this date
INSERT INTO DimDates
([Date], [DateName], [ Month], [MonthName], [Quarter], [QuarterName], [ Year], [YearName])
VALUES (           @DateInProcess -- [Date]
           ,       DateName(weekday, @DateInProcess) -- [DateName]
           , Month(@DateInProcess) -- [Month]
           ,       DateName(month, @DateInProcess) -- [MonthName]
           ,       DateName(quarter, @DateInProcess) -- [Quarter]
           ,       'Q' + DateName(quarter, @DateInProcess) + ' - ' +
                   Cast(Year (@DateInProcess) as nVarchar(50)) -- [QuarterName]
           , Year( @DateInProcess)
           ,       Cast(Year (@DateInProcess) as nVarchar(50)) -- [Year]
       )
       -- Add a day and loop again
    SET @DateInProcess = DateAdd(d, 1, @DateInProcess)
END

-- Check the table SELECT Top 10 * FROM DimDates

/****** Create the Fact Tables ******/

CREATE TABLE [dbo].[FactTitlesAuthors]
(
    [
    TitleKey] [
    int]
    NOT
    NULL, [
    AuthorKey] [
    int]
    NOT
    NULL, [
    AuthorOrder] [
    int]
    NOT
    NULL,
    CONSTRAINT [
    PK_FactTitlesAuthors]
    PRIMARY
    KEY
    CLUSTERED
    (
    [
    TitleKey]
    ASC,
[
    AuthorKey]
    ASC
)
    )
    GO

CREATE TABLE [dbo].[FactSales]
(
    [
    OrderNumber] [
    nvarchar]
(
    50
) NOT NULL,
    [OrderDateKey] [int] NOT NULL,
    [TitleKey] [int] NOT NULL,
    [StoreKey] [int] NOT NULL,
    [SalesQuantity] [int] NOT NULL,
    CONSTRAINT [PK_FactSales] PRIMARY KEY CLUSTERED
(
    [
    OrderNumber]
    ASC, [
    OrderDateKey]
    ASC, [
    TitleKey]
    ASC,
[
    StoreKey]
    ASC
)
    )
    GO

/****** Add Foreign Keys ******/
Alter Table [dbo].[DimTitles] With Check Add Constraint [FK_DimTitles_DimPublishers]
    Foreign Key ([PublisherKey]) References [dbo].[DimPublishers] ([PublisherKey])

Alter Table [dbo].[FactTitlesAuthors] With Check Add Constraint [FK_FactTitlesAuthors_DimAuthors]
    Foreign Key ([AuthorKey]) References [dbo].[DimAuthors] ([AuthorKey])

Alter Table [dbo].[FactTitlesAuthors] With Check Add Constraint [FK_FactTitlesAuthors_DimTitles]
    Foreign Key ([TitleKey]) References [dbo].[DimTitles] ([TitleKey])
    Go
Alter Table [dbo].[FactSales] With Check Add Constraint [FK_FactSales_DimStores]
    Foreign Key ([StoreKey]) References [dbo].[DimStores] ([Storekey])
    Go
Alter Table [dbo].[FactSales] With Check Add Constraint [FK_FactSales_DimTitles]
    Foreign Key ([TitleKey]) References [dbo].[DimTitles] ([TitleKey])
    Go
Alter Table [dbo].[FactSales] With Check Add Constraint [FK_FactSales_DimDates]
    Foreign Key ([OrderDateKey]) References [dbo].[DimDates] ( [DateKey] )
    Go
Alter Table [dbo].[DimTitles] With Check Add Constraint [FK_DimTitles_DimDates]
    Foreign Key ([PublishedDateKey]) References [dbo].[DimDates] ( [DateKey] )


-- All done! -- 
Select [Message] = 'The DWPubsSales data warehouse is now created' 