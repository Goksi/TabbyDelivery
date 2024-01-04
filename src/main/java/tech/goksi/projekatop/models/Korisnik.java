package tech.goksi.projekatop.models;

import tech.goksi.projekatop.utils.EncryptionUtils;

public class Korisnik {
    private final int id;
    private final String username;
    private final String encryptedPassword;
    private final boolean admin;

    public Korisnik(int id, String username, String encryptedPassword, boolean admin) {
        this.id = id;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.admin = admin;
    }

    public boolean tryLogin(String password) {
        return EncryptionUtils.validatePassword(password, encryptedPassword);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return admin;
    }
}
