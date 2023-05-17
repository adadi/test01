import java.security.SecureRandom;

public String generatePassword() {
    int length = 10;
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
        int randomIndex = random.nextInt(characters.length());
        char randomChar = characters.charAt(randomIndex);
        sb.append(randomChar);
    }

    String generatedString = sb.toString();
    System.out.println("Generated password: " + generatedString);
    return generatedString;
}
