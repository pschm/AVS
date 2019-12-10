using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using UnityEngine.AI;

public class PathDisplayer : MonoBehaviour {

    [SerializeField] private LineRenderer rendererPath = null;
    [SerializeField] private LineRenderer rendererStraight = null;

    private const float hightOffset = .1f;

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


    public void DisplayPath(List<Vector3> waypoints) {
        DisplayStraightPath(waypoints);
        DisplayNavPath(waypoints);
    }

    private void DisplayStraightPath(List<Vector3> waypoints) {
        var renderPoints = waypoints.ToArray();
        for(int i = 0; i < renderPoints.Length; i++) {
            renderPoints[i].y += hightOffset; //Apply hight offset
        }

        rendererStraight.positionCount = waypoints.Count;
        rendererStraight.SetPositions(renderPoints);
    }

    private void DisplayNavPath(List<Vector3> waypoints) {
        List<Vector3> linePoints = new List<Vector3>();
        NavMeshPath path = new NavMeshPath();

        //Calculate a path from each self to the next one
        for(int i = 0; i < waypoints.Count - 1; i++) {
            if(NavMesh.CalculatePath(waypoints[i], waypoints[i + 1], NavMesh.AllAreas, path)) {

                foreach(var point in path.corners) {
                    linePoints.Add(new Vector3(point.x, point.y + hightOffset, point.z));
                }
            }
        }

        rendererPath.positionCount = linePoints.Count;
        rendererPath.SetPositions(linePoints.ToArray());

        rendererPath.Simplify(1);
    }
}
