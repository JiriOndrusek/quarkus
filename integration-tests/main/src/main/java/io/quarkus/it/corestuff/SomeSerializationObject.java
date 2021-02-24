package io.quarkus.it.corestuff;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(serialization = true)
public class SomeSerializationObject implements Serializable {
}
