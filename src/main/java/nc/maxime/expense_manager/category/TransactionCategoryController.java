package nc.maxime.expense_manager.category;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.category.dto.TransactionCategoryDto;
import nc.maxime.expense_manager.category.dto.TransactionCategoryMapper;
import nc.maxime.expense_manager.category.dto.UpsertTransactionCategoryDto;
import nc.maxime.expense_manager.common.response.AppResponse;
import nc.maxime.expense_manager.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@Validated
public class TransactionCategoryController {

    private final TransactionCategoryService transactionCategoryService;
    private final TransactionCategoryMapper transactionCategoryMapper;

    public TransactionCategoryController(
            TransactionCategoryService transactionCategoryService,
            TransactionCategoryMapper transactionCategoryMapper) {
        this.transactionCategoryService = transactionCategoryService;
        this.transactionCategoryMapper = transactionCategoryMapper;
    }

    @PostMapping
    public ResponseEntity<AppResponse<TransactionCategoryDto>> createCategory(
            @NotNull @AuthenticationPrincipal User user,
            @Valid @RequestBody UpsertTransactionCategoryDto request) {
        var category = transactionCategoryService.createCategory(user, request);
        var response = transactionCategoryMapper.toDto(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.message("Category created").data(response));
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<TransactionCategoryDto>>> getUserCategories(
            @NotNull @AuthenticationPrincipal User user) {
        var categories = transactionCategoryService.getCategories(user).stream()
                .map(transactionCategoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(AppResponse.message("User categories fetched").data(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<TransactionCategoryDto>> getCategory(@PathVariable Long id) {
        var category = transactionCategoryService.getCategory(id);
        var response = transactionCategoryMapper.toDto(category);
        return ResponseEntity.ok(AppResponse.message("Category retrieved").data(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<TransactionCategoryDto>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpsertTransactionCategoryDto request) {
        var category = transactionCategoryService.updateCategory(id, request);
        var response = transactionCategoryMapper.toDto(category);
        return ResponseEntity.ok(AppResponse.message("Category updated").data(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<Void>> deleteCategory(@PathVariable Long id) {
        transactionCategoryService.deleteCategory(id);
        return ResponseEntity.ok(AppResponse.message("Category deleted").data(null));
    }
}
