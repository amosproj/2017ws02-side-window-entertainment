package de.tuberlin.amos.ws17.swit.application.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;

public class UserExpressionViewModel {
    private ExpressionType type;

    private SimpleBooleanProperty active;

    public UserExpressionViewModel() {
        this(null, false);
    }

    public UserExpressionViewModel(ExpressionType type, boolean active) {
        this.type = type;
        this.active = new SimpleBooleanProperty(active);
    }

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active.get();
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserExpressionViewModel that = (UserExpressionViewModel) o;

        if (type != that.type) return false;
        return active != null ? active.equals(that.active) : that.active == null;
    }
}
