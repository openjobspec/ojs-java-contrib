package org.openjobspec.smoke;

import org.openjobspec.micronaut.OjsConfiguration;
import org.openjobspec.quarkus.OjsConfig;
import org.openjobspec.spring.OjsProperties;

public final class ConsumerSmoke {
    private ConsumerSmoke() {
    }

    public static void main(String[] args) {
        System.out.println(OjsProperties.class.getName());
        System.out.println(OjsConfig.class.getName());
        System.out.println(OjsConfiguration.class.getName());
    }
}
