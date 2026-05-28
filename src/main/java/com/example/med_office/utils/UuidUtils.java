package com.example.med_office.utils;

import java.util.UUID;

public final class UuidUtils {

    private UuidUtils() {
    }

    public static String newUuid() {
        return UUID.randomUUID().toString();
    }
}
