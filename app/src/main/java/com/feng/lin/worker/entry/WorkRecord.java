package com.feng.lin.worker.entry;


import java.util.List;

public class WorkRecord {
    private Long id;
    private String workDay;
    private Account account;
    private TimeType timeType;
    private List<WorkTime> workTimeList;



    public List<WorkTime> getWorkTimeList() {
        return workTimeList;
    }

    public void setWorkTimeList(List<WorkTime> workTimeList) {
        this.workTimeList = workTimeList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


}
