package tech.goksi.projekatop;

public enum TabbyViews {
    MAIN("/views/main-view.fxml"),
    LOGIN("/views/login-view.fxml"),
    REGISTER("/views/register-view.fxml");

    private final String path;

    TabbyViews(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
