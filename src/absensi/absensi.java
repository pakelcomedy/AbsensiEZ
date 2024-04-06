package absensi;

import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import koneksi.koneksi;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.event.ActionEvent;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class absensi extends javax.swing.JFrame {
    private Timer timer;
    private Timer t;
    private boolean maxTimeSet = false;
    private Connection conn; // java.sql.Connection;
    private String userID;
    public String username;
    public PDType0Font font;
    
    
    public absensi() {
        initComponents();
        setTimeFromDatabase();
        dt();
        times();
        setupTimer();
        Date currentDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentTime = timeFormat.format(currentDate);
        this.userID = userID;
        JLabel IDOK = new JLabel();
        loadAttendanceData(TableAbsensi);

    }
        
        public void setUserDetails(String username, String userID) {
        usernameOK.setText(username);
        IDOK.setText(userID);
        }
        
    private void exportTableToPDF(JTable table) {
        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set the font for text
                PDType1Font font = PDType1Font.HELVETICA;
                contentStream.setFont(font, 12);

                // Load the table data
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int rowCount = model.getRowCount();
                int columnCount = model.getColumnCount();
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                float cellHeight = 20;
                float rowHeight = 30;
                float tableHeight = cellHeight * rowCount + rowHeight;

                // Draw table header
                contentStream.moveTo(margin, yStart - 15);
                contentStream.lineTo(margin + tableWidth, yStart - 15);
                contentStream.stroke();
                for (int i = 0; i < columnCount; i++) {
                    float x = margin + (tableWidth / columnCount) * i;
                    float y = yStart - 15;
                    contentStream.moveTo(x, y);
                    contentStream.lineTo(x, y - rowHeight);
                    contentStream.stroke();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + 2, y - 17);
                    contentStream.showText(model.getColumnName(i));
                    contentStream.endText();
                }

                // Draw table content and grid lines
                for (int i = 0; i < rowCount; i++) {
                    contentStream.moveTo(margin, yStart - (cellHeight * (i + 1)) - rowHeight);
                    contentStream.lineTo(margin + tableWidth, yStart - (cellHeight * (i + 1)) - rowHeight);
                    contentStream.stroke();
                    for (int j = 0; j < columnCount; j++) {
                        float x = margin + (tableWidth / columnCount) * j;
                        float y = yStart - (cellHeight * (i + 1)) - rowHeight;
                        contentStream.moveTo(x, y);
                        contentStream.lineTo(x, y - rowHeight);
                        contentStream.stroke();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(x + 2, y - 17);
                        contentStream.showText(model.getValueAt(i, j).toString());
                        contentStream.endText();
                    }
                }
            }

        // Prompt user for file name
        JFileChooser fileChooser = new JFileChooser();
        String defaultFileName = "Rekap Absensi " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")) + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setDialogTitle("Save PDF File");
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf"; // Ensure the file has a .pdf extension
            }
            document.save(fileName);
        }

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error exporting table to PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

        
 private void loadAttendanceData(JTable table) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // Clear existing data
    
    // Add columns only if they haven't been added before
    if (model.getColumnCount() == 0) {
        model.addColumn("Id User");
        model.addColumn("Username");
        model.addColumn("Time In");
        model.addColumn("Time Out");
    }

    try {
        String sql = "SELECT absensi.id_user, user.username, absensi.time_in, absensi.time_out " +
                     "FROM absensi " +
                     "INNER JOIN user ON absensi.id_user = user.id_user";
        try (Connection conn = koneksi.configDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                String idUser = res.getString("id_user");
                String username = res.getString("username");
                String timeIn = res.getString("time_in");
                String timeOut = res.getString("time_out");

                model.addRow(new Object[]{idUser, username, timeIn, timeOut});
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading attendance data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    // Method to refresh table data
    private void refreshTableData(JTable table) {
        loadAttendanceData(table);
    }

    // Method to set cell renderer for table
    private void setTableCellRenderer(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

                  
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
        private void setupTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock();
            }
        });
        timer.start();
    }
        
        private void updateClock() {
        Date currentDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(currentDate);
        lbl_time.setText(formattedTime);
    }
        
        public void times() {
        t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date dt = new Date();
                SimpleDateFormat st = new SimpleDateFormat("HH:mm:ss");
                String tt = st.format(dt);
                lbl_time.setText(tt);
            }
        });
        t.start();
    }
        
        public void dt() {
        Date currentDate = new Date();
        Locale indonesianLocale = new Locale("id", "ID");

        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd MMMM yyyy", indonesianLocale);
        String formattedDate = sdfDate.format(currentDate);
        lbl_date.setText(formattedDate);
    }
        
        
