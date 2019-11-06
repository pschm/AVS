using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

[RequireComponent(typeof(NavMeshAgent))]
public class Customer : MonoBehaviour {

    private List<Vector3> waypoints;
    private int waypointIndex;
    private float grabCooldown;

    private NavMeshAgent agent;
    private Vector3 initialPos;

    private void Awake() {
        agent = GetComponent<NavMeshAgent>();
        initialPos = transform.position;
    }


    private void Update() {
        if(waypointIndex < waypoints?.Count && agent.remainingDistance <= 0.05f && agent.pathPending == false) {
            if(grabCooldown <= 0) {
                agent.SetDestination(waypoints[waypointIndex]);
                waypointIndex++;
                grabCooldown = 3f;
            } else {
                grabCooldown--;
            }
        }
    }


    public void SetWaypoints(List<Vector3> waypoints) {
        this.waypoints = waypoints;
        waypointIndex = 0;
        agent.isStopped = false;
    }

    public void ResetPosition() {
        agent.isStopped = true;
        transform.position = initialPos;
    }

}
