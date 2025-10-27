package nc.maxime.expense_manager.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nc.maxime.expense_manager.common.model.BaseEntity;
import nc.maxime.expense_manager.transaction.Transaction;
import nc.maxime.expense_manager.user.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_category")
public class TransactionCategory extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions = new ArrayList<>();
}
