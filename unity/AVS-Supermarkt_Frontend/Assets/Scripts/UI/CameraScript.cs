using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof(Camera))]
public class CameraScript : MonoBehaviour {

    [Header("Movement")]
    public float moveSpeed = 30;
    public Vector2 moveSpace = new Vector2(50, 50);

    [Header("Zoom")]
    public float zoomSpeed = 2;
    public float zoomMinSize = 10;
    public float zoomMaxSize = 60;

    private Camera cam;
    private Vector2 moveSpacePositive;
    private Vector2 moveSpaceNegative;

    void Start() {
        cam = GetComponent<Camera>();

        Vector3 pos = transform.position;
        moveSpacePositive = new Vector2(pos.x + moveSpace.x, pos.z + moveSpace.y);
        moveSpaceNegative = new Vector2(pos.x - moveSpace.x, pos.z - moveSpace.y);
    }


    void Update() {
        Vector3 camPos = transform.position;
        Vector3 up = transform.up;
        Vector3 right = transform.right;
        up.y = 0;
        right.y = 0;

        if(Input.GetKey(KeyCode.W)) {
            camPos += up * Time.deltaTime * moveSpeed;
        }
        if(Input.GetKey(KeyCode.S)) {
            camPos -= up * Time.deltaTime * moveSpeed;
        }

        if(Input.GetKey(KeyCode.A)) {
            camPos -= right * Time.deltaTime * moveSpeed;
        }
        if(Input.GetKey(KeyCode.D)) {
            camPos += right * Time.deltaTime * moveSpeed;
        }

        float scroll = Input.GetAxis("Mouse ScrollWheel");
        if(scroll < 0 && cam.orthographicSize < zoomMaxSize || scroll > 0 && cam.orthographicSize > zoomMinSize) {
            cam.orthographicSize -= scroll * zoomSpeed * Time.deltaTime * 200;
        }


        camPos.x = Mathf.Clamp(camPos.x, moveSpaceNegative.x, moveSpacePositive.x);
        //camPos.y = Mathf.Clamp(camPos.y, zoomMinSize, zoomMaxSize);
        camPos.z = Mathf.Clamp(camPos.z, moveSpaceNegative.y, moveSpacePositive.y);

        transform.position = camPos;
    }

}
