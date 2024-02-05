package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

public class ReplicationModel {


    @SerializedName("ReplicationCode")
    private int ReplicationCode;
    @SerializedName("ServerTable")
    private String ServerTable;
    @SerializedName("ClientTable")
    private String ClientTable;
    @SerializedName("ServerPrimaryKey")
    private String ServerPrimaryKey;
    @SerializedName("ClientPrimaryKey")
    private String ClientPrimaryKey;
    @SerializedName("Condition")
    private String Condition;
    @SerializedName("ConditionDelete")
    private String ConditionDelete;
    @SerializedName("LastRepLogCode")
    private int LastRepLogCode;
    @SerializedName("LastRepLogCodeDelete")
    private int LastRepLogCodeDelete;



    //region $ getter setter
    public int getReplicationCode() {
        return ReplicationCode;
    }

    public void setReplicationCode(int replicationCode) {
        ReplicationCode = replicationCode;
    }

    public String getServerTable() {
        return ServerTable;
    }

    public void setServerTable(String serverTable) {
        ServerTable = serverTable;
    }

    public String getClientTable() {
        return ClientTable;
    }

    public void setClientTable(String clientTable) {
        ClientTable = clientTable;
    }

    public String getServerPrimaryKey() {
        return ServerPrimaryKey;
    }

    public void setServerPrimaryKey(String serverPrimaryKey) {
        ServerPrimaryKey = serverPrimaryKey;
    }

    public String getClientPrimaryKey() {
        return ClientPrimaryKey;
    }

    public void setClientPrimaryKey(String clientPrimaryKey) {
        ClientPrimaryKey = clientPrimaryKey;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public String getConditionDelete() {
        return ConditionDelete;
    }

    public void setConditionDelete(String conditionDelete) {
        ConditionDelete = conditionDelete;
    }

    public int getLastRepLogCode() {
        return LastRepLogCode;
    }

    public void setLastRepLogCode(int lastRepLogCode) {
        LastRepLogCode = lastRepLogCode;
    }

    public int getLastRepLogCodeDelete() {
        return LastRepLogCodeDelete;
    }

    public void setLastRepLogCodeDelete(int lastRepLogCodeDelete) {
        LastRepLogCodeDelete = lastRepLogCodeDelete;
    }

    //endregion

}
