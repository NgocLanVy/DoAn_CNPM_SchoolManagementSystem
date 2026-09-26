/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.parent;

import view.common.MainMenuFrm;
import dao.StudentDAO;
import dao.TuitionDAO;
import model.StudentProfile;
import model.User;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class ParentTuitionFrm extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ParentTuitionFrm.class.getName());
    private final User currentUser;
    private final StudentDAO studentDAO = new StudentDAO();
    private final TuitionDAO tuitionDAO = new TuitionDAO();

    private DefaultTableModel tableModel;
    private List<StudentProfile> children;

    private int selectedTuitionId = -1;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    /**
     * Creates new form ParentTuitionFrm
     */
    public ParentTuitionFrm(User currentUser) {
        this.currentUser = currentUser;

        initComponents();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        customizeUI();
        loadChildren();
        registerEvents();
        util.UITheme.apply(getContentPane());
    }

    private void customizeUI() {

        lblTitle.setText("XEM HỌC PHÍ CỦA CON");
        lblParentName.setText(currentUser.getFullName());

        tableModel = new DefaultTableModel(
                new Object[]{
                    "STT",
                    "Khoản thu",
                    "Học kỳ",
                    "Số tiền",
                    "Hạn đóng",
                    "Ngày đóng",
                    "Trạng thái",
                    "Phương thức"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTuition.setModel(tableModel);
        tblTuition.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION
        );

        btnPay.setText("Thanh toán");
        btnRefresh.setText("Làm mới");
        btnBack.setText("Quay lại");
    }

    private void loadChildren() {

        children = studentDAO.getChildrenByParentUserId(currentUser.getId());

        cboChild.removeAllItems();

        for (StudentProfile child : children) {
            cboChild.addItem(child);
        }

        if (!children.isEmpty()) {
            cboChild.setSelectedIndex(0);
            loadTuition();
        } else {
            tableModel.setRowCount(0);

            JOptionPane.showMessageDialog(
                    this,
                    "Phụ huynh chưa có học sinh nào được liên kết!"
            );
        }
    }

    private void loadTuition() {

        tableModel.setRowCount(0);
        selectedTuitionId = -1;

        StudentProfile child
                = (StudentProfile) cboChild.getSelectedItem();

        if (child == null) {
            return;
        }

        List<Object[]> list
                = tuitionDAO.getByStudent(child.getId());

        int stt = 1;

        for (Object[] row : list) {

            // row:
            // 0 = Id
            // 1 = TuitionType
            // 2 = Semester
            // 3 = Amount
            // 4 = DueDate
            // 5 = PaidDate
            // 6 = Status
            // 7 = PaymentMethod
            String dueDate = row[4] != null
                    ? sdf.format(row[4])
                    : "-";

            String paidDate = row[5] != null
                    ? sdf.format(row[5])
                    : "-";

            BigDecimal amount = (BigDecimal) row[3];

            String status = formatStatus(
                    String.valueOf(row[6])
            );

            String paymentMethod = row[7] != null
                    ? String.valueOf(row[7])
                    : "-";

            tableModel.addRow(new Object[]{
                stt++,
                row[1], // Khoản thu
                row[2], // Học kỳ
                moneyFormat.format(amount) + " đ",
                dueDate,
                paidDate,
                status,
                paymentMethod
            });
        }

        if (list.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Học sinh này chưa có khoản học phí nào."
            );
        }
    }

    private String formatStatus(String status) {

        if ("Paid".equalsIgnoreCase(status)) {
            return "Đã đóng";
        }

        if ("Unpaid".equalsIgnoreCase(status)) {
            return "Chưa đóng";
        }

        return status;
    }

    private void onRowSelected() {

        int row = tblTuition.getSelectedRow();

        if (row < 0) {
            selectedTuitionId = -1;
            return;
        }

        StudentProfile child
                = (StudentProfile) cboChild.getSelectedItem();

        if (child == null) {
            selectedTuitionId = -1;
            return;
        }

        List<Object[]> list
                = tuitionDAO.getByStudent(child.getId());

        if (row >= list.size()) {
            selectedTuitionId = -1;
            return;
        }

        Object[] selected = list.get(row);

        selectedTuitionId = ((Number) selected[0]).intValue();
    }

    private void registerEvents() {

        cboChild.addActionListener(e -> loadTuition());

        tblTuition.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblParent = new javax.swing.JLabel();
        lblParentName = new javax.swing.JLabel();
        lblChild = new javax.swing.JLabel();
        cboChild = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTuition = new javax.swing.JTable();
        btnPay = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setText("XEM HỌC PHÍ CỦA CON");

        lblParent.setText("Phụ huynh:");

        lblParentName.setText("...");

        lblChild.setText("Chọn con:");

        tblTuition.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblTuition);

        btnPay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPay.setText("Thanh toán");
        btnPay.addActionListener(this::btnPayActionPerformed);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRefresh.setText("Làm mới");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnBack.setText("Quay lại");
        btnBack.addActionListener(this::btnBackActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(201, 201, 201))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblParent, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblParentName, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(lblChild, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboChild, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParent)
                    .addComponent(lblParentName)
                    .addComponent(lblChild)
                    .addComponent(cboChild, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPay)
                    .addComponent(btnRefresh)
                    .addComponent(btnBack))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        // TODO add your handling code here:
        if (selectedTuitionId < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn khoản học phí cần thanh toán!"
            );
            return;
        }

        int row = tblTuition.getSelectedRow();

        if (row < 0) {
            return;
        }

        Object statusValue = tableModel.getValueAt(row, 6);

        String status = statusValue == null
                ? ""
                : statusValue.toString();

        if ("Đã đóng".equals(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Khoản học phí này đã được thanh toán!"
            );
            return;
        }

        String[] methods = {
            "Tiền mặt",
            "Chuyển khoản",
            "Ví điện tử"
        };

        String paymentMethod = (String) JOptionPane.showInputDialog(
                this,
                "Chọn phương thức thanh toán:",
                "Thanh toán học phí",
                JOptionPane.QUESTION_MESSAGE,
                null,
                methods,
                methods[0]
        );

        if (paymentMethod == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn thanh toán khoản học phí này?\n\n"
                + "Phương thức: " + paymentMethod,
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success
                = tuitionDAO.markAsPaid(
                        selectedTuitionId,
                        paymentMethod
                );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán học phí thành công!"
            );

            loadTuition();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán thất bại!"
            );
        }
    }//GEN-LAST:event_btnPayActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadChildren();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        this.dispose();

        new MainMenuFrm(currentUser).setVisible(true);
    }//GEN-LAST:event_btnBackActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<StudentProfile> cboChild;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChild;
    private javax.swing.JLabel lblParent;
    private javax.swing.JLabel lblParentName;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblTuition;
    // End of variables declaration//GEN-END:variables
}
