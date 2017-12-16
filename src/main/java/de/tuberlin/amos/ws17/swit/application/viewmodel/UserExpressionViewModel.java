package de.tuberlin.amos.ws17.swit.application.viewmodel;

public class UserExpressionViewModel {
    private ExpressionType type;
    private boolean active;

    public UserExpressionViewModel() {
        this(null, false);
    }

    public UserExpressionViewModel(ExpressionType type, boolean active) {
        this.type = type;
        this.active = active;
    }

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserExpressionViewModel that = (UserExpressionViewModel) o;

        if (active != that.active) return false;
        return type == that.type;
    }
}
