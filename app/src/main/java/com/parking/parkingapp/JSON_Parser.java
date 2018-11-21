package com.parking.parkingapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSON_Parser {
    public static List<ParkingLotModel> parseFeed(String content)
    {
        try {
            JSONArray ar = new JSONArray(content);
            List<ParkingLotModel> parkingLotList = new ArrayList<>();

            for (int x = 0; x < ar.length(); x++) {
                JSONObject obj = ar.getJSONObject(x);
                ParkingLotModel parkingLotModel = new ParkingLotModel();

                parkingLotModel.setId(obj.getString("id"));
                parkingLotModel.setName(obj.getString("name"));
                parkingLotModel.setStatus(obj.getString("status"));
                parkingLotModel.setAmountLeft(obj.getString("amountLeft"));
                parkingLotModel.setTotalAmount(obj.getString("totalAmount"));

                parkingLotList.add(parkingLotModel);
            }

            return parkingLotList;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}