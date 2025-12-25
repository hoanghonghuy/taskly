package io.github.hoanghonghuy.taskly.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Tìm các task có recurrenceRule và chưa hoàn thành (hoặc đã hoàn thành nhưng cần lặp lại)
        // Logic đơn giản: 
        // 1. Tìm các task có recurrenceRule != null
        // 2. Kiểm tra xem đã đến lúc tạo instance mới chưa dựa trên rule và lần cuối tạo.
        // Đây là một implementation phức tạp cần thư viện parse RRULE (như google-rfc-2445 hoặc lib-recur).
        // Để đơn giản cho MVP, ta sẽ giả lập logic:
        // Nếu task có recurrenceRule = "DAILY", và dueDate là hôm qua, tạo task mới cho hôm nay.
        
        List<Task> recurringTasks = taskRepository.findAll().stream()
                .filter(t -> t.getRecurrenceRule() != null && !t.getRecurrenceRule().isEmpty())
                .toList();

        for (Task task : recurringTasks) {
            processRecurringTask(task);
        }
    }

    private void processRecurringTask(Task task) {
        if ("DAILY".equalsIgnoreCase(task.getRecurrenceRule())) {
            LocalDate nextDue = task.getDueDate().plusDays(1);
            if (nextDue.isEqual(LocalDate.now())) {
                // Tạo task mới
                Task newTask = new Task();
                newTask.setTitle(task.getTitle());
                newTask.setDescription(task.getDescription());
                newTask.setPriority(task.getPriority());
                newTask.setDueDate(nextDue);
                newTask.setOwner(task.getOwner());
                newTask.setProject(task.getProject());
                newTask.setRecurrenceRule(task.getRecurrenceRule());
                newTask.setTags(task.getTags()); // Copy tags
                
                taskRepository.save(newTask);
                
                // Update task cũ để không lặp lại nữa hoặc đánh dấu là parent của chuỗi?
                // Trong mô hình đơn giản này, ta chỉ tạo task mới và giữ task cũ nguyên vẹn.
                // Để tránh tạo trùng lặp, cần logic kiểm tra xem task cho ngày hôm nay đã được tạo chưa.
                // (Bỏ qua chi tiết phức tạp này cho MVP)
            }
        }
    }
}
