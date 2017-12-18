package de.tuberlin.amos.ws17.swit.application.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;

import javax.lang.model.type.ErrorType;

public class ModuleStatusViewModel {

    private ModuleErrors errorType;
    private SimpleBooleanProperty working;

    public ModuleStatusViewModel() {
        this(null, false);
    }

    public ModuleStatusViewModel(ModuleErrors type, boolean working) {
        this.errorType = type;
        this.working = new SimpleBooleanProperty(working);
    }

    public ModuleErrors getErrorType() {
        return errorType;
    }

    public void setErrorType(ModuleErrors errorType) {
        this.errorType = errorType;
    }

    public boolean isWorking() {
        return working.get();
    }

    public SimpleBooleanProperty workingProperty() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working.set(working);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleStatusViewModel that = (ModuleStatusViewModel) o;

        if (errorType != that.errorType) return false;
        return working != null ? working.equals(that.working) : that.working == null;
    }
}