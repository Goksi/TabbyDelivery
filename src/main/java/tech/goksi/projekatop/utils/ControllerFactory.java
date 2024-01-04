package tech.goksi.projekatop.utils;

import tech.goksi.projekatop.models.Model;
import tech.goksi.projekatop.models.ModelInjectable;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerFactory {
    private static final Logger LOGGER = Logger.getLogger(ControllerFactory.class.getName());

    private ControllerFactory() {
    }

    public static <T> T controllerForClass(Class<T> clazz, DataStorage dataStorage, Model model) {
        try {
            T controllerInstance = clazz.getConstructor().newInstance();
            if (model != null && ModelInjectable.class.isAssignableFrom(clazz)) {
                ModelInjectable modelInjectable = (ModelInjectable) controllerInstance;
                modelInjectable.setModel(model);
            }
            if (dataStorage != null && DataStorageInjectable.class.isAssignableFrom(clazz)) {
                DataStorageInjectable dataStorageInjectable = (DataStorageInjectable) controllerInstance;
                dataStorageInjectable.setDataStorage(dataStorage);
            }
            return controllerInstance;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while providing controller for class {0}", clazz);
            return null;
        }
    }
}
