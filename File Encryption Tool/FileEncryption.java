import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Scanner;

public class FileEncryption {

    // Generate AES key from user password
    public static SecretKey getKeyFromPassword(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes("UTF-8"));
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Encrypt file
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // Decrypt file
    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    // Delete file securely
    public static void deleteFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
            System.out.println("Deleted original file: " + filePath);
        } catch (Exception e) {
            System.out.println("Error deleting file: " + filePath);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Ask for operation
            System.out.println("Choose an option: (1) Encrypt (2) Decrypt");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Get file path
            System.out.println("Enter file path: ");
            String filePathStr = scanner.nextLine();
            Path filePath = Paths.get(filePathStr);

            // Get password
            System.out.println("Enter a password: ");
            String password = scanner.nextLine();
            SecretKey key = getKeyFromPassword(password);

            byte[] fileData = Files.readAllBytes(filePath);

            if (choice == 1) { // Encryption
                byte[] encryptedData = encrypt(fileData, key);
                Path encryptedPath = Paths.get(filePathStr + ".enc");
                Files.write(encryptedPath, encryptedData);
                System.out.println("File Encrypted Successfully: " + encryptedPath);

                // Delete original file
                deleteFile(filePath);

            } else if (choice == 2) { // Decryption
                byte[] decryptedData = decrypt(fileData, key);
                Path decryptedPath = Paths.get(filePathStr.replace(".enc", ""));
                Files.write(decryptedPath, decryptedData);
                System.out.println("File Decrypted Successfully: " + decryptedPath);

                // Delete encrypted file
                deleteFile(filePath);

            } else {
                System.out.println("Invalid Choice!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }
}
