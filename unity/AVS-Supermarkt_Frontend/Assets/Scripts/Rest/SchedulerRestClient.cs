using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Networking;

public static class SchedulerRestClient {
    
    public static readonly string url = "http://localhost:8080";


    public static long PostShoppingList(List<NodeModel> nodes) {
        var jsonBody = JsonHelper.ToJson(nodes);
        UnityWebRequest www = UnityWebRequest.Post(url + "/map", jsonBody);
        www.SetRequestHeader("Accept", "application/json");

        www.SendWebRequest();
        if(www.isNetworkError) return -1;

        return www.responseCode;
    }


    public static List<NodeModel> GetCalculatedWaypoints() {
        UnityWebRequest www = UnityWebRequest.Get(url + "/waypoints");
        www.SetRequestHeader("Accept", "application/json");

        www.SendWebRequest();

        if(www.isNetworkError || www.responseCode != 200) {
            return null;
        } else {
            var data = www.downloadHandler.text;
            var convertedData = JsonUtility.FromJson<List<NodeModel>>(data);

            return convertedData;
        }
    }


}
