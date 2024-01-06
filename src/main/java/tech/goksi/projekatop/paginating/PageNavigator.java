package tech.goksi.projekatop.paginating;

import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class PageNavigator {
    private final SubScene rootScene;
    private final Map<Page, Parent> pages;
    private Parent currentPage;

    public PageNavigator(SubScene rootScene) {
        this.rootScene = rootScene;
        pages = new HashMap<>();
    }

    public void goToPage(MenuItem item) {
        Page page = Page.valueOf(item.getId().toUpperCase());
        Parent currentParent = pages.get(page);
        if (currentParent != currentPage) {
            rootScene.setRoot(currentParent);
            currentPage = currentParent;
        }
    }

    private enum Page {
        /*Moj nalog*/
        PODESAVANJA,
        /*Porudzbine*/
        NOVA_PORUDZBINA,
        MOJE_PORUDZBINE,
        /*Admin*/
        KORISNICI,
        PORUDZBINE,
        RESTORANI
    }
}
