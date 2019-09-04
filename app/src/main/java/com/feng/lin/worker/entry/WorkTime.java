package com.feng.lin.worker.entry;



public class WorkTime {
    private  int id;
    private WorkRecord workRecord;
    private PayType payType;
   private TimeZone timeZone;
    private double workCount;
    private Land land;



    public WorkRecord getWorkRecord() {
        return workRecord;
    }

    public void setWorkRecord(WorkRecord workRecord) {
        this.workRecord = workRecord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public Land getLand() {
        return land;
    }

    public void setLand(Land land) {
        this.land = land;
    }





    public double getWorkCount() {
        return workCount;
    }

    public void setWorkCount(double workCount) {
        this.workCount = workCount;
    }
}
