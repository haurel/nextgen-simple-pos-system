package processpos.main;

import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import processpos.classes.CustomerInfo;
import processpos.classes.MySqlConnection;
import processpos.classes.ProductInfo;
import processpos.classes.TransactionInfo;


public class PosSystem extends JFrame {

    private JTextField textProdId, textProdQuantity;
    private JButton getProducts, getMoneyBalance, buyProduct, getBill;
    private JTextArea appLog;
    private void loadGUI() {
        this.getContentPane().setLayout(null);
	this.setBounds(50, 50, 800, 600);
        
        JLabel pidLabel = new JLabel("Product ID");
        pidLabel.setBounds(35, 20, 60, 40);
	this.getContentPane().add(pidLabel);
        
        textProdId = new JTextField();
	textProdId.setBounds(100, 23, 90, 25);
	getContentPane().add(textProdId);
	//textProdId.setColumns(10);
        
        JLabel pqLabel = new JLabel("Quantity");
        pqLabel.setBounds(35, 45, 60, 40);
	this.getContentPane().add(pqLabel);
        
        textProdQuantity = new JTextField();
	textProdQuantity.setBounds(100, 53, 90, 25);
	this.getContentPane().add(textProdQuantity);
        //textProdQuantity.setColumns(10);
        
        JScrollPane scroll = new JScrollPane();
        scroll.setBounds(20, 250, 740, 280);
        this.getContentPane().add(scroll);
        appLog = new JTextArea();
        scroll.setViewportView(appLog);
        
        buyProduct = new JButton("Buy Item");
        buyProduct.setBounds(20, 95, 180, 23);
	this.getContentPane().add(buyProduct); 
        getProducts = new JButton("Get Products List");
        getProducts.setBounds(20, 150, 180, 23);
	this.getContentPane().add(getProducts);
        getMoneyBalance = new JButton("Get Money Balance");
        getMoneyBalance.setBounds(20, 180, 180, 23);
	this.getContentPane().add(getMoneyBalance);
        getBill = new JButton("get Bill");
        getBill.setBounds(20, 210, 180, 23);
	this.getContentPane().add(getBill);
    }
    public PosSystem() {
        
        MySqlConnection mysqlconn = new MySqlConnection();
        TransactionInfo transaction = new TransactionInfo(mysqlconn);
        CustomerInfo customer = new CustomerInfo("John Doe", 50f); // loading app with customer John Doe with 50$
        customer.deleteCustomerRecords(mysqlconn); // optional
        customer.insertDataToTable(mysqlconn);  
        ProductInfo product = new ProductInfo();
            
        this.loadGUI();
      
        buyProduct.addActionListener(new ActionListener() {	
            @Override
            public void actionPerformed(ActionEvent e) {
                String txtPId = textProdId.getText().trim(), txtPQuan = textProdQuantity.getText().trim();
                if(txtPId.isEmpty() || txtPQuan.isEmpty()) {
                    appLog.append("ERROR: Missing statement value!\n");
                } else if(Integer.parseInt(txtPId) == 0 || Integer.parseInt(txtPQuan) == 0) {
                    appLog.append("ERROR: Invalid input values!\n");
                } else {
                    product.setId(Integer.parseInt(txtPId));
                    int buyQuantity = Integer.parseInt(txtPQuan);
                    product.getProductFromTextField(mysqlconn); 
                    //System.out.println(product.getId() + " " + product.getName() + " " + product.getPrice() + " " + product.getQuantity());
                    if(product.getQuantity() < buyQuantity || product.getQuantity() == 0) {
                        appLog.append("ERROR: Not enough products on stock!\n");
                    } else {
                        if(customer.getMoney() < (product.getPrice()*buyQuantity)) {
                            appLog.append("ERROR: Insufficient funds!\n");
                        } else {
                            product.buyQuantity(buyQuantity, mysqlconn);
                            customer.giveMoney(product.getPrice()*buyQuantity, mysqlconn);
                            transaction.addTransaction(mysqlconn, customer, product, buyQuantity, product.getPrice()*buyQuantity);
                            appLog.append("SUCCESS: Your transaction has completed succesfully!\n");
                            //System.out.println(": " + product.getPrice()*buyQuantity);
                        }
                    }
                }
                
            }
	});
          
        getProducts.addActionListener(new ActionListener() {	
            @Override
            public void actionPerformed(ActionEvent e) {
                product.printProductsList(appLog, mysqlconn);
            }
	});
    
        getMoneyBalance.addActionListener(new ActionListener() {	
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullOutput = "[CUSTOMER] | ID: " + customer.getId()  + " | Name: " + customer.getName() + " | Cash: " + customer.getMoney() + "$";
		appLog.append(fullOutput + "\n");
            }
	});
       
        getBill.addActionListener(new ActionListener() {	
            @Override
            public void actionPerformed(ActionEvent e) {
		transaction.printTransactionLog(appLog, mysqlconn, customer);
            }
	});   
    }
    
    public static void main(String[] args) throws SQLException {
        PosSystem mainApp = new PosSystem();
        mainApp.setVisible(true);
    }
    
}
