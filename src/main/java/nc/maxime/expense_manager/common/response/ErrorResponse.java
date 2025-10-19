package nc.maxime.expense_manager.common.response;

import java.util.Map;

public record ErrorResponse(boolean success, String error, Map<String, String> details) {
    public ErrorResponse(String error, Map<String, String> details) {
        this(false, error, details);
    }
}
