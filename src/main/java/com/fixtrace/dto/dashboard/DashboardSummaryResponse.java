package com.fixtrace.dto.dashboard;

public class DashboardSummaryResponse {

    private long totalEquipment;
    private long workingEquipment;
    private long underRepairEquipment;
    private long damagedEquipment;
    private long retiredEquipment;
    private long totalIssues;
    private long openIssues;
    private long resolvedIssues;
    private long closedIssues;
    private long criticalIssues;
    private double averageResolutionTimeHours;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(long totalEquipment, long workingEquipment, long underRepairEquipment,
                                    long damagedEquipment, long retiredEquipment, long totalIssues,
                                    long openIssues, long resolvedIssues, long closedIssues,
                                    long criticalIssues, double averageResolutionTimeHours) {
        this.totalEquipment = totalEquipment;
        this.workingEquipment = workingEquipment;
        this.underRepairEquipment = underRepairEquipment;
        this.damagedEquipment = damagedEquipment;
        this.retiredEquipment = retiredEquipment;
        this.totalIssues = totalIssues;
        this.openIssues = openIssues;
        this.resolvedIssues = resolvedIssues;
        this.closedIssues = closedIssues;
        this.criticalIssues = criticalIssues;
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }

    public long getTotalEquipment() {
        return totalEquipment;
    }

    public void setTotalEquipment(long totalEquipment) {
        this.totalEquipment = totalEquipment;
    }

    public long getWorkingEquipment() {
        return workingEquipment;
    }

    public void setWorkingEquipment(long workingEquipment) {
        this.workingEquipment = workingEquipment;
    }

    public long getUnderRepairEquipment() {
        return underRepairEquipment;
    }

    public void setUnderRepairEquipment(long underRepairEquipment) {
        this.underRepairEquipment = underRepairEquipment;
    }

    public long getDamagedEquipment() {
        return damagedEquipment;
    }

    public void setDamagedEquipment(long damagedEquipment) {
        this.damagedEquipment = damagedEquipment;
    }

    public long getRetiredEquipment() {
        return retiredEquipment;
    }

    public void setRetiredEquipment(long retiredEquipment) {
        this.retiredEquipment = retiredEquipment;
    }

    public long getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(long totalIssues) {
        this.totalIssues = totalIssues;
    }

    public long getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(long openIssues) {
        this.openIssues = openIssues;
    }

    public long getResolvedIssues() {
        return resolvedIssues;
    }

    public void setResolvedIssues(long resolvedIssues) {
        this.resolvedIssues = resolvedIssues;
    }

    public long getClosedIssues() {
        return closedIssues;
    }

    public void setClosedIssues(long closedIssues) {
        this.closedIssues = closedIssues;
    }

    public long getCriticalIssues() {
        return criticalIssues;
    }

    public void setCriticalIssues(long criticalIssues) {
        this.criticalIssues = criticalIssues;
    }

    public double getAverageResolutionTimeHours() {
        return averageResolutionTimeHours;
    }

    public void setAverageResolutionTimeHours(double averageResolutionTimeHours) {
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }
}
