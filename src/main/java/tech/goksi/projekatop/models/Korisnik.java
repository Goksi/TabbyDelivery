package tech.goksi.projekatop.models;

import tech.goksi.projekatop.utils.EncryptionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Korisnik extends Model {
    private final String username;
    private final String encryptedPassword;
    private final Date createDate;
    private final boolean admin;

    public Korisnik(int id, String username, String encryptedPassword, Date createDate, boolean admin) {
        super(id);
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.createDate = createDate;
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

    public String getDatumRegistracije() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return simpleDateFormat.format(createDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Korisnik korisnik = (Korisnik) o;

        if (admin != korisnik.admin) return false;
        if (!username.equals(korisnik.username)) return false;
        if (!encryptedPassword.equals(korisnik.encryptedPassword)) return false;
        return createDate.equals(korisnik.createDate);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + encryptedPassword.hashCode();
        result = 31 * result + createDate.hashCode();
        result = 31 * result + (admin ? 1 : 0);
        return result;
    }
}
