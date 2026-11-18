======================================================
IST 4070 - Case 3: Retail Shop Stock
Student: Wanjiru (660263)
======================================================

HOW TO SET UP AND RUN THE PROGRAM:

1. REQUIREMENTS
   - Java JDK installed.
   - VS Code with the Extension Pack for Java.
   - The sqlite-jdbc-3.54.1.0.jar file located in the 'lib' folder.

2. DATABASE SETUP
   - No manual setup is required! 
   - The program is programmed to automatically create the SQLite database file ('shop.db'), build the tables, and insert 6 sample products and 3 sample sales the very first time it is run.

3. HOW TO RUN
   - Open the project folder in VS Code.
   - Ensure the Java language server has loaded (wait for the bottom bar to say "Java: Ready").
   - Open the file 'src/Main.java'.
   - Click the "Run" button above the main method (or right-click and select "Run Java").

4. HOW TO TEST
   - To see the raw database output, run 'src/DatabaseTest.java'.
   - Use the GUI to Add, Update, Delete products, and record Sales.
   - Try selling a quantity higher than the available stock to see the error handling.
   - Click "Total Stock Value" to see the array and loop in action.