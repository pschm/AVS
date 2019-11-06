using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class ItemButton : MonoBehaviour {

    public Button buttonComponent;
    public TextMeshProUGUI nameLabel;

    private ShoppingPlannerUI plannerUI;
    private Shelf shelf;

    private void Start() {
        buttonComponent.onClick.AddListener(HandleClick);
    }

    public void Setup(ShoppingPlannerUI plannerUI, Shelf shelf) {
        this.plannerUI = plannerUI;
        this.shelf = shelf;
        nameLabel.text = shelf.productName;
    }

    public void HandleClick() {
        plannerUI.TryTransferItemToOtherList(transform, shelf);
    }

}
