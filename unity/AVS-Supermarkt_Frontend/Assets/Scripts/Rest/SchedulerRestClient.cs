using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using Random = UnityEngine.Random;

public class SchedulerRestClient : MonoBehaviour {

    public NavNode startNavNode;
    public float delayBetweenRequests = 2f;

    private bool calculationActive;
    private bool cancelCalculation;

    private List<NavNode> navMesh = new List<NavNode>();

    public static SchedulerRestClient Instance { get; private set; }

    void Awake() {
        if(Instance == null) {
            Instance = this;
        } else {
            Destroy(gameObject);
            return;
        }
    }

    private void Start() {
        //Debug.Log("Generating custom nav mesh..");
        SetupNavMeshList(startNavNode);
    }

    private void SetupNavMeshList(NavNode startNode) {
        foreach(var node in startNode.NextNodes) {
            if(navMesh.Contains(node)) continue;

            navMesh.Add(node);
            SetupNavMeshList(node);
        }
    }


    public void StartCalculationForShoppinglist(List<NodeModel> nodes, string hostUrl, Action<PathResponse> intAction, Action<PathResponse, bool> actionOnResult) {
        if(calculationActive) {
            throw new NotImplementedException("Caculation still active. Cannot start a new one!");
        }

        PathRequest pathRequest = new PathRequest() {
            Items = nodes,
            NavMesh = NavNodeModel.CreateList(navMesh)
        };

        calculationActive = true;
        if(string.IsNullOrWhiteSpace(hostUrl)) StartCoroutine(DoCalculationEmulated(nodes, actionOnResult));
        else StartCoroutine(DoCalculation(pathRequest, hostUrl, intAction, actionOnResult));
    }

    public void CancelCalculation() {
        if(calculationActive) cancelCalculation = true;
    }


    private IEnumerator DoCalculationEmulated(List<NodeModel> nodes, Action<PathResponse, bool> actionOnResult) {
        Debug.Log("Emulating Scheduler is enabled.");
        var remainingTime = Random.Range(1.5f, 3f);

        while(remainingTime > 0 && !cancelCalculation) {
            remainingTime -= Time.deltaTime;
            yield return new WaitForEndOfFrame();
        }

        Debug.Log("Calculation done. Canceled: " + cancelCalculation);
        calculationActive = false;

        float calDistance = 0f;
        for(int i = 0; i < nodes.Count - 1; i++) {
            calDistance += Vector2.Distance(nodes[i].position, nodes[i + 1].position);
        }

        actionOnResult(new PathResponse() { Items = nodes, distance = calDistance }, cancelCalculation);

        cancelCalculation = false;
    }


    private IEnumerator DoCalculation(PathRequest requestBody, string hostUrl, Action<PathResponse> intAction, Action<PathResponse, bool> actionOnResult) {
        var request = CreatePostShoppingListRequest(requestBody, hostUrl);
        yield return request.SendWebRequest();

        if(request.responseCode != 200) {
            Debug.LogWarning("Error: " + request.responseCode);
            throw new NotImplementedException("No error handling for given response code while POST shopping list");

        } else {
            Debug.Log("Sending to scheduler successful.");
            yield return QueryCalculationResult(hostUrl, intAction, actionOnResult);
        }
    }

    private IEnumerator QueryCalculationResult(string hostUrl, Action<PathResponse> intAction, Action<PathResponse, bool> actionOnResult) {
        Debug.Log("Checking for result...");
        PathResponse result = null;

        while(result == null && !cancelCalculation) {
            var request = CreateGetCalculatedWaypointsRequest(hostUrl);
            yield return request.SendWebRequest();

            if(!request.isNetworkError && request.responseCode == 200) {
                Debug.Log("Got result.");

                var response = Encoding.UTF8.GetString(request.downloadHandler.data);
                result = JsonUtility.FromJson<PathResponse>(response);

            } else if(request.isNetworkError /*|| request.responseCode == 503*/) {
                Debug.LogWarning($"Cant get result. Network-Error: {request.isNetworkError}, Response-Code: {request.responseCode}");
                break;

            } else {

                yield return HandleIntermediateRequest(hostUrl, intAction);
            }

            yield return new WaitForSeconds(delayBetweenRequests);
        }

        calculationActive = false;
        actionOnResult(result, cancelCalculation);

        cancelCalculation = false;
    }

    private IEnumerator HandleIntermediateRequest(string hostUrl, Action<PathResponse> intAction) {
        var request = CreateGetIntermediateWaypointsRequest(hostUrl);
        yield return request.SendWebRequest();

        if(!request.isNetworkError && request.responseCode == 200) {
            var response = Encoding.UTF8.GetString(request.downloadHandler.data);
            var result = JsonUtility.FromJson<PathResponse>(response);
            intAction(result);
        }
    }


    private static UnityWebRequest CreatePostShoppingListRequest(PathRequest requestBody, string hostUrl) {
        var jsonBody = JsonUtility.ToJson(requestBody);

        var request = new UnityWebRequest(hostUrl + "/map", "POST");
        byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonBody);

        request.uploadHandler = new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json;charset=utf-8");

        return request;
    }

    private static UnityWebRequest CreateGetIntermediateWaypointsRequest(string hostUrl) {
        UnityWebRequest request = UnityWebRequest.Get(hostUrl + "/path"); //TODO Adjust to Scheduler-Endpoint
        request.SetRequestHeader("Accept", "application/json");

        return request;
    }

    private static UnityWebRequest CreateGetCalculatedWaypointsRequest(string hostUrl) {
        UnityWebRequest request = UnityWebRequest.Get(hostUrl + "/path"); //TODO Adjust to Scheduler-Endpoint
        request.SetRequestHeader("Accept", "application/json");

        return request;
    }

}
