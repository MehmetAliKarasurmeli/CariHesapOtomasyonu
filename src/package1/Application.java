package package1;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Application extends javax.swing.JFrame {

    Database db = new Database();
    Validator vd = new Validator();
    ArrayList<String> catidlist = new ArrayList<>();
    ArrayList<String> pcatidlist = new ArrayList<>();
    int custExist = -1;
    String cid = "";
    String pid = "";
    String sid = "";
    String catid = "";
    String tableName = "";
    String username = UserLogin.userName;
    boolean sameUserHandle = false;

    public Application() {
        initComponents();
        readCustomersData("");
        readProduct();
        readCategories();
        readSales();
        readSellableProduct();
        readFiltersSales("2000-01-01", "2500-01-01", "");

//        cmbSaleDay.setSelectedItem(LocalDate.now().getDayOfMonth());
//        System.out.println(LocalDate.now().getDayOfMonth());
//        cmbSaleMonth.setSelectedItem(LocalDate.now().getMonthValue());
//        System.out.println(LocalDate.now().getMonthValue());
//        cmbFilterYear1.setSelectedItem(LocalDate.now().getYear());
//        System.out.println(LocalDate.now().getYear());
        lblWelcome.setText("HOSGELDINIZ SN." + username);
        btnSaleSave.setEnabled(false);
        txtSellProduct.setEnabled(false);
        txtSellStock.setEnabled(false);
        btnSingin.setEnabled(false);
        btnSaleUpdate.setEnabled(false);

    }

//CUSTOMERS READ,WRITE,DELETE,UPDATE OPERATIONS
    public void readCustomersData(String like) {
        String customersQuery = "select * from customers where (cName like '%" + like + "%') or (cSurname like '%" + like + "%') or (cPhone like '%" + like + "%')";
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("ID");
        dtm.addColumn("ISIM");
        dtm.addColumn("SOYISIM");
        dtm.addColumn("TELEFON");
        dtm.addColumn("ADRES");
        custExist = -1;
        try {
            ResultSet rs = db.connectSt().executeQuery(customersQuery);

            while (rs.next()) {
                dtm.addRow(new String[]{rs.getString("cId"), rs.getString("cName"), rs.getString("cSurname"), rs.getString("cPhone"), rs.getString("cAddress")});
                if (txtSellCustPhone.getText().trim().equals(rs.getString("cPhone") + "")) {
                    custExist = Integer.parseInt(rs.getString("cId"));
                }
            }

            tblCustomers.setModel(dtm);
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        } finally {
            db.close();
        }

    }

    public int controlAddingCustomer(String name, String surname, String phone) {
        int control = 1;

        txtCustomerName.setBackground(Color.white);
        txtCustomerSurname.setBackground(Color.white);
        txtCustomerPhone.setBackground(Color.white);

        if (name.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nAd bolumunu doldurmalisiniz.");
            txtCustomerName.requestFocus();
            txtCustomerName.setBackground(Color.red);
            control = -1;
        } else if (vd.nameValidator(name).equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nAd bolumunde sadece harf kullanilmalidir.");
            txtCustomerName.requestFocus();
            txtCustomerName.setBackground(Color.red);
            control = -1;
        } else if (surname.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSoyad bolumunu doldurmalisiniz.");
            txtCustomerSurname.requestFocus();
            txtCustomerSurname.setBackground(Color.red);
            control = -1;
        } else if (vd.surnameValidator(surname).equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSoyad bolumunde sadece harf kullanilmalidir.");
            txtCustomerSurname.requestFocus();
            txtCustomerSurname.setBackground(Color.red);
            control = -1;
        } else if (phone.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nTelefon bolumunu doldurmalisiniz.");
            txtCustomerPhone.requestFocus();
            txtCustomerPhone.setBackground(Color.red);
            control = -1;
        } else if (vd.phoneValidator(phone).equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nTelefon bolumunde sadece rakam kullanilmalidir.");
            txtCustomerPhone.requestFocus();
            txtCustomerPhone.setBackground(Color.red);
            control = -1;
        }

        return control;
    }

    public void addCustomer() {

        String name = txtCustomerName.getText().trim();
        String surname = txtCustomerSurname.getText().trim();
        String phone = txtCustomerPhone.getText().trim();
        String address = txtCustomerAddress.getText().trim();
        int control = controlAddingCustomer(name, surname, phone);

        if (control == 1) {

            int answer;
            String insertQuery = db.insertQuery("customers", new String[]{null, name, surname, phone, address});
            try {
                answer = db.connectSt().executeUpdate(insertQuery);
                if (answer > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Kayıt işlemi gerçekleşti.");
                    txtCustomerName.setText("");
                    txtCustomerSurname.setText("");
                    txtCustomerPhone.setText("");
                    txtCustomerAddress.setText("");
                }
            } catch (SQLException ex) {
                System.out.println("Data Insert Error" + ex);
                System.out.println("Data Insert Error" + ex);
                if (ex.toString().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    JOptionPane.showMessageDialog(rootPane, "Ayni telefon numarasi ile birden fazla kayit yapilamaz!");
                }
            } finally {
                db.close();
                readCustomersData("");
            }

        }
    }

    public void deleteCustomer() {
        if (cid.equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Lutfen bir satiri seciniz!");
        } else {
            int answer = JOptionPane.showConfirmDialog(rootPane, "Musteriyi silmek istediginize emin misiniz?");
            if (answer == 0) {
                try {
                    String deleteQuery = "delete from customers where cId = '" + cid + "' ";
                    int deleteResult = db.connectSt().executeUpdate(deleteQuery);
                    if (deleteResult > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Silme islemi basarili");
                        txtCustomerName.setText("");
                        txtCustomerSurname.setText("");
                        txtCustomerPhone.setText("");
                        txtCustomerAddress.setText("");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Silme Hatasi");
                    }
                } catch (SQLException e) {
                    System.err.println("Silme hatasi:" + e);
                } finally {
                    db.close();
                    readCustomersData("");
                }
            }
        }
    }

    public void updateCustomer() {
        String name = txtCustomerName.getText().trim();
        String surname = txtCustomerSurname.getText().trim();
        String phone = txtCustomerPhone.getText().trim();
        String address = txtCustomerAddress.getText().trim();
        int control = controlAddingCustomer(name, surname, phone);
        if (control == 1) {
            if (!cid.equals("")) {
                try {
                    String q = "update customers set cName = '" + name + "', "
                            + "cSurname = '" + surname + "', cPhone = '" + phone + "', "
                            + "cAddress = '" + address + "' where cId = '" + cid + "'";
                    int answer = db.connectSt().executeUpdate(q);
                    if (answer >= 0) {
                        readCustomersData("");
                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlendi");
                        txtCustomerName.setText("");
                        txtCustomerSurname.setText("");
                        txtCustomerPhone.setText("");
                        txtCustomerAddress.setText("");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlenemedi!");
                    }

                } catch (SQLException e) {
                    System.err.println("Hata: " + e);
                } finally {
                    db.close();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Kayit duzenlemek icin once bir satir seciniz!");
            }
        }
    }

//PRODUCTS READ,WRITE,DELETE,UPDATE OPERATIONS    
    public void readProduct() {
        String customersQuery = "select products.pId,products.catId,products.pName,categories.catName, products.pBuying,products.pSelling,products.pStock,products.pExplain from products join categories on categories.catId=products.catId";
        DefaultTableModel dtm = new DefaultTableModel();
        pcatidlist.clear();
        dtm.addColumn("ID");
        dtm.addColumn("URUN ISMI");
        dtm.addColumn("URUN KATEGORISI");
        dtm.addColumn("URUN ALIS FIYATI");
        dtm.addColumn("URUN SATIS FIYATI");
        dtm.addColumn("URUN STOK");
        dtm.addColumn("ACIKLAMA");

        try {
            ResultSet rs = db.connectSt().executeQuery(customersQuery);

            while (rs.next()) {
                dtm.addRow(new String[]{rs.getString("pId"), rs.getString("pName"), rs.getString("catName"), rs.getString("pBuying"), rs.getString("pSelling"), rs.getString("pStock"), rs.getString("pExplain")});
                pcatidlist.add(rs.getString("catId"));
            }
            tblProduct.setModel(dtm);
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        } finally {
            db.close();
        }
    }

    public int controlAddingProduct(String name, String buy, String sell, String stock) {
        int control = 1;

        txtProductName.setBackground(Color.white);
        txtProductBuy.setBackground(Color.white);
        txtProductsell.setBackground(Color.white);
        txtProductStock.setBackground(Color.white);

        if (name.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nUrun adi bolumunu doldurmalisiniz.");
            txtProductName.requestFocus();
            txtProductName.setBackground(Color.red);
            control = -1;
        } else if (vd.nameValidator(name).equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nUrun adi bolumunde sadece harf kullanilmalidir.");
            txtProductName.requestFocus();
            txtProductName.setBackground(Color.red);
            control = -1;
        } else if (buy.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nFiyat bolumunu doldurmalisiniz.");
            txtProductBuy.requestFocus();
            txtProductBuy.setBackground(Color.red);
            control = -1;
        } else if (vd.moneyValidator(buy) == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nFiyat bolumunde sadece rakam virgil ve nokta kullanilmalidir.");
            txtProductBuy.requestFocus();
            txtProductBuy.setBackground(Color.red);
            control = -1;
        } else if (sell.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nFiyat bolumunu doldurmalisiniz.");
            txtProductsell.requestFocus();
            txtProductsell.setBackground(Color.red);
            control = -1;
        } else if (vd.moneyValidator(sell) == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nFiyat bolumunde sadece rakam virgil ve nokta kullanilmalidir.");
            txtProductsell.requestFocus();
            txtProductsell.setBackground(Color.red);
            control = -1;
        } else if (stock.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nStock bolumunu doldurmalisiniz.");
            txtProductStock.requestFocus();
            txtProductStock.setBackground(Color.red);
            control = -1;
        } else if (vd.numberValidator(stock) == -1) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nStock bolumunde sadece rakam kullanilmalidir.");
            txtProductStock.requestFocus();
            txtProductStock.setBackground(Color.red);
            control = -1;
        }

        return control;
    }

    public void addProduct() {
        String name = txtProductName.getText().trim();
        String category = "" + (catidlist.get(cmbCategory.getSelectedIndex()));
        String buy = txtProductBuy.getText().trim();
        String sell = txtProductsell.getText().trim();
        String stock = txtProductStock.getText().trim();
        String explain = txtProductExplain.getText().trim();
        int control = controlAddingProduct(name, buy, sell, stock);

        if (control == 1) {
            buy = vd.moneyValidator(buy) + "";
            sell = vd.moneyValidator(sell) + "";
            int answer;
            String insertQuery = db.insertQuery("products", new String[]{null, name, category, buy, sell, stock, explain});
            try {
                answer = db.connectSt().executeUpdate(insertQuery);
                if (answer > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Kayıt işlemi gerçekleşti.");
                    txtProductName.setText("");
                    txtProductBuy.setText("");
                    txtProductsell.setText("");
                    txtProductStock.setText("");
                    txtProductExplain.setText("");
                }
            } catch (SQLException ex) {
                System.out.println("Data Insert Error" + ex);
                if (ex.toString().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    JOptionPane.showMessageDialog(rootPane, "Ayni urunden birden fazla kayit yapilamaz!");
                }
            } finally {
                db.close();
            }
        }
    }

    public void deleteProduct() {
        if (pid.equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Lutfen bir satiri seciniz!");
        } else {
            int answer = JOptionPane.showConfirmDialog(rootPane, "Urunu silmek istediginize emin misiniz?");
            if (answer == 0) {
                try {
                    String deleteQuery = "delete from products where pId = '" + pid + "' ";
                    int deleteResult = db.connectSt().executeUpdate(deleteQuery);
                    if (deleteResult > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Silme islemi basarili");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Silme Hatasi");
                    }
                } catch (SQLException e) {
                    System.err.println("Silme hatasi:" + e);
                } finally {
                    db.close();
                    readProduct();
                }
            }
        }
    }

    public void updateProduct() {
        String name = txtProductName.getText().trim();
        String category = "" + (catidlist.get(cmbCategory.getSelectedIndex()));
        String buy = txtProductBuy.getText().trim();
        String sell = txtProductsell.getText().trim();
        String stock = txtProductStock.getText().trim();
        String explain = txtProductExplain.getText().trim();
        int control = controlAddingProduct(name, buy, sell, stock);

        if (control == 1) {
            buy = vd.moneyValidator(buy) + "";
            sell = vd.moneyValidator(sell) + "";
            if (!pid.equals("")) {
                try {
                    String q = "update products set pName = '" + name + "', "
                            + "catId = '" + category + "', pBuying = '" + buy + "', "
                            + "pSelling = '" + sell + "', pStock = '" + stock + "', "
                            + "pExplain = '" + explain + "' where pId = '" + pid + "'";
                    int answer = db.connectSt().executeUpdate(q);
                    if (answer >= 0) {

                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlendi");
                        txtProductName.setText("");
                        txtProductBuy.setText("");
                        txtProductsell.setText("");
                        txtProductStock.setText("");
                        txtProductExplain.setText("");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlenemedi!");
                    }

                } catch (SQLException e) {
                    System.err.println("Hata: " + e);
                } finally {
                    db.close();
                    readProduct();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Kayit duzenlemek icin once bir satir seciniz!");
            }
        }
    }

//CATEGORIES READ,WRITE,DELETE,UPDATE OPERATIONS
    public void readCategories() {
        String customersQuery = "select * from categories";
        DefaultTableModel dtm = new DefaultTableModel();
        DefaultComboBoxModel dcbmCat = new DefaultComboBoxModel();
        DefaultComboBoxModel dcbmSaleCat = new DefaultComboBoxModel();
        catidlist.clear();
        dtm.addColumn("ID");
        dtm.addColumn("ADI");
        dtm.addColumn("ACIKLAMA");

        try {
            ResultSet rs = db.connectSt().executeQuery(customersQuery);
            dcbmSaleCat.addElement("TÜMÜ");
            while (rs.next()) {
                dtm.addRow(new String[]{rs.getString("catId"), rs.getString("catName"), rs.getString("catExplain")});
                dcbmCat.addElement(rs.getString("catName"));
                dcbmSaleCat.addElement(rs.getString("catName"));
                catidlist.add(rs.getString("catId"));
            }
            System.out.println("!!!!!!!!!!!!!!!!!" + catidlist);
            cmbCategory.setModel(dcbmCat);
            cmbSalesCategory.setModel(dcbmSaleCat);
            tblCategories.setModel(dtm);
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        } finally {
            db.close();
        }

    }

    public int controlAddingCategory(String name) {
        int control = 1;
        txtCategoryName.setBackground(Color.white);

        if (name.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nKategori adi bolumunu doldurmalisiniz.");
            txtCategoryName.requestFocus();
            txtCategoryName.setBackground(Color.red);
            control = -1;
        } else if (vd.nameValidator(name).equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nKategori adi bolumunde sadece harf kullanilmalidir.");
            txtCategoryName.requestFocus();
            txtCategoryName.setBackground(Color.red);
            control = -1;
        }

        return control;
    }

    public void addCategory() {
        String categoryName = txtCategoryName.getText().trim();
        String categoryExplain = txtCategoryExplain.getText().trim();
        int control = controlAddingCategory(categoryName);
        if (control == 1) {

            int answer;
            String insertQuery = db.insertQuery("categories", new String[]{null, categoryName, categoryExplain});
            try {
                answer = db.connectSt().executeUpdate(insertQuery);
                if (answer > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Kayıt işlemi gerçekleşti.");
                    txtCategoryName.setText("");
                    txtCategoryExplain.setText("");
                }
            } catch (SQLException ex) {
                System.out.println("Data Insert Error" + ex);
            } finally {
                db.close();
            }
        }
    }

    public void deleteCategory() {
        if (catid.equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Lutfen bir satiri seciniz!");
        } else if (pcatidlist.contains(catid)) {
            JOptionPane.showMessageDialog(rootPane, "Silme islemi yapabilmek icin o kategoride urununuz olmamasi gerekir!");
        } else {
            int answer = JOptionPane.showConfirmDialog(rootPane, "Kategoriyi silmek istediginize emin misiniz?");
            if (answer == 0) {
                try {
                    String deleteQuery = "delete from categories where catId = '" + catid + "' ";
                    int deleteResult = db.connectSt().executeUpdate(deleteQuery);
                    if (deleteResult > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Silme islemi basarili");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Silme Hatasi");
                    }
                } catch (SQLException e) {
                    System.err.println("Silme hatasi:" + e);
                } finally {
                    db.close();
                    readCategories();
                }
            }
        }
    }

    public void updateCategory() {
        String categoryName = txtCategoryName.getText().trim();
        String categoryExplain = txtCategoryExplain.getText().trim();
        int control = controlAddingCategory(categoryName);
        if (control == 1) {
            if (!catid.equals("")) {
                try {
                    String q = "update categories set catName = '" + categoryName + "', "
                            + "catExplain = '" + categoryExplain + "' where catId = '" + catid + "'";
                    int answer = db.connectSt().executeUpdate(q);
                    if (answer >= 0) {
                        readCategories();
                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlendi");
                        txtCategoryName.setText("");
                        txtCategoryExplain.setText("");

                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlenemedi!");
                    }

                } catch (SQLException e) {
                    System.err.println("Hata: " + e);
                } finally {
                    db.close();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Kayit duzenlemek icin once bir satir seciniz!");
            }
        }
    }

//USERS READ,WRITE,UPDATE OPERATIONS
    public int controlAddingUser() {
        int control = 1;
        String userName = txtNewUserName.getText().trim();
        String password = txtNewPassword.getText().trim();
        txtNewUserName.setBackground(Color.white);
        txtNewPassword.setBackground(Color.white);

        if (userName.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nKullanici adi bolumunu doldurmalisiniz.");
            txtNewUserName.requestFocus();
            txtNewUserName.setBackground(Color.red);
            control = -1;
        } else if (password.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSifre bolumunu doldurmalisiniz.");
            txtNewPassword.requestFocus();
            txtNewPassword.setBackground(Color.red);
            control = -1;
        } else {
            String usersQuery = " select * from users where uUserName='" + userName + "'";
            try {
                ResultSet rsUsers = db.connectSt().executeQuery(usersQuery);
                if (rsUsers.next()) {
                    control = -1;
                    JOptionPane.showMessageDialog(rootPane, "Kayit yapilamadi!\nAyni kullanici adi ile bir kayit daha yapilamaz!");
                }
                db.close();
            } catch (SQLException ex) {
                System.out.println("Data Access Error:" + ex);
            }
        }
        return control;

    }

    public void addUser() {
        String name = txtNewUserName.getText().trim();
        String password = txtNewPassword.getText().trim();
        int control = controlAddingUser();
        if (control == 1) {
            int answer;
            String insertQuery = db.insertQuery("users", new String[]{null, name, password});
            try {
                answer = db.connectSt().executeUpdate(insertQuery);
                if (answer > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Kayıt işlemi gerçekleşti.");
                    txtNewUserName.setText("");
                    txtNewPassword.setText("");
                    txtUserName.setText("");
                    txtPassword.setText("");
                    btnSingin.setEnabled(false);
                }
            } catch (SQLException ex) {
                System.out.println("Data Insert Error" + ex);
            } finally {
                db.close();
            }
        }
    }

    public int userLogin() {
        String userName = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();
        int answer = -1;
        String usersQuery = " select * from users where uUserName='" + userName + "' and uPassword='" + password + "' ";
        try {
            ResultSet rsUsers = db.connectSt().executeQuery(usersQuery);
            if (rsUsers.next()) {
                answer = 0;
                btnSingin.setEnabled(true);

            }
            db.close();
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        }
        return answer;

    }

    public void updateUserPassword() {
        String oldPassword = txtOldPassword.getText().trim();
        String newPassword = txtUpdatePassword.getText().trim();
        String newPassword1 = txtUpdatePassword1.getText().trim();
        if (newPassword.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Yeni sifre cok kisa! ");
        } else {
            if (oldPassword.equals(UserLogin.passWord)) {
                if (newPassword.equals(newPassword1)) {
                    try {
                        String q = "update users set uPassword = '" + newPassword + "' where uUserName = '" + username + "'";
                        int answer = db.connectSt().executeUpdate(q);
                        if (answer >= 0) {
                            readCategories();
                            JOptionPane.showMessageDialog(rootPane, "Sifre basari ile degistirildi");
                            txtOldPassword.setText("");
                            txtUpdatePassword.setText("");
                            txtUpdatePassword1.setText("");

                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Sifre duzenlenemedi!");
                        }

                    } catch (SQLException e) {
                        System.err.println("Hata: " + e);
                    } finally {
                        db.close();
                    }

                } else {
                    JOptionPane.showMessageDialog(rootPane, "sifre tekrarini yanlis yazdiniz!");
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "eski sifre hatali");
            }
        }
    }

//SALES READ,WRITE,DELETE,UPDATE OPERATIONS
    public void readSellableProduct() {
        String sellableProductQuery;
        if (cmbSalesCategory.getSelectedItem().equals("TÜMÜ")) {
            sellableProductQuery = "select * from products join categories on categories.catId=products.catId ";
        } else {
            String selection = catidlist.get(cmbSalesCategory.getSelectedIndex() - 1);
            sellableProductQuery = "select * from products join categories on categories.catId=products.catId where products.catId=" + selection + "";
        }

        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("ID");
        dtm.addColumn("URUN ISMI");
        dtm.addColumn("URUN ALIS FIYATI");
        dtm.addColumn("URUN SATIS FIYATI");
        dtm.addColumn("URUN STOK");

        try {
            ResultSet rs = db.connectSt().executeQuery(sellableProductQuery);

            while (rs.next()) {
                if (Integer.parseInt(rs.getString("pStock")) > 0) {
                    dtm.addRow(new String[]{rs.getString("pId"), rs.getString("pName"), rs.getString("pBuying"), rs.getString("pSelling"), rs.getString("pStock")});
                }
            }
            tblSellable.setModel(dtm);
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        } finally {
            db.close();
        }
    }

    public void readSales() {
        String salesQuery = "select * from sales join products on products.pId=sales.pId  "
                + "join customers on customers.cId= sales.cId order by sID desc";
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("ID");
        dtm.addColumn("MUSTERI TEL");
        dtm.addColumn("URUN ADI");
        dtm.addColumn("SATILAN FIYAT");
        dtm.addColumn("SATILAN ADET");
        dtm.addColumn("SATIS TARIHI");
        dtm.addColumn("ACIKLAMA");

        try {
            ResultSet rs = db.connectSt().executeQuery(salesQuery);
            while (rs.next()) {
                dtm.addRow(new String[]{rs.getString("sId"), rs.getString("cPhone"), rs.getString("pName"), rs.getString("sSelling"), rs.getString("sAmount"), rs.getString("sDate"), rs.getString("sExplain")});

            }
            tblSales.setModel(dtm);
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        } finally {
            db.close();
        }

    }

    public int controlAddingSales(String custPhone, String amount, String prices, String date) {
        int control = 1;

        txtSellCustPhone.setBackground(Color.white);
        txtSellAmount.setBackground(Color.white);
        txtSellPrices.setBackground(Color.white);

        if (custPhone.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nMusteri tel bolumunu doldurmalisiniz.");
            txtSellCustPhone.requestFocus();
            txtSellCustPhone.setBackground(Color.red);
            control = -1;
        } else if (vd.phoneValidator(custPhone).equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nMusteri tel bolumunde sadece rakam kullanilmalidir.");
            txtSellCustPhone.requestFocus();
            txtSellCustPhone.setBackground(Color.red);
            control = -1;
        } else if (amount.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSatis adeti bolumunu doldurmalisiniz.");
            txtSellAmount.requestFocus();
            txtSellAmount.setBackground(Color.red);
            control = -1;
        } else if (vd.numberValidator(amount) == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSatis adeti bolumunde sadece rakam kullanilmalidir.");
            txtSellAmount.requestFocus();
            txtSellAmount.setBackground(Color.red);
            control = -1;
        } else if (prices.length() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSatis fiyat bolumunu doldurmalisiniz.");
            txtSellPrices.requestFocus();
            txtSellPrices.setBackground(Color.red);
            control = -1;
        } else if (vd.moneyValidator(prices) == 0) {
            JOptionPane.showMessageDialog(rootPane, "Kayit islemi basarisiz!\nSatis fiyati bolumunde sadece rakam virgil ve nokta kullanilmalidir.");
            txtSellPrices.requestFocus();
            txtSellPrices.setBackground(Color.red);
            control = -1;
        }

        return control;
    }

    public void addSales() {
        int row = tblSellable.getSelectedRow();
        String productId = tblSellable.getValueAt(row, 0) + "";
        String amount = txtSellAmount.getText().trim();
        String customerId = custExist + "";
        String prices = txtSellPrices.getText().trim();
        String date = cmbSaleYear.getSelectedItem() + "-" + cmbSaleMonth.getSelectedItem() + "-" + cmbSaleDay.getSelectedItem();
        String explain = txtSellExplain.getText().trim();

        String custPhone = txtSellCustPhone.getText();
        int control = controlAddingSales(custPhone, amount, prices, date);

        if (control == 1) {

            int answer;
            String insertQuery = db.insertQuery("sales", new String[]{null, productId, amount, customerId, prices, date, explain});
            try {
                answer = db.connectSt().executeUpdate(insertQuery);
                if (answer > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Kayıt işlemi gerçekleşti.");
                    updateStockAddSales(amount, productId);
                    txtSellCustPhone.setText("");
                    txtSellProduct.setText("");
                    txtSellExplain.setText("");
                    txtSellPrices.setText("");
                    txtSellStock.setText("");
                    txtSellAmount.setText("");
                }
            } catch (SQLException ex) {
                System.out.println("Data Insert Error" + ex);
            } finally {
                db.close();
                readSales();
            }
        }
    }

    public void updateStockAddSales(String upAmount, String upProductId) {

        int upAmount1 = Integer.parseInt(upAmount);
        int upProductId1 = Integer.parseInt(upProductId);

        String queryStock = "update products set pStock=(select pStock from "
                + "products where pId='" + upProductId1 + "' ) - " + upAmount1 + " "
                + "where pId='" + upProductId1 + "'";

        int answer;
        try {
            answer = db.connectSt().executeUpdate(queryStock);
            if (answer >= 0) {
                System.out.println("Stok duzenlendi.");
            }
        } catch (SQLException ex) {
            System.err.println("Stok yenileme hatasi:" + ex);
        } finally {
            db.close();
            readSellableProduct();
            readProduct();
        }

    }

    public void updateStockUpdateSales(String upAmount, String upProductId) {

        int upAmount1 = Integer.parseInt(upAmount);
        int upProductId1 = Integer.parseInt(upProductId);

        String queryStock = "update products set pStock=(select pStock from "
                + "products where pId='" + upProductId1 + "' )+ " + tblSales.getValueAt(tblSales.getSelectedRow(), 4) + " - " + upAmount1 + " "
                + "where pId='" + upProductId1 + "'";

        int answer;
        try {
            answer = db.connectSt().executeUpdate(queryStock);
            if (answer >= 0) {
                System.out.println("Stok duzenlendi.");
            }
        } catch (SQLException ex) {
            System.err.println("Stok yenileme hatasi:" + ex);
        } finally {
            db.close();
            readSellableProduct();
            readProduct();
        }

    }

    public void deleteSales() {
        if (sid.equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Lutfen bir satiri seciniz!");
        } else {
            int answer = JOptionPane.showConfirmDialog(rootPane, "Satisi silmek istediginize emin misiniz?");
            if (answer == 0) {
                try {
                    String deleteQuery = "delete from sales where sId = '" + sid + "' ";
                    int deleteResult = db.connectSt().executeUpdate(deleteQuery);
                    if (deleteResult > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Silme islemi basarili");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Silme Hatasi");
                    }
                } catch (SQLException e) {
                    System.err.println("Silme hatasi:" + e);
                } finally {
                    db.close();
                    readSales();
                }
            }
        }
    }

    public void updateSales() {
        String amount = txtSellAmount.getText().trim();
        String prices = txtSellPrices.getText().trim();
        String date = cmbSaleDay.getSelectedItem() + "." + cmbSaleMonth.getSelectedItem() + "." + cmbSaleYear.getSelectedItem();
        String explain = txtSellExplain.getText().trim();
        String custPhone = txtSellCustPhone.getText();
        int control = controlAddingSales(custPhone, amount, prices, date);
        String upProductName = txtSellProduct.getText();
        String query = "select pId from products where pName='" + upProductName + "'";
        String upProductId = "";
        try {
            upProductId = db.connectSt().executeQuery(query).getString("pId");
        } catch (SQLException e) {
            System.err.println("Hata: " + e);
        } finally {
            db.close();
        }

        if (control == 1) {
            if (!sid.equals("")) {
                try {
                    String q = "update sales set sAmount = '" + amount + "', "
                            + "sSelling = '" + prices + "', sDate = '" + date + "', "
                            + "sExplain = '" + explain + "' where sId = '" + sid + "'";
                    int answer = db.connectSt().executeUpdate(q);
                    if (answer >= 0) {

                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlendi");
                        txtSellCustPhone.setText("");
                        txtSellProduct.setText("");
                        txtSellExplain.setText("");
                        txtSellPrices.setText("");
                        txtSellStock.setText("");
                        txtSellAmount.setText("");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Kayit duzenlenemedi!");
                    }

                } catch (SQLException e) {
                    System.err.println("Hata: " + e);
                } finally {
                    db.close();
                    updateStockUpdateSales(amount, upProductId);
                    readSellableProduct();
                    readSales();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Kayit duzenlemek icin once bir satir seciniz!");
            }
        }
    }

//Filter Select Method
    int totalSaleAmount;
    double totalSalePrices;
    double totalBuyPrices;
    double totalProfit;
    int mostSalesProduct;

    public void readFiltersSales(String date1, String date2, String search) {
        totalSaleAmount = 0;
        totalSalePrices = 0.0;
        totalBuyPrices = 0.0;
        totalProfit = 0.0;
        mostSalesProduct = 0;
        String salesQuery = "select * from sales join products on products.pId=sales.pId  join customers on customers.cId= sales.cId where (sDate between '" + date1 + "'  and '" + date2 + "') and ( cPhone like '%" + search + "%' or  pName like '%" + search + "%'  or  sSelling like '%" + search + "%'  or  sAmount like '%" + search + "%' or  sExplain like '%" + search + "%'  )";
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("ID");
        dtm.addColumn("MUSTERI TEL");
        dtm.addColumn("URUN ADI");
        dtm.addColumn("SATILAN FIYAT");
        dtm.addColumn("SATILAN ADET");
        dtm.addColumn("SATIS TARIHI");
        dtm.addColumn("ACIKLAMA");

        try {
            ResultSet rs = db.connectSt().executeQuery(salesQuery);
            while (rs.next()) {
                dtm.addRow(new String[]{rs.getString("sId"), rs.getString("cPhone"), rs.getString("pName"), rs.getString("sSelling"), rs.getString("sAmount"), rs.getString("sDate"), rs.getString("sExplain")});

                totalSaleAmount += Integer.parseInt(rs.getString("sAmount"));
                totalBuyPrices += Double.parseDouble(rs.getString("pBuying")) * Integer.parseInt(rs.getString("sAmount"));
                totalSalePrices += Double.parseDouble(rs.getString("sSelling")) * Integer.parseInt(rs.getString("sAmount"));

            }
            totalProfit = totalSalePrices - totalBuyPrices;
            tblFilter.setModel(dtm);
        } catch (SQLException ex) {
            System.out.println("Data Access Error:" + ex);
        } finally {
            db.close();
            lblTotalSaleAmount.setText(totalSaleAmount + "");
            lblTotalProfit.setText(totalProfit + "");
            lblTotalBuyPrices.setText(totalBuyPrices + "");
            lblTotalSalePrices.setText(totalSalePrices + "");
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlCustomer = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlSave = new javax.swing.JPanel();
        txtCustomerName = new javax.swing.JTextField();
        txtCustomerSurname = new javax.swing.JTextField();
        txtCustomerPhone = new javax.swing.JTextField();
        txtCustomerAddress = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        pnlCustomerTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomers = new javax.swing.JTable();
        lblWelcome = new javax.swing.JLabel();
        pnlSales = new javax.swing.JPanel();
        pnlSalesSave = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbSalesCategory = new javax.swing.JComboBox<>();
        pnlSalesTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSales = new javax.swing.JTable();
        pnlSellableProducts = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSellable = new javax.swing.JTable();
        pnlSaleSave = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtSellAmount = new javax.swing.JTextField();
        txtSellPrices = new javax.swing.JTextField();
        btnSaleSave = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSellProduct = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtSellStock = new javax.swing.JTextField();
        txtSellCustPhone = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtSellExplain = new javax.swing.JTextField();
        btnSaleUpdate = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        cmbSaleDay = new javax.swing.JComboBox<>();
        cmbSaleMonth = new javax.swing.JComboBox<>();
        cmbSaleYear = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        pnlProductTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        pnlProductSave = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        txtProductBuy = new javax.swing.JTextField();
        txtProductsell = new javax.swing.JTextField();
        txtProductStock = new javax.swing.JTextField();
        txtProductExplain = new javax.swing.JTextField();
        btnProductSave = new javax.swing.JButton();
        btnDeleteProduct = new javax.swing.JButton();
        btnUpdateProduct = new javax.swing.JButton();
        pnlCategories = new javax.swing.JPanel();
        pnlTableCategories = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCategories = new javax.swing.JTable();
        pnlCategoryAdd = new javax.swing.JPanel();
        lblCatName = new javax.swing.JLabel();
        lblCatName1 = new javax.swing.JLabel();
        txtCategoryName = new javax.swing.JTextField();
        txtCategoryExplain = new javax.swing.JTextField();
        btnCategorySave = new javax.swing.JButton();
        btnCategoryDelete = new javax.swing.JButton();
        btnCategoryUpdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        pnlFilter = new javax.swing.JPanel();
        cmbFilterDay1 = new javax.swing.JComboBox<>();
        cmbFilterMonth1 = new javax.swing.JComboBox<>();
        cmbFilterYear1 = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        cmbFilterDay2 = new javax.swing.JComboBox<>();
        cmbFilterMonth2 = new javax.swing.JComboBox<>();
        cmbFilterYear2 = new javax.swing.JComboBox<>();
        btnFilterShow = new javax.swing.JButton();
        txtFilterSearch = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        pnlReportTabels = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblFilter = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        lblTotalSaleAmount = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        lblTotalBuyPrices = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        lblTotalProfit = new javax.swing.JLabel();
        lblTotalSalePrices = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        pnlUserSetting = new javax.swing.JPanel();
        pnlUserSing = new javax.swing.JPanel();
        txtNewUserName = new javax.swing.JTextField();
        txtNewPassword = new javax.swing.JTextField();
        btnSingin = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        pnlUserlogin = new javax.swing.JPanel();
        txtUserName = new javax.swing.JTextField();
        txtPassword = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        pnlPasswordUpdate = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtUpdatePassword1 = new javax.swing.JTextField();
        txtUpdatePassword = new javax.swing.JTextField();
        txtOldPassword = new javax.swing.JTextField();
        btnUpdatePassword = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cari Hesap Otomasyonu");
        setResizable(false);
        setSize(new java.awt.Dimension(791, 539));

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(805, 590));
        jTabbedPane1.setRequestFocusEnabled(false);

        btnSearch.setText("Ara");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        pnlSave.setBorder(javax.swing.BorderFactory.createTitledBorder("Müşteri Kayıt Formu"));

        txtCustomerName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtCustomerSurname.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtCustomerPhone.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtCustomerAddress.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Adı");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Soyadı");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Telefon");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Adres");

        btnSave.setText("KAYDET");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setText("SIL");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setText("DUZENLE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSaveLayout = new javax.swing.GroupLayout(pnlSave);
        pnlSave.setLayout(pnlSaveLayout);
        pnlSaveLayout.setHorizontalGroup(
            pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSaveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSaveLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8))
                    .addGroup(pnlSaveLayout.createSequentialGroup()
                        .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel5)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtCustomerPhone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                            .addComponent(txtCustomerSurname, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCustomerName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCustomerAddress))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnlSaveLayout.setVerticalGroup(
            pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSaveLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCustomerSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCustomerPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCustomerAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdate))
                .addGap(23, 23, 23))
        );

        pnlCustomerTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Müsteriler Tablosu"));

        tblCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCustomers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomers);

        javax.swing.GroupLayout pnlCustomerTableLayout = new javax.swing.GroupLayout(pnlCustomerTable);
        pnlCustomerTable.setLayout(pnlCustomerTableLayout);
        pnlCustomerTableLayout.setHorizontalGroup(
            pnlCustomerTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pnlCustomerTableLayout.setVerticalGroup(
            pnlCustomerTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomerTableLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlCustomerLayout = new javax.swing.GroupLayout(pnlCustomer);
        pnlCustomer.setLayout(pnlCustomerLayout);
        pnlCustomerLayout.setHorizontalGroup(
            pnlCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomerLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCustomerLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(pnlCustomerTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(32, Short.MAX_VALUE))
                    .addGroup(pnlCustomerLayout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblWelcome)
                        .addGap(127, 127, 127))))
        );
        pnlCustomerLayout.setVerticalGroup(
            pnlCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomerLayout.createSequentialGroup()
                .addGroup(pnlCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCustomerLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(pnlCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCustomerLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSearch)))
                    .addComponent(lblWelcome))
                .addGap(6, 6, 6)
                .addGroup(pnlCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCustomerTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("MUSTERI YONETIMI", pnlCustomer);

        pnlSalesSave.setBorder(javax.swing.BorderFactory.createTitledBorder("Satis Kayit Formu"));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Satış Yapılacak Kategoriyi Seçiniz");

        cmbSalesCategory.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSalesCategoryİtemStateChanged(evt);
            }
        });

        pnlSalesTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Satis Listesi"));

        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSalesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblSales);

        javax.swing.GroupLayout pnlSalesTableLayout = new javax.swing.GroupLayout(pnlSalesTable);
        pnlSalesTable.setLayout(pnlSalesTableLayout);
        pnlSalesTableLayout.setHorizontalGroup(
            pnlSalesTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalesTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlSalesTableLayout.setVerticalGroup(
            pnlSalesTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalesTableLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlSellableProducts.setBorder(javax.swing.BorderFactory.createTitledBorder("Urun Listesi"));

        tblSellable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSellable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSellableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblSellable);

        javax.swing.GroupLayout pnlSellableProductsLayout = new javax.swing.GroupLayout(pnlSellableProducts);
        pnlSellableProducts.setLayout(pnlSellableProductsLayout);
        pnlSellableProductsLayout.setHorizontalGroup(
            pnlSellableProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSellableProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlSellableProductsLayout.setVerticalGroup(
            pnlSellableProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSellableProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlSaleSave.setBorder(javax.swing.BorderFactory.createTitledBorder("Satış Formu"));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Musteri Tel.");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Satis Fiyati");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Satis adeti");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Aciklama");

        btnSaleSave.setText("SATIS EKLE");
        btnSaleSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaleSaveActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Urun Adi");

        jLabel25.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel25.setText("Stok");

        jLabel26.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel26.setText("Tarih");

        btnSaleUpdate.setText("SATIS DUZENLE");
        btnSaleUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaleUpdateActionPerformed(evt);
            }
        });

        jButton2.setText("SATIS SIL");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        cmbSaleDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        cmbSaleMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        cmbSaleYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035" }));

        javax.swing.GroupLayout pnlSaleSaveLayout = new javax.swing.GroupLayout(pnlSaleSave);
        pnlSaleSave.setLayout(pnlSaleSaveLayout);
        pnlSaleSaveLayout.setHorizontalGroup(
            pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSaleSaveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSaleSaveLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSaleUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(btnSaleSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlSaleSaveLayout.createSequentialGroup()
                        .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING))
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLabel10)
                            .addComponent(jLabel25)
                            .addComponent(jLabel7)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSellStock, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSellAmount)
                            .addComponent(txtSellCustPhone, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSellProduct)
                            .addComponent(txtSellPrices, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSellExplain)
                            .addGroup(pnlSaleSaveLayout.createSequentialGroup()
                                .addComponent(cmbSaleYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbSaleMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbSaleDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        pnlSaleSaveLayout.setVerticalGroup(
            pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSaleSaveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSellCustPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSellProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtSellStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSellAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSellPrices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(cmbSaleYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSaleMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSaleDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(pnlSaleSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtSellExplain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaleSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaleUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlSalesSaveLayout = new javax.swing.GroupLayout(pnlSalesSave);
        pnlSalesSave.setLayout(pnlSalesSaveLayout);
        pnlSalesSaveLayout.setHorizontalGroup(
            pnlSalesSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalesSaveLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlSalesSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSalesSaveLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSalesCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 336, Short.MAX_VALUE))
                    .addGroup(pnlSalesSaveLayout.createSequentialGroup()
                        .addGroup(pnlSalesSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlSellableProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlSalesTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSaleSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlSalesSaveLayout.setVerticalGroup(
            pnlSalesSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalesSaveLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlSalesSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbSalesCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSalesSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSalesSaveLayout.createSequentialGroup()
                        .addComponent(pnlSellableProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSalesTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlSaleSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout pnlSalesLayout = new javax.swing.GroupLayout(pnlSales);
        pnlSales.setLayout(pnlSalesLayout);
        pnlSalesLayout.setHorizontalGroup(
            pnlSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSalesSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlSalesLayout.setVerticalGroup(
            pnlSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSalesSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("SATIS YONETIMI", pnlSales);

        pnlProductTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Urun Tablosu"));

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblProduct);

        javax.swing.GroupLayout pnlProductTableLayout = new javax.swing.GroupLayout(pnlProductTable);
        pnlProductTable.setLayout(pnlProductTableLayout);
        pnlProductTableLayout.setHorizontalGroup(
            pnlProductTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        pnlProductTableLayout.setVerticalGroup(
            pnlProductTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlProductSave.setBorder(javax.swing.BorderFactory.createTitledBorder("Urun Kayit Formu"));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Urun Ismi");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Kategori");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("Alis Fiyati");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText("Satis Fiyati");

        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText("Miktar");

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setText("Aciklama");

        txtProductName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtProductBuy.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtProductsell.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtProductStock.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtProductExplain.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        btnProductSave.setText("KAYDET");
        btnProductSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductSaveActionPerformed(evt);
            }
        });

        btnDeleteProduct.setText("SIL");
        btnDeleteProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteProductActionPerformed(evt);
            }
        });

        btnUpdateProduct.setText("DUZENLE");
        btnUpdateProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateProductActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlProductSaveLayout = new javax.swing.GroupLayout(pnlProductSave);
        pnlProductSave.setLayout(pnlProductSaveLayout);
        pnlProductSaveLayout.setHorizontalGroup(
            pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductSaveLayout.createSequentialGroup()
                .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlProductSaveLayout.createSequentialGroup()
                        .addComponent(btnUpdateProduct)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteProduct)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnProductSave))
                    .addGroup(pnlProductSaveLayout.createSequentialGroup()
                        .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(pnlProductSaveLayout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14))
                            .addGroup(pnlProductSaveLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(37, 37, 37)
                                .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(jLabel13)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProductBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductsell, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProductStock, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductExplain, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlProductSaveLayout.setVerticalGroup(
            pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductSaveLayout.createSequentialGroup()
                .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(txtProductBuy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtProductStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtProductsell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtProductExplain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProductSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProductSave)
                    .addComponent(btnDeleteProduct)
                    .addComponent(btnUpdateProduct))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlProductTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlProductSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(143, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlProductTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProductSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("URUNLER YONETIMI", jPanel1);

        pnlTableCategories.setBorder(javax.swing.BorderFactory.createTitledBorder("Kategori Tablosu"));

        tblCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCategories.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCategoriesMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblCategories);

        javax.swing.GroupLayout pnlTableCategoriesLayout = new javax.swing.GroupLayout(pnlTableCategories);
        pnlTableCategories.setLayout(pnlTableCategoriesLayout);
        pnlTableCategoriesLayout.setHorizontalGroup(
            pnlTableCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableCategoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTableCategoriesLayout.setVerticalGroup(
            pnlTableCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTableCategoriesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(312, 312, 312))
        );

        pnlCategoryAdd.setBorder(javax.swing.BorderFactory.createTitledBorder("Kategori Ekle"));

        lblCatName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblCatName.setText("Kategori Adi");

        lblCatName1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblCatName1.setText("Aciklama");

        btnCategorySave.setText("EKLE");
        btnCategorySave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategorySaveActionPerformed(evt);
            }
        });

        btnCategoryDelete.setText("SIL");
        btnCategoryDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoryDeleteActionPerformed(evt);
            }
        });

        btnCategoryUpdate.setText("DUZENLE");
        btnCategoryUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoryUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlCategoryAddLayout = new javax.swing.GroupLayout(pnlCategoryAdd);
        pnlCategoryAdd.setLayout(pnlCategoryAddLayout);
        pnlCategoryAddLayout.setHorizontalGroup(
            pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCategoryAddLayout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addGroup(pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlCategoryAddLayout.createSequentialGroup()
                        .addGroup(pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCatName)
                            .addComponent(lblCatName1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCategoryExplain, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txtCategoryName)))
                    .addGroup(pnlCategoryAddLayout.createSequentialGroup()
                        .addComponent(btnCategoryDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCategorySave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCategoryUpdate)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCategoryAddLayout.setVerticalGroup(
            pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCategoryAddLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCatName)
                    .addComponent(txtCategoryName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCatName1)
                    .addComponent(txtCategoryExplain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCategoryAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCategorySave)
                    .addComponent(btnCategoryDelete)
                    .addComponent(btnCategoryUpdate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlCategoriesLayout = new javax.swing.GroupLayout(pnlCategories);
        pnlCategories.setLayout(pnlCategoriesLayout);
        pnlCategoriesLayout.setHorizontalGroup(
            pnlCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCategoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlCategoryAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTableCategories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(274, Short.MAX_VALUE))
        );
        pnlCategoriesLayout.setVerticalGroup(
            pnlCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCategoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTableCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCategoryAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(182, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("KATEGORI YONETIMI", pnlCategories);

        pnlFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtrele"));

        cmbFilterDay1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        cmbFilterMonth1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        cmbFilterYear1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035" }));

        jLabel27.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel27.setText("  ILE");

        cmbFilterDay2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        cmbFilterMonth2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        cmbFilterYear2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035" }));

        btnFilterShow.setText("FILTRELE");
        btnFilterShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterShowActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel28.setText("Baslangis Tarihi");

        jLabel29.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel29.setText("Bitis Tarihi");

        jLabel30.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel30.setText("Arama Kelimesi");

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFilterLayout.createSequentialGroup()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbFilterYear1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFilterMonth1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFilterDay1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(cmbFilterYear2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFilterMonth2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel29)))
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFilterDay2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(txtFilterSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFilterShow))
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel30)))
                .addGap(45, 45, 45))
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilterSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbFilterYear1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbFilterYear2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbFilterMonth1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbFilterDay1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbFilterMonth2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbFilterDay2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFilterShow)))
                .addGap(49, 49, 49))
        );

        pnlReportTabels.setBorder(javax.swing.BorderFactory.createTitledBorder("Satis Tablosu"));

        tblFilter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(tblFilter);

        javax.swing.GroupLayout pnlReportTabelsLayout = new javax.swing.GroupLayout(pnlReportTabels);
        pnlReportTabels.setLayout(pnlReportTabelsLayout);
        pnlReportTabelsLayout.setHorizontalGroup(
            pnlReportTabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportTabelsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6)
                .addContainerGap())
        );
        pnlReportTabelsLayout.setVerticalGroup(
            pnlReportTabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportTabelsLayout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Secilen Urun Istatistikleri"));

        jLabel32.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel32.setText("Toplam Satılan Ürün Adeti:");

        lblTotalSaleAmount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblTotalSaleAmount.setText("00");

        jLabel37.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel37.setText("Satış Yapılan Ürünlerin Toplam Alış Fiyatı:");

        lblTotalBuyPrices.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblTotalBuyPrices.setText("00");

        jLabel33.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel33.setText("Toplam Kar");

        lblTotalProfit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblTotalProfit.setText("00");

        lblTotalSalePrices.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblTotalSalePrices.setText("00");

        jLabel36.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel36.setText("Satış Yapılan Ürünlerin Toplam Satış Fiyatı:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalSaleAmount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblTotalSalePrices)
                        .addGap(53, 53, 53)
                        .addComponent(lblTotalProfit))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblTotalBuyPrices)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel33)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTotalSaleAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(23, 23, 23))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTotalBuyPrices, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTotalProfit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTotalSalePrices, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlReportTabels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlReportTabels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("RAPORLAR", jPanel2);

        pnlUserSetting.setPreferredSize(new java.awt.Dimension(800, 600));
        pnlUserSetting.setRequestFocusEnabled(false);

        pnlUserSing.setBorder(javax.swing.BorderFactory.createTitledBorder("Yeni Kullanici Ekle"));

        txtNewUserName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtNewPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        btnSingin.setText("KAYIT EKLE");
        btnSingin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSinginActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText("Kullanici Adi");

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText("Sifre");

        javax.swing.GroupLayout pnlUserSingLayout = new javax.swing.GroupLayout(pnlUserSing);
        pnlUserSing.setLayout(pnlUserSingLayout);
        pnlUserSingLayout.setHorizontalGroup(
            pnlUserSingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserSingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUserSingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlUserSingLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(3, 3, 3)
                        .addComponent(txtNewUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUserSingLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUserSingLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSingin, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlUserSingLayout.setVerticalGroup(
            pnlUserSingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserSingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUserSingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNewUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlUserSingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSingin, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlUserlogin.setBorder(javax.swing.BorderFactory.createTitledBorder("Kullanici Girisi"));

        txtUserName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        btnLogin.setText("GIRIS YAP");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel19.setText("Kullanici Adi");

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setText("Sifre");

        javax.swing.GroupLayout pnlUserloginLayout = new javax.swing.GroupLayout(pnlUserlogin);
        pnlUserlogin.setLayout(pnlUserloginLayout);
        pnlUserloginLayout.setHorizontalGroup(
            pnlUserloginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserloginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUserloginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlUserloginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlUserloginLayout.createSequentialGroup()
                            .addComponent(jLabel19)
                            .addGap(3, 3, 3)
                            .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUserloginLayout.createSequentialGroup()
                            .addComponent(jLabel20)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUserloginLayout.setVerticalGroup(
            pnlUserloginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserloginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUserloginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlUserloginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlPasswordUpdate.setBorder(javax.swing.BorderFactory.createTitledBorder("Sifre Degistirme Formu"));

        jLabel21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel21.setText("Eski Sifre");

        jLabel22.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel22.setText("Yeni Sifre");

        jLabel23.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel23.setText("Yeni Sifre Tekrar");

        txtUpdatePassword1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtUpdatePassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtOldPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        btnUpdatePassword.setText("DEGISTIR");
        btnUpdatePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPasswordUpdateLayout = new javax.swing.GroupLayout(pnlPasswordUpdate);
        pnlPasswordUpdate.setLayout(pnlPasswordUpdateLayout);
        pnlPasswordUpdateLayout.setHorizontalGroup(
            pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPasswordUpdateLayout.createSequentialGroup()
                .addGroup(pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPasswordUpdateLayout.createSequentialGroup()
                        .addComponent(txtUpdatePassword1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(pnlPasswordUpdateLayout.createSequentialGroup()
                        .addGroup(pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUpdatePassword)
                            .addComponent(txtOldPassword))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPasswordUpdateLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdatePassword)
                .addContainerGap())
        );
        pnlPasswordUpdateLayout.setVerticalGroup(
            pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPasswordUpdateLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtOldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtUpdatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPasswordUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtUpdatePassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 0, 0));
        jLabel24.setText("*Yeni kullanici eklemek icin once tekrar giris yapmaniz gerekmektedir!");

        javax.swing.GroupLayout pnlUserSettingLayout = new javax.swing.GroupLayout(pnlUserSetting);
        pnlUserSetting.setLayout(pnlUserSettingLayout);
        pnlUserSettingLayout.setHorizontalGroup(
            pnlUserSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserSettingLayout.createSequentialGroup()
                .addGroup(pnlUserSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(pnlUserSettingLayout.createSequentialGroup()
                        .addGap(354, 354, 354)
                        .addComponent(jLabel24))
                    .addGroup(pnlUserSettingLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(pnlUserlogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlUserSing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUserSettingLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(pnlPasswordUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(229, 229, 229))
        );
        pnlUserSettingLayout.setVerticalGroup(
            pnlUserSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserSettingLayout.createSequentialGroup()
                .addContainerGap(63, Short.MAX_VALUE)
                .addComponent(pnlPasswordUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(pnlUserSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlUserSing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlUserlogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50)
                .addComponent(jLabel24)
                .addGap(64, 64, 64))
        );

        jTabbedPane1.addTab("KULLANICI AYARLARI", pnlUserSetting);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        int answer = JOptionPane.showConfirmDialog(rootPane, "Kaydetmek istediğinize emin misiniz?", "Kayıt işlemi", JOptionPane.YES_NO_OPTION);

        if (answer == 0) {
            addCustomer();
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblCustomersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomersMouseClicked
        int row = tblCustomers.getSelectedRow();
        cid = tblCustomers.getValueAt(row, 0) + "";
        txtCustomerName.setText(tblCustomers.getValueAt(row, 1) + "");
        txtCustomerSurname.setText(tblCustomers.getValueAt(row, 2) + "");
        txtCustomerPhone.setText(tblCustomers.getValueAt(row, 3) + "");
        txtCustomerAddress.setText(tblCustomers.getValueAt(row, 4) + "");
    }//GEN-LAST:event_tblCustomersMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteCustomer();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        readCustomersData(txtSearch.getText().trim());
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnProductSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductSaveActionPerformed
        addProduct();
        readProduct();
    }//GEN-LAST:event_btnProductSaveActionPerformed

    private void btnSinginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSinginActionPerformed
        addUser();
    }//GEN-LAST:event_btnSinginActionPerformed

    private void btnCategorySaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategorySaveActionPerformed
        addCategory();
        readCategories();
    }//GEN-LAST:event_btnCategorySaveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateCustomer();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteProductActionPerformed
        deleteProduct();
    }//GEN-LAST:event_btnDeleteProductActionPerformed

    private void tblProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductMouseClicked
        int row = tblProduct.getSelectedRow();
        pid = tblProduct.getValueAt(row, 0) + "";
        txtProductName.setText(tblProduct.getValueAt(row, 1) + "");
        cmbCategory.setSelectedItem(tblProduct.getValueAt(row, 2));
        txtProductBuy.setText(tblProduct.getValueAt(row, 3) + "");
        txtProductsell.setText(tblProduct.getValueAt(row, 4) + "");
        txtProductStock.setText(tblProduct.getValueAt(row, 5) + "");
        txtProductExplain.setText(tblProduct.getValueAt(row, 6) + "");
    }//GEN-LAST:event_tblProductMouseClicked

    private void btnUpdateProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateProductActionPerformed
        updateProduct();
    }//GEN-LAST:event_btnUpdateProductActionPerformed

    private void btnCategoryDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoryDeleteActionPerformed
        deleteCategory();
    }//GEN-LAST:event_btnCategoryDeleteActionPerformed

    private void tblCategoriesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCategoriesMouseClicked
        int row = tblCategories.getSelectedRow();
        catid = tblCategories.getValueAt(row, 0) + "";
        txtCategoryName.setText(tblCategories.getValueAt(row, 1) + "");
        txtCategoryExplain.setText(tblCategories.getValueAt(row, 2) + "");
    }//GEN-LAST:event_tblCategoriesMouseClicked

    private void btnCategoryUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoryUpdateActionPerformed
        updateCategory();
    }//GEN-LAST:event_btnCategoryUpdateActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed

        int answer = userLogin();
        if (answer == 0) {
            btnSingin.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Hatali giris yaptiniz!");
        }


    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnUpdatePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdatePasswordActionPerformed
        updateUserPassword();


    }//GEN-LAST:event_btnUpdatePasswordActionPerformed

    private void tblSellableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSellableMouseClicked
        btnSaleSave.setEnabled(true);
        int row = tblSellable.getSelectedRow();
        txtSellProduct.setText(tblSellable.getValueAt(row, 1) + "");
        txtSellStock.setText(tblSellable.getValueAt(row, 4) + "");

    }//GEN-LAST:event_tblSellableMouseClicked

    private void btnSaleSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaleSaveActionPerformed
        readCustomersData("");
        if (custExist < 0) {
            JOptionPane.showMessageDialog(rootPane, "Bu telefon numarasinda kullanici kayitli degil! \n Isleme devam etmek icin once kullanici kayit yapiniz.");
            txtSellCustPhone.requestFocus();
            txtSellCustPhone.setBackground(Color.red);
        } else {
            addSales();
            custExist = -1;
        }
    }//GEN-LAST:event_btnSaleSaveActionPerformed

    private void cmbSalesCategoryİtemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSalesCategoryİtemStateChanged
        readSellableProduct();
    }//GEN-LAST:event_cmbSalesCategoryİtemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        deleteSales();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tblSalesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSalesMouseClicked
        btnSaleUpdate.setEnabled(true);
        int row = tblSales.getSelectedRow();
        sid = tblSales.getValueAt(row, 0) + "";
        txtSellCustPhone.setText(tblSales.getValueAt(row, 1) + "");
        txtSellProduct.setText(tblSales.getValueAt(row, 2) + "");
        txtSellPrices.setText(tblSales.getValueAt(row, 3) + "");
        txtSellAmount.setText(tblSales.getValueAt(row, 4) + "");
        txtSellExplain.setText(tblSales.getValueAt(row, 6) + "");


    }//GEN-LAST:event_tblSalesMouseClicked

    private void btnSaleUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaleUpdateActionPerformed
        updateSales();
    }//GEN-LAST:event_btnSaleUpdateActionPerformed

    private void btnFilterShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterShowActionPerformed

        String date1 = cmbFilterYear1.getSelectedItem() + "-" + cmbFilterMonth1.getSelectedItem() + "-" + cmbFilterDay1.getSelectedItem();
        String date2 = cmbFilterYear2.getSelectedItem() + "-" + cmbFilterMonth2.getSelectedItem() + "-" + cmbFilterDay2.getSelectedItem();
        String search = txtFilterSearch.getText();
        readFiltersSales(date1, date2, search);

    }//GEN-LAST:event_btnFilterShowActionPerformed

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Application().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCategoryDelete;
    private javax.swing.JButton btnCategorySave;
    private javax.swing.JButton btnCategoryUpdate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteProduct;
    private javax.swing.JButton btnFilterShow;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnProductSave;
    private javax.swing.JButton btnSaleSave;
    private javax.swing.JButton btnSaleUpdate;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSingin;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdatePassword;
    private javax.swing.JButton btnUpdateProduct;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbFilterDay1;
    private javax.swing.JComboBox<String> cmbFilterDay2;
    private javax.swing.JComboBox<String> cmbFilterMonth1;
    private javax.swing.JComboBox<String> cmbFilterMonth2;
    private javax.swing.JComboBox<String> cmbFilterYear1;
    private javax.swing.JComboBox<String> cmbFilterYear2;
    private javax.swing.JComboBox<String> cmbSaleDay;
    private javax.swing.JComboBox<String> cmbSaleMonth;
    private javax.swing.JComboBox<String> cmbSaleYear;
    private javax.swing.JComboBox<String> cmbSalesCategory;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCatName;
    private javax.swing.JLabel lblCatName1;
    private javax.swing.JLabel lblTotalBuyPrices;
    private javax.swing.JLabel lblTotalProfit;
    private javax.swing.JLabel lblTotalSaleAmount;
    private javax.swing.JLabel lblTotalSalePrices;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel pnlCategories;
    private javax.swing.JPanel pnlCategoryAdd;
    private javax.swing.JPanel pnlCustomer;
    private javax.swing.JPanel pnlCustomerTable;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JPanel pnlPasswordUpdate;
    private javax.swing.JPanel pnlProductSave;
    private javax.swing.JPanel pnlProductTable;
    private javax.swing.JPanel pnlReportTabels;
    private javax.swing.JPanel pnlSaleSave;
    private javax.swing.JPanel pnlSales;
    private javax.swing.JPanel pnlSalesSave;
    private javax.swing.JPanel pnlSalesTable;
    private javax.swing.JPanel pnlSave;
    private javax.swing.JPanel pnlSellableProducts;
    private javax.swing.JPanel pnlTableCategories;
    private javax.swing.JPanel pnlUserSetting;
    private javax.swing.JPanel pnlUserSing;
    private javax.swing.JPanel pnlUserlogin;
    private javax.swing.JTable tblCategories;
    private javax.swing.JTable tblCustomers;
    private javax.swing.JTable tblFilter;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblSales;
    private javax.swing.JTable tblSellable;
    private javax.swing.JTextField txtCategoryExplain;
    private javax.swing.JTextField txtCategoryName;
    private javax.swing.JTextField txtCustomerAddress;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtCustomerPhone;
    private javax.swing.JTextField txtCustomerSurname;
    private javax.swing.JTextField txtFilterSearch;
    private javax.swing.JTextField txtNewPassword;
    private javax.swing.JTextField txtNewUserName;
    private javax.swing.JTextField txtOldPassword;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtProductBuy;
    private javax.swing.JTextField txtProductExplain;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtProductStock;
    private javax.swing.JTextField txtProductsell;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSellAmount;
    private javax.swing.JTextField txtSellCustPhone;
    private javax.swing.JTextField txtSellExplain;
    private javax.swing.JTextField txtSellPrices;
    private javax.swing.JTextField txtSellProduct;
    private javax.swing.JTextField txtSellStock;
    private javax.swing.JTextField txtUpdatePassword;
    private javax.swing.JTextField txtUpdatePassword1;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

}
