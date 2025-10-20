package nc.maxime.expense_manager.common.response;

public record AppResponse<TData>(boolean success, String message, TData data) {

    public AppResponse {
        success = true;
    }

    public AppResponse(String message, TData data) {
        this(true, message, data);
    }

    public static Builder message(String message) {
        return new Builder(message);
    }

    public static final class Builder {
        private final String message;

        private Builder(String message) {
            this.message = message;
        }

        public <TData> AppResponse<TData> data(TData data) {
            return new AppResponse<>(true, message, data);
        }
    }
}
