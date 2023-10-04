-- CREATE TABLE MenuItems (
--     id int PRIMARY KEY,
--     _name text,
--     price numeric,
--     calories text,
--     category text,
--     hasCaffine boolean
-- );


-- CREATE TABLE Inventory (
--     id int,
--     _name text,
--     supplier text,
--     quantity int,
--     recieved text,
--     expiration text, 
--     inStock boolean,
--     PRIMARY KEY (id)
-- );


-- CREATE TABLE Employees (
--     id int PRIMARY KEY,
--     _name text,
--     isManager boolean,
--     payroll float,
--     schedule text
-- );

-- CREATE TABLE Sales (
--     orderID SERIAL PRIMARY KEY,
--     orderNo int,
--     _date Text,
--     _time text,
--     price float,
--     isLarge boolean, 
--     menuItemsID int,
--     employeeID int,
--     FOREIGN KEY (employeeID) REFERENCES Employees(id)
-- );

-- junction table for menuItems and inventory
CREATE TABLE MenuItems_Inventory (
    menuItemsID int NOT NULL,
    inventoryListID text NOT NULL,  -- Assuming inventoryListID is an integer; change the type if needed
    PRIMARY KEY (menuItemsID),
    FOREIGN KEY (menuItemsID) REFERENCES MenuItems(id),
    -- /FOREIGN KEY (inventoryListID) REFERENCES Inventory(id),
    measurementsList text NOT NULL
);

\copy MenuItems FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/menuItems.csv' DELIMITER ',' CSV HEADER;

\copy Inventory FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/inventory.csv' DELIMITER ',' CSV HEADER;

\copy Sales FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/sales_history.csv' DELIMITER ',' CSV HEADER;

\copy Employees FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/employeeMap.csv' DELIMITER ',' CSV HEADER;

\copy MenuItems_Inventory FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/menuItems_inventory.csv' DELIMITER ',' CSV HEADER;





