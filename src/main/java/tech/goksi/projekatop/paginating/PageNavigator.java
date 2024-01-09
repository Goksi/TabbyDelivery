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
import java.util.function.Supplier;

/*TODO: change impl to use suppliers*/
public class PageNavigator {
    private final Map<Page, Supplier<Parent>> pages;
    private final Map<Page, Parent> cachedPages;
    private final SubScene rootScene;
    private Page currentPage = Page.MOJE_PORUDZBINE;

    public PageNavigator(SubScene rootScene, DataStorage dataStorage, Korisnik currentUser) {
        this.rootScene = rootScene;
        pages = new HashMap<>();
        cachedPages = new HashMap<>();
        pages.put(Page.PODESAVANJA, () -> ViewLoader.load(TabbyViews.PODESAVANJA, clazz -> ControllerFactory.controllerForClass(clazz, dataStorage, currentUser)));
        pages.put(Page.KORISNICI, () -> ViewLoader.load(TabbyViews.KORISNICI, clazz -> ControllerFactory.controllerForClass(clazz, dataStorage, null)));
    }

    public void goToPage(MenuItem item) {
        Page page = Page.valueOf(item.getId().toUpperCase());
        if (currentPage != page) {
            Parent parent = cachedPages.computeIfAbsent(page, k -> pages.get(k).get());
            rootScene.setRoot(parent);
            currentPage = page;
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
