using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;

[Serializable]
public class NavNodeModel {

    public string id;
    public Vector2 position;
    public List<string> nextNodes;

    public NavNodeModel() { }

    public NavNodeModel(NavNode navNode) {
        id = navNode.gameObject.GetInstanceID().ToString();
        position = new Vector2(navNode.NodeCenter.x, navNode.NodeCenter.z);
        nextNodes = new List<string>();

        foreach(var nextNode in navNode.NextNodes) {
            nextNodes.Add(nextNode.gameObject.GetInstanceID().ToString());
        }

    }

    public static List<NavNodeModel> CreateList(List<NavNode> navNodes) {
        var navModelNodes = new List<NavNodeModel>();
        navNodes.ForEach(x => navModelNodes.Add(new NavNodeModel(x)));

        return navModelNodes;
    }


}
