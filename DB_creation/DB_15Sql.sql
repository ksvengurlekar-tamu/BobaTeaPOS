-- 1) 52 weeks of sales history
SELECT 
    EXTRACT(WEEK FROM saleDate) AS week_number,
    COUNT(orderID) AS order_count
FROM Sales
WHERE saleDate BETWEEN current_date - INTERVAL '52 weeks' AND current_date
GROUP BY week_number
ORDER BY week_number;


-- 2) Realistic sales history
SELECT 
    EXTRACT(HOUR FROM saleTime) AS hour_of_day,
    COUNT(orderID) AS order_count,
    SUM(salePrice) AS total_sales
FROM Sales
GROUP BY hour_of_day
ORDER BY hour_of_day;


-- 3) 2 peak days
SELECT 
    saleDate AS order_date,
    SUM(salePrice) AS total_sales
FROM Sales
GROUP BY saleDate
ORDER BY total_sales DESC
LIMIT 10;


-- 4) 20 items in inventory
SELECT COUNT(*) AS total_items_in_inventory
FROM Inventory;


-- 5) gets the corresponding employee to each sale
SELECT employeeID FROM Sales;


-- 6) check inventory availablity for an item:
SELECT * FROM Inventory WHERE inventoryID = 1 AND inventoryInStock = true;


-- 7) list sales made by a given employee: 3
SELECT * FROM Sales WHERE employeeID = 3;


-- 8) list the managers from employees 
SELECT * FROM Employees WHERE isManager = true;


-- 9) check if an item contains caffine; ex: Caramel Milk Tea
SELECT * FROM MenuItems WHERE menuItemName = 'Caramel Milk Tea' AND hasCaffeine = true;


-- 10) change the price of a menu item: ex: id = 7 
UPDATE MenuItems SET price = 52.99 WHERE id = 2;


-- 11) find all the items in a given category
SELECT * FROM MenuItems WHERE menuitems.menuItemCategory = 'Seasonal';


-- 12) add a new menu item
INSERT INTO MenuItems (menuItemID, menuItemName, menuItemPrice, menuItemCalories, menuItemCategory, hasCaffeine) 
VALUES (65, 'Super Chocolate Tea', 9.99, '500-600', 'Seasonal', true);


-- 13) add a new invetory item
INSERT INTO Inventory (inventoryID, inventoryName, supplier, quantity, recieved, expiration, inStock) 
VALUES (54, 'Chocolate Milk', 'Milk Company', 100,  '2023-10-01', '10-09-2023', true);


-- 14) add a new sale
INSERT INTO Sales (orderNo, saleDate, saleTime, salePrice, isLarge, menuItemID) 
VALUES (787878, '2023-10-01', '12:00:00', 5.75, true, 1);


-- 15) add a new employee
INSERT INTO Employees (employeeID, employeeName, employeeUsername, employeeUserPassword, isManager, payroll, schedule) 
VALUES (12, 'Eyad Morales', 'M1234512', "EyadMorales12" false, 10.50, 'MWF, 10:30 am - 9:00 pm');

-- 16) Query the inventory used for the Earl Grey Milk Tea With 3J's (with Pearls, Pudding, and Herbal Jelly) id = 17
SELECT 
    inv.inventoryID as InventoryID,
    inv.inventoryName as InventoryItemName,
    mi.measurement as AmountUsed,
    inv.inventoryQuantity as TotalQuantityInInventory,
    inv.inventoryReceivedDate,
    inv.inventoryExpirationDate,
    inv.inventoryInStock,
    inv.inventorySupplier
FROM 
    menuItems m
JOIN 
    menuItems_Inventory mi ON m.menuItemID = mi.menuItemID
JOIN 
    Inventory inv ON mi.inventoryID = inv.inventoryID
WHERE 
    m.menuItemID = 17;

-- 17) Query the menu items used with the inventory item: Oolong Tea id = 16
SELECT 
    m.menuItemID as MenuItemID,
    m.menuItemName as MenuItemName,
    m.menuItemPrice,
    m.menuItemCalories,
    m.menuItemCategory,
    m.hasCaffeine
FROM 
    menuItems m
JOIN 
    menuItems_Inventory mi ON m.menuItemID = mi.menuItemID
JOIN 
    Inventory inv ON mi.inventoryID = inv.inventoryID
WHERE 
    inv.inventoryID = 16;

