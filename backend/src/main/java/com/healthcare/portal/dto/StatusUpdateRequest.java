package com.healthcare.portal.dto;

import com.healthcare.portal.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private AppointmentStatus status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
