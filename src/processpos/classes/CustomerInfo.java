package processpos.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerInfo {
    private int id;
    private String name;
    private float money;
    public CustomerInfo(String name, float money) {
       this.name = name;
       this.money = money;
    }
    private void updateMoney(MySqlConnection mysqlconn) {
        try {
            String sql = "UPDATE customer SET Money = ? WHERE CustomerId = ?;";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            statement.setFloat(1, this.money);
            statement.setInt(2, this.id);
            statement.executeUpdate();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
    public void giveMoney(float value, MySqlConnection mysqlconn) {
        this.money -= value;
        this.updateMoney(mysqlconn);
    }
    public void deleteCustomerRecords(MySqlConnection mysqlconn) {
        // optional method
        // deletes the previous customers
        try {
            String sql = "DELETE FROM customer;";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            statement.executeUpdate();

            // resets auto_increment
            // comment the lines below to disable this
            sql = "ALTER TABLE customer AUTO_INCREMENT = 1;";
            statement = mysqlconn.connect().prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
    private int getCustomerID(MySqlConnection mysqlconn) {
        int cid = -1;
        try {
            String sql = "SELECT CustomerId FROM customer ORDER BY CustomerId DESC LIMIT 1;";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()) 
                cid = rs.getInt("CustomerId");
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
        return cid;    
    }
    public void insertDataToTable(MySqlConnection mysqlconn) {
        // method inserts to table the name & money value every time the app runs
        try {
            String sql = "INSERT INTO customer(Name, Money) VALUES (?, ?);";
            PreparedStatement stmt = mysqlconn.connect().prepareStatement(sql);
            stmt.setString(1, this.name);
            stmt.setFloat(2, this.money);
            stmt.executeUpdate();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
        this.id = getCustomerID(mysqlconn);
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public float getMoney() {
        return money;
    }
}
