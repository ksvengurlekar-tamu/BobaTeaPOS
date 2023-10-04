-- 1) 52 weeks of sales history
SELECT 
    EXTRACT(WEEK FROM TO_DATE(_date, 'MM-DD-YYYY')) AS week_number,
    COUNT(orderID) AS order_count
FROM Sales
GROUP BY week_number
ORDER BY week_number;

-- 2) Realistic sales history
SELECT 
    EXTRACT(HOUR FROM TO_TIMESTAMP(_time, 'HH24:MI')) AS hour_of_day,
    COUNT(orderID) AS order_count,
    SUM(price) AS total_sales
FROM Sales
GROUP BY hour_of_day
ORDER BY hour_of_day;


-- 3) 2 peak days
SELECT 
    TO_DATE(_date, 'MM-DD-YYYY') AS order_date,
    SUM(price) AS total_sales
FROM Sales
GROUP BY order_date
ORDER BY total_sales DESC
LIMIT 10;

-- 4) 20 items in inventory
SELECT COUNT(*) AS total_items_in_inventory
FROM Inventory;

-- 5) gets the corresponding employee to each sale
SELECT employeeID FROM Sales;
-- 6) check inventory availablity for an item:
SELECT * FROM Inventory WHERE id = 1 AND inStock = true;

-- 7) list sales made by a given employee: 3
SELECT * FROM Sales WHERE employeeID = 3;

-- 8) list the managers from employees 
SELECT * FROM Employees WHERE isManager = true;

-- 9) check if an item contains caffine; ex: Caramel Milk Tea
SELECT * FROM MenuItems WHERE _name = 'Caramel Milk Tea' AND hasCaffeine = true;

-- 10) change the price of a menu item: ex: id = 7 
UPDATE MenuItems SET price = 52.99 WHERE id = 2;

-- 11) find all the items in a given category
SELECT * FROM MenuItems WHERE menuitems.category = 'Seasonal';

-- 12) add a new menu item
INSERT INTO MenuItems (id, _name, price, calories, category, hasCaffeine) 
VALUES (65, 'Super Chocolate Tea', 9.99, '500-600', 'Seasonal', true);

-- 13) add a new invetory item
INSERT INTO Inventory (id, _name, supplier, quantity, recieved, expiration, inStock) 
VALUES (54, 'Chocolate Milk', 'Milk Company', 100,  '10-01-2023', '10-09-2023', true);

-- 14) add a new sale
INSERT INTO Sales (orderid, orderNo, _date, _time, price, isLarge, menuItemsID) 
VALUES (700000,1, '10-01-2023', '12:00 PM', 5.75, true, 1);

-- 15) add a new employee
INSERT INTO Employees (id, _name, isManager, payroll, schedule) 
VALUES (12, 'Eyad Morales', false, 10.50, 'MWF, 10:30 am - 9:00 pm');