private void setTimeFromDatabase() {
    // Get the current date
    LocalDate currentDate = LocalDate.now();
    // Format the current date
    String formattedCurrentDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    try (Connection conn = koneksi.configDB();
         PreparedStatement pstmt = conn.prepareStatement("SELECT time_in, time_out FROM absensi WHERE DATE(time_in) = ? ORDER BY id_absensi DESC LIMIT 1")) {
         
        // Set the current date as a parameter for the query
        pstmt.setString(1, formattedCurrentDate);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                // Get the time_in and time_out values from the ResultSet as strings
                String timeInString = rs.getString("time_in");
                String timeOutString = rs.getString("time_out");

                // Check if time_in and time_out are not null before parsing
                LocalTime timeIn = null;
                if (timeInString != null && !timeInString.isEmpty()) {
                    timeIn = LocalTime.parse(timeInString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }
                LocalTime timeOut = null;
                if (timeOutString != null && !timeOutString.isEmpty()) {
                    timeOut = LocalTime.parse(timeOutString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }

                // Format the time portions as strings or placeholders
                String formattedTimeIn = (timeIn != null) ? timeIn.format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
                String formattedTimeOut = (timeOut != null) ? timeOut.format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";

                // Set the retrieved time values in the corresponding text fields
            }
        } catch (SQLException ex) {
            // Handle SQL exception
            ex.printStackTrace();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
private String generateUserId(Connection conn) throws SQLException {
    String prefix = "KAP"; 
    int number = 1; 
    String idAbsen = ""; 

    boolean idFound = false;
    while (!idFound) {
        String userIdToCheck = prefix + number;
        String query = "SELECT COUNT(*) FROM absensi WHERE id_absensi = ?";
        PreparedStatement checkStmt = conn.prepareStatement(query);
        checkStmt.setString(1, userIdToCheck);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            int count = rs.getInt(1);
            if (count == 0) {
                idAbsen = userIdToCheck;
                idFound = true;
            } else {
                number++; // Jika ID sudah digunakan, tambahkan nomor
            }
        }

        rs.close();
        checkStmt.close();
    }

    return idAbsen;
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TableAbsensi = new javax.swing.JTable();
        updf = new javax.swing.JButton();
        usernameOK = new javax.swing.JLabel();
        IDOK = new javax.swing.JLabel();
        lbl_time = new javax.swing.JLabel();
        lbl_date = new javax.swing.JLabel();
        timein = new javax.swing.JButton();
        timeout = new javax.swing.JButton();
        bg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TableAbsensi.setBackground(new java.awt.Color(237, 237, 237));
        TableAbsensi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        TableAbsensi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        TableAbsensi.setAlignmentY(10.0F);
        TableAbsensi.setFocusable(false);
        TableAbsensi.setRowHeight(55);
        jScrollPane1.setViewportView(TableAbsensi);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, 840, 260));

        updf.setBorder(null);
        updf.setContentAreaFilled(false);
        updf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updfActionPerformed(evt);
            }
        });
        getContentPane().add(updf, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 580, 180, 60));

        usernameOK.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        usernameOK.setText("User");
        getContentPane().add(usernameOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, 120, -1));

        IDOK.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        IDOK.setText("ID");
        getContentPane().add(IDOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 50, 100, -1));

        lbl_time.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lbl_time.setForeground(new java.awt.Color(255, 255, 255));
        lbl_time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_time.setText("-");
        getContentPane().add(lbl_time, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 220, 220, 30));

        lbl_date.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lbl_date.setForeground(new java.awt.Color(255, 255, 255));
        lbl_date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_date.setText("-");
        getContentPane().add(lbl_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 180, 220, 40));

        timein.setBorder(null);
        timein.setContentAreaFilled(false);
        timein.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeinActionPerformed(evt);
            }
        });
        getContentPane().add(timein, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, 270, 80));

        timeout.setBorder(null);
        timeout.setContentAreaFilled(false);
        timeout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeoutActionPerformed(evt);
            }
        });
        getContentPane().add(timeout, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 160, 270, 80));

        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/absensi/bg.png"))); // NOI18N
        getContentPane().add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void timeinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeinActionPerformed
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = now.format(dateFormatter);
    
    boolean recordExists = checkRecordExists(formattedDate);
            
    if (!recordExists) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(dateTimeFormatter);
        
        try (java.sql.Connection conn = koneksi.configDB(); 
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO absensi (id_absensi, id_user, time_in, time_out) VALUES (?, ?, ?, ?)")) {

    String userID = IDOK.getText();
    String idAbsen = generateUserId(conn);
    
    pstmt.setString(1, idAbsen); // id_absensi
    pstmt.setString(2, userID); // id_user
    pstmt.setString(3, formattedDateTime); // time_in
    pstmt.setNull(4, java.sql.Types.NULL); // time_out
            
            // Execute the INSERT query
            pstmt.executeUpdate();
            setTimeFromDatabase();
            refreshTableData(TableAbsensi);
            
            // Display a message indicating successful insertion
            JOptionPane.showMessageDialog(this, "Time inserted into database successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } else {
        // Display a message indicating that a record already exists for the current date
        JOptionPane.showMessageDialog(this, "A record for today already exists.", "Warning", JOptionPane.WARNING_MESSAGE);
    }
}

