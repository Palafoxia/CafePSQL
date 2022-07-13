package src;


/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science & Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * //@param hostname the MySQL or PostgreSQL server hostname
    * @param dbname the name of the database
    * @param dbport port of the database
    * @param user the username used to login to the database
    * @param passwd the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try {
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      } // end catch
   } // end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close();
   } // end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).
    * This method issues the query to the DBMS and outputs the results
    * to standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()) {
		   if(outputHeader) {
   			for(int i = 1; i <= numCol; i++) {
	      		System.out.print(rsmd.getColumnName(i) + "\t");
			   }
			   System.out.println();
			   outputHeader = false;
		   }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      } // end while
      stmt.close();
      return rowCount;
   } // end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();

      // iterates through the result set and saves the data returned by the query.
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()) {
         List<String> record = new ArrayList<String>();
		   for (int i=1; i<=numCol; ++i)
			   record.add(rs.getString(i));
         result.add(record);
      } // end while
      stmt.close();
      return result;
   } // end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close ();
         }
      } catch (SQLException e) {
         // ignored.
      }
   }

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main(String[] args) {
      if (!(args.length == 3 || args.length == 4)) {
         System.err.println ("Usage: " + "java [-classpath <classpath>] " + Cafe.class.getName () +" <dbname> <port> <user> <password>");
         System.err.println("<password> may be left empty");
         return;
      }

      Greeting();
      Cafe esql = null;
      try { // use postgres JDBC driver
         try {
            Class.forName("org.postgresql.Driver");
         }
         catch (ClassNotFoundException e) {
            System.err.println (e);
            System.exit (-1);
         }
         // instantiate the Cafe object and create a physical connection
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         if(args.length == 4) {
            String passwd = args[3];
            esql = new Cafe(dbname, dbport, user, passwd);
         } else {
            esql = new Cafe(dbname, dbport, user, "");
         }
         

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()) {
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); clear(); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Menu");
                System.out.println("2. Update Profile");
                System.out.println("3. Place an Order");
                System.out.println("4. Update an Order");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()) {
                   case 1: Menu(esql, authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: PlaceOrder(esql, authorisedUser); break;
                   case 4: UpdateOrder(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }
      } catch(Exception e) {
         System.err.println (e.getMessage ());
      } finally {
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup();
               System.out.println("Done\n\nBye !");
            } // end if
         } catch (Exception e) {
            // ignored.
         } // end try
      } // end try
   } // end main

   public static void Greeting() {
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   } // end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      } while (true);
      return input;
   } // end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql) {
      try {
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	      String type = "Customer";
	      String favItems = "";

			String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      } catch(Exception e) {
         System.err.println (e.getMessage ());
      }
   } // end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql) {
      try {
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	      if (userNum > 0)
		      return login;
         System.out.println("Wrong username/password!");
         return null;
      } catch(Exception e) {
         System.err.println (e.getMessage ());
         return null;
      }
   } // end

// MAIN MENU FUNCTIONS

// 1. "Goto Menu"
public static void Menu(Cafe esql, String login) {
   try {
      clear();
      // Determine if user is manager
      boolean isManager = isManager(esql, login);

      // Run while loop until exit (9)
      boolean run = true;
      while(run) {
         System.out.println("MENU");
         System.out.println("---------");
         System.out.println("1. Search");
         System.out.println("2. Guided");
         if(isManager) { // Manager only menu options
            System.out.println("6. Add item");
            System.out.println("7. Delete item");
            System.out.println("8. Update item");
         }
         System.out.println("9. < EXIT");

         // User Input
         switch(readChoice()){
            case 1: // Search
               search(esql);
               break;
            case 2: // Guided Search
               guided(esql, login);
               break;
            case 6: // Add Item (Manager Only)
               if(isManager)
                  addItem(esql);
               else
                  System.out.println("Unrecognized choice!");
               break;
            case 7: // Delete Item (Manager Only)
               if(isManager)
                  deleteItem(esql, login);
               else
                  System.out.println("Unrecognized choice!");
               break;
            case 8: // Update Item (Manager Only)
               if(isManager)
                  updateItem(esql, login);
               else
                  System.out.println("Unrecognized choice!");
               break;
            case 9: // Exit
               run = false;
               break;
            default: // Other
               System.out.println("Unrecognized choice!");
               break;
         }
      }
      clear();
   } catch(Exception e) {
      System.err.println(e.getMessage());
   }
} // End Menu

