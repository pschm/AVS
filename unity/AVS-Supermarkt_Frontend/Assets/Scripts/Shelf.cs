using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

[ExecuteInEditMode]
public class Shelf : MonoBehaviour {

    public string productName;

    private TextMeshPro textPlane;


    private void Update() {
#if UNITY_EDITOR
        if(transform.hasChanged && textPlane != null) {
            var rect = textPlane.GetComponent<RectTransform>();
            if(transform.localScale.x != 0) rect.localScale = new Vector3(1 / transform.localScale.x, rect.localScale.y, rect.localScale.z);
        }
#endif


    }

    /// <summary>
    /// Returns the point for "accessing" the shelf.
    /// </summary>
    /// <returns></returns>
    public Vector3 GetWalkToPoint() {
        //Add an offset because the shelf point itself is inside the shelf.
        return transform.position + transform.forward * 1.5f;
    }


#if UNITY_EDITOR
    private void OnDrawGizmosSelected() {
        Gizmos.color = Color.grey;
        Gizmos.DrawSphere(GetWalkToPoint(), .5f);
    }

    private void OnValidate() {
        if(textPlane == null) textPlane = transform.GetComponentInChildren<TextMeshPro>();
        textPlane.text = productName;       
    }
#endif

}
