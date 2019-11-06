using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Rotator : MonoBehaviour {

    public float speedX = 1f;
    public float speedY = 1f;
    public float speedZ = 1f;


    void Update() {
        transform.Rotate(new Vector3(speedX, speedY, speedZ));
    }
}
