# CAFE DATABASE | POSTGRESQL USING JAVA DATABASE CONNECTIVITY (JDBC)

This project is a command line application that connects to a PSQL database of a cafe. 
The main application is run with `Cafe.java` under the `src/` folder.
The application allows users to search/order from a cafe menu.
It includes functionality to create multiple users and keep track of their orders and favorite items.

The .sql files under the `sql/` folder create and initialize a PSQL database with data for the tables Users, Menu, Orders, and ItemStatus.
They use the data given from .csv files under the `sql/data` folder.

## Running the application
Create and connect to a PSQL server then run the sql scripts in the following order: `create_tables.sql`, `load_data.sql`, `create_indexes.sql`.
If `load_data.sql` fails, change the .csv paths to absolute paths.

Run `src/Cafe.java` with arguments: `java [-classpath <classpath>] src/Cafe.java <dbname> <port> <user> <password>`.
The `<password>` argument can be left empty if your PSQL database does not require one.
Make sure to stop the PSQL server after closing the application.

On a Linux machine, you may alternatively run the .sh scripts under the `scripts/` folder to create/connect to a PSQL database, load data to the database, and compile the main Java program.
The directories/paths in the scripts may need to be changed.

Within the application, you may login using username `Admin` and password `admin` to use Manager only menu options.

## Project structure
- The `lib/` folder contains the PSQL driver for JDBC
- The `sql/` folder contains the sql scripts to initialize the database and create indexes
  - **Important:** You may have to change the .csv paths in `load_data.sql` to absolute paths
- The `sql/data` folder contains data in .csv files utilized by the sql scripts

#### Linux Only - Optional
- The `scripts/` folder contains .sh scripts for Linux
  - `....PostgreDB.sh` create/start/stop a PSQL server and database on your machine
  - `create_db.sh` initializes the database using the sql scripts
  - `compile.sh` compiles the java program (you must edit the java path in the script)

# PROJECT REPORT
### MAIN FUNCTIONS
#### Menu Functions
- Search - All users
  - Users are asked to search by type or item name
    - Name must be an exact match, including capitalization and spaces

- Guided - All users
  - Users are asked to search by favorites or an item type from a list
    - Items of this type are listed and the user can choose one
      - Users may add this item to their favorites
      - No duplicate protection is implemented
      - Users may order this item using addOrder()
      - Searches by favorites don’t allow ordering (bug)

- Add - Managers only
  - User is prompted to fill out the needed columns for the new item

- Delete - Managers only
  - User chooses an item to delete using the guide procedure

- Update - Managers only
  - User chooses an item using the guide procedure
  - User is asked to choose a column to update from a list
  - User is asked for a new value for the column

#### Updating Profiles
- Managers are asked if they want to update their own profile, or another user’s
  - If they choose another user, they must enter the login of that user
    - Name must be an exact match, including capitalization and spaces
- User is asked what profile column they want to edit
  - All users can edit
    - Phone number
    - Login
    - Password
    - Favorite Items
    - Delete only
  - Managers edit the above as well as
    - Type
    - Manager can assign the user a role from the list

#### Placing Orders
- User is prompted to find an item to add to a new order
- User is asked how many of the item they want to add
  - Total price is calculated by (quantity * price)
- User is asked if they’d like to add another item to the order
  - If yes (y), user finds another item and quantity to add to their order
  - If not (n), a new order is inserted into the Orders table
    - User’s orders are displayed
- 
#### Updating Orders
- All of the user’s orders are displayed in a numbered list
- User is prompted to select which order they’d like to update
  - The orderid is extracted from the chosen order
- If the order is not already paid for, user is prompted to make a choice
  - `1` Pay for order
    - Sets the paid boolean the current order to true
  - `2.` Cancel order
    - Deletes the current order from the Orders table

### HELPER FUNCTIONS
#### favItems Handlers
- These functions handle the insertion deletion and reading of a user’s favorite items
  - There is no duplicate protection for the favorites

#### Dynamic Input Handlers
- These functions enable the user to choose an item from a list without having to type it out

#### String Sanitizer
- Rudimentary sanitizing function for string input
  - Deletes semicolons (;) from string inputs
  - Escapes out commas (‘) from string inputs
 
#### updateField()
- Updates a user’s field (login, phoneNum, password, favItems)
  - Prints old and new value
  - New field is updated in Users table
 
#### addOrder()
- Adds an order to the Orders table given a chosenItem String
- This function is used within PlaceOrder() and Guide()
 
#### payOrder()
- Pays for a selected order given an orderid
  - Changes paid boolean to true
 
#### cancelOrder()
- Cancels a selected order given an orderid
- Deletes order from Orders table
 
### Special Requirements for Compiling/Running
- Create and connect to a PSQL server then run the sql scripts in the following order: `create_tables.sql`, `load_data.sql`, `create_indexes.sql`
  - If `load_data.sql` fails, change the .csv paths to absolute paths
- Run `src/Cafe.java` with arguments: `java [-classpath <classpath>] src/Cafe.java <dbname> <port> <user> <password>`
  - The `<password>` argument can be left empty if your PSQL database does not require one
- On a Linux machine, you may alternatively run the .sh scripts under the `scripts/` folder to create/connect to a PSQL database, load data to the database, and compile the main Java program
  - The directories/paths in the scripts may need to be changed
- Within the application, you may login using username `Admin` and password `admin` to use Manager only menu options.
