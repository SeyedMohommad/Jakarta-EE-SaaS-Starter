package dev.saasstarter.app.resources;

import jakarta.ws.rs.BadRequestException;

public class CrudUtil {
    public static int parseIntOr(String v, int def) {
        try { return v == null ? def : Integer.parseInt(v); }
        catch (Exception e) { throw new BadRequestException("invalid int param"); }
    }
    public static boolean parseBoolOr(String v, boolean def) {
        return v == null ? def : Boolean.parseBoolean(v);
    }
}
