using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class ResultUI : MonoBehaviour {

    public GameObject resultLabelPrefab;
    public RectTransform resultViewPortContent;

    public List<NodeModel> ResultNodeList { get; private set; }

    public void SetResult(List<NodeModel> nodes) {
        Clear();
        ResultNodeList = nodes;

        int i = 1;
        foreach(var node in nodes) {
            var label = Instantiate(resultLabelPrefab);
            label.transform.SetParent(resultViewPortContent);
            label.transform.localScale = Vector3.one;

            label.GetComponentInChildren<TextMeshProUGUI>().text = $"{i}. {node.name}";
            i++;
        }
    }

    public void Clear() {
        ResultNodeList = null;
        foreach(Transform label in resultViewPortContent) {
            Destroy(label.gameObject);
        }
    }

}
