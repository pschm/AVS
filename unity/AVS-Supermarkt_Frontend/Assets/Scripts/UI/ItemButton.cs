using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class ItemButton : MonoBehaviour {

    public Shelf Shelf { get; private set; }

    //Used if the button is moved back in the shop list to remain its original position
    //and not get moved to the list as the last element
    public int SiblingIndex { get; private set; }

    public Button buttonComponent;
    public TextMeshProUGUI nameLabel;

    private ShoppingPlannerUI plannerUI;
    
    
    private void Start() {
        buttonComponent.onClick.AddListener(HandleClick);
    }

    /// <summary>
    /// Setup the button with all the required information like product name etc.
    /// </summary>
    /// <param name="plannerUI"></param>
    /// <param name="shelf"></param>
    /// <param name="siblingIndex">Index on which position the button is in the shop-list child hierachy</param>
    public void Setup(ShoppingPlannerUI plannerUI, Shelf shelf, int siblingIndex) {
        this.plannerUI = plannerUI;
        Shelf = shelf;
        SiblingIndex = siblingIndex;
        nameLabel.text = shelf.productName;

        transform.localScale = Vector3.one;
    }

    public void HandleClick() {
        plannerUI.TryTransferItemToOtherList(this);
    }

    public void ResetButtonToShopList() {
        //Only reset to the shop list if its a clickable button
        if(buttonComponent.interactable) plannerUI?.MoveOutOfShoppingList(this);
    }

}
