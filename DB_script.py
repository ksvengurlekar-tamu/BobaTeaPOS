import csv
import random
from datetime import datetime, timedelta

# entry  ID: [Name, mediumPrice, Calories, Series, hasCaffeine]

drinks_map = {
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
    29: ["Passionfruit Tea", 5.00, "100-290", "Seasonal", True],
    30: ["Lemon Tea", 5.00, "100-290", "Seasonal", True],
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
    59: ["Coconut Jelly", .50, "45", "Toppings", False],
    60: ["Pudding", 0.50, "23", "Toppings", False],
    61: ["Herbal Jelly", 0.50, "23", "Toppings", False],
    62: ["Ai-yu Jelly", 0.50, "11", "Toppings", False],
    63: ["Basil Seeds", 0.50, "0.2", "Toppings", False],
    64: ["Oreo Crumbs", 0.50, "48", "Toppings", False]
}

