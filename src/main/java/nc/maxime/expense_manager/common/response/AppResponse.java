package nc.maxime.expense_manager.common.response;

public record AppResponse<TData>(boolean success, String message, TData data) {

    public AppResponse(String message, TData data) {
        this(true, message, data);
    }
}
