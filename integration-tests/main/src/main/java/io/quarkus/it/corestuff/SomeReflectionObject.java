package io.quarkus.it.corestuff;

import java.io.Serializable;

public class SomeReflectionObject implements Serializable {

    String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
