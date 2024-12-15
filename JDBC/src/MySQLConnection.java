import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    protected static final String URL = "jdbc:mysql://localhost:3306/sample"; // 데이터베이스 URL
    protected static final String USER = "root"; // 사용자명
    protected static final String PASSWORD = "1111"; // 비밀번호

    public static void main(String[] args) {
    	Connection connection = null;
        try {
            // MySQL 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 데이터베이스 연결
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("데이터베이스에 성공적으로 연결되었습니다.");
            
            // 연결 종료
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 오류.");
            e.printStackTrace();
        }
    }
}
