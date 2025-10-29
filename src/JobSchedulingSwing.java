import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JobSchedulingSwing extends JFrame {
    private JTextField txtName, txtBurst, txtPriority;
    private DefaultTableModel jobModel, resultModel;
    private JTable jobTable, resultTable;
    private JButton btnAdd, btnSchedule;
    
    // Enhanced color scheme for better visibility
    private Color backgroundColor = new Color(70, 130, 180);        // Steel blue
    private Color backgroundGradientEnd = new Color(25, 25, 112);    // Midnight blue
    private Color primaryColor = new Color(255, 255, 255);          // White
    private Color secondaryColor = new Color(173, 216, 230);        // Light blue
    private Color successColor = new Color(50, 205, 50);            // Lime green
    private Color errorColor = new Color(220, 20, 60);              // Crimson
    private Color tableBackgroundColor = new Color(245, 245, 245);  // Light gray
    private Color textColor = new Color(25, 25, 25);                // Dark gray

    public JobSchedulingSwing() {
        setTitle("Job Scheduling System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, backgroundColor, getWidth(), getHeight(), backgroundGradientEnd);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Header
        JLabel header = new JLabel("Job Scheduling System", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(primaryColor);
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBackground(tableBackgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Input panel
        JPanel inputPanel = createInputPanel();
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Tables panel
        JPanel tablesPanel = createTablesPanel();
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        setVisible(true);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(tableBackgroundColor);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2), 
            "Add New Job", 
            0, 0, new Font("Segoe UI", Font.BOLD, 16), 
            new Color(106, 17, 203)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Job Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblName = new JLabel("Job Name:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(textColor);
        panel.add(lblName, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtName = new JTextField(20);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtName.setBackground(new Color(240, 240, 240));
        txtName.setForeground(textColor);
        panel.add(txtName, gbc);
        
        // Burst Time
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblBurst = new JLabel("Burst Time:");
        lblBurst.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBurst.setForeground(textColor);
        panel.add(lblBurst, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtBurst = new JTextField(20);
        txtBurst.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBurst.setBackground(new Color(240, 240, 240));
        txtBurst.setForeground(textColor);
        panel.add(txtBurst, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPriority = new JLabel("Priority:");
        lblPriority.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPriority.setForeground(textColor);
        panel.add(lblPriority, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPriority = new JTextField(20);
        txtPriority.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPriority.setBackground(new Color(240, 240, 240));
        txtPriority.setForeground(textColor);
        panel.add(txtPriority, gbc);
        
        // Add Button
        gbc.gridx = 2; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        btnAdd = new JButton("Add Job");
        styleButton(btnAdd, successColor);
        btnAdd.setPreferredSize(new Dimension(120, 35));
        panel.add(btnAdd, gbc);
        
        // Add action listener
        btnAdd.addActionListener(e -> addJob());
        
        return panel;
    }
    
    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 20));
        panel.setBackground(tableBackgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Pending Jobs Table
        JPanel pendingPanel = new JPanel(new BorderLayout());
        pendingPanel.setBackground(tableBackgroundColor);
        
        JLabel pendingLabel = new JLabel("Pending Jobs");
        pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pendingLabel.setForeground(new Color(106, 17, 203));
        pendingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pendingPanel.add(pendingLabel, BorderLayout.NORTH);
        
        jobModel = new DefaultTableModel(new String[]{"Job", "Burst Time", "Priority"}, 0);
        jobTable = new JTable(jobModel);
        styleTable(jobTable);
        
        JScrollPane jobScrollPane = new JScrollPane(jobTable);
        jobScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        jobScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jobScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jobScrollPane.setPreferredSize(new Dimension(0, 150));
        pendingPanel.add(jobScrollPane, BorderLayout.CENTER);
        
        // Scheduled Jobs Table
        JPanel scheduledPanel = new JPanel(new BorderLayout());
        scheduledPanel.setBackground(tableBackgroundColor);
        
        JLabel scheduledLabel = new JLabel("Scheduled Jobs");
        scheduledLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scheduledLabel.setForeground(new Color(106, 17, 203));
        scheduledLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        scheduledPanel.add(scheduledLabel, BorderLayout.NORTH);
        
        resultModel = new DefaultTableModel(new String[]{"Job", "Waiting Time", "Turnaround Time"}, 0);
        resultTable = new JTable(resultModel);
        styleTable(resultTable);
        
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        resultScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScrollPane.setPreferredSize(new Dimension(0, 150));
        scheduledPanel.add(resultScrollPane, BorderLayout.CENTER);
        
        // Schedule Button
        btnSchedule = new JButton("Schedule Jobs");
        styleButton(btnSchedule, errorColor);
        btnSchedule.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSchedule.setPreferredSize(new Dimension(200, 40));
        btnSchedule.addActionListener(e -> scheduleJobs());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(tableBackgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(btnSchedule);
        scheduledPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(pendingPanel);
        panel.add(scheduledPanel);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(200, 200, 200));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(25, 25, 25));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(255, 255, 255)); // White background
        header.setForeground(new Color(0, 0, 0));       // Black text
        header.setOpaque(true);
        
        // Ensure column headers are clearly visible
        table.getTableHeader().setForeground(new Color(0, 0, 0));
        table.getTableHeader().setBackground(new Color(255, 255, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
    }
    
    private void addJob() {
        try {
            String name = txtName.getText().trim();
            int burst = Integer.parseInt(txtBurst.getText().trim());
            int priority = Integer.parseInt(txtPriority.getText().trim());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a job name!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (burst <= 0) {
                JOptionPane.showMessageDialog(this, "Burst time must be greater than 0!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (priority < 0) {
                JOptionPane.showMessageDialog(this, "Priority cannot be negative!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            jobModel.addRow(new Object[]{name, burst, priority});
            txtName.setText("");
            txtBurst.setText("");
            txtPriority.setText("");
            txtName.requestFocus();
            
            JOptionPane.showMessageDialog(this, "Job added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for burst time and priority!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void scheduleJobs() {
        java.util.List<Job> jobs = new ArrayList<>();
        
        // Collect jobs from table
        for (int i = 0; i < jobModel.getRowCount(); i++) {
            String name = jobModel.getValueAt(i, 0).toString();
            int burst = Integer.parseInt(jobModel.getValueAt(i, 1).toString());
            int priority = Integer.parseInt(jobModel.getValueAt(i, 2).toString());
            jobs.add(new Job(name, burst, priority));
        }
        
        // Sort by priority (lower = higher)
        jobs.sort(Comparator.comparingInt(j -> j.priority));
        
        // Calculate waiting & turnaround times
        resultModel.setRowCount(0);
        int waitingTime = 0;
        for (Job j : jobs) {
            int turnaround = waitingTime + j.burstTime;
            resultModel.addRow(new Object[]{j.name, waitingTime, turnaround});
            waitingTime += j.burstTime;
        }
        
        // Show completion message and force UI update
        if (!jobs.isEmpty()) {
            // Force the table to update its view
            resultTable.revalidate();
            resultTable.repaint();
            JOptionPane.showMessageDialog(this, "Jobs scheduled successfully! Check the Scheduled Jobs table below.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No jobs to schedule!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new JobSchedulingSwing());
    }
}