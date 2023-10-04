import csv
import random
from datetime import datetime, timedelta

# Drink ID: [Name, mediumPrice, Calories, Series, hasCaffeine]

drinksMap = {
    # Milk Foam Series
    0: ["Green Tea", 5.00, "190-270", "Milk Foam", True],
    1: ["Black Tea", 5.00, "190-270", "Milk Foam", True],
    2: ["Oolong Tea", 5.00, "190-270", "Milk Foam", True],
    3: ["Earl Grey Tea", 5.00, "190-270", "Milk Foam", True],
    4: ["Wintermelon", 5.00, "290-305", "Milk Foam", False],
    5: ["Dirty Brown Sugar Milk Tea", 5.25, "290-305", "Milk Foam", True],
    6: ["Creme Brulee Brown Sugar Milk Tea", 6.25, "461-608", "Milk Foam", True],
    7: ["Creme Brulee Strawberry Latte", 6.25, "364-542", "Milk Foam", False],
    
    # Milk Tea Series
    8: ["Oolong Tea", 4.50, "265-315", "Milk Tea", True],
    9: ["Green Milk Tea", 4.50, "265-315", "Milk Tea", True],
    10: ["Black Tea", 4.50, "265-315", "Milk Tea", True], 
    11: ["Earl Grey Tea", 4.50, "265-315", "Milk Tea", True],
    12: ["Pearl Milk Tea", 5.00, "335-430", "Milk Tea", True],
    13: ["Caramel Milk Tea", 5.00, "270-315", "Milk Tea", True],
    14: ["Brown Sugar Milk Tea", 5.00, "290-390", "Milk Tea", True],
    15: ["Wintermelon Milk Tea", 5.00, "150-175", "Milk Tea", True],
    16: ["Strawberry Milk Tea", 5.25, "290-330", "Milk Tea", True],
    17: ["Earl Grey Milk Tea With 3J's (with Pearls, Pudding, and Herbal Jelly)", 5.75, "315-355", "Milk Tea", True],

    # Slush Series
    18: ["Passionfruit Yogurt", 6.00, "170-210", "Slush", False],
    19: ["Lychee", 6.00, "90-130", "Slush", False],
    20: ["Mango Milk", 6.00, "216-270", "Slush", False],
    21: ["Strawberry Milk", 6.00, "290-330", "Slush", False],
    22: ["Taro Milk", 6.00, "250-310", "Slush", False],
    23: ["Matcha Milk", 6.00, "135-190", "Slush", True],
    24: ["Caramel Chocolate", 6.00, "290-370", "Slush", True],
    25: ["Peach Slush", 6.00, "106-129", "Slush", True],
    
    # Seasonal Series
    26: ["Peach Green Tea", 5.00, "100-290", "Seasonal", True],
    27: ["Strawberry Green Tea", 5.00, "100-290", "Seasonal", True],
    28: ["Mango Green Tea", 5.00, "100-290", "Seasonal", True],
    29: ["Passionfruit Green Tea", 5.00, "100-290", "Seasonal", True],
    30: ["Lemon Green Tea", 5.00, "100-290", "Seasonal", True],
    31: ["Grapefruit Green Tea", 5.00, "100-290", "Seasonal", True],
    32: ["Hibiscus Green Tea", 5.00, "90-170", "Seasonal", True],
    33: ["Honey Green Tea", 5.00, "130-150", "Seasonal", True],
    34: ["Lychee Oolong", 5.00, "70-110", "Seasonal", True],
    35: ["Lemon Ai-Yu White Pearl", 5.25, "170-210", "Seasonal", False],
    36: ["Lemon Wintermelon Basil Seeds", 5.25, "170-210", "Seasonal", False],
    37: ["Taro", 5.25, "250-310", "Seasonal", False],
    38: ["Caramel Chocolate", 5.25, "270-350", "Seasonal", True],
    39: ["Peach Yogurt Drink", 5.50, "130-225", "Seasonal", False],
    40: ["Strawberry Yogurt Drink", 5.50, "130-235", "Seasonal", False],
    41: ["Mango Yogurt Drink", 5.50, "130-235", "Seasonal", False],
    42: ["Passionfruit Yogurt Drink", 5.50, "130-235", "Seasonal", False],
    43: ["Lemon Yogurt Drink", 5.50, "130-235", "Seasonal", False],
    44: ["Grapefruit Yogurt Drink", 5.50, "130-235", "Seasonal", False],
    
    # Tea Latte Series
    45: ["Green Tea Latte", 5.25, "85-170", "Tea Latte", True],
    46: ["Black Tea Latte", 5.25, "85-170", "Tea Latte", True],
    47: ["Oolong Tea Latte", 5.25, "85-170", "Tea Latte", True],
    48: ["Earl Grey Latte", 5.25, "85-170", "Tea Latte", True],
    49: ["Matcha Tea Latte", 5.25, "190-230", "Tea Latte", True],
    50: ["Thai Tea Latte", 5.75, "280-360", "Tea Latte", True],
    51: ["Strawberry Matcha Latte", 6.00, "158-225", "Tea Latte", True],


    # Coffee Series
    52: ["Milk Coffee", 5.00, "350-450", "Coffee", True],
    53: ["Coffee Milk Tea", 5.00, "215-270", "Coffee", True],
    54: ["Milk Foam Black Coffee", 5.00, "190-250", "Coffee", True],
    55: ["Dolce Milk Coffee", 5.00, "348-450", "Coffee", True],

    # Toppings
    56: ["Milk Foam", 1.25, "125-135", "Toppings", False],
    57: ["White Pearls", 0.75, "28", "Toppings", False],
    58: ["Tapioca Pearls", 0.50, "148", "Toppings", False],
    59: ["Coconut Jelly", 0.50, "45", "Toppings", False],
    60: ["Pudding", 0.50, "23", "Toppings", False],
    61: ["Herbal Jelly", 0.50, "23", "Toppings", False],
    62: ["Ai-yu Jelly", 0.50, "11", "Toppings", False],
    63: ["Basil Seeds", 0.50, "0.2", "Toppings", False],
    64: ["Oreo Crumbs", 0.50, "48", "Toppings", False]
}


