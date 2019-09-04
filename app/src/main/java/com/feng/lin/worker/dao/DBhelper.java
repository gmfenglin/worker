package com.feng.lin.worker.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.feng.lin.worker.entry.Account;
import com.feng.lin.worker.entry.Land;
import com.feng.lin.worker.entry.WorkRecord;
import com.feng.lin.worker.entry.WorkTime;
import com.feng.lin.worker.utils.SimpleLogger;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBhelper extends SQLiteOpenHelper {
    public static final String DB_NAME="worker";
    private static final String TABLE_LAND="create table if not exists land (id integer primary key autoincrement,name varchar(20),status integer default 0)";
    private static final String TABLE_ACCOUNT="create table if not exists account (id integer primary key autoincrement,name varchar(20),status integer default 0)";
    private static final String TABLE_PAY_TYPE="create table if not exists payType (id integer primary key ,name varchar(20))";
    private static final String TABLE_TIME_TYPE="create table if not exists timeType (id integer primary key ,name varchar(20))";
    private static final String TABLE_TIME_ZONE="create table if not exists timeZone (id integer primary key ,name varchar(20))";
    private static final String TABLE_WORK_RECORD="create table if not exists workRecord (id integer primary key ,account_id integer,work_day integer,time_type_id integer)";
    private static final String TABLE_WORK_TIME="create table if not exists workTime (id integer primary key autoincrement,pay_type_id integer,record_id integer,land_id integer,time_zone_id integer,work_count double)";
    private static final int VERSION=1;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_LAND);
        db.execSQL(TABLE_ACCOUNT);
        db.execSQL(TABLE_PAY_TYPE);
        db.execSQL(TABLE_TIME_TYPE);
        db.execSQL(TABLE_TIME_ZONE);
        db.execSQL(TABLE_WORK_RECORD);
        db.execSQL(TABLE_WORK_TIME);
        //
        db.execSQL("insert into payType (id,name) values(1,'包工');");
        db.execSQL("insert into payType (id,name) values(2,'点工');");
        db.execSQL("insert into timeType (id,name) values(1,'全天');");
        db.execSQL("insert into timeType (id,name) values(2,'时段');");
        db.execSQL("insert into timeType (id,name) values(3,'休息');");
        db.execSQL("insert into timeZone (id,name) values(1,'上午');");
        db.execSQL("insert into timeZone (id,name) values(2,'下午');");
        db.execSQL("insert into timeZone (id,name) values(3,'晚上');");
        db.execSQL("insert into timeZone (id,name) values(4,'全天');");
       }
    public long recordWork(WorkRecord workRecord){
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",workRecord.getId());
        contentValues.put("account_id",workRecord.getAccount().getId());
        contentValues.put("work_day",workRecord.getWorkDay());
        contentValues.put("time_type_id",workRecord.getTimeType().getId());

        if(workRecord.getWorkTimeList()!=null && workRecord.getWorkTimeList().size()>0){
            List<WorkTime> workTimeList= workRecord.getWorkTimeList();
            for (WorkTime workTime:workTimeList){
                ContentValues contentWorkTimeValues=new ContentValues();
                contentWorkTimeValues.put("pay_type_id",workTime.getPayType().getId());
                contentWorkTimeValues.put("record_id",workTime.getWorkRecord().getId());
                contentWorkTimeValues.put("land_id",workTime.getLand().getId());
                contentWorkTimeValues.put("time_zone_id",workTime.getTimeZone().getId());
                contentWorkTimeValues.put("work_count",workTime.getWorkCount());
                getWritableDatabase().insert("workTime",null,contentWorkTimeValues);
            }

        }


        return getWritableDatabase().insert("workRecord",null,contentValues);
    }
    public long saveAccount(Account account){
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",account.getName());
       return getWritableDatabase().insert("account",null,contentValues);

    }
    public List<String> hitSearch(String hit){
        List<String> hitList=new ArrayList<>();
        Cursor cursor=getReadableDatabase().rawQuery("select * from account where name like ? limit 0,5",new String[]{hit+"%"});
        if(cursor.moveToFirst()){
            do{
                String outName=cursor.getString(cursor. getColumnIndex("name"));
                hitList.add(outName);
            }while (cursor.moveToNext());
        }
        SimpleLogger.getInstance().log(hitList.size()+":hitList");
        return hitList;
    }
    public List<String> hitSearchLand(String hit){
        List<String> hitList=new ArrayList<>();
        Cursor cursor=getReadableDatabase().rawQuery("select * from land where name like ? limit 0,5",new String[]{hit+"%"});
        if(cursor.moveToFirst()){
            do{
                String outName=cursor.getString(cursor. getColumnIndex("name"));
                hitList.add(outName);
            }while (cursor.moveToNext());
        }
        SimpleLogger.getInstance().log(hitList.size()+":hitList");
        return hitList;
    }
    public Map<String,Integer> hitSearchEnableLand(String hit){
        Map<String,Integer> hitMap=new HashMap<>();
        Cursor cursor=getReadableDatabase().rawQuery("select * from land where status=0 and name like ? limit 0,5",new String[]{hit+"%"});
        if(cursor.moveToFirst()){
            do{

                String outName=cursor.getString(cursor. getColumnIndex("name"));
                int id=cursor.getInt(cursor. getColumnIndex("id"));
               hitMap.put(outName,id);

            }while (cursor.moveToNext());
        }

        return hitMap;
    }
    public List<Land> searchLand(String name){
        List<Land> landList=new ArrayList<>();
        Cursor cursor=null;
        if(name==null || name.trim().length()<=0){

            cursor= getReadableDatabase().rawQuery("select * from land order by status asc",null);
        }else{
            cursor=getReadableDatabase().rawQuery("select * from land where name like ? order by status asc",new String[]{"%"+name+"%"});
        }
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    int id=cursor.getInt(cursor. getColumnIndex("id"));
                    String outName=cursor.getString(cursor. getColumnIndex("name"));
                    int status=cursor.getInt(cursor. getColumnIndex("status"));
                    Land land=new Land();
                    land.setId(id);
                    land.setName(outName);
                    land.setStatus(status);
                    landList.add(land);
                }while (cursor.moveToNext());
            }
        }

        return landList;
    }
    public List<Account> search(String name){
        List<Account> accountList=new ArrayList<>();
        Cursor cursor=null;
        if(name==null || name.trim().length()<=0){

            cursor= getReadableDatabase().rawQuery("select * from account order by status asc",null);
        }else{
            cursor=getReadableDatabase().rawQuery("select * from account where name like ? order by status asc",new String[]{"%"+name+"%"});
        }
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    int id=cursor.getInt(cursor. getColumnIndex("id"));
                    String outName=cursor.getString(cursor. getColumnIndex("name"));
                            int status=cursor.getInt(cursor. getColumnIndex("status"));
                            Account account=new Account();
                            account.setId(id);
                            account.setName(outName);
                            account.setStatus(status);
                    accountList.add(account);
                }while (cursor.moveToNext());
            }
        }
        SimpleLogger.getInstance().log(accountList.size()+":accountList");
        return accountList;
    }
    public long modifyAccount(Account account){
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",account.getName());
        contentValues.put("status",account.getStatus());
      return  getWritableDatabase().update("account",contentValues,"id=?",new String[]{account.getId().toString()});

    }
    public long saveLand(Land land){
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",land.getName());
        return getWritableDatabase().insert("land",null,contentValues);
    }
    public long modifyLand(Land land){
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",land.getName());
        contentValues.put("status",land.getStatus());
        return  getWritableDatabase().update("land",contentValues,"id=?",new String[]{land.getId().toString()});
    }
    public List<Map<String,Object>> searchRecordByAccount(String yearMonth){
        List<Map<String,Object>> result=new ArrayList<>();
      Cursor cursor= getReadableDatabase().rawQuery("select account.id,account.name,payType.name as payTypeName,ifnull(sum(work_count),0) as workCount from " +
                                "account left join payType  left join workRecord left join workTime " +
                               "on workRecord.id= workTime.record_id and account.id=workRecord.account_id and payType.id=workTime.pay_type_id " +
                               "and work_day like  ? where account.status =0 group by account.name,payTypeName ",new String[]{yearMonth+"%"});
        if(cursor.moveToFirst()){
            do{
                Map<String,Object> map=new HashMap<>();
                String [] keys=cursor.getColumnNames();
                for(String key:keys){
                    map.put(key,cursor.getString(cursor.getColumnIndex(key)));
                }
                System.out.println(map);
                result.add(map);

            }while (cursor.moveToNext());

        }

        return result;
    }

    public boolean isExistsRecordByDay(String day,String id){
        Cursor cursor=getReadableDatabase().rawQuery("select * from workRecord where work_day=? and account_id=?",new String[]{day,id});
        if(cursor.moveToFirst()){
            return true;
        }
        return false;
    }
    public List<Map<String,Object>> searchByAccountAndMonthCount(String yearMonth,String id){
        List<Map<String,Object>> result=new ArrayList<>();
        Cursor cursor= getReadableDatabase().rawQuery("select account.id,account.name,payType.name as payTypeName,ifnull(sum(work_count),0) as workCount from " +
                "account left join payType  left join workRecord left join workTime " +
                "on workRecord.id= workTime.record_id and account.id=workRecord.account_id and payType.id=workTime.pay_type_id " +
                "and work_day like  ? where account.status =0 and account.id=? group by account.name,payTypeName ",new String[]{yearMonth+"%",id});
        if(cursor.moveToFirst()){
            do{
                Map<String,Object> map=new HashMap<>();
                String [] keys=cursor.getColumnNames();
                for(String key:keys){
                    map.put(key,cursor.getString(cursor.getColumnIndex(key)));
                }
                System.out.println(map);
                result.add(map);

            }while (cursor.moveToNext());

        }

        return result;
    }
    public List<Map<String,Object>> searchByAccountAndMonth(String yearMonth,String id){
        Cursor cursor=getReadableDatabase().rawQuery("select * from workRecord where work_day like ? and account_id=?",new String[]{yearMonth+"%",id});
        List<Map<String,Object>> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Map<String,Object> map=new HashMap<>();
                String [] keys=cursor.getColumnNames();
                for(String key:keys){
                    map.put(key,cursor.getString(cursor.getColumnIndex(key)));
                }
                System.out.println(map);
                result.add(map);

            }while (cursor.moveToNext());

        }

        return result;
    }
    public DBhelper(Context context ) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
