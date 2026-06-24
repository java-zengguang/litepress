package io.github.java_zengguang.litepress.chain.factory;

import io.github.java_zengguang.litepress.chain.drivers.AutoDriver;
import io.github.java_zengguang.litepress.chain.drivers.Driver;
import io.github.java_zengguang.litepress.core.init.AnnotationCache;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DriverFactory {
    private final static DriverFactory driverFactory = new DriverFactory();
    private final static Map<String, AutoDriver> driverMap = new HashMap<>();

    private DriverFactory() {

    }

    public static DriverFactory newInstance() {
        return driverFactory;
    }

    public AutoDriver getDriver(String systemFlag, String functionFlag) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        AutoDriver autoDriver;
        autoDriver = driverMap.get(systemFlag + ":" + functionFlag);
        if (autoDriver == null) {
            Set<Class<?>> classList = AnnotationCache.get(Driver.class);
            for (Class classes : classList) {
                Driver driver = (Driver) classes.getAnnotation(Driver.class);
                if (driver != null && systemFlag.equals(driver.systemFlag()) && functionFlag.equals(driver.functionFlag())) {
                    autoDriver = (AutoDriver) classes.newInstance();
                    Field systemFlagField = classes.getField("systemFlag");
                    Field functionField = classes.getField("functionFlag");
                    systemFlagField.set(autoDriver, systemFlag);
                    functionField.set(autoDriver, functionFlag);
                    driverMap.put(systemFlag + ":" + functionFlag, autoDriver);
                }

            }
        }

        return autoDriver;
    }

}