// Method to check if a record for the specified date already exists in the database
private boolean checkRecordExists(String date) {
    boolean recordExists = false;
    try (java.sql.Connection conn = koneksi.configDB();
         PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM absensi WHERE DATE(time_in) = ?")) {
        pstmt.setString(1, date);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    recordExists = true;
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return recordExists;
}
    
private String getTimeStatus(String time) {
    String[] parts = time.split(":");
    int hours = Integer.parseInt(parts[0]);
    int minutes = Integer.parseInt(parts[1]);

    if (hours > 7 || (hours == 7 && minutes > 0)) {
        return "Terlambat";
    } else {
        return "Tepat Waktu"; // You can set another status here if needed
    }
    }//GEN-LAST:event_timeinActionPerformed

    private void timeoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeoutActionPerformed
    LocalDateTime now = LocalDateTime.now();
    
    // Format date
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = now.format(dateFormatter);
    
    // Check if a timeout has already been recorded for the current date and user
    boolean timeoutRecorded = checkTimeoutRecorded(formattedDate, userID);

    
    if (!timeoutRecorded) {
        // If no timeout has been recorded for the current date and user, proceed with update
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(dateTimeFormatter);
        
        try (java.sql.Connection conn = koneksi.configDB(); 
             PreparedStatement pstmt = conn.prepareStatement("UPDATE absensi SET time_out = ? WHERE DATE(time_in) = ? AND id_user = ?")) {
            String userID = IDOK.getText();
            pstmt.setString(1, formattedDateTime); // Insert the current date and time
            pstmt.setString(2, formattedDate); // Update the row with the current date
            pstmt.setString(3, userID); // Update the row for the current user
            
            // Execute the UPDATE query
            int rowsAffected = pstmt.executeUpdate();
            setTimeFromDatabase();
            refreshTableData(TableAbsensi);
            if (rowsAffected > 0) {
                // Display a message indicating successful update
                JOptionPane.showMessageDialog(this, "Time out updated in database successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Display a message indicating failure to update
                JOptionPane.showMessageDialog(this, "Failed to update time out.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } else {
        // Display a message indicating that a timeout has already been recorded for the current date and user
        JOptionPane.showMessageDialog(this, "A timeout has already been recorded for today.", "Warning", JOptionPane.WARNING_MESSAGE);
    }
}

// Method to check if a timeout has already been recorded for the specified date and user in the database
private boolean checkTimeoutRecorded(String date, String userID) {
    boolean timeoutRecorded = false;
    try (java.sql.Connection conn = koneksi.configDB();
         PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM absensi WHERE DATE(time_out) = ? AND id_user = ? AND time_in IS NOT NULL")) {
        pstmt.setString(1, date);
        pstmt.setString(2, userID);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    timeoutRecorded = true;
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return timeoutRecorded;
}


private String getLastInsertedId() throws SQLException {
    String lastInsertedId = null;

    try (Connection conn = koneksi.configDB();
         PreparedStatement pstmt = conn.prepareStatement("SELECT id_absensi FROM absensi ORDER BY id_absensi DESC LIMIT 1")) {
        
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            lastInsertedId = rs.getString("id_absensi"); // Retrieve the value from the result set
        }
    }

    return lastInsertedId;
    }//GEN-LAST:event_timeoutActionPerformed

    private void updfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updfActionPerformed
        exportTableToPDF(TableAbsensi);
    }//GEN-LAST:event_updfActionPerformed
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new absensi().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel IDOK;
    private javax.swing.JTable TableAbsensi;
    private javax.swing.JLabel bg;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_date;
    private javax.swing.JLabel lbl_time;
    private javax.swing.JButton timein;
    private javax.swing.JButton timeout;
    private javax.swing.JButton updf;
    private javax.swing.JLabel usernameOK;
    // End of variables declaration//GEN-END:variables
}
