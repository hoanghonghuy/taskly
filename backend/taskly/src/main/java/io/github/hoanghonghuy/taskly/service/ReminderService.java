package io.github.hoanghonghuy.taskly.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoanghonghuy.taskly.entity.Task;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;

@Service
public class ReminderService {

    private final TaskRepository taskRepository;

    public ReminderService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Chạy mỗi phút để kiểm tra reminder
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(readOnly = true)
    public void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        // Tìm task có reminderTime trong khoảng [now - 1 min, now] hoặc đơn giản là <= now và chưa thông báo (cần thêm cờ status nếu muốn chính xác hơn)
        // Để đơn giản: tìm các task có reminderTime trong 1 phút vừa qua.
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        
        List<Task> tasksToRemind = taskRepository.findAll().stream()
            .filter(t -> t.getReminderTime() != null && 
                         t.getReminderTime().isAfter(oneMinuteAgo) && 
                         t.getReminderTime().isBefore(now))
            .toList();

        for (Task task : tasksToRemind) {
            sendNotification(task);
        }
    }

    private void sendNotification(Task task) {
        // Giả lập gửi thông báo
        System.out.println("=========================================");
        System.out.println("REMINDER: Task due soon - " + task.getTitle());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Due Date: " + task.getDueDate());
        System.out.println("Owner: " + task.getOwner().getEmail());
        System.out.println("=========================================");
    }
}
