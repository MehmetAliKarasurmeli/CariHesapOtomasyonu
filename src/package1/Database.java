package package1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    final private String url = "org.sqlite.JDBC";
    final private String db = "jdbc:sqlite:db/";
    final private String dbName = "Account Book.db";

    private Connection conn = null;
    private Statement st = null;

    public Statement connectSt() {
        try {
            Class.forName(url);
            conn = DriverManager.getConnection(db + dbName);
            st = conn.createStatement();
            System.out.println("Connection Success!");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Connection Error : " + e);
        }
        return st;
    }

    public void close() {
        if (st != null && conn != null) {
            try {
                st.close();
                conn.close();
                st = null;
                conn = null;
                System.out.println("Connection Closed!");
            } catch (SQLException e) {
                System.err.println("Closing Error: " + e);
            }
        }
    }

    public String insertQuery(String tableName, String[] values) {
        String insert = "insert into '" + tableName + "' values(";
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                if (values[i] == null) {
                    insert += values[i];
                } else {
                    insert += "'" + values[i] + "'";
                }
            } else {
                if (values[i] == null) {
                    insert += "," + values[i];
                } else {
                    insert += ",'" + values[i] + "'";
                }
                if (i == values.length - 1) {
                    insert += ")";
                }
            }
        }
        return insert;
    }
}
