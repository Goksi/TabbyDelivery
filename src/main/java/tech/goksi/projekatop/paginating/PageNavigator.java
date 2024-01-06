package tech.goksi.projekatop.paginating;

import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.MenuItem;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.HashMap;
import java.util.Map;

public class PageNavigator {
    private final SubScene rootScene;
    private final Map<Page, Parent> pages;
    private Parent currentPage;

    public PageNavigator(SubScene rootScene, DataStorage dataStorage, Korisnik currentUser) {
        this.rootScene = rootScene;
        pages = new HashMap<>();
        pages.put(Page.PODESAVANJA, ViewLoader.load(TabbyViews.PODESAVANJA, clazz -> ControllerFactory.controllerForClass(clazz, dataStorage, currentUser)));
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
