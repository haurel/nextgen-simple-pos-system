package processpos.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTextArea;

public class TransactionInfo {
    private int boughtQuantity, productId;
    private float paidPrice;
    
    public TransactionInfo(MySqlConnection mysqlconn) {
        try {
            String sql = "DELETE FROM transaction;";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
    
    public void addTransaction(MySqlConnection mysqlconn, CustomerInfo customer, ProductInfo product, int bQuan, float pPrice) {
        this.boughtQuantity = bQuan;
        this.paidPrice = pPrice;
         try {
            String sql = "INSERT INTO Transaction(CustomerId, ProductId, boughtQuantity, paidPrice) VALUES (?, ?, ?, ?);";
            PreparedStatement stmt = mysqlconn.connect().prepareStatement(sql);
            stmt.setInt(1, customer.getId());
            stmt.setInt(2, product.getId());
            stmt.setInt(3, this.boughtQuantity);
            stmt.setFloat(4, this.paidPrice);
            stmt.executeUpdate();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
    
    private void computeTransaction(JTextArea appLog, MySqlConnection mysqlconn, CustomerInfo customer) {
        String pName = "error";
        float pPrice = -1f;
        try {
            String sql = "SELECT product.Name, product.Price FROM product WHERE ProductId ='" + this.productId + "';";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) { 
                pName = rs.getString("product.Name");
                pPrice = rs.getFloat("product.Price");
                //System.out.println(pName + " - " + pPrice);
            }  
        } catch (SQLException s) {
            s.printStackTrace();
        }
        String fullText = "[TRANSACTION] | Customer: " + customer.getName() + " | Product Name: " + pName + " | Product Price/Quantity: " + pPrice + " | Bought Quantity: " + this.boughtQuantity + " | Full Price: " + this.paidPrice + "$    ";
        appLog.append(fullText + "\n");
    }
    private void computeTotalPrice(JTextArea appLog, MySqlConnection mysqlconn, CustomerInfo customer) {
        try {
            String sql = "SELECT SUM(paidPrice) AS TotalPrice FROM transaction WHERE CustomerId ='" + customer.getId() + "';";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()) { 
                float tPrice = rs.getFloat("TotalPrice");
                String finalString = "[TRANSACTION] | Total: " + tPrice + "$ ";
                appLog.append(finalString + "\n");
            }  
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
    public void printTransactionLog(JTextArea appLog, MySqlConnection mysqlconn, CustomerInfo customer) {
        try {
            String sql = "SELECT ProductId, boughtQuantity, paidPrice FROM transaction WHERE CustomerId ='" + customer.getId() + "';";
            PreparedStatement statement = mysqlconn.connect().prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) { 
                this.productId = rs.getInt("ProductId");
                this.boughtQuantity = rs.getInt("boughtQuantity");
                this.paidPrice = rs.getFloat("paidPrice");
                //System.out.println(productId + " - " + boughtQuantity + " - " + paidPrice);
                computeTransaction(appLog, mysqlconn, customer);
            }  
            computeTotalPrice(appLog, mysqlconn, customer);
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            mysqlconn.disconnect();
        }
    }
}
