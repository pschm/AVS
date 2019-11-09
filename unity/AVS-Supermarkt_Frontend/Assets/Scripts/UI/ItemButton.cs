using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class ItemButton : MonoBehaviour {

    public Shelf Shelf { get; private set; }
    public int SiblingIndex { get; private set; }

    public Button buttonComponent;
    public TextMeshProUGUI nameLabel;

    private ShoppingPlannerUI plannerUI;
    
    
    private void Start() {
        buttonComponent.onClick.AddListener(HandleClick);
    }

    public void Setup(ShoppingPlannerUI plannerUI, Shelf shelf, int siblingIndex) {
        this.plannerUI = plannerUI;
        Shelf = shelf;
        SiblingIndex = siblingIndex;
        nameLabel.text = shelf.productName;
    }

    public void HandleClick() {
        plannerUI.TryTransferItemToOtherList(this, Shelf);
    }

    private void OnEnable() {
        plannerUI?.ResetToShopList(this, Shelf);
    }

}
