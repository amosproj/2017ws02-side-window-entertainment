package de.tuberlin.amos.ws17.swit.application.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;

import javax.lang.model.type.ErrorType;

public class ModuleStatusViewModel {

    private ModuleErrors errorType;
    private boolean working;

    public ModuleStatusViewModel() {
        errorType = null;
        working = false;
    }

    public ModuleStatusViewModel(ModuleErrors type, boolean working) {
        super();
        this.errorType = type;
        this.working = working;
    }

    public ModuleErrors getErrorType() {
        return errorType;
    }

    public void setErrorType(ModuleErrors errorType) {
        this.errorType = errorType;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleStatusViewModel that = (ModuleStatusViewModel) o;

        if (working != that.working) return false;
        return errorType == that.errorType;
    }
}