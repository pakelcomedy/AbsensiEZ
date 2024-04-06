package login;

import absensi.absensi;
import com.sun.jdi.connect.spi.Connection;
import java.awt.Color;
import javax.swing.JOptionPane;
import koneksi.koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class login extends javax.swing.JFrame {
    public login() {
        initComponents();
        login.requestFocusInWindow();
        String username = txt_username.getText();
        txt_password.setText("Password");
        txt_password.setForeground(new Color(153, 153, 153));
        txt_username.setText("Username or Email");
        txt_username.setForeground(new Color(153, 153, 153));
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt_username = new javax.swing.JTextField();
        txt_password = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();
        BG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 350));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_username.setBackground(new java.awt.Color(234, 234, 234));
        txt_username.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txt_username.setBorder(null);
        txt_username.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_usernameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_usernameFocusLost(evt);
            }
        });
        txt_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_usernameActionPerformed(evt);
            }
        });
        getContentPane().add(txt_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 320, 30));

        txt_password.setBackground(new java.awt.Color(234, 234, 234));
        txt_password.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txt_password.setBorder(null);
        txt_password.setEchoChar('-');
        txt_password.setFocusAccelerator('-');
        txt_password.setOpaque(true);
        txt_password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_passwordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_passwordFocusLost(evt);
            }
        });
        txt_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_passwordActionPerformed(evt);
            }
        });
        getContentPane().add(txt_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 160, 320, 30));

        login.setBackground(new java.awt.Color(255, 102, 204));
        login.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        login.setForeground(new java.awt.Color(255, 255, 255));
        login.setBorder(null);
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });
        getContentPane().add(login, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 180, 50));

        BG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/login/Group 155.png"))); // NOI18N
        getContentPane().add(BG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_usernameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_usernameFocusGained
        if(txt_username.getText().equals("Username or Email")) {
            txt_username.setText("");
            txt_username.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_txt_usernameFocusGained

    private void txt_usernameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_usernameFocusLost
        if(txt_username.getText().equals("")) {
            txt_username.setText("Username or Email");
            txt_username.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_txt_usernameFocusLost

    private void txt_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usernameActionPerformed

    }//GEN-LAST:event_txt_usernameActionPerformed

    private void txt_passwordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_passwordFocusGained
        if(txt_password.getText().equals("Password")) {
            txt_password.setText("");
            txt_password.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_txt_passwordFocusGained

    private void txt_passwordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_passwordFocusLost
        if(txt_password.getText().isEmpty()) {
            txt_password.setText("Password");
            txt_password.setForeground(new Color(153, 153, 153)); // Set text color to light gray
        }
    }//GEN-LAST:event_txt_passwordFocusLost

    private void txt_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_passwordActionPerformed

    }//GEN-LAST:event_txt_passwordActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
    String loginQuery = "SELECT id_user, username FROM user WHERE username=? AND password=?";
    try (java.sql.Connection connLogin = koneksi.configDB();
         PreparedStatement pstLogin = connLogin.prepareStatement(loginQuery)) {
        
        pstLogin.setString(1, txt_username.getText());
        pstLogin.setString(2, new String(txt_password.getPassword()));

        try (ResultSet rs = pstLogin.executeQuery()) {
            if (rs.next()) {
                // Login successful
                String username = rs.getString("username");
                String userID = rs.getString("id_user"); // Retrieve id_user from the result set
                
                // Pass username and userID to the absensi frame
                this.setVisible(false);
                absensi absensiFrame = new absensi();
                absensiFrame.setUserDetails(username, userID);
                absensiFrame.setVisible(true);
  
            } else {
                // Login failed, show an error message
                JOptionPane.showMessageDialog(this, "Invalid username/email or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // Handle SQL exception
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while executing the login query", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        // Handle SQL exception related to connection
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_loginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG;
    private javax.swing.JButton login;
    private javax.swing.JPasswordField txt_password;
    private javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables
}
