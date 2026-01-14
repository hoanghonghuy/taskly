package io.github.hoanghonghuy.taskly.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoanghonghuy.taskly.entity.RecurrenceFrequency;
import io.github.hoanghonghuy.taskly.entity.Task;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;

@Service
public class RecurringTaskService {

    private final TaskRepository taskRepository;

    public RecurringTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Chạy mỗi ngày lúc 00:00 (nửa đêm)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void generateRecurringTasks() {
        List<Task> recurringTasks = taskRepository.findRecurringTasks();

        for (Task task : recurringTasks) {
            processRecurringTask(task);
        }
    }

    private void processRecurringTask(Task task) {
        LocalDate lastRecurrence = task.getLastRecurrenceDate();
        if (lastRecurrence == null) {
            lastRecurrence = task.getDueDate();
        }

        LocalDate nextDueDate = calculateNextDueDate(lastRecurrence, task.getRecurrenceRule());

        // Tạo task mới cho tất cả các ngày từ lastRecurrence đến hôm nay
        LocalDate currentDate = LocalDate.now();
        while (!nextDueDate.isAfter(currentDate)) {
            createRecurringTaskInstance(task, nextDueDate);
            task.setLastRecurrenceDate(nextDueDate);
            nextDueDate = calculateNextDueDate(nextDueDate, task.getRecurrenceRule());
        }

        taskRepository.save(task);
    }

    private LocalDate calculateNextDueDate(LocalDate fromDate, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> fromDate.plusDays(1);
            case WEEKLY -> fromDate.plusWeeks(1);
            case MONTHLY -> fromDate.plusMonths(1);
            case YEARLY -> fromDate.plusYears(1);
            default -> fromDate;
        };
    }

    private void createRecurringTaskInstance(Task originalTask, LocalDate dueDate) {
        Task newTask = new Task();
        newTask.setTitle(originalTask.getTitle());
        newTask.setDescription(originalTask.getDescription());
        newTask.setPriority(originalTask.getPriority());
        newTask.setDueDate(dueDate);
        newTask.setOwner(originalTask.getOwner());
        newTask.setProject(originalTask.getProject());
        newTask.setRecurrenceRule(RecurrenceFrequency.NONE); // Instance mới không lặp lại
        newTask.setTags(originalTask.getTags());
        newTask.setReminderTime(originalTask.getReminderTime());

        taskRepository.save(newTask);
    }
}