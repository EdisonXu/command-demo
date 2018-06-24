package com.ex.commanddemo.concurrent.command;

/**
 * Created by edison
 * On 2018/4/17 13:52
 */
public interface Topics {

//    String DEFAULT_GROUP = "x";
    String NO_GROUP = "-1";
	String REDUCE_INVENTORY_OR_SCALE_GROUP = "0";
	String ADD_INVENTORY_GROUP = "1";
	String ADD_SCALE_GROUP = "2";
	String MONITOR_GROUP = "3";
	String PROFITS_GROUP = "4";
	String CALC_BOND_INVENTORY = "5";
	String CALC_BOND_INVENTORY_HISTORY = "6";
	String CALC_BOND_MONITOR = "7";
	String CALC_BOND_MONITOR_HISTORY = "8";
	String CALC_PROFIT = "9";
	String ASSET_ALLOCATION = "10";
    String CALC_HISTORY_PROFIT = "12";
    String ASSET_ALLOCATION_HISTORY = "13";
    String CASH_PROFIT = "14";
    String CALC_BOND_PROFIT = "15";
    String SOCKET_BOND = "16";
    String SOCKET_PANDECT_BOND = "17";
    String SOCKET_PANDECT_MONITOR = "18";
    String CALC_BOND_CASH = "19";
    String CALC_BOND_HISTORY_CASH = "20";
    String CALC_BOND = "21";
    String CALC_BOND_HISTORY = "22";
}
