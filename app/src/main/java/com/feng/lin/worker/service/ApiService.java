package com.feng.lin.worker.service;

import android.content.Context;

import com.feng.lin.worker.dao.DBhelper;
import com.feng.lin.worker.entry.Account;
import com.feng.lin.worker.entry.Land;
import com.feng.lin.worker.entry.WorkRecord;
import com.feng.lin.worker.utils.SimpleLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiService {
    private static final ApiService ourInstance = new ApiService();

    public static ApiService getInstance() {
        return ourInstance;
    }

    private ApiService() {
    }
    private DBhelper dao;

    public void init(Context context){
        if(dao!=null) return;
        dao=new DBhelper(context);
    }

    public boolean saveAccount(String name){
        boolean flag=false;

        try{
            Account account=new Account();
            account.setName(name);
           if( dao.saveAccount(account)>0){
               flag=true;
           }
        }catch (Exception e){
            flag=false;
        }finally {
            return flag;
        }
    }
    public boolean saveLand(String name){
        boolean flag=false;
        try{
            Land land=new Land();
            land.setName(name);
            if( dao.saveLand(land)>0){
                flag=true;
            }
        }catch (Exception e){
            flag=false;
        }finally {
            return flag;
        }
    }
    public List<Map<String,Object>> searchAccounts(){
        String today= new SimpleDateFormat("yyyy-MM").format(new Date());
        List<Map<String,Object>> result= dao.searchRecordByAccount(today);
        List<Map<String,Object>> backData=new ArrayList<>();
        Map<String,Map<String,Object>> mapData=new HashMap<>();
        for (Map<String,Object> map:result){
            if(!mapData.containsKey(map.get("id"))){
                mapData.put(map.get("id").toString(),map);
                map.put("yearMonth",today);
                backData.add(map);
            }else{
                mapData.get(map.get("id")).put("spayTypeName",map.get("payTypeName"));
                mapData.get(map.get("id")).put("sworkCount",map.get("workCount"));
            }
        }

        return backData;
    }

    public List<String> hitSearch(String hit){
        return dao.hitSearch(hit);
    }
    public List<String> hitSearchLand(String hit){
        return dao.hitSearchLand(hit);
    }
    public List<Account> search(String name){
        return dao.search(name);
    }
    public List<Land> searchLand(String name){
        return dao.searchLand(name);
    }
    public boolean modifyLand(Land land){
        boolean flag=false;
        try{
            if( dao.modifyLand(land)>0){
                flag=true;
            }
        }catch (Exception e){
            flag=false;
        }finally {
            return flag;
        }
    }
    public boolean modifyAccount(Account account){

        boolean flag=false;
        try{
           if( dao.modifyAccount(account)>0){
               flag=true;
           }
        }catch (Exception e){
            flag=false;
        }finally {
            return flag;
        }
    }
    public  Map<String,Integer> hitSearchEnableLand(String hit){
        return dao.hitSearchEnableLand( hit);
    }
    public int recordWork(WorkRecord workRecord){
        int flag=0;
        try{
            if(dao.isExistsRecordByDay(workRecord.getWorkDay())){
               return -1;
            }
            if( dao.recordWork(workRecord)>0){
                flag=1;
            }
        }catch (Exception e){
            e.printStackTrace();
            flag=0;
        }
            return flag;

    }
}