// 2. Update Profile
public static void UpdateProfile(Cafe esql, String login) {
   try {
      clear();
      // Determine if user is manager
      boolean isManager = isManager(esql, login);
      String editUser = login;

      // Manager only menu options
      if(isManager) {
         System.out.println("UPDATE PROFILE");
         System.out.println("---------");
         System.out.println("1. Update own profile");
         System.out.println("2. Update other user");
         
         boolean run = true;
         
         // Run while loop until exit (9)
         while(run) {   
            switch(readChoice()) {
               case 1: // Do nothing
                  run = false;
                  break;
               case 2: // Edit other user
                  System.out.print("Enter user to edit(login):  ");
                  editUser = in.readLine();
                  System.out.println();
                  run = false;
                  break;
               default: // Other
                  System.out.println("Unrecognized Choice");
                  break;
            }
         }
      }

      // Run while loop until exit (9)
      clear();
      boolean run = true;
      while(run) {
         System.out.println("UPDATE PROFILE");
         System.out.println("---------");
         System.out.println("1. Phone number");
         System.out.println("2. User login");
         System.out.println("3. Password");
         System.out.println("4. Favourite Items");
         if(isManager) { // Manager Only
            System.out.println("5. Type");
         }
         System.out.println("8. Make user manager for demo");
         System.out.println("9. Exit");

         // User Input
         switch(readChoice()){
            case 1: // Update Phone Number
               updateField("phonenum", editUser, esql);
               clear();
               break;
            case 2: // Update Login
               updateField("login", editUser, esql);
               clear();
               break;
            case 3: // Update Password
               updateField("password", editUser, esql);
               clear();
               break;
            case 4: // Update Favorite Items
               List<List<String>> favorites = parseFavorites(esql, login);
               clear();
               System.out.println("Unfavorite Item");
               System.out.println("---------");
               printAndNumberResult(favorites, 1);
               int choice = getInputPosFromDynamic(favorites);
               if(choice != -1){
                  removeFromFavorites(esql, login, favorites, choice);
                  esql.executeQueryAndPrintResult(String.format("SELECT favitems FROM users WHERE login = '%s'", login));
               }
               clear();
               break;
            case 5: // Update Type | Manager Only
               if(isManager){
                  esql.executeQuery(String.format("UPDATE users SET type = '%s' WHERE login = '%s'", selectUserType(), editUser));
                  clear();
               } else {
                  System.out.println("Unrecognized choice!");
               }
               break;
            case 8: // Make User Manager
               String query = String.format("UPDATE users SET type = 'Manager' WHERE login = '%s'", login);
               esql.executeUpdate(query);
               clear();
               break;
            case 9: // Exit
               run = false;
               break;
            default: // Other
               System.out.println("Unrecognized choice!");
               break;
         }
      }
      clear();
   } catch(Exception e) {
      System.err.println(e.getMessage());
   }
} // End UpdateProfile

   // 3. "Place an Order"
   public static void PlaceOrder(Cafe esql, String login) {
      try {
          clear();
          // Wait for Enter to continue
          System.out.println("PLACE AN ORDER");
          System.out.println("---------");
          System.out.println("Select item from the following menu.... (press Enter key to continue)");
          try {System.in.read();} catch(Exception e) {}

          // Choose an item
          String chosenItem = findItem(esql, login);

          // Add order to Orders table
          addOrder(esql, login, chosenItem);
          clear();
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
  } // End PlaceOrder

   // 4. "Update an Order"
   public static void UpdateOrder(Cafe esql, String login) {
      try{
         //Code Here
         clear();
         
         // Run while loop until exit (9)
         clear();
         boolean run = true;
         while(run) {
            System.out.println("UPDATE ORDER");
            System.out.println("---------");

            // Print current user's orders
            System.out.println("YOUR ORDERS");
            System.out.println("orderid \t paid (t/f) \t timeStampRecieved \t total");
            String query = String.format("SELECT orderid, paid, timeStampRecieved, total FROM Orders WHERE login = '%s'", login);
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            printAndNumberResult(result, 4);

            // Select an order
            System.out.println("Which order would you like to update?");
            int orderID = Integer.parseInt(getInputStringFromDynamic(result));

            // Get paid boolean in String form
            query = String.format("SELECT paid FROM Orders WHERE orderID = '%d'", orderID);
            result = esql.executeQueryAndReturnResult(query);
            String paid = result.get(0).get(0);

            // Update order if not paid for
            if(paid.equals("t")) {
               System.out.println("Order is already paid for!");
            } else {
               System.out.println("What would you like to do?");
               System.out.println("1. Pay");
               System.out.println("2. Cancel");

               switch(readChoice()) {
                  case 1: payOrder(esql, orderID); break;
                  case 2: cancelOrder(esql, orderID); break;
               }
            }
         }
         clear();
   } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   } // End UpdateOrder

  
// HELPER FUNCTIONS

// Checks if user is a manager
public static boolean isManager(Cafe esql, String login) {
   try{
      String query = String.format("SELECT login FROM users WHERE login = '%s' AND type = 'Manager'", login);
      boolean isManager = (esql.executeQuery(query) != 0);
      return isManager;
   } catch(Exception e) {
      System.err.println (e.getMessage ());
      return false;
   }
}

// Update Field
public static void updateField(String field, String editUser, Cafe esql) {
   try {
      System.out.println("Old value: ");
      String query = String.format("SELECT %s FROM users where login = '%s'", field, editUser);
      esql.executeQueryAndPrintResult(query);

      System.out.println("Enter New Value: ");
      String newValue = in.readLine();
      query = String.format("UPDATE users SET %s = '%s' WHERE login = '%s'", field, newValue, editUser);
      esql.executeUpdate(query);

      System.out.println("\nNew value: ");
      query = String.format("SELECT %s FROM users where login = '%s'", field, editUser);
      esql.executeQueryAndPrintResult(query);
   } catch(Exception e) {
      System.err.println (e.getMessage ());
   }
}

// Searches by either item name or type
private static void search(Cafe esql) {
   try {
      System.out.println("SEARCH BY");
      System.out.println("---------");
      System.out.println("1. Item Name");
      System.out.println("2. Type");

      int choice = readChoice();
      System.out.print("Search for: ");
      String search = sanatizeString(in.readLine());
      System.out.println();

      boolean runSearchBy = true;
      String query = null;

      while(runSearchBy) { //Allows fixing typoes without re-entering menu
         switch(choice){
            case 1:  //Search by item name
               query = String.format("SELECT * FROM menu WHERE itemname = '%s'", search);
               runSearchBy = false;
               break;
            case 2:  //Search by item type
               query = String.format("SELECT * FROM menu WHERE type = '%s'", search);
               runSearchBy = false;
               break;
            default: // Other
               System.out.println("Unrecognized choice!");
               break;
         }
      }
      if(query != null) {
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }
   } catch(Exception e) {
      System.err.println (e.getMessage ());
   }
}

// Guided Search
private static void guided(Cafe esql, String login){
   try {
      String chosenType = findItem(esql, login);
      if(chosenType != null){
         clear();
         System.out.println(chosenType.toUpperCase());
         System.out.println("---------");
         System.out.println("1. Make favorite");   //Don't want to create incompatable algorithm
         System.out.println("2. Order");
         System.out.println("9. < Exit");
         boolean run = true;
         while(run){
            switch(readChoice()){
               case 1:
                  addToFavorites(esql, login, parseFavorites(esql, login), chosenType);
                  run = false;
                  break;
               case 2:
                  addOrder(esql, login, chosenType);
                  boolean paynow = false;
                  System.out.print("Would you like to pay now? (y/n)");
                  String choice = in.readLine().toLowerCase();

                  if(choice.equals("y"))
                     paynow = true;
                  if(paynow){
                     String query = String.format("SELECT orderid FROM orders WHERE login = '%s'", login);
                     List<List<String>> result = esql.executeQueryAndReturnResult(query);
                     payOrder(esql, Integer.parseInt(result.get(result.size()-1).get(0)));
                  }
                  run = false;
                  break;
               case 9:
                  run = false;
                  break;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }
         }
      }
   } catch(Exception e) {
      System.err.println (e.getMessage ());
   }
}

// Add Item to Menu
private static void addItem(Cafe esql) {
   try {
      // Ask user for item type, name, price, desription, and image URL
      System.out.print("\tEnter item type: ");
      String type = sanatizeString(in.readLine());

      System.out.print("\tEnter item name: ");
      String name = sanatizeString(in.readLine());

      System.out.print("\tEnter enter price: ");
      int price = readChoice();

      System.out.print("\tEnter description: ");
      String description = sanatizeString(in.readLine());

      System.out.print("\tEnter Image URL: ");
      String url = sanatizeString(in.readLine());

      // Insert user defined item into Menu
      String query = String.format("INSERT INTO menu (type, itemname, price, description, imageurl) VALUES ('%s','%s','%d','%s','%s')", type, name, price, description, url);
      esql.executeUpdate(query);

      System.out.println ("Item successfully added!");
   } catch(Exception e) {
      System.err.println(e.getMessage());
   }
}

// Delete Item from Menu
private static void deleteItem(Cafe esql, String login) {
   try {
      String itemname = findItem(esql, login);
      System.out.println(itemname.toUpperCase());
      System.out.println("---------");

      if(itemname != null){
         String query = String.format("DELETE FROM menu WHERE itemname = '%s'", itemname);
         esql.executeUpdate(query);
         System.out.println ("Item successfully deleted!");
      }
   }
   catch(Exception e) {
      System.err.println(e.getMessage());
   }
}

// Update Item in Menu
private static void updateItem(Cafe esql, String login) {
   try {
      String itemname = findItem(esql, login);
      boolean runUpdateField = true;
      while(runUpdateField) {
         System.out.println("Update field");
         System.out.println("---------");
         System.out.println("1. Type");
         System.out.println("2. Item name");
         System.out.println("3. Price");
         System.out.println("4. Description");
         System.out.println("5. Image URL");
         System.out.println("9. Exit");

         int choice = readChoice();
         String type = null;
         String change = null;
         if(choice != 9){
            System.out.print("New Value: ");
            change = sanatizeString(in.readLine());
            System.out.println();
         }
         String query = null;

         switch(choice) { //Choosing field to update
            case 1: type = "type"; break; // type
            case 2: type = "itemname"; break; // itemname
            case 3: // Price is int, not string
               try {
                  query = String.format("UPDATE menu SET price = '%f' WHERE itemname = '%s'", Double.parseDouble(change), itemname);
                  esql.executeQuery(query);
               } catch(Exception e) {
               System.err.println(e.getMessage());
               }
               break;
            case 4: type = "description"; break; // description
            case 5: type = "imageurl"; break; // imageurl
            case 9: runUpdateField = false; break; // Exit
            default:
               System.out.println("Unrecognized choice!");
               break;
         }

         if(type != null && query != null) {
            try {
               query = String.format("UPDATE menu SET %s = '%s' WHERE itemname = '%s'", choice, change, itemname);
            } catch(Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
   } catch(Exception e) {
      System.err.println(e.getMessage());
   }
}

// Clear the console screen
private static void clear() {
   System.out.print("\033[H\033[2J");
}

// Prints the results of the query search
private static void printAndNumberResult(List<List<String>> results, int colCount) {
   for(int i = 0; i < results.size(); i++){
      System.out.print("" + i + ". " + results.get(i).get(0));
      for(int j = 1; j < colCount; j++){
         System.out.print("\t" + results.get(i).get(j));
      }
      System.out.println();
   }
   System.out.println("" + results.size() + ". < Exit\n");
}

private static String getInputStringFromDynamic(List<List<String>> results){
   boolean runChoice = true;
   String chosenType = null;
   while(runChoice) { //Allows typoes without re-entering menu
      int choice = readChoice();
      if(results.size() == choice) //Exit program
         runChoice = false;
      else if(0 <= choice && results.size() > choice) {  //Allowed choice
         chosenType = results.get(choice).get(0);
         runChoice = false;
      } else {
         System.out.println("Unrecognized choice!");
      }
   }
   return chosenType;
}

private static int getInputPosFromDynamic(List<List<String>> results){
   boolean runChoice = true;
   int choice = -1;
   while(runChoice) { //Allows typoes without re-entering menu
      choice = readChoice();
      if(results.size() == choice){ //Exit program
         choice = -1;
         runChoice = false;
      }
      else if(0 <= choice && results.size() > choice) //Allowed choice
         runChoice = false;
      else {
         System.out.println("Unrecognized choice!");
      }
   }
   return choice;
}

// Cleans String, removes ; escapes ''
private static String sanatizeString(String input) {
   boolean consecutiveEscapes = false;
   boolean evenEscapes = true;

   // For every character in input
   for(int i = 0; i < input.length(); i++) {
      switch(input.charAt(i)) {
         case '\'': // Must have escape character in front
            String insert = "";
            if(consecutiveEscapes && evenEscapes)
               insert = "\\";
            input = input.substring(0, i - 1) + insert + input.substring(i);
            consecutiveEscapes = false;
            evenEscapes = true;
            break;
         case ';': // No semicolons allowed | escape character trackers unaffected
            input = input.substring(0, i - 1) + input.substring(i+1);
            i--;
            break;
         case '\\': // Tracks escape characters | need to know if even number or not
            evenEscapes = !evenEscapes;
            consecutiveEscapes = true;
            break;
         default: // Uninteresting character
            consecutiveEscapes = false;
            evenEscapes = true;
            break;
      }
   }
   return input;
}

// Cafe esql, String login
private static List<List<String>> parseFavorites(Cafe esql, String login) {
   try {
      List<List<String>> favorites = new ArrayList<List<String>>();
      List<String> tmp = new ArrayList<String>();
      String query = String.format("SELECT favitems FROM users WHERE login = '%s'", login);
      List<List<String>> rs = esql.executeQueryAndReturnResult(query);
      String result = rs.get(0).get(0);
      String item = "";
      int start = 0;
      int lastPos = 0;
      boolean lastFound = false;
      for(int i = 0; i < result.length(); i++) {
         if(' ' == result.charAt(i)) {  // The final favorite item does not have a trailing comma
            if(!lastFound) {
               lastPos = i;
               lastFound = true;
               item += result.charAt(i);
            }
         }
         else if (',' == result.charAt(i)) {
            tmp.add(item);
            tmp.add("" + start);
            tmp.add("" + i);
            item = "";
            start = i+1;
            favorites.add(tmp);
            tmp = new ArrayList<String>();
            lastFound = false;
         }
         else {
            item += result.charAt(i);
            lastFound = false;
         }
      }
      tmp.add(item);
      tmp.add("" + start);
      tmp.add("" + lastPos);
      favorites.add(tmp);
      return favorites;
   }catch(Exception e) {
      System.err.println (e.getMessage ());
      return null;
   }
}

//Intended to be used with getInputDynamic
private static List<List<String>> removeFromFavorites(Cafe esql, String login, List<List<String>> parsed, int remove){
   try {
      String query = String.format("SELECT favitems FROM users WHERE login = '%s'", login);
      List<List<String>> rs = esql.executeQueryAndReturnResult(query);
      String result = rs.get(0).get(0);
      int firstPos = Integer.parseInt(parsed.get(remove).get(1));
      int lastPos = Integer.parseInt(parsed.get(remove).get(2));
      if (0 < firstPos)
         firstPos--;
      else {
         lastPos++;
      }
      int size = lastPos - firstPos;
      result = result.substring(0,firstPos) + result.substring(lastPos);
      for(int i = remove; i < parsed.size(); i++) {
         parsed.get(i).set(1, "" + (Integer.parseInt(parsed.get(i).get(1)) - size));
         parsed.get(i).set(2, "" + (Integer.parseInt(parsed.get(i).get(2)) - size));
      }
      parsed.get(remove).remove(2);
      parsed.get(remove).remove(1);
      parsed.get(remove).remove(0);
      parsed.remove(remove);
      query = String.format("UPDATE users SET favitems = '%s' WHERE login = '%s'", result, login);
      esql.executeQuery(query);
      return parsed;
   }catch(Exception e){
      System.err.println (e.getMessage ());
      return null;
   }
}

private static List<List<String>> addToFavorites(Cafe esql, String login, List<List<String>> parsed, String newFavorite) {
   try {
      String query = String.format("SELECT favitems FROM users WHERE login = '%s'", login);
      List<List<String>> rs = esql.executeQueryAndReturnResult(query);
      String result = rs.get(0).get(0);
      int last = Integer.parseInt(parsed.get(parsed.size()-1).get(2));
      if(0 < last){
         result = result.substring(0, last) + ',' + newFavorite + result.substring(last);
      }
      else{
         result = newFavorite + result;
      }
      result = result.substring(0, 400);
      List<String> tmp = new ArrayList<String>();
      tmp.add(newFavorite);
      tmp.add("" + (last + 1));
      tmp.add("" + (last + 1 + newFavorite.length()));
      parsed.add(tmp);
      query = String.format("UPDATE users SET favitems = '%s' WHERE login = '%s'", result, login);
      esql.executeQuery(query);
      return parsed;  
   } catch(Exception e) {
      System.err.println (e.getMessage ());
      return null;
   }
}

// Guided Search
private static String findItem(Cafe esql, String login) {
   try {
      clear();

      // Print types of items
      String query = "SELECT DISTINCT type FROM menu";
      List<List<String>> result = esql.executeQueryAndReturnResult(query);
      List<String> tmp = new ArrayList<String>();
      tmp.add("Favorites");
      result.add(tmp);
      System.out.println("TYPES");
      System.out.println("---------");
      printAndNumberResult(result, 1);

      // 
      String chosenType = getInputStringFromDynamic(result);
      System.out.println(chosenType);
      if(chosenType != null) {
         clear();
         if("Favorites" == chosenType){
            result = parseFavorites(esql, login);
         }
         else{
            query = String.format("SELECT itemname, price, description FROM menu WHERE type = '%s'", chosenType);
            result = esql.executeQueryAndReturnResult(query);
         }
         System.out.println(chosenType.toUpperCase());
         System.out.println("---------");
         System.out.println("#  Item\t\t\t\t\t\t\tPrice\t\tDescription\n");
         printAndNumberResult(result, 3);
         return getInputStringFromDynamic(result);
      }
      else
         return null;
   } catch(Exception e) {
      System.err.println (e.getMessage ());
      return null;
   }
}

private static String selectUserType(){
   boolean run = true;
   System.out.println("USER TYPE");
   System.out.println("---------");
   String type = null;
   while(run){
      System.out.println("1. Customer");
      System.out.println("2. Employee");
      System.out.println("3. Manager");
      switch(readChoice()){
         case 1:
            type = "Customer";
            run = false;
            break;
         case 2:
            type = "Employee";
            run = false;
            break;
         case 3:
            type = "Manager";
            run = false;
            break;
         default:
            System.out.println("Unrecognized choice!");
            break;
      }
   }
   return type;
}

// Pay Order
private static void payOrder(Cafe esql, int orderID) throws SQLException {
   String query = String.format("UPDATE Orders SET paid = true WHERE orderid = '%d'", orderID);
   esql.executeUpdate(query);
   System.out.println("Order successfully paid for!");
}

// Cancel Order
private static void cancelOrder(Cafe esql, int orderID) throws SQLException {
   String query = String.format("DELETE FROM Orders WHERE orderID = '%d'", orderID);
   esql.executeUpdate(query);
   System.out.println("Order successfully canceled!");
}

// Add Order
private static void addOrder(Cafe esql, String login, String chosenItem) throws SQLException, IOException{
   boolean run = true;
   boolean anotherItem = false;
   double total = 0;
   String query;
   chosenItem = chosenItem.trim();

   while(run) {
      // If adding another item, search again
      if(anotherItem) {
         chosenItem = findItem(esql, login);
      }

      // Add item to order
      chosenItem = chosenItem.trim();

      System.out.println(chosenItem.toUpperCase());
 
      // Calculate total price
      query = String.format("SELECT price FROM menu WHERE itemName = '%s'", chosenItem);
      List<List<String>> result = esql.executeQueryAndReturnResult(query);
      double price = Double.parseDouble((result.get(0).get(0)));
      System.out.print("How Many? ");
      double quantity = Double.parseDouble(in.readLine());
      total += price * quantity;

      // Print
      if(quantity == 1) {
         System.out.println((int)quantity + " " + chosenItem + " added to order!");
      } else {
         System.out.println((int)quantity + " " + chosenItem + "s added to order!");
      }

      // Add another item 
      System.out.print("Would you like to add another item to your order? (y/n)");
      String choice = in.readLine().toLowerCase();
 
      switch(choice) {
         case "y": anotherItem = true; break; // Add another item
         case "n": // Don't add another item and create order
            query = String.format("INSERT INTO Orders (login, paid, timeStampRecieved, total) VALUES ('%s','%s','%s','%s')"
                                     , login, "false", "now()", String.valueOf(total));
            esql.executeUpdate(query);
            System.out.println("Order Successfully Placed! (Not Paid)");
            System.out.println("");

            // Print current user's orders
            System.out.println("YOUR ORDERS");
            System.out.println("orderid \t paid (t/f) \t timeStampRecieved \t total");
            query = String.format("SELECT orderid, paid, timeStampRecieved, total FROM Orders WHERE login = '%s'", login);
            result = esql.executeQueryAndReturnResult(query);
            printAndNumberResult(result, 4);

            // Wait for Enter to continue
            System.out.println("(press Enter key to continue)");
            try {System.in.read();} catch(Exception e) {}
            run = false;
            break;
        default: System.out.println("Unrecognized Choice"); break; // Other
      }
   }
} 

} //end Cafe