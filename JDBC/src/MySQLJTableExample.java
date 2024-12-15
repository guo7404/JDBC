import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MySQLJTableExample {
    private static final String URL = "jdbc:mysql://localhost:3306/sample"; // 데이터베이스 URL
    private static final String USER = "root"; // 사용자명
    private static final String PASSWORD = "1111"; // 비밀번호
    private static DefaultTableModel model; // JTable 모델을 클래스 필드로 변경

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("전화번호부");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            // JTable 모델 생성
            model = new DefaultTableModel(new String[]{"ID", "Name", "PhoneNumber"}, 0);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            // 입력 패널 생성
            JPanel inputPanel = new JPanel();
            JTextField nameField = new JTextField(15);
            JTextField phoneField = new JTextField(15);
            JButton addButton = new JButton("추가");
            JButton deleteButton = new JButton("삭제");

            inputPanel.add(new JLabel("이름:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("전화번호:"));
            inputPanel.add(phoneField);
            frame.add(inputPanel, BorderLayout.NORTH);

            // 버튼 패널 생성
            JPanel buttonPanel = new JPanel();
            JButton searchButton = new JButton("검색");

            buttonPanel.add(searchButton);
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            // 데이터베이스에서 데이터 불러오기
            loadData();

            // 추가 버튼 클릭 시 데이터베이스에 추가
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getText();
                    String phoneNumber = phoneField.getText();

                    if (!phoneNumber.matches("\\d+")) {
                        JOptionPane.showMessageDialog(frame, "전화번호는 숫자만 기입가능합니다.");
                        return;
                    }
                    if (!name.isEmpty() && !phoneNumber.isEmpty()) {
                        try {
                            addPersonToDatabase(name, phoneNumber); // 데이터베이스에 추가
                            loadData(); // JTable 업데이트
                            nameField.setText(""); // 입력 필드 초기화
                            phoneField.setText("");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "데이터베이스에 추가하는 데 실패했습니다.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "모든 필드를 입력하세요.");
                    }
                }
            });

            // 삭제 버튼 클릭 시 선택된 행 삭제
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int id = (int) model.getValueAt(selectedRow, 0); // 선택된 행의 ID 가져오기

                        // 삭제 확인 다이얼로그
                        int confirm = JOptionPane.showConfirmDialog(frame, "삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                deletePersonFromDatabase(id); // 데이터베이스에서 삭제
                                loadData(); // JTable 업데이트
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(frame, "데이터베이스에서 삭제하는 데 실패했습니다.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "삭제할 행을 선택하세요.");
                    }
                }
            });

            // 검색 버튼 클릭 시 이름과 전화번호 입력 필드 값으로 데이터 필터링
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getText().trim();
                    String phoneNumber = phoneField.getText().trim();
                    filterData(name, phoneNumber); // 필터링
                }
            });

            frame.setVisible(true);
        });
    }

    private static void loadData() {
        model.setRowCount(0); // 기존 데이터 초기화
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM people")) {
             
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phoneNumber");
                model.addRow(new Object[]{id, name, phoneNumber});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "데이터를 불러오는 데 실패했습니다.");
        }
    }

    private static void filterData(String name, String phoneNumber) {
        model.setRowCount(0); // 기존 데이터 초기화
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM people WHERE name LIKE ? AND phoneNumber LIKE ?")) {
             
            preparedStatement.setString(1, "%" + name + "%"); // 이름 검색
            preparedStatement.setString(2, "%" + phoneNumber + "%"); // 전화번호 검색
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String personName = resultSet.getString("name");
                String personPhoneNumber = resultSet.getString("phoneNumber");
                model.addRow(new Object[]{id, personName, personPhoneNumber});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "데이터를 필터링하는 데 실패했습니다.");
        }
    }

    private static void addPersonToDatabase(String name, String phoneNumber) throws SQLException {
        String insertSQL = "INSERT INTO people (name, phoneNumber) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
             
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.executeUpdate();
        }
    }

    private static void deletePersonFromDatabase(int id) throws SQLException {
        String deleteSQL = "DELETE FROM people WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
             
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
