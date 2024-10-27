/*  Account
 *
 *  Copyright (C) 2023  Robert Schoech
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

 package ims;

 import org.mindrot.jbcrypt.BCrypt;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 public class Account extends DatabaseAPI {
 
     private static final String PEPPER = "SECRET_PEPPER_VALUE";  // Definieren Sie die Pepper als zusätzliche Sicherheitsschicht
 
     public void initAccount() {
         String usersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                 + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + "email TEXT NOT NULL UNIQUE, "
                 + "password_hash TEXT NOT NULL);";
         createTable("users", usersTableSql);
     }
 
     public void addAccount(String email, String password) {
         // Verwenden der Pepper
         String pepperedPassword = password + PEPPER;
 
         // Generiere Salt und hashe das Passwort
         String salt = BCrypt.gensalt();
         String hashedPassword = BCrypt.hashpw(pepperedPassword, salt);
 
         // SQL-Abfrage zur Speicherung des Benutzers
         String insertUserSql = "INSERT INTO users (email, password_hash) VALUES (?, ?)";
 
         try (Connection conn = connect();
              PreparedStatement pstmtUser = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
 
             pstmtUser.setString(1, email);
             pstmtUser.setString(2, hashedPassword);
             pstmtUser.executeUpdate();
 
             System.out.println("Account erfolgreich hinzugefügt.");
         } catch (SQLException e) {
             System.out.println("Fehler beim Hinzufügen des Accounts: " + e.getMessage());
         }
     }
 
     public boolean verifyAccount(String email) {
         String selectSql = "SELECT COUNT(*) AS count FROM users WHERE email = ?";
 
         try (Connection conn = connect();
              PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
 
             pstmt.setString(1, email);
             ResultSet rs = pstmt.executeQuery();
 
             if (rs.next() && rs.getInt("count") > 0) {
                 return true;  // Benutzer existiert bereits
             }
 
         } catch (SQLException e) {
             System.out.println("Fehler bei der Überprüfung des Accounts: " + e.getMessage());
         }
         return false;
     }
 
     public boolean verifyPassword(String email, String password) {
         String selectSql = "SELECT password_hash FROM users WHERE email = ?";
 
         try (Connection conn = connect();
              PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
 
             pstmt.setString(1, email);
             ResultSet rs = pstmt.executeQuery();
 
             if (rs.next()) {
                 String storedHash = rs.getString("password_hash");
                 // Fügen Sie die Pepper vor dem Hash-Vergleich hinzu
                 String pepperedPassword = password + PEPPER;
                 return BCrypt.checkpw(pepperedPassword, storedHash);
             }
 
         } catch (SQLException e) {
             System.out.println("Fehler bei der Passwortüberprüfung: " + e.getMessage());
         }
         return false;
     }
 }