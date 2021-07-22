package sqlite;

import java.sql.*;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteHelper {

    public static String db;

    public static void put(String sql) {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection(db);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate(sql);
        } catch (Exception ex) {

        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public static ResultSet get(String sql) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(db);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            return statement.executeQuery(sql);
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public static ResultSet del(String sql) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(db);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            return statement.executeQuery(sql);
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}
