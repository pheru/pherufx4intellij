package de.pheru.fx.intellij.createview;

public enum Scope {

    DEPENDENT("Dependent (Default Scope)", "Dependent"),
    APPLICATION("Application", "ApplicationScoped"),
    SESSION("Session", "SessionScoped"),
    REQUEST("Request", "RequestScoped"),
    CONVERSATION("Conversation", "ConversationScoped");

    private final String name;
    private final String annotationName;

    Scope(final String name, final String annotationName) {
        this.name = name;
        this.annotationName = annotationName;
    }

    public String getName() {
        return name;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    @Override
    public String toString() {
        return getName();
    }
}
