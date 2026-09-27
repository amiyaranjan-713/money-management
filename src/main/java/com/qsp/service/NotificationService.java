package com.qsp.service;

import java.time.LocalDate;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.qsp.dto.ExpenseDTO;
import com.qsp.entity.ProfileEntity;
import com.qsp.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	
	
	private final ProfileRepository profileRepository;
	private final EmailService emailService;
	private final ExpanseService expanseService;
	
	@Value("${money.manager.frontend.url}")
	private String frontendurl;
	

	@Scheduled(cron = "0 0 22 * * *",zone = "Asia/Kolkata")
	public void sendDailyIncomeExpenseReminder() {
		log.info("Job started: sendDailyIncomeExpenseReminder()");
		List<ProfileEntity> profiles= profileRepository.findAll();
		
		for(ProfileEntity profile : profiles) {
			
			String body = "Hi " + profile.getFullName() + ", <br><br>"

		        + "This is a friendly reminder to add your income and expense today in Money Manager.<br><br>"
		
		        + "<a href=\"" + frontendurl + "\" "
		        + "style=\"display:inline-block; padding:10px 20px; "
		        + "background-color:#4CAF50; color:#fff; "
		        + "text-decoration:none; border-radius:5px;\">"
		        + "Open Money Manager"
		        + "</a>"
		
		        + "<br><br>Best regards,<br><br>"
		        + "Money Manager Team";
			
			emailService.sendEmail(profile.getEmail(), "Daily reminder: Add your income and expense", body);
			
			
		}
		log.info("Job completed: sendDailyIncomeExpenseReminder()");
	}
	
	
	@Scheduled(cron = "0 0 23 * * *",zone = "Asia/Kolkata")
	public void sendDailyexpensesummary() {
		
		log.info("Job started: sendDailyExpenseSummary()");
		List<ProfileEntity> profiles=profileRepository.findAll();
		for(ProfileEntity profile : profiles) {
		List<ExpenseDTO> todaysexpenses= expanseService.getExpensesFromUserOnDate(profile.getId(), LocalDate.now());
			if(!todaysexpenses.isEmpty()) {
				StringBuilder table= new StringBuilder();

	            table.append("<table style='border-collapse:collapse; width:100%;'>");

	            // Header
	            table.append("<tr style='background-color:#f2f2f2;'>")
	                    .append("<th style='border:1px solid #ddd; padding:8px;'>S.No</th>")
	                    .append("<th style='border:1px solid #ddd; padding:8px;'>Expense</th>")
	                    .append("<th style='border:1px solid #ddd; padding:8px;'>Amount</th>")
	                    .append("<th style='border:1px solid #ddd; padding:8px;'>Category</th>")
	                    .append("</tr>");

	            int i = 1;

	            for (ExpenseDTO expense : todaysexpenses) {

	                table.append("<tr>");

	                table.append("<td style='border:1px solid #ddd; padding:8px;'>")
	                        .append(i++)
	                        .append("</td>");

	                table.append("<td style='border:1px solid #ddd; padding:8px;'>")
	                        .append(expense.getName())
	                        .append("</td>");

	                table.append("<td style='border:1px solid #ddd; padding:8px;'>")
	                        .append(expense.getAmount())
	                        .append("</td>");

	                table.append("<td style='border:1px solid #ddd; padding:8px;'>")
	                        .append(expense.getCategoryId() != null
	                                ? expense.getCategoryName()
	                                : "N/A")
	                        .append("</td>");

	                table.append("</tr>");
	            }

	            table.append("</table>");
				String body= "Hi "+profile.getFullName()+
						",<br><br> Here is a summary of your expenses for today:"
						+table+"<br><br>Best regards,<br> Money Management Team";
				emailService.sendEmail(profile.getEmail(), "Your daily Expense summary", body);
			}
			
		}
		log.info("Job ended: senddailyExpensesummery");
	}
}
