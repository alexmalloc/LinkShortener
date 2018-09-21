package com.malloc.io.server.view;

import com.malloc.io.server.view.controller.RequestController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>(1);
        classes.add(RequestController.class);
        return super.getClasses();
    }
}
