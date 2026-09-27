package com.qsp.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.qsp.dto.ExpenseDTO;
import com.qsp.dto.IncomeDTO;
import com.qsp.dto.RecentTransactionDTO;
import com.qsp.entity.ProfileEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashbordService {

    private final IncomeService incomeService;
    private final ExpanseService expanseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {

        ProfileEntity profile = profileService.getCurrentProfile();

        Map<String, Object> returnValue = new LinkedHashMap<>();

        List<IncomeDTO> latestIncomes =
                incomeService.getCurrentMonthIncomeForCurrentUser();

        List<ExpenseDTO> latestExpenses =
                expanseService.getCurrentMonthExpenseForCurrentUser();

        
        List<RecentTransactionDTO> recentTransactions =
                Stream.concat(

                    latestIncomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                            .id(income.getId())
                            .profileId(profile.getId())
                            .icon(income.getIcon())
                            .name(income.getName())
                            .amount(income.getAmount())
                            .date(income.getDate())
                            .createdAt(income.getCreatedAt())
                            .updatedAt(income.getUpdatedAt())
                            .type("income")
                            .build()
                    ),

                    latestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                            .id(expense.getId())
                            .profileId(profile.getId())
                            .icon(expense.getIcon())
                            .name(expense.getName())
                            .amount(expense.getAmount())
                            .date(expense.getDate())
                            .createdAt(expense.getCreatedAt())
                            .updatedAt(expense.getUpdatedAt())
                            .type("expense")
                            .build()
                    )

                )
                .sorted((a, b) -> {

                    int cmp = b.getDate().compareTo(a.getDate());

                    if (cmp == 0 &&
                        a.getCreatedAt() != null &&
                        b.getCreatedAt() != null) {

                        return b.getCreatedAt()
                                .compareTo(a.getCreatedAt());
                    }

                    return cmp;
                })
                .collect(Collectors.toList());

       
        BigDecimal totalIncome =
                incomeService.getTotalIncomeForCurrentUser();

        BigDecimal totalExpense =
                expanseService.getTotalExpenseForCurrentUser();

        BigDecimal totalBalance =
                totalIncome.subtract(totalExpense);

        
        returnValue.put("totalBalance", totalBalance);
        returnValue.put("totalIncome", totalIncome);
        returnValue.put("totalExpense", totalExpense);

        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }
}