# Employee ID: [ID, Name, isManager, payroll, schedule]

employeeMap = {
    0: ["Eyad Nazir", False, 12.25, "MWF 10:30 am - 7:00 pm"],
    1: ["Jedidiah Samrajkumar", True, 25.00, "MWF 12:30 pm - 9:00 pm"],
    2: ["Kiran Vengurlekar", False, 16.25, "MWF 10:30 am - 7:00 pm"],
    3: ["Camila Brigueda", True, 25.00, "TRFS 1:00 pm - 9:00 pm"], 
    4: ["Rose Chakraborty", False, 15.25, "TWRS 10:30 am - 4:30 pm"],
    5: ["Lebron Aggie", False, 15.25, "TWRS 10:30 am - 4:30 pm"],
    6: ["Messi Brigueda", True, 25.00, "MTWF 10:30 am - 7:00 pm"],
    7: ["Ronaldo Chakraborty", False, 15.25, "MWF 10:30 am - 6:00 pm"],
    8: ["Neymar Aggie", False, 16.25, "TRS 6:00 pm - 9:00 pm"],
    9: ["Eyada Nazir", True, 12.00, "WRFS 10:30 am - 7:00 pm"]
}


# Inventory ID: [Name, Quantity (liquids in fl. oz. and others in amount), receivedDate, expirationDate, inStock, supplier]

