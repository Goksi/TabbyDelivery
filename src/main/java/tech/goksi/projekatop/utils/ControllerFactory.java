package tech.goksi.projekatop.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerFactory {
    private static final Logger LOGGER = Logger.getLogger(ControllerFactory.class.getName());

    private ControllerFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T controllerForClass(Class<T> clazz, Object... args) {
        try {
            T controllerInstance;
            if (Injectable.class.isAssignableFrom(clazz)) {
                controllerInstance = (T) clazz.getConstructors()[0].newInstance(args); // injectable class will always have exactly 1 constructor
            } else {
                controllerInstance = clazz.getConstructor().newInstance();
            }
            return controllerInstance;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while providing controller for class {0}", clazz);
            return null;
        }
    }
}
