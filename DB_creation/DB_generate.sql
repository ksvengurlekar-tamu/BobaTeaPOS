CREATE TABLE MenuItems (
    PRIMARY KEY id int,
    _name text,
    price float,
    calories text,
    category text,
    hasCaffine boolean,
    --FOREIGN KEY inventoryID int
);


CREATE TABLE Ingredients (
    PRIMARY KEY id int,
    _name text,
    amount float
);


CREATE TABLE Inventory (
    PRIMARY KEY id int,
    _name text,
    supplier text,
    quantity, int,
    recieved date,
    expiration date, 
    inStock, boolean,
    --FOREIGN KEY menuItemID int
);


CREATE TABLE Sales (
    -- issue with orderNo with the csv input: can only have 1 PK
    -- we need a way to consolidate these into a single entry
    -- if we make a table entry with a unqiue primary key we have to delete the table to change it
    
    --orderNo int PRIMARY KEY,
    
    _date date,
    _time text,
    price float,
    isLarge boolean, 
    menuItemsID int,
    --FOREIGN KEY employeeID int
);


CREATE TABLE Employees (
    PRIMARY KEY id int,
    _name text,
    isManager boolean,
    payroll float,
    schedule text
);


-- junction table for menuItems and ingredients
CREATE TABLE MenuItems_Ingredients (
    menuItemsID int NOT NULL,
    ingredientsID int NOT NULL,
    PRIMARY KEY (menuItemID, ingredientsID),
    --FOREIGN KEY (menuItemID) REFERENCES MenuItems(menuItemID)
    --FOREIGN KEY (ingredientsID) REFERENCES Ingredients(ingredientsID)
);


-- 15 SQL Queries