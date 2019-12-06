using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.UI;

public class ResultUI : MonoBehaviour {

    public GameObject resultLabelPrefab;
    public RectTransform resultViewPortContent;
    public Color startAndEndpointColor;

    public List<NodeModel> ResultNodeList { get; private set; }

    public void SetResult(List<NodeModel> nodes) {
        Clear();
        ResultNodeList = nodes;

        int i = 1;
        foreach(var node in nodes) {
            var label = Instantiate(resultLabelPrefab);
            label.transform.SetParent(resultViewPortContent);
            label.transform.localScale = Vector3.one;

            label.GetComponentInChildren<TextMeshProUGUI>().text = node.name;
            i++;
        }

        //First an last element are the start and endpoint
        //Make them grey just like the ones in the planner ui
        resultViewPortContent.GetChild(0).GetComponent<Image>().color = startAndEndpointColor;
        resultViewPortContent.GetChild(resultViewPortContent.childCount - 1).GetComponent<Image>().color = startAndEndpointColor;
    }

    public void Clear() {
        ResultNodeList = null;
        foreach(Transform label in resultViewPortContent) {
            Destroy(label.gameObject);
        }
    }

}
