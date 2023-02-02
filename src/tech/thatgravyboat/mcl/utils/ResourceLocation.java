package tech.thatgravyboat.mcl.utils;

public record ResourceLocation(String namespace, String path) {

    public ResourceLocation(String value) {
        this(namespace(value), path(value));
    }

    private static String namespace(String value) {
        if (value.contains(":")) {
            return value.split(":")[0];
        }
        return "minecraft";
    }

    private static String path(String value) {
        if (value.contains(":")) {
            return value.split(":")[1];
        }
        return value;
    }

    public String toString() {
        return namespace + ":" + path;
    }
}
