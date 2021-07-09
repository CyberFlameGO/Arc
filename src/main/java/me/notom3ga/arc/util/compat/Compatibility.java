package me.notom3ga.arc.util.compat;

public enum Compatibility {
    COMPATIBLE(""),
    OS("You must be on Linux x86_64 to use the Arc profiler."),
    PROFILER_NOT_FOUND("Could not find the async profiler in the Arc jar.");

    private final String message;

    Compatibility(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
