using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;

public static class SchedulerRestRequests {
    
    public static readonly string url = "http://localhost:8080";


    public static UnityWebRequest BuildPostShoppingListRequest(List<NodeModel> nodes) {
        var jsonBody = JsonHelper.ToJson(nodes);

        var request = new UnityWebRequest(url + "/map", "POST");
        byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonBody);

        request.uploadHandler = new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");

        return request;
    }


    public static UnityWebRequest BuildGetCalculatedWaypointsRequest() {
        UnityWebRequest request = UnityWebRequest.Get(url + "/path");
        request.SetRequestHeader("Accept", "application/json");

        return request;
    }


}
