CREATE TABLE menuItems (
    menuItemID int PRIMARY KEY,
    menuItemName text,
    menuItemPrice float,
    menuItemCalories text,
    menuItemCategory text,
    hasCaffeine boolean
);


CREATE TABLE Inventory (
    inventoryID int PRIMARY KEY,
    inventoryName text,
    inventoryQuantity int,
    inventoryReceivedDate date,
    inventoryExpirationDate date, 
    inventoryInStock boolean,
    inventorySupplier text
);


CREATE TABLE Employees (
    employeeID int PRIMARY KEY,
    employeeName text,
    employeeUsername text,
    employeeUserPassword text,
    isManager boolean,
    employeePayroll float,
    employeeSchedule text
);


CREATE TABLE Sales (
    orderID SERIAL PRIMARY KEY,
    orderNo int,
    saleDate date,
    saleTime time,
    employeeID int,
    salePrice float,
    isLarge boolean, 
    menuItemID int,
    FOREIGN KEY (employeeID) REFERENCES Employees(employeeId) 
);


-- junction table for menuItems and inventory
CREATE TABLE menuItems_Inventory (
    menuItemID int NOT NULL,
    inventoryID int NOT NULL, 
    measurement float NULL,
    FOREIGN KEY (menuItemID) REFERENCES menuItems(menuItemID),
    FOREIGN KEY (inventoryID) REFERENCES Inventory(inventoryID),
    PRIMARY KEY (menuItemID, inventoryID)
);


\copy MenuItems FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/menuItems.csv' DELIMITER ',' CSV HEADER;

\copy Inventory FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/inventory.csv' DELIMITER ',' CSV HEADER;

\copy Sales FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/salesHistory.csv' DELIMITER ',' CSV HEADER;

\copy Employees FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/employees.csv' DELIMITER ',' CSV HEADER;

\copy MenuItems_Inventory FROM '/Users/Eyad/Desktop/Classes/23/Fall_23/CSCE_331/project-2-csce331_900_00g/DB_creation/menuItems_inventory.csv' DELIMITER ',' CSV HEADER;

