package tech.goksi.projekatop;

public enum TabbyViews {
    MAIN("/views/main-view.fxml"),
    LOGIN("/views/login-view.fxml"),
    REGISTER("/views/register-view.fxml"),
    PODESAVANJA("/views/nalog/podesavanja-view.fxml"),
    KORISNICI("/views/admin/korisnici-view.fxml"),
    MODIFY_KORISNIK("/views/admin/modify-korisnik-view.fxml"),
    RESTORANI("/views/admin/restorani-view.fxml"),
    DODAJ_RESTORAN("/views/admin/dodaj-restoran-view.fxml"),
    UREDI_RESTORAN("/views/admin/uredi-restoran-view.fxml"),
    DODAJ_JELO("/views/admin/dodaj-jelo-view.fxml"),
    NOVA_PORUDZBINA("/views/porudzbine/nova-porudzbina-view.fxml"),
    RESTORAN_KARTICA("/view/porudzbine/restoran-kartica-view.fxml");

    private final String path;

    TabbyViews(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
