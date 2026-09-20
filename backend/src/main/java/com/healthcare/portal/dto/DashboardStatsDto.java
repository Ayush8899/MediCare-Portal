package com.healthcare.portal.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {

    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long completedAppointments;
    private long pendingAppointments;
    private long cancelledAppointments;
    private BigDecimal totalRevenue;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalPatients, long totalDoctors, long totalAppointments,
                             long completedAppointments, long pendingAppointments,
                             long cancelledAppointments, BigDecimal totalRevenue) {
        this.totalPatients = totalPatients;
        this.totalDoctors = totalDoctors;
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.pendingAppointments = pendingAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
