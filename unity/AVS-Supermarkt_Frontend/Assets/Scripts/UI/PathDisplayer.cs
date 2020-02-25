using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using UnityEngine.AI;
using TMPro;

public class PathDisplayer : MonoBehaviour {

    [SerializeField] private TextMeshPro orderNumberPrefab = null;
    [SerializeField] private LineRenderer rendererPath = null;
    [SerializeField] private LineRenderer rendererStraight = null;

    private const float hightOffsetPath = .1f;
    private const float hightOffsetStraight = 2.5f;
    private const float hightOffsetNumbers = 3f;

    private List<GameObject> activeOrderNumbers = new List<GameObject>();
    private bool orderNumbersEnabled = true;

    public static PathDisplayer Instance { get; private set; }

    void Awake() {
        if(Instance == null) {
            Instance = this;
        } else {
            Destroy(gameObject);
        }
    }

    public void EnableDisplayNavPath(bool b) => rendererPath.gameObject.SetActive(b);
    public void EnableDisplayStraithPath(bool b) => rendererStraight.gameObject.SetActive(b);


    public void DisplayAllPath(List<Vector3> waypoints) {
        DisplayStraightPath(waypoints);
        DisplayNavPath(waypoints);
    }

    public void DisplayOrderNumbers(List<Vector3> waypoints) {
        if(orderNumberPrefab == null) {
            Debug.LogWarning("No order number prefab set. Skipping displaying them.");
            return;
        }

        ClearOrderNumbers();

        //Skit first and two last points (entry, checkout, exit)
        for(int i = 1; i < waypoints.Count - 2; i++) {
            var numVal = i - 1;

            var number = Instantiate(orderNumberPrefab);
            number.text = numVal.ToString();
            number.transform.SetParent(transform);
            number.transform.position = new Vector3(waypoints[i].x, hightOffsetNumbers, waypoints[i].z);
            number.name = "No. " + numVal;
            number.gameObject.SetActive(orderNumbersEnabled);

            activeOrderNumbers.Add(number.gameObject);
        }
    }

    public void ClearOrderNumbers() {
        for(int i = activeOrderNumbers.Count - 1; i > 0; i--) {
            Destroy(activeOrderNumbers[i].gameObject);
        }
        activeOrderNumbers = new List<GameObject>();
    }

    public void DisplayStraightPath(List<Vector3> waypoints) {
        var renderPoints = waypoints.ToArray();
        for(int i = 0; i < renderPoints.Length; i++) {
            renderPoints[i].y += hightOffsetStraight; //Apply hight offset
        }

        rendererStraight.positionCount = waypoints.Count;
        rendererStraight.SetPositions(renderPoints);
    }

    public void DisplayNavPath(List<Vector3> waypoints) {
        List<Vector3> linePoints = new List<Vector3>();
        NavMeshPath path = new NavMeshPath();

        //Calculate a path from each self to the next one
        for(int i = 0; i < waypoints.Count - 1; i++) {
            if(NavMesh.CalculatePath(waypoints[i], waypoints[i + 1], NavMesh.AllAreas, path)) {

                foreach(var point in path.corners) {
                    linePoints.Add(new Vector3(point.x, point.y + hightOffsetPath, point.z));
                }
            }
        }

        rendererPath.positionCount = linePoints.Count;
        rendererPath.SetPositions(linePoints.ToArray());

        rendererPath.Simplify(1);
    }

    public void ClearAll() {
        rendererPath.positionCount = 0;
        rendererStraight.positionCount = 0;
        ClearOrderNumbers();
    }


    public void EnableStraightPath(bool visable) => rendererStraight.enabled = visable;
    public void EnableNavPath(bool visable) => rendererPath.enabled = visable;
    public void EnableOrderNumbers(bool visable) {
        orderNumbersEnabled = visable;
        activeOrderNumbers.ForEach(x => x.SetActive(visable));
    }
}
