using System;
using System.Collections.Generic;

[Serializable]
public class PathResponse {

    public float Distance;

    public List<NodeModel> Items; //Final Result. Only set when available.

    public List<NodeModel> DemoItems; //Intermediate Result

}

