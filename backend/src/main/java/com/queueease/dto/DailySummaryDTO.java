package com.queueease.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailySummaryDTO {
    private Long businessId;
    private String businessName;
    private LocalDate date;
    private long totalServed;
    private long totalCancelled;
    private long totalNoShow;
    private long totalWaiting;
    private long totalInService;
    private long totalAppointments;
    private double avgWaitTimeMinutes;
    private List<HourlyStatDTO> hourlyTraffic = new ArrayList<>();

    public static class HourlyStatDTO {
        private String hourLabel;
        private int count;

        public HourlyStatDTO() {}

        public HourlyStatDTO(String hourLabel, int count) {
            this.hourLabel = hourLabel;
            this.count = count;
        }

        public String getHourLabel() { return hourLabel; }
        public void setHourLabel(String hourLabel) { this.hourLabel = hourLabel; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }

    public DailySummaryDTO() {}

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public long getTotalServed() { return totalServed; }
    public void setTotalServed(long totalServed) { this.totalServed = totalServed; }

    public long getTotalCancelled() { return totalCancelled; }
    public void setTotalCancelled(long totalCancelled) { this.totalCancelled = totalCancelled; }

    public long getTotalNoShow() { return totalNoShow; }
    public void setTotalNoShow(long totalNoShow) { this.totalNoShow = totalNoShow; }

    public long getTotalWaiting() { return totalWaiting; }
    public void setTotalWaiting(long totalWaiting) { this.totalWaiting = totalWaiting; }

    public long getTotalInService() { return totalInService; }
    public void setTotalInService(long totalInService) { this.totalInService = totalInService; }

    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }

    public double getAvgWaitTimeMinutes() { return avgWaitTimeMinutes; }
    public void setAvgWaitTimeMinutes(double avgWaitTimeMinutes) { this.avgWaitTimeMinutes = avgWaitTimeMinutes; }

    public List<HourlyStatDTO> getHourlyTraffic() { return hourlyTraffic; }
    public void setHourlyTraffic(List<HourlyStatDTO> hourlyTraffic) { this.hourlyTraffic = hourlyTraffic; }
}
