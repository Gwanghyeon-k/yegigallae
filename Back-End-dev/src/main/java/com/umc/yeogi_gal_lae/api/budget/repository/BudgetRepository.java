package com.umc.yeogi_gal_lae.api.budget.repository;

import com.umc.yeogi_gal_lae.api.budget.domain.Budget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByAiCourseId(Long aiCourseId);
}
