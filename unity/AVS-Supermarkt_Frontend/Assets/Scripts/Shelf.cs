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


    private void OnValidate() {
        if(textPlane == null) textPlane = transform.GetComponentInChildren<TextMeshPro>();
        textPlane.text = productName;       
    }
}
