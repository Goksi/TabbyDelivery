package tech.goksi.projekatop.models;

import tech.goksi.projekatop.utils.EncryptionUtils;

public class Korisnik extends Model {
    private final String username;
    private final String encryptedPassword;
    private final boolean admin;

    public Korisnik(int id, String username, String encryptedPassword, boolean admin) {
        super(id);
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.admin = admin;
    }

    public boolean tryLogin(String password) {
        return EncryptionUtils.validatePassword(password, encryptedPassword);
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return admin;
    }
}
