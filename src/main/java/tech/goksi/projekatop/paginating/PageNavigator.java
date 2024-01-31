package tech.goksi.projekatop.paginating;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PageNavigator {
    private final Map<Page, Supplier<Parent>> pages;
    private final Map<Page, Parent> cachedPages;
    private final Pane rootPane;
    private Page currentPage = Page.NOVA_PORUDZBINA;

    public PageNavigator(Pane rootPane, DataStorage dataStorage, Korisnik currentUser) {
        this.rootPane = rootPane;
        pages = new HashMap<>();
        cachedPages = new HashMap<>();
        pages.put(Page.PODESAVANJA, () -> ViewLoader.load(TabbyViews.PODESAVANJA, dataStorage, currentUser));
        pages.put(Page.KORISNICI, () -> ViewLoader.load(TabbyViews.KORISNICI, dataStorage));
        pages.put(Page.RESTORANI, () -> ViewLoader.load(TabbyViews.RESTORANI, dataStorage));
        pages.put(Page.NOVA_PORUDZBINA, () -> ViewLoader.load(TabbyViews.NOVA_PORUDZBINA, dataStorage, currentUser));
        goToPage(currentPage);
    }

    public void goToPage(MenuItem item) {
        Page page = Page.valueOf(item.getId().toUpperCase());
        if (currentPage != page) {
            goToPage(page);
        }
    }

    private void goToPage(Page page) {
        Parent parent = page.isCached ? cachedPages.computeIfAbsent(page, k -> pages.get(k).get()) : pages.get(page).get();
        rootPane.getChildren().setAll(parent);
        Scene scene = rootPane.getScene();
        if (scene != null) {
            scene.getWindow().sizeToScene();
        }
        currentPage = page;
    }

    private enum Page {
        /*Moj nalog*/
        PODESAVANJA,
        /*Porudzbine*/
        NOVA_PORUDZBINA(false),
        MOJE_PORUDZBINE,
        /*Admin*/
        KORISNICI,
        PORUDZBINE,
        RESTORANI;

        private final boolean isCached;

        Page() {
            this.isCached = true;
        }

        Page(boolean isCached) {
            this.isCached = isCached;
        }

    }
}
