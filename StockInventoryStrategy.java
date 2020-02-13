
/*
 *Chad Collins
 * Assignment 3: Stock Investment Strategy
 * 
 * This program is used to get data from a mysql server and determine stock investment strategies.
 * 
 * Note: steps are numbered on the left respective to the steps from the assignment.
 ************************************************************************************************/


package package1;

import java.sql.*;
import java.util.*;
import java.io.*;

public class StockInventoryStrategy {
        private Map<String, ArrayList<Object>> splitMap;

        // Open a connection from the database to memory.
        static Connection conn = null;

        public StockInventoryStrategy() {
                splitMap = new TreeMap<String, ArrayList<Object>>();
        }

        public static void main(String[] args) throws Exception {

                String parameterFile = "ConnectionParameters.txt";
                if (args.length >=1) {
                        parameterFile = args[0];
                }
                Properties connectprops = new Properties();
                connectprops.load(new FileInputStream(parameterFile));

                try {
//1.            // Get a connection.
                        Class.forName("com.mysql.jdbc.Driver");
                        String dburl = connectprops.getProperty("dburl");
                        String username = connectprops.getProperty("user");
                        conn = DriverManager.getConnection(dburl, connectprops);
                        System.out.printf("Database connection %s %s established.\n",  dburl, username);

                        StockInventoryStrategy start = new StockInventoryStrategy();
//2.            // Repeat the second step indefinitely.
                        start.repeat();
                        // Close the connection.
                        conn.close();
                } catch (SQLException e) {
                        System.out.printf("SQLException: %s\nSQLState: %s\nVendorError: %s\n",
                                        e.getMessage(), e.getSQLState(), e.getErrorCode());
             }
                System.out.println("Exiting...");
        }

//2
        // repeat
        //
        // Main function, repeats until either an empty string or a string containing
        // only spaces is input from the user.
        //
        // return nothing (which breaks the loop).
        //
        private void repeat() throws Exception {
                boolean quit = false;
                String minDate = "0000.00.00", maxDate = "9999.99.99";
                while(!quit) {
//2.1           // Get input from user. 
                        // Request a ticker symbol and optionally start/end dates.
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("\nEnter ticker symbol [start/end dates]: ");
                        String userInput = scanner.next();

                        // Simultaneously checks and quits if nothing or only whitespace was entered.
                        if(quit = (userInput.matches("^\\s*$")))
                                break;
                        // Set date range if the user input them.
//                      else if (scanner.hasNext())
//                              minDate = scanner.next();
//                      else if (scanner.hasNext())
//                              maxDate = scanner.next();

//2.2           // Get company name from the company table of the input ticker.
                        // If the company is not found, restart from (2).
                        if(!getCompanyName(userInput))
                                break;

//2.3           // Retrieve all the PriceVolume data in the input data range for the ticker.
                        // If no userInput dates, get all of the PriceVolume data.
			int totalDays = getPriceVolumeData(userInput, minDate, maxDate);

//2.5           //
                        processSplitDay(userInput, totalDays);
                }
                if(quit)
                        return;
                else
                        repeat();
        }

/*
2.2     getCompanyName
        
                Get company name from table and print it.
        
                input: user-input ticker.
                
                return: a boolean in case of a nonexistent company.
        */
        private boolean getCompanyName(String Ticker) throws Exception {
                PreparedStatement pstmt = conn.prepareStatement(
                                "select Name " +
                                " from Company " +
                                " where Ticker = ?");
                pstmt.setString(1, Ticker);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                        System.out.printf("%s%n", rs.getString("Name"));
                        return true;
                } else {
                        System.out.println("No such company.");
                        return false;
                }

        }


