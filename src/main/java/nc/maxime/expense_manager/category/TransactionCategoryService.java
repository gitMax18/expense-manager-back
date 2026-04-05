package nc.maxime.expense_manager.category;

import java.util.List;
import java.util.Optional;
import nc.maxime.expense_manager.category.dto.TransactionCategoryMapper;
import nc.maxime.expense_manager.category.dto.UpsertTransactionCategoryDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategoryService {

    private static final List<DefaultCategory> DEFAULT_CATEGORIES = List.of(
            new DefaultCategory("Groceries", "Food and household supplies", "#22C55E"),
            new DefaultCategory("Rent", "Monthly rent or mortgage payments", "#EA580C"),
            new DefaultCategory("Utilities", "Electricity, water, internet, and other utilities", "#0EA5E9"),
            new DefaultCategory("Transportation", "Fuel, public transport, ride sharing", "#6366F1"),
            new DefaultCategory("Dining Out", "Restaurants, cafes, and takeout", "#F43F5E"),
            new DefaultCategory("Entertainment", "Streaming, movies, events, and hobbies", "#8B5CF6"),
            new DefaultCategory("Healthcare", "Medical visits, pharmacy, and insurance co-pays", "#EF4444"),
            new DefaultCategory("Insurance", "Home, auto, life, and other insurance premiums", "#475569"),
            new DefaultCategory("Savings", "Emergency fund and long-term savings", "#14B8A6"),
            new DefaultCategory("Salary", "Primary income and payroll deposits", "#FACC15"));

    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionCategoryMapper transactionCategoryMapper;

    public TransactionCategoryService(
            TransactionCategoryRepository transactionCategoryRepository,
            TransactionCategoryMapper transactionCategoryMapper) {
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.transactionCategoryMapper = transactionCategoryMapper;
    }

    public TransactionCategory createCategory(User user, UpsertTransactionCategoryDto request) {
        User owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User required to create category"));

        return Optional.ofNullable(request)
                .map(dto -> transactionCategoryMapper.toEntity(owner, dto))
                .map(transactionCategoryRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category creation request"));
    }

    public List<TransactionCategory> getCategories(User user) {
        User owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User required to list categories"));
        return transactionCategoryRepository.findByUser(owner);
    }

    public TransactionCategory getCategory(Long categoryId) {
        return Optional.ofNullable(categoryId)
                .flatMap(transactionCategoryRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public TransactionCategory updateCategory(Long categoryId, UpsertTransactionCategoryDto request) {
        return Optional.ofNullable(categoryId)
                .flatMap(transactionCategoryRepository::findById)
                .map(category -> transactionCategoryMapper.updateEntity(category, request))
                .map(transactionCategoryRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public void deleteCategory(Long categoryId) {
        Optional.ofNullable(categoryId)
                .flatMap(transactionCategoryRepository::findById)
                .ifPresentOrElse(
                        transactionCategoryRepository::delete,
                        () -> {
                            throw new IllegalArgumentException("Category not found");
                        });
    }

    public List<TransactionCategory> createDefaultCategories(User user) {
        User owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User required to generate categories"));

        List<TransactionCategory> existingCategories = transactionCategoryRepository.findByUser(owner);
        if (!existingCategories.isEmpty()) {
            return existingCategories;
        }

        var categories = DEFAULT_CATEGORIES.stream()
                .map(defaultCategory -> TransactionCategory.builder()
                        .name(defaultCategory.name())
                        .description(defaultCategory.description())
                        .color(defaultCategory.color())
                        .user(owner)
                        .build())
                .toList();

        return transactionCategoryRepository.saveAll(categories);
    }

    private record DefaultCategory(String name, String description, String color) {
    }
}
