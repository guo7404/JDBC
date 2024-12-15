import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLQuery extends MySQLConnection{
    // ... (위의 MySQLConnection 코드와 동일)

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            
            // SQL 쿼리 실행
            String sql = "SELECT * FROM student";
            ResultSet resultSet = statement.executeQuery(sql);
            
            // 결과 처리
            while (resultSet.next()) {
            	System.out.println("id : " + resultSet.getString(1));
            	System.out.println("name : " + resultSet.getString(2));
            	System.out.println("class : "+resultSet.getString(3));                
            }
            
            // 자원 정리
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
