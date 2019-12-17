using System;
using System.Collections.Generic;
using UnityEngine;

[Serializable]
public class PathRequest  {

    public List<NodeModel> Items;

    public List<NavNodeModel> NavMesh;


    public PathRequest() { }

}