inventoryMap = {
    # Miscellaneous
    0: ["Ice", 100, "10-01-2023", "10-09-2023", True, "Ice To Meet You Inc."],
    1: ["Milk", 500, "10-01-2023", "10-09-2023", True, "Milk Company"],
    2: ["Caramel Drizzle", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    3: ["Honey", 100, "10-01-2023", "10-09-2023", True, "Honey Company"],
    4: ["Brown Sugar", 100, "10-01-2023", "10-09-2023", True, "Sugar Company"],
    5: ["Sugar", 100, "10-01-2023", "10-09-2023", True, "Sugar Company"],
    6: ["Salt", 100, "10-01-2023", "10-09-2023", True, "Salt Company"],
    7: ["Matcha Powder", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    8: ["Taro Powder", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    9: ["Yogurt", 100, "10-01-2023", "10-09-2023", True, "Yogurt Company"],
    10: ["Arabica Coffee", 100, "10-01-2023", "10-09-2023", True, "Coffee Company"],
    11: ["Sweetened Condensed Milk", 100, "10-01-2023", "10-09-2023", True, "Milk Company"],
    12: ["Creamer", 100, "10-01-2023", "10-09-2023", True, "Creamer Company"],
    13: ["Water", 1000, "10-01-2023", "10-09-2023", True, "Water Company"],

    # Tea
    14: ["Black Tea", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    15: ["Green Tea", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    16: ["Oolong Tea", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    17: ["Earl Grey Tea", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    18: ["Jasmine Tea", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    
    # Syrup
    19: ["Wintermelon Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    20: ["Strawberry Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    21: ["Caramel Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    22: ["Peach Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    23: ["Chocolate Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    24: ["Creme Brulee Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    25: ["Grapefruit Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    26: ["Lemon Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    27: ["Hibiscus Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    28: ["Lychee Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    29: ["Mango Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],
    30: ["Passionfruit Syrup", 100, "10-01-2023", "10-09-2023", True, "Syrup Company"],

    # Toppings
    31: ["Oreo Crumbs", 100, "10-01-2023", "10-09-2023", True, "Nabisco"],
    32: ["Herbal Jelly", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    33: ["Ai-Yu Jelly", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    34: ["Basil Seeds", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    35: ["Pudding", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    36: ["Milk Foam", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    37: ["Coconut Jelly", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    38: ["Tapioca Pearls", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    39: ["White Pearls", 100, "10-01-2023", "10-09-2023", True, "Tea Company"],
    
    # Non-food
    40: ["Medium Cups", 500, "10-01-2023", "10-09-2023", True, "Cup Company"],
    41: ["Large Cups", 500, "10-01-2023", "10-09-2023", True, "Cup Company"],
    42: ["Medium Straws", 1000, "10-01-2023", "10-09-2023", True, "Straw Company"],
    43: ["Large Straws", 1000, "10-01-2023", "10-09-2023", True, "Straw Company"],
    44: ["Lids", 100, "10-01-2023", "10-09-2023", True, "Cup Company"],
    45: ["Napkins", 1000, "10-01-2023", "10-09-2023", True, "Napkin Company"],
    46: ["Gloves", 1000, "10-01-2023", "10-09-2023", True, "Glove Company"],
    47: ["Cleaning Solution", 1000, "10-01-2023", "10-09-2023", True, "Cleaning Company"],
    48: ["Cleaning Wipes", 1000, "10-01-2023", "10-09-2023", True, "Cleaning Company"],
    49: ["Trash Bags", 1000, "10-01-2023", "10-09-2023", True, "Trash Company"],
    50: ["Hand Soap", 10, "10-01-2023", "10-09-2023", True, "Soap Company"],
    51: ["Hand Sanitizer", 1000, "10-01-2023", "10-09-2023", True, "Sanitizer Company"],
    52: ["Paper Towels", 1000, "10-01-2023", "10-09-2023", True, "Paper Company"],
    53: ["Dish Soap", 50, "10-01-2023", "10-09-2023", True, "Soap Company"],
}


# menuItem_ID : ["ingredients ID List", "measurements"] - measurements are in fl. oz. and assumed medium size

menuItems_inventory = {
    # Milk Foam Series
    0: ["0, 5, 6, 13, 15, 36", "1, 1, 0.5, 1.5, 11, 1"], # Green Tea
    1: ["0, 5, 6, 13, 14, 36", "1, 1, 0.5, 1.5, 11, 1"], # Black Tea
    2: ["0, 5, 6, 13, 16, 36", "1, 1, 0.5, 1.5, 11, 1"], # Oolong Tea
    3: ["0, 5, 6, 13, 17, 36", "1, 1, 0.5, 1.5, 11, 1"], # Earl Grey Tea
    4: ["0, 5, 6, 13, 18, 19, 36", "1, 1, 0.5, 1.5, 1, 11, 1"], # Wintermelon
    5: ["0, 1, 4, 6, 13, 14, 36", "1, 2, 1, 0.5, 0.5, 11, 1"], # Dirty Brown Sugar Milk Tea
    6: ["0, 1, 4, 13, 14, 24, 36", "1, 2, 1, 0.5, 11, 1, 1"], # Creme Brulee Brown Sugar Milk Tea  
    7: ["0, 1, 5, 13, 20, 24, 36", "1, 2, 1, 0.5, 11, 1, 1"], # Creme Brulee Strawberry Latte
    
    # Milk Tea Series
    8: ["0, 1, 5, 13, 16", "1, 2, 4, 2, 7"], # Oolong Tea
    9: ["0, 1, 5, 13, 15", "1, 2, 4, 2, 7"], # Green Milk Tea
    10: ["0, 1, 5, 13, 14", "1, 2, 4, 2, 7"], # Black Tea
    11: ["0, 1, 5, 13, 17","1, 2, 4, 2, 7"], # Earl Grey Tea
    12: ["0, 1, 5, 13, 14, 38", "1, 2, 4, 1, 6, 2"], # Pearl Milk Tea
    13: ["0, 1, 2, 5, 13, 14", "1, 2, 2, 4, 1, 6"], # Caramel Milk Tea
    14: ["0, 1, 4, 5, 13, 14", "1, 2, 2, 4, 1, 6"], # Brown Sugar Milk Tea
    15: ["0, 1, 5, 13, 18, 19", "1, 2, 4, 2, 5, 2"], # Wintermelon Milk Tea
    16: ["0, 1, 5, 13, 14, 20", "1, 2, 4, 2, 5, 2"], # Strawberry Milk Tea
    17: ["0, 1, 5, 13, 17, 32, 33, 38", "1, 2, 4, 1, 5, 1, 1, 1"], # Earl Grey Milk Tea With 3J's

    # Slush Series
    18: ["0, 5, 9, 13, 30", "2, 1, 2, 11, 10"], # Passionfruit Yogurt
    19: ["0, 5, 13, 28", "2, 1, 11, 10"], # Lychee
    20: ["0, 1, 5, 13, 29", "2, 2, 1, 11, 10"], # Mango Milk
    21: ["0, 1, 5, 13, 20", "2, 2, 1, 11, 10"], # Strawberry Milk
    22: ["0, 1, 5, 8, 13", "2, 2, 5, 10, 11"], # Taro Milk
    23: ["0, 1, 5, 7, 13", "2, 2, 1, 10, 11"], # Matcha Milk
    24: ["0, 1, 2, 5, 13, 23", "2, 2, 1, 1, 11, 10"], # Caramel Chocolate
    25: ["0, 5, 13, 22", "2, 1, 11, 10"], # Peach Slush
    
    # Seasonal Series
    26: ["0, 5, 13, 15, 22", "1, 2, 2, 7, 4"], # Peach Green Tea
    27: ["0, 5, 13, 15, 20", "1, 2, 2, 7, 4"], # Strawberry Green Tea
    28: ["0, 5, 13, 15, 29", "1, 2, 2, 7, 4"], # Mango Green Tea
    29: ["0, 5, 13, 15, 30", "1, 2, 2, 7, 4"], # Passionfruit Green Tea
    30: ["0, 5, 13, 15, 26", "1, 2, 2, 7, 4"], # Lemon Green Tea
    31: ["0, 5, 13, 15, 25", "1, 2, 2, 7, 4"], # Grapefruit Green Tea
    32: ["0, 5, 13, 15, 27", "1, 2, 2, 7, 4"], # Hibiscus Green Tea
    33: ["0, 3, 5, 13, 15", "1, 2, 2, 7, 4"], # Honey Green Tea
    34: ["0, 5, 13, 16, 28", "1, 2, 2, 7, 4"], # Lychee Oolong
    35: ["0, 5, 13, 14, 26, 33, 39", "1, 2, 2, 7, 4, 1, 1"], # Lemon Ai-Yu White Pearl
    36: ["0, 5, 13, 14, 19, 26, 34", "1, 2, 2, 7, 1, 1, 4"], # Lemon Wintermelon Basil Seeds
    37: ["0, 5, 8, 13, 14", "1, 2, 2, 7, 4"], # Taro
    38: ["0, 1, 2, 5, 13, 23", "1, 6, 2, 1, 2, 3"], # Caramel Chocolate
    39: ["0, 5, 9, 13, 22", "1, 1, 6, 4,"], # Peach Yogurt Drink
    40: ["0, 5, 9, 13, 20", "1, 1, 6, 4, 4"], # Strawberry Yogurt Drink
    41: ["0, 5, 9, 13, 29", "1, 1, 6, 4, 4"], # Mango Yogurt Drink
    42: ["0, 5, 9, 13, 30", "1, 1, 6, 4, 4"], # Passionfruit Yogurt Drink
    43: ["0, 5, 9, 13, 26", "1, 1, 6, 4, 4"], # Lemon Yogurt Drink
    44: ["0, 5, 9, 13, 25", "1, 1, 6, 4, 4"], # Grapefruit Yogurt Drink
        
    # Tea Latte Series
    45: ["0, 1, 5, 6, 13, 15", "1, 6, 1, 0.5, 0.5, 7"], # Green Tea Latte
    46: ["0, 1, 5, 6, 13, 14", "1, 6, 1, 0.5, 0.5, 7"], # Black Tea Latte
    47: ["0, 1, 5, 6, 13, 16", "1, 6, 1, 0.5, 0.5, 7"], # Oolong Tea Latte
    48: ["0, 1, 5, 6, 13, 17", "1, 6, 1, 0.5, 0.5, 7"], # Earl Grey Latte
    49: ["0, 1, 5, 6, 7, 13", "1, 5, 1, 0.5, 5, 3"], # Matcha Tea Latte
    50: ["0, 1, 5, 6, 11, 13, 14", "1, 5, 1, 0.5, 2, 0.5, 6"], # Thai Tea Latte
    51: ["0, 1, 5, 7, 13, 20", "1, 5, 1, 4, 3, 2"], # Strawberry Matcha Latte
    
    # Coffee Series
    52: ["0, 1, 5, 10, 12", "1, 6, 1, 6, 2"], # Milk Coffee
    53: ["0, 1, 5, 10, 13, 17", "1, 4, 1, 6, 1, 3"], # Coffee Milk Tea 
    54: ["0, 5, 6, 10, 13, 36", "1, 1, 0.5, 11, 1, 1"], # Milk Foam Black Coffee
    55: ["0, 1, 5, 10, 11", "1, 6, 1, 6, 2"] # Dolce Milk Coffee
}


random.seed(34)


# Write to CSV file for the Menu Items Table
with open("menuItems.csv", 'w', newline = '') as file:
    writer = csv.writer(file)
    
    # Write the header row
    writer.writerow(["ID", "name", "price", "calories", "category", "has_Caffeine"])
    
    for ID, row in drinksMap.items():
        writer.writerow([
            ID,
            row[0],  # Name
            row[1],  # Price
            row[2],  # Calories
            row[3],  # Category
            row[4]   # HasCaffeine
        ])
        

# Write to CSV file for the Employees Table
with open("employeeMap.csv", 'w', newline = '') as file:
    writer = csv.writer(file)
    
    # Write the header row
    writer.writerow(["ID", "Name", "isManager", "payroll", "schedule"])
    
    for ID, row in employeeMap.items():
        writer.writerow([
            ID,
            row[0],  # Name
            row[1],  # isManager
            row[2],  # Payroll
            row[3]   # Schedule
        ])


#  Write to CSV file for the Inventory Table
with open("inventory.csv", 'w', newline = '') as file:
    writer = csv.writer(file)
    
    # Write the header row
    writer.writerow(["ID", "Name", "Quantity", "receivedDate", "expirationDate", "inStock", "supplier"])
    
    for ID, row in inventoryMap.items():
        writer.writerow([
            ID,
            row[0],  # Name
            row[1],  # Quanitity
            row[2],  # receivedDate
            row[3],  # expirationDate
            row[4],  # inStock
            row[5]   # supplier
        ])


# Write to CSV file for the menuItems_inventory Table
with open("menuItems_inventory.csv", 'w', newline = '') as file:
    writer = csv.writer(file)
    
    # Write the header row
    writer.writerow(["menuItem_ID", "ingredientsList_ID", "measurementsList"])
    
    for ID, row in menuItems_inventory.items():
        writer.writerow([
            ID,      # menuItem_ID
            row[0],  # ingredientsList_ID
            row[1]   # measurementsList
        ])        


# Write to CSV file for the Sales history Table
# Peak days
peakDays = ['01-15-2023', '08-21-2023']

count = 0
employeeID = random.randint(0, 9)

# Initialize order number
orderNo = 0
orderID = 0

# Starting and ending dates
startDate = datetime.strptime('10-01-2021', '%m-%d-%Y')
endDate = datetime.strptime('10-01-2023', '%m-%d-%Y')

# Open the CSV file for writing
with open('sales_history.csv', 'w', newline = '') as file:
    writer = csv.writer(file)

    # Write the headers
    writer.writerow(['orderID','orderNo', 'date', 'time', 'employeeID', 'price', 'isLarge', 'menuItemID'])

    # Generate data for each day in the year
    currentDate = startDate
    while currentDate <= endDate:
        dateStr = currentDate.strftime('%m-%d-%Y')
        
        # Determine the number of orders for the day
        numOrders = random.randint(75, 125)
        if dateStr in peakDays:
            numOrders *= 2 # Double the orders on peak days

        # Generate data for each order
        for i in range(numOrders):
            if count > 4:
                employeeID = random.randint(0, 9)
                count = 0
            # Generate other order data
            timeStr = f'{random.randint(10, 20)}:{random.randint(0, 59)}'
            if timeStr < '10:30': # Make sure it is between 10:30 am and 9:00 pm
                timeStr = '10:30' 
                
            numDrinks = random.randint(1, 4) # How many drinks in an order
            for i in range(numDrinks):
                # Randomly select a drink
                drinkID = random.randint(0, 55)
                drink = drinksMap[drinkID]
                   
                isLarge = random.choice([True, False])
                price = drink[1] + (0.75 if isLarge else 0) # Add $0.75 for large drinks
                menuItemID = drinkID

                # Write the data to the CSV
                writer.writerow([orderID, orderNo, dateStr, timeStr, employeeID, price, isLarge, menuItemID])
                orderID += 1
            # Increment the order number
            orderNo += 1
            count += 1
        # Move to the next day
        currentDate += timedelta(days=1)
        


        