/*
2.3             getPriceVolumeData
        
                Gets the PriceVolume data within a data range for a ticker.
                                                                                                          90,1-8        42%
        
                Gets the PriceVolume data within a data range for a ticker.
                In order to later adjust for splits, this function also gets data for all
                of the dates in reverse chronological order.

                 input: optional first and last dates to get a datarange for the ticker.

                 return: a ResultSet (the PriceVolume data).
*/
        private int getPriceVolumeData(String Ticker, String minDate, String maxDate) throws Exception {
                Companies currDay = new Companies();
                PreparedStatement pstmt = conn.prepareStatement(
                "select * " +
                "from PriceVolume " +
                "where Ticker = ? " +
                "order by TransDate DESC ");
                //"order by TransDate DESC " +
                //"limit 10");
                pstmt.setString(1, Ticker);
                ResultSet rs = (pstmt.executeQuery());
                int days = 0;
                while(rs.next()) {
                        /* Find and store split days into a TreeMap. Returns currDay Object.*/
                        currDay.setFutureOpeningPrice(findSplitDays(Ticker, rs.getString("TransDate"), Double.parseDouble(rs.getString("openPrice")),
                                        Double.parseDouble(rs.getString("closePrice")), currDay.getFutureOpeningPrice()));
                        days++;
                }
                return days;
    }


        /*--------------------------------------------------------------------------------------------------
         * Finds split days and stores it into a TreeMap along with its value. checks and Stores either a 
         * two-to-one, three-to-one, or three-to-two split.
         *-------------------------------------------------------------------------------------------------*/
        private double findSplitDays(String name, String date, double openingPrice, double closingPrice, double futureOpeningPrice) {
                Companies split_days;

                if (futureOpeningPrice != 0) {
                        double twoToOneSplit = closingPrice / futureOpeningPrice - 2;
                        double threeToOneSplit = closingPrice / futureOpeningPrice- 3;
                        double threeToTwoSplit = closingPrice / futureOpeningPrice - 1.5;

                        if ((Math.abs(threeToOneSplit) < 0.3)) {
                                split_days = new Companies(name, date, closingPrice, futureOpeningPrice, 0,
                                                threeToOneSplit, 0);
                                hasKey(splitMap, name, split_days);
                        }
                        else if ((Math.abs(threeToTwoSplit) < 0.15)) {
                                split_days = new Companies(name, date, closingPrice, futureOpeningPrice, 0,
                                                0, threeToTwoSplit);
                                hasKey(splitMap, name, split_days);
                        }
                        else if ((Math.abs(twoToOneSplit) < 0.2)) {
                                split_days = new Companies(name, date, closingPrice, futureOpeningPrice,
                                                twoToOneSplit, 0, 0);
                                hasKey(splitMap, name, split_days);
                        }
                        else if (!splitMap.containsKey(name))
                                splitMap.put(name, new ArrayList<Object>());
                }
                return openingPrice;
        }
        private void hasKey(Map<String,ArrayList<Object>> map, String name, Companies company) {
                ArrayList<Object> tempList;

                if(map.containsKey(name))
                        tempList = map.get(name);
                else
                        tempList = new ArrayList<Object>();

                tempList.add(company);
                map.put(name, tempList);
        }

        /*--------------------------------------------------------------------------------------------------
         * Processes the split days TreeMap. Uses three ArrayLists to hold the three different possible splits.
         * Then displays the information in order of splits first and descending dates second.
         *-------------------------------------------------------------------------------------------------*/
        private void processSplitDay(String key, int totalDays) {
                List<Object> twoOneList = new ArrayList<Object>();
                List<Object> threeOneList = new ArrayList<Object>();
                List<Object> threeTwoList = new ArrayList<Object>();
                List<Object> tempList = splitMap.get(key);

                for (Object S : tempList) {
                        double twoToOneSplitValue = ((Companies) S).getTwoToOneSplit();
                        double threeToOneSplitValue = ((Companies) S).getThreeToOneSplit();
                        double threeToTwoSplitValue = ((Companies) S).getThreeToTwoSplit();
                        if(twoToOneSplitValue != 0)
                                twoOneList.add((Companies) S);
                        if(threeToOneSplitValue != 0)
                                threeTwoList.add((Companies) S);
                }

                int i, j, k;
                for(i = 0; i < twoOneList.size(); i++)
                        System.out.printf("2:1 split on %s%n", twoOneList.get(i));
                for(j = 0; j < threeOneList.size(); j++)
                        System.out.printf("3:1 split on %s%n", threeOneList.get(j));
                for(k = 0; k < threeTwoList.size(); k++)
                        System.out.printf("3:2 split on %s%n", threeTwoList.get(k));
                System.out.printf("%d splits in %d trading days%n%n", i+j+k, totalDays);
        }
}

