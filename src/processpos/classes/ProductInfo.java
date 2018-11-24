package processpos.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTextArea;

public class ProductInfo {
    private int id, quantity;
    private String name;
    private float price;
    public void setId(int id) {
        this.id = id;
    }
    public void buyQuantity(int value, MySqlConnection mysqlconn) {
        this.quantity -= value;
        this.updateQuantity(mysqlconn);
    }
    private void updateQuantity(MySqlConnection mysqlconn) {
        try {
            String sql = "UPDATE product SET Quantity = ? WHERE ProductId = ?;";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            statement.setInt(1, this.quantity);
            statement.setInt(2, this.id);
            statement.executeUpdate();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
    public int getId() {
        return id;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getName() {
        return name;
    }
    public float getPrice() {
        return price;
    }
    public void printProductsList(JTextArea appLog, MySqlConnection mysqlconn) {
        try {
                String sql = "SELECT * FROM product;";
                PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
                ResultSet rs = statement.executeQuery(sql);
                while(rs.next()) { 
                    int pid = rs.getInt("ProductId"); 
                    String pname = rs.getString("Name");
                    float pprice = rs.getFloat("Price");
                    int pquantity = rs.getInt("Quantity");
                    String fullQuery = "[PRODUCT ITEM] | ID: " + pid + " | Name: " + pname + " | Price: " + pprice +  "$ | In Stock: " + pquantity;
                    appLog.append(fullQuery + "\n");
                }   
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
    
    public void getProductFromTextField(MySqlConnection mysqlconn) {
        try {
            String sql = "SELECT Name, Price, Quantity FROM product WHERE ProductId = '" + this.id + "';";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()) { 
                this.name = rs.getString("Name"); // can't check for null  where id doesn't exist
                this.price = rs.getFloat("Price");
                this.quantity = rs.getInt("Quantity");
            }  
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